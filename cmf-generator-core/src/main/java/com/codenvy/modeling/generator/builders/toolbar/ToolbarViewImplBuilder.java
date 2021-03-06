/*
 * Copyright 2014 Codenvy, S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codenvy.modeling.generator.builders.toolbar;

import com.codenvy.modeling.configuration.metamodel.diagram.Connection;
import com.codenvy.modeling.configuration.metamodel.diagram.Element;
import com.codenvy.modeling.generator.builders.AbstractBuilder;
import com.codenvy.modeling.generator.builders.ContentReplacer;
import com.codenvy.modeling.generator.builders.xml.api.GStyle;
import com.codenvy.modeling.generator.builders.xml.api.UIXmlBuilder;
import com.codenvy.modeling.generator.builders.xml.api.widgets.GPushButton;
import com.codenvy.modeling.generator.builders.xml.api.widgets.containers.GDockLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.codenvy.modeling.generator.builders.FileExtensionConstants.JAVA;
import static com.codenvy.modeling.generator.builders.FileExtensionConstants.UI_XML;
import static com.codenvy.modeling.generator.builders.MarkerBuilderConstants.ACTION_DELEGATES_MARKER;
import static com.codenvy.modeling.generator.builders.MarkerBuilderConstants.ARGUMENT_NAME_MARKER;
import static com.codenvy.modeling.generator.builders.MarkerBuilderConstants.CURRENT_PACKAGE_MARKER;
import static com.codenvy.modeling.generator.builders.MarkerBuilderConstants.ELEMENT_NAME_MARKER;
import static com.codenvy.modeling.generator.builders.MarkerBuilderConstants.MAIN_PACKAGE_MARKER;
import static com.codenvy.modeling.generator.builders.MarkerBuilderConstants.UI_FIELDS_MARKER;
import static com.codenvy.modeling.generator.builders.OffsetBuilderConstants.OFFSET;
import static com.codenvy.modeling.generator.builders.OffsetBuilderConstants.TWO_TABS;
import static com.codenvy.modeling.generator.builders.PathConstants.CLIENT_PACKAGE;
import static com.codenvy.modeling.generator.builders.PathConstants.JAVA_SOURCE_FOLDER;
import static com.codenvy.modeling.generator.builders.PathConstants.MAIN_SOURCE_PATH;
import static com.codenvy.modeling.generator.builders.PathConstants.TOOLBAR_PACKAGE;

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class ToolbarViewImplBuilder extends AbstractBuilder<ToolbarViewImplBuilder> {

    private static final String ELEMENT_FIELD = OFFSET + "@UiField(provided = true)\n" +
                                                OFFSET + "PushButton argumentName;\n";

    private static final String INITIALIZE_ELEMENT_FIELD =
            TWO_TABS + "argumentName = new PushButton(new Image(resources.argumentNameToolbar()));\n";

    private static final String INITIALIZE_CONNECTION_FIELD =
            TWO_TABS + "argumentName = new PushButton(new Image(resources.argumentName()));\n";

    private static final String BIND_ELEMENT_BUTTONS_FIELD   = TWO_TABS + "buttons.put(\"elementName\", argumentName);\n";
    private static final String ADD_CONNECTION_BUTTONS_FIELD = TWO_TABS + "mainPanel.addNorth(argumentName, ITEM_HEIGHT);\n";

    private static final String ON_BUTTON_CLICKED_METHOD = OFFSET + "@UiHandler(\"argumentName\")\n" +
                                                           OFFSET + "public void onelementNameButtonClicked(ClickEvent event) {\n" +
                                                           TWO_TABS + "delegate.onelementNameButtonClicked();\n" +
                                                           OFFSET + "}\n\n";

    private static final String FIELDS_INITIALIZE_MARKER      = "fields_initialize";
    private static final String BIND_ELEMENT_BUTTONS_MARKER   = "bind_element_buttons";
    private static final String ADD_CONNECTION_BUTTONS_MARKER = "add_connection_button";

    private static final String TOOLBAR_VIEW_IMPL_NAME = "ToolbarViewImpl";

    private final UIXmlBuilder          uiXmlBuilder;
    private final GStyle                styleBuilder;
    private final GDockLayoutPanel      dockLayoutPanelBuilder;
    private final Provider<GPushButton> pushButtonProvider;
    private       String                mainPackage;
    private       Set<Element>          elements;
    private       Set<Connection>       connections;

    @Inject
    public ToolbarViewImplBuilder(UIXmlBuilder uiXmlBuilder,
                                  GStyle styleBuilder,
                                  GDockLayoutPanel dockLayoutPanelBuilder,
                                  Provider<GPushButton> pushButtonProvider) {
        super();
        builder = this;

        this.uiXmlBuilder = uiXmlBuilder;
        this.styleBuilder = styleBuilder;
        this.dockLayoutPanelBuilder = dockLayoutPanelBuilder;
        this.pushButtonProvider = pushButtonProvider;
    }

    @Nonnull
    public ToolbarViewImplBuilder mainPackage(@Nonnull String mainPackage) {
        this.mainPackage = mainPackage;
        return this;
    }

    @Nonnull
    public ToolbarViewImplBuilder elements(@Nonnull Set<Element> elements) {
        this.elements = elements;
        return this;
    }

    @Nonnull
    public ToolbarViewImplBuilder connections(@Nonnull Set<Connection> connections) {
        this.connections = connections;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void build() throws IOException {
        String toolbarPackage = mainPackage + '.' + CLIENT_PACKAGE + '.' + TOOLBAR_PACKAGE;

        GDockLayoutPanel dockLayoutPanel = dockLayoutPanelBuilder.withPrefix("g")
                                                                 .withName("mainPanel")
                                                                 .withAddStyle("style.fullSize");
        uiXmlBuilder.withXmlns("g", "urn:import:com.google.gwt.user.client.ui")
                    .withStyle(
                            styleBuilder.withStyle("fullSize", "width: 100%; height: 100%;")
                                        .withStyle("buttonStyle",
                                                   "width: 25px; height: 27px; position: relative !important; left: 3px !important;")
                              )
                    .setWidget(dockLayoutPanel);

        StringBuilder uiFields = new StringBuilder();
        StringBuilder createFields = new StringBuilder();
        StringBuilder bindElementButtons = new StringBuilder();
        StringBuilder methods = new StringBuilder();
        StringBuilder addConnectionButtons = new StringBuilder();

        for (Element element : elements) {
            String elementName = element.getName();

            uiFields.append(createFieldCode(elementName));
            createFields.append(createElementFieldInitializeCode(elementName));
            methods.append(createOnElementButtonClickedCode(elementName));
            bindElementButtons.append(createBindElementButtonCode(elementName));
            addButtonOnPanel(elementName);
        }

        for (Connection connection : connections) {
            String connectionName = connection.getName();

            uiFields.append(createFieldCode(connectionName));
            createFields.append(createConnectionFieldInitializeCode(connectionName));
            methods.append(createOnElementButtonClickedCode(connectionName));
            addButtonOnPanel(connectionName);
            addConnectionButtons.append(createAddConnectionButtonCode(connectionName));
        }

        source = Paths.get(path,
                           MAIN_SOURCE_PATH,
                           JAVA_SOURCE_FOLDER,
                           TOOLBAR_PACKAGE,
                           TOOLBAR_VIEW_IMPL_NAME + JAVA);
        target = Paths.get(path,
                           MAIN_SOURCE_PATH,
                           JAVA_SOURCE_FOLDER,
                           convertPathToPackageName(mainPackage),
                           CLIENT_PACKAGE,
                           TOOLBAR_PACKAGE,
                           TOOLBAR_VIEW_IMPL_NAME + JAVA);

        replaceElements.put(CURRENT_PACKAGE_MARKER, toolbarPackage);
        replaceElements.put(MAIN_PACKAGE_MARKER, mainPackage);
        replaceElements.put(UI_FIELDS_MARKER, uiFields.toString());
        replaceElements.put(FIELDS_INITIALIZE_MARKER, createFields.toString());
        replaceElements.put(BIND_ELEMENT_BUTTONS_MARKER, bindElementButtons.toString());
        replaceElements.put(ACTION_DELEGATES_MARKER, methods.toString());
        replaceElements.put(ADD_CONNECTION_BUTTONS_MARKER, addConnectionButtons.toString());

        super.build();

        Path toolbarUiXMLPath = Paths.get(path,
                                          MAIN_SOURCE_PATH,
                                          JAVA_SOURCE_FOLDER,
                                          convertPathToPackageName(mainPackage),
                                          CLIENT_PACKAGE,
                                          TOOLBAR_PACKAGE,
                                          TOOLBAR_VIEW_IMPL_NAME + UI_XML);
        Files.write(toolbarUiXMLPath, uiXmlBuilder.build().getBytes());
    }

    @Nonnull
    private String createFieldCode(@Nonnull String elementName) {
        Map<String, String> masks = new LinkedHashMap<>(1);
        masks.put(ARGUMENT_NAME_MARKER, changeFirstSymbolToLowCase(elementName));

        return ContentReplacer.replace(ELEMENT_FIELD, masks);
    }

    @Nonnull
    private String createElementFieldInitializeCode(@Nonnull String elementName) {
        Map<String, String> masks = new LinkedHashMap<>(1);
        masks.put(ARGUMENT_NAME_MARKER, changeFirstSymbolToLowCase(elementName));

        return ContentReplacer.replace(INITIALIZE_ELEMENT_FIELD, masks);
    }

    @Nonnull
    private String createConnectionFieldInitializeCode(@Nonnull String connectionName) {
        Map<String, String> masks = new LinkedHashMap<>(1);
        masks.put(ARGUMENT_NAME_MARKER, changeFirstSymbolToLowCase(connectionName));

        return ContentReplacer.replace(INITIALIZE_CONNECTION_FIELD, masks);
    }

    @Nonnull
    private String createBindElementButtonCode(@Nonnull String elementName) {
        Map<String, String> masks = new LinkedHashMap<>(2);
        masks.put(ELEMENT_NAME_MARKER, elementName);
        masks.put(ARGUMENT_NAME_MARKER, changeFirstSymbolToLowCase(elementName));

        return ContentReplacer.replace(BIND_ELEMENT_BUTTONS_FIELD, masks);
    }

    @Nonnull
    private String createAddConnectionButtonCode(@Nonnull String connectionName) {
        Map<String, String> masks = new LinkedHashMap<>(1);
        masks.put(ARGUMENT_NAME_MARKER, changeFirstSymbolToLowCase(connectionName));

        return ContentReplacer.replace(ADD_CONNECTION_BUTTONS_FIELD, masks);
    }

    @Nonnull
    private String createOnElementButtonClickedCode(@Nonnull String elementName) {
        Map<String, String> masks = new LinkedHashMap<>(2);
        masks.put(ELEMENT_NAME_MARKER, elementName);
        masks.put(ARGUMENT_NAME_MARKER, changeFirstSymbolToLowCase(elementName));

        return ContentReplacer.replace(ON_BUTTON_CLICKED_METHOD, masks);
    }

    private void addButtonOnPanel(@Nonnull String elementName) {
        dockLayoutPanelBuilder.withNorth(37, pushButtonProvider.get()
                                                               .withPrefix("g")
                                                               .withName(changeFirstSymbolToLowCase(elementName))
                                                               .withAddStyle("style.buttonStyle")
                                        );
    }

}
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

package com.codenvy.modeling.configuration;

import com.codenvy.modeling.configuration.editor.EditorConfiguration;
import com.codenvy.modeling.configuration.metamodel.diagram.DiagramConfiguration;
import com.codenvy.modeling.configuration.metamodel.serialization.SerializationConfiguration;
import com.codenvy.modeling.configuration.style.StyleConfiguration;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Dmitry Kuleshov
 * @author Andrey Plotnikov
 */
public class ConfigurationKeeperImpl implements ConfigurationKeeper {

    private final DiagramConfiguration       diagramConfiguration;
    private final SerializationConfiguration serializationConfiguration;
    private final EditorConfiguration        editorConfiguration;
    private       StyleConfiguration         styleConfiguration;

    @Inject
    public ConfigurationKeeperImpl(ConfigurationAdapterFactory factory,
                                   @Assisted String diagramConfPath,
                                   @Assisted String editorConfPath,
                                   @Assisted String serializationConfPath,
                                   @Assisted String styleConfPath) throws IOException {
        editorConfiguration = factory.createEditorConfAdapter(editorConfPath).getConfiguration();
        diagramConfiguration = factory.createDiagramConfAdapter(diagramConfPath).getConfiguration();
        serializationConfiguration = factory.createSerializationConfAdapter(serializationConfPath).getConfiguration();
        // TODO add style configuration
    }

    @Nonnull
    @Override
    public DiagramConfiguration getDiagramConfiguration() {
        return diagramConfiguration;
    }

    @Nonnull
    @Override
    public SerializationConfiguration getSerializationConfiguration() {
        return serializationConfiguration;
    }

    @Nonnull
    @Override
    public EditorConfiguration getEditorConfiguration() {
        return editorConfiguration;
    }

    @Nonnull
    @Override
    public StyleConfiguration getStyleConfiguration() {
        return styleConfiguration;
    }

}

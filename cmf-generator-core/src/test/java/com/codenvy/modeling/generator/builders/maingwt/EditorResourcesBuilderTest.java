/*
 * Copyright [2014] Codenvy, S.A.
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
package com.codenvy.modeling.generator.builders.maingwt;

import com.codenvy.modeling.generator.builders.AbstractBuilderHelper;
import com.codenvy.modeling.generator.builders.resource.ResourcesBuilder;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.codenvy.modeling.generator.GenerationController.Param.TARGET_PATH;
import static com.codenvy.modeling.generator.builders.FileExtensionConstants.JAVA;
import static com.codenvy.modeling.generator.builders.PathConstants.JAVA_SOURCE_FOLDER;
import static com.codenvy.modeling.generator.builders.PathConstants.MAIN_SOURCE_PATH;
import static com.codenvy.modeling.generator.builders.ResourceNameConstants.EDITOR_RESOURCES;
import static org.junit.Assert.assertFalse;

/**
 * @author Valeriy Svydenko
 */
public class EditorResourcesBuilderTest extends AbstractBuilderHelper {

    @Before
    public void setUp() throws IOException {
        EditorResourcesBuilder builder = new EditorResourcesBuilder();
        ResourcesBuilder resourcesBuilder = new ResourcesBuilder();

        resourcesBuilder.properties(properties)

                        .needRemoveTemplate(true)

                        .build();

        builder.path(properties.getProperty(TARGET_PATH.name()))

               .properties(properties)

               .build();
    }

    @Test
    public void editorResourcesShouldBeGenerated() throws IOException {
        assertContent(File.separator + EDITOR_RESOURCES, clientFolder, EDITOR_RESOURCES + JAVA);
    }

    @Test
    public void editorResourcesTemplateShouldBeRemoved() {
        Path path = Paths.get(properties.getProperty(TARGET_PATH.name()),
                              MAIN_SOURCE_PATH,
                              JAVA_SOURCE_FOLDER,
                              EDITOR_RESOURCES + JAVA);

        assertFalse(Files.exists(path));
    }
}
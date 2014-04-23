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

package com.codenvy.modeling.adapter;

import com.codenvy.modeling.adapter.metamodel.diagram.DiagramConfigurationAdapter;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * The factory for creating configuration adapters.
 *
 * @author Andrey Plotnikov
 */
public interface AdapterFactory {
    /**
     * Create Meta model configuration adapter.
     *
     * @param inputStream stream that contains configuration
     * @return meta model configuration adapter
     */
    @Nonnull
    DiagramConfigurationAdapter getMetaModelConfAdapter(@Nonnull InputStream inputStream);
}
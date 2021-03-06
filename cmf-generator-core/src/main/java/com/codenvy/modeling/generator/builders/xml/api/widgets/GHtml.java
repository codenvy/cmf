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

package com.codenvy.modeling.generator.builders.xml.api.widgets;

/**
 * The builder for HTML GWT widget.
 * <p/>
 * The returned result must look like the following content:
 * <pre>
 * {@code
 * <g:HTML ui:field="field" addStyleDependentNames="{res.editorCSS.fullSize}"/>
 * }
 * </pre>
 *
 * @author Andrey Plotnikov
 */
public interface GHtml extends GWidget<GHtml>, HasText<GHtml> {

    String HTML_FORMAT = "<%s:HTML%s/>";

}
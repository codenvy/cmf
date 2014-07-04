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
package com.codenvy.editor.client.workspace;

import com.codenvy.editor.api.editor.EditorState;
import com.codenvy.editor.api.editor.SelectionManager;
import com.codenvy.editor.api.editor.elements.Link;
import com.codenvy.editor.api.editor.elements.Shape;
import com.codenvy.editor.api.editor.workspace.AbstractWorkspacePresenter;
import com.codenvy.editor.api.editor.workspace.AbstractWorkspaceView;
import com.codenvy.editor.client.State;
import com.codenvy.editor.client.elements.Link1;
import com.codenvy.editor.client.elements.MainElement;
import com.codenvy.editor.client.elements.Shape1;
import com.codenvy.editor.client.elements.Shape2;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

import static com.codenvy.editor.client.State.CREATING_LINK1_TARGET;
import static com.codenvy.editor.client.State.CREATING_NOTING;

/**
 * @author Andrey Plotnikov
 */
public class WorkspacePresenter extends AbstractWorkspacePresenter<State> {

    @Inject
    public WorkspacePresenter(WorkspaceView view, @Assisted EditorState<State> state, @Assisted SelectionManager selectionManager) {
        super(view, state, new MainElement(), selectionManager);
    }

    /** {@inheritDoc} */
    @Override
    public void onLeftMouseButtonClicked(int x, int y) {
        selectionManager.setElement(null);
        ((AbstractWorkspaceView)view).setZoomInButtonEnable(false);

        switch (getState()) {
            case CREATING_SHAPE1:
                Shape1 shape1 = new Shape1();

                ((WorkspaceView)view).addShape1(x, y, shape1);
                addShape(shape1, x, y);

                setState(CREATING_NOTING);
                break;

            case CREATING_SHAPE2:
                Shape2 shape2 = new Shape2();

                ((WorkspaceView)view).addShape2(x, y, shape2);
                addShape(shape2, x, y);

                setState(CREATING_NOTING);
                break;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDiagramElementClicked(@Nonnull String elementId) {
        String prevSelectedElement = selectedElement;
        selectedElement = elementId;

        Shape element = (Shape)elements.get(elementId);
        selectionManager.setElement(element);

        ((AbstractWorkspaceView)view).setZoomInButtonEnable(element.isContainer());

        Shape source;
        Shape parent;

        switch (getState()) {
            case CREATING_LINK1_SOURCE:
                setState(CREATING_LINK1_TARGET);
                break;

            case CREATING_LINK1_TARGET:
                ((WorkspaceView)view).addLink(prevSelectedElement, selectedElement);

                source = (Shape)elements.get(prevSelectedElement);

                Link1 link = new Link1(source.getId(), element.getId());

                elements.put(element.getId(), element);

                parent = source.getParent();
                if (parent != null) {
                    parent.addLink(link);
                }

                setState(CREATING_NOTING);

                notifyDiagramChangeListeners();
                break;
        }
    }

    @Override
    protected void showElements(@Nonnull Shape element) {
        ((AbstractWorkspaceView)view).clearDiagram();

        nodeElement = element;
        Shape selectedElement = null;

        ((AbstractWorkspaceView)view).setZoomOutButtonEnable(nodeElement.getParent() != null);
        ((AbstractWorkspaceView)view).setAutoAlignmentParam(nodeElement.isAutoAligned());

        int defaultX = 100;
        int defaultY = 100;

        for (Shape shape : nodeElement.getShapes()) {

            int x = shape.getX();
            int y = shape.getY();

            if (x == Shape.UNDEFINED_POSITION || nodeElement.isAutoAligned()) {
                x = defaultX;
                defaultX += 100;
            }

            if (y == Shape.UNDEFINED_POSITION || nodeElement.isAutoAligned()) {
                y = defaultY;
            }

            if (shape instanceof Shape1) {
                ((WorkspaceView)view).addShape1(x, y, (Shape1)shape);
            } else if (shape instanceof Shape2) {
                ((WorkspaceView)view).addShape2(x, y, (Shape2)shape);
            }

            shape.setX(x);
            shape.setY(y);

            if (shape.getId().equals(this.selectedElement)) {
                selectedElement = shape;
            }

            elements.put(shape.getId(), shape);
        }

        selectionManager.setElement(selectedElement);
        ((AbstractWorkspaceView)view).setZoomInButtonEnable(selectedElement != null && selectedElement.isContainer());
        this.selectedElement = selectedElement != null ? selectedElement.getId() : null;

        for (Link link : mainElement.getLinks()) {
            if (link instanceof Link1) {
                ((WorkspaceView)view).addLink(link.getSource(), link.getTarget());
            }
        }

        notifyMainElementChangeListeners();
    }

}
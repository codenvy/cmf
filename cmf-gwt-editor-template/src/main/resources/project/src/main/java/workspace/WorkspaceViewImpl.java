package current_package;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.codenvy.editor.api.editor.elements.Shape;
import com.codenvy.editor.api.editor.elements.widgets.shape.ShapeWidget;
import com.codenvy.ide.client.EditorResources;
import_elements
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orange.links.client.DiagramController;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class WorkspaceViewImpl extends WorkspaceView {

    interface WorkspaceViewImplUiBinder extends UiBinder<Widget, WorkspaceViewImpl> {
    }

    @UiField
    FlowPanel mainPanel;

    private EditorResources       resources;
    private DiagramController     controller;
    private PickupDragController  dragController;
    private Provider<ShapeWidget> widgetProvider;
    private Map<String, Widget>   elements;

    @Inject
    public WorkspaceViewImpl(WorkspaceViewImplUiBinder ourUiBinder, Provider<ShapeWidget> widgetProvider, EditorResources resources) {
        this.resources = resources;
        this.widgetProvider = widgetProvider;
        this.elements = new HashMap<>();

        widget = ourUiBinder.createAndBindUi(this);

        controller = new DiagramController(1600, 1000);
        mainPanel.add(controller.getView());

        dragController = new PickupDragController(controller.getView(), true);
        controller.registerDragController(dragController);

        bind();
    }

    private void bind() {
        mainPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onLeftMouseButtonClicked(event.getRelativeX(mainPanel.getElement()), event.getRelativeY(mainPanel.getElement()));
            }
        }, ClickEvent.getType());

        mainPanel.addDomHandler(new ContextMenuHandler() {
            @Override
            public void onContextMenu(ContextMenuEvent event) {
                NativeEvent nativeEvent = event.getNativeEvent();
                delegate.onRightMouseButtonClicked(nativeEvent.getClientX(), nativeEvent.getClientY());
            }
        }, ContextMenuEvent.getType());

        mainPanel.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                delegate.onMouseMoved(event.getRelativeX(mainPanel.getElement()), event.getRelativeY(mainPanel.getElement()));
            }
        }, MouseMoveEvent.getType());
    }

    @Override
    public void clearDiagram() {
        controller.clearDiagram();
    }

    private void addShape(int x, int y, @Nonnull final Shape shape, @Nonnull ImageResource resource) {
        final ShapeWidget elementWidget = widgetProvider.get();

        elementWidget.setTitle(shape.getTitle());
        elementWidget.setIcon(resource);
        elementWidget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                delegate.onDiagramElementClicked(shape.getId());
            }
        }, MouseDownEvent.getType());
        elementWidget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                delegate.onDiagramElementMoved(shape.getId(),
                                               elementWidget.getAbsoluteLeft() - mainPanel.getAbsoluteLeft(),
                                               elementWidget.getAbsoluteTop() - mainPanel.getAbsoluteTop());
            }
        }, MouseUpEvent.getType());

        controller.addWidget(elementWidget, x, y);
        dragController.makeDraggable(elementWidget);

        elements.put(shape.getId(), elementWidget);
    }

action_delegates}
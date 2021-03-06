package current_package;

import com.codenvy.editor.api.mvp.AbstractView;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.Set;

@ImplementedBy(ToolbarViewImpl.class)
public abstract class ToolbarView extends AbstractView<ToolbarView.ActionDelegate> {

    public interface ActionDelegate extends AbstractView.ActionDelegate {

action_delegates    }

    public abstract void showButtons(@Nonnull Set<String> components);

}
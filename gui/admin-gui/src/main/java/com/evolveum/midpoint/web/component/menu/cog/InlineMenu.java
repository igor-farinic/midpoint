package com.evolveum.midpoint.web.component.menu.cog;

import com.evolveum.midpoint.web.component.util.SimplePanel;
import com.evolveum.midpoint.web.component.util.VisibleEnableBehaviour;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author lazyman
 */
public class InlineMenu extends SimplePanel<List<InlineMenuItem>> {

    private static String ID_MENU_ITEM = "menuItem";
    private static String ID_MENU_ITEM_BODY = "menuItemBody";

    private boolean hideByDefault;

    public InlineMenu(String id, IModel model) {
        this(id, model, false);
    }

    public InlineMenu(String id, IModel model, boolean hideByDefault) {
        super(id, model);
        this.hideByDefault = hideByDefault;

        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        StringBuilder sb = new StringBuilder();
        sb.append("initInlineMenu('").append(getMarkupId()).append("', ").append(hideByDefault).append(");");

        response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
    }

    @Override
    protected void initLayout() {
        ListView<InlineMenuItem> li = new ListView<InlineMenuItem>(ID_MENU_ITEM, getModel()) {

            @Override
            protected void populateItem(ListItem<InlineMenuItem> item) {
                initMenuItem(item);
            }
        };
        li.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                List list = InlineMenu.this.getModel().getObject();
                return list != null && !list.isEmpty();
            }
        });
        add(li);
    }

    private void initMenuItem(ListItem<InlineMenuItem> menuItem) {
        final InlineMenuItem item = menuItem.getModelObject();

        menuItem.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (item.isMenuHeader()) {
                    return "dropdown-header";
                } else if (item.isDivider()) {
                    return "divider";
                }

                return getBoolean(item.getEnabled(), true) ? null : "disabled";
            }
        }));

        if (item.getEnabled() != null || item.getVisible() != null) {
            menuItem.add(new VisibleEnableBehaviour() {

                @Override
                public boolean isEnabled() {
                    return getBoolean(item.getEnabled(), true);
                }

                @Override
                public boolean isVisible() {
                    return getBoolean(item.getVisible(), true);
                }
            });
        }

        WebMarkupContainer menuItemBody;
        if (item.isMenuHeader() || item.isDivider()) {
            menuItemBody = new MenuDividerPanel(ID_MENU_ITEM_BODY, menuItem.getModel());
        } else {
            menuItemBody = new MenuLinkPanel(ID_MENU_ITEM_BODY, menuItem.getModel());
        }
        menuItemBody.setRenderBodyOnly(true);
        menuItem.add(menuItemBody);
    }

    private boolean getBoolean(IModel<Boolean> model, boolean def) {
        if (model == null) {
            return def;
        }

        Boolean value = model.getObject();
        return value != null ? value : def;
    }
}

/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.web.component.wizard;

import com.evolveum.midpoint.web.page.PageBase;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author lazyman
 */
public class WizardStep extends org.apache.wicket.extensions.wizard.WizardStep {

    public WizardStep() {
        setTitleModel(new StringResourceModel("WizardStep.title", this, null, "WizardStep.title"));
    }

    @Override
    public Component getHeader(String id, Component parent, IWizard wizard) {
        Label header = new Label(id, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getTitle();
            }
        });

        return header;
    }

    public PageBase getPageBase() {
        return (PageBase) getPage();
    }

    public String getString(String resourceKey, Object... objects) {
        return createStringResource(resourceKey, objects).getString();
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey, this, null, resourceKey, objects);
    }

    public StringResourceModel createStringResource(Enum e) {
        return createStringResource(e, null);
    }

    public StringResourceModel createStringResource(Enum e, String prefix) {
        return createStringResource(e, prefix, null);
    }

    public StringResourceModel createStringResource(Enum e, String prefix, String nullKey) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix).append('.');
        }

        if (e == null) {
            if (StringUtils.isNotEmpty(nullKey)) {
                sb.append(nullKey);
            } else {
                sb = new StringBuilder();
            }
        } else {
            sb.append(e.getDeclaringClass().getSimpleName()).append('.');
            sb.append(e.name());
        }

        return createStringResource(sb.toString());
    }

    protected String createComponentPath(String... components) {
        return StringUtils.join(components, ":");
    }
}

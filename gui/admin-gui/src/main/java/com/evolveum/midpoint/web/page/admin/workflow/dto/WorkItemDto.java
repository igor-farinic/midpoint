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

package com.evolveum.midpoint.web.page.admin.workflow.dto;

import com.evolveum.midpoint.prism.polystring.PolyString;
import com.evolveum.midpoint.prism.xml.XmlTypeConverter;
import com.evolveum.midpoint.web.component.util.Selectable;
import com.evolveum.midpoint.web.util.WebMiscUtil;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.WorkItemType;

/**
 * @author lazyman
 */
public class WorkItemDto extends Selectable {

    public static final String F_NAME = "name";
    public static final String F_OWNER_OR_CANDIDATES = "ownerOrCandidates";
    public static final String F_CREATED = "created";

    WorkItemType workItem;

    public WorkItemDto(WorkItemType workItem) {
        this.workItem = workItem;
    }

    public String getName() {
        return PolyString.getOrig(workItem.getName());
    }

    public String getOwner() {
        return workItem.getAssigneeRef() != null ? workItem.getAssigneeRef().getOid() : null;
    }

    public WorkItemType getWorkItem() {
        return workItem;
    }

    public String getCreated() {
        if (workItem.getMetadata() != null && workItem.getMetadata().getCreateTimestamp() != null) {
            return WebMiscUtil.formatDate(XmlTypeConverter.toDate(workItem.getMetadata().getCreateTimestamp()));
        } else {
            return null;
        }
    }

    public String getOwnerOrCandidates() {
        if (workItem.getAssignee() != null && workItem.getAssignee().getName() != null) {
            return workItem.getAssignee().getName().getOrig();
        } else if (workItem.getAssigneeRef() != null) {
            return workItem.getAssigneeRef().getOid();
        } else {
            return null;            // TODO get candidates
        }
    }
}

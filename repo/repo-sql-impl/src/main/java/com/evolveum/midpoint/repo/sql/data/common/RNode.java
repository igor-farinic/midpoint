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

package com.evolveum.midpoint.repo.sql.data.common;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.repo.sql.data.common.embedded.RPolyString;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.repo.sql.util.RUtil;
import com.evolveum.midpoint.schema.GetOperationOptions;
import com.evolveum.midpoint.schema.SelectorOptions;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.NodeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;

/**
 * @author lazyman
 */
@Entity
@ForeignKey(name = "fk_node")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name_norm"}))
@org.hibernate.annotations.Table(appliesTo = "m_node",
        indexes = {@Index(name = "iNodeName", columnNames = "name_orig")})
public class RNode extends RObject<NodeType> {

    private RPolyString name;
    private String nodeIdentifier;
    private String hostname;
    private Integer jmxPort;
    private XMLGregorianCalendar lastCheckInTime;
    private Boolean running;
    private Boolean clustered;
    private String internalNodeIdentifier;

    @Column(nullable = true)
    public String getHostname() {
        return hostname;
    }

    @Column(name = "clusteredNode")
    public Boolean getClustered() {
        return clustered;
    }

    public String getInternalNodeIdentifier() {
        return internalNodeIdentifier;
    }

    @Column(nullable = true)
    public Integer getJmxPort() {
        return jmxPort;
    }

    @Column(nullable = true)
    public XMLGregorianCalendar getLastCheckInTime() {
        return lastCheckInTime;
    }

    public Boolean getRunning() {
        return running;
    }

    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    @Embedded
    public RPolyString getName() {
        return name;
    }

    public void setName(RPolyString name) {
        this.name = name;
    }

    public void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setClustered(Boolean clustered) {
        this.clustered = clustered;
    }

    public void setInternalNodeIdentifier(String internalNodeIdentifier) {
        this.internalNodeIdentifier = internalNodeIdentifier;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public void setLastCheckInTime(XMLGregorianCalendar lastCheckInTime) {
        this.lastCheckInTime = lastCheckInTime;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RNode rNode = (RNode) o;

        if (name != null ? !name.equals(rNode.name) : rNode.name != null) return false;
        if (clustered != null ? !clustered.equals(rNode.clustered) : rNode.clustered != null) return false;
        if (hostname != null ? !hostname.equals(rNode.hostname) : rNode.hostname != null) return false;
        if (internalNodeIdentifier != null ? !internalNodeIdentifier.equals(rNode.internalNodeIdentifier) :
                rNode.internalNodeIdentifier != null) return false;
        if (jmxPort != null ? !jmxPort.equals(rNode.jmxPort) : rNode.jmxPort != null) return false;
        if (lastCheckInTime != null ? !lastCheckInTime.equals(rNode.lastCheckInTime) : rNode.lastCheckInTime != null)
            return false;
        if (nodeIdentifier != null ? !nodeIdentifier.equals(rNode.nodeIdentifier) : rNode.nodeIdentifier != null)
            return false;
        if (running != null ? !running.equals(rNode.running) : rNode.running != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (nodeIdentifier != null ? nodeIdentifier.hashCode() : 0);
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (jmxPort != null ? jmxPort.hashCode() : 0);
        result = 31 * result + (lastCheckInTime != null ? lastCheckInTime.hashCode() : 0);
        result = 31 * result + (running != null ? running.hashCode() : 0);
        result = 31 * result + (clustered != null ? clustered.hashCode() : 0);
        result = 31 * result + (internalNodeIdentifier != null ? internalNodeIdentifier.hashCode() : 0);
        return result;
    }

    public static void copyToJAXB(RNode repo, NodeType jaxb, PrismContext prismContext,
                                  Collection<SelectorOptions<GetOperationOptions>> options) throws
            DtoTranslationException {
        RObject.copyToJAXB(repo, jaxb, prismContext, options);

        jaxb.setName(RPolyString.copyToJAXB(repo.getName()));
        jaxb.setHostname(repo.getHostname());
        jaxb.setNodeIdentifier(repo.getNodeIdentifier());
        jaxb.setJmxPort(repo.getJmxPort());
        jaxb.setLastCheckInTime(repo.getLastCheckInTime());
        jaxb.setRunning(repo.getRunning());
        jaxb.setClustered(repo.getClustered());
        jaxb.setInternalNodeIdentifier(repo.getInternalNodeIdentifier());
    }

    public static void copyFromJAXB(NodeType jaxb, RNode repo, PrismContext prismContext) throws
            DtoTranslationException {
        RObject.copyFromJAXB(jaxb, repo, prismContext);

        repo.setName(RPolyString.copyFromJAXB(jaxb.getName()));
        repo.setHostname(jaxb.getHostname());
        repo.setNodeIdentifier(jaxb.getNodeIdentifier());
        repo.setJmxPort(jaxb.getJmxPort());
        repo.setLastCheckInTime(jaxb.getLastCheckInTime());
        repo.setRunning(jaxb.isRunning());
        repo.setClustered(jaxb.isClustered());
        repo.setInternalNodeIdentifier(jaxb.getInternalNodeIdentifier());
    }

    @Override
    public NodeType toJAXB(PrismContext prismContext, Collection<SelectorOptions<GetOperationOptions>> options)
            throws DtoTranslationException {
        NodeType object = new NodeType();
        RUtil.revive(object, prismContext);
        RNode.copyToJAXB(this, object, prismContext, options);

        return object;
    }
}

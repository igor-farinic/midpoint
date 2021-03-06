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
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.repo.sql.data.common.embedded.RCapabilities;
import com.evolveum.midpoint.repo.sql.data.common.embedded.REmbeddedReference;
import com.evolveum.midpoint.repo.sql.data.common.embedded.ROperationalState;
import com.evolveum.midpoint.repo.sql.data.common.embedded.RPolyString;
import com.evolveum.midpoint.repo.sql.data.common.other.RReferenceOwner;
import com.evolveum.midpoint.repo.sql.data.common.enums.RResourceAdministrativeState;
import com.evolveum.midpoint.repo.sql.data.common.type.RResourceApproverRef;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.repo.sql.util.RUtil;
import com.evolveum.midpoint.schema.GetOperationOptions;
import com.evolveum.midpoint.schema.SelectorOptions;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
@ForeignKey(name = "fk_resource")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name_norm"}))
@org.hibernate.annotations.Table(appliesTo = "m_resource",
        indexes = {@Index(name = "iResourceName", columnNames = "name_orig")})
public class RResource extends RObject<ResourceType> {

    private static final Trace LOGGER = TraceManager.getTrace(RResource.class);
    private RPolyString name;
    private REmbeddedReference connectorRef;
    private String namespace;
    private String configuration;
    private String xmlSchema;
    private String schemaHandling;
    private RCapabilities capabilities;
    private String scripts;
    private String synchronization;
    private String consistency;
    private ROperationalState operationalState;
    //resource business configuration, embedded component can't be used, because then it couldn't use
    //non embedded approverRef relationship
    private RResourceAdministrativeState administrativeState;
    private Set<RObjectReference> approverRef;
    //end of resource business configuration
    private String projection;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = true)
    public RResourceAdministrativeState getAdministrativeState() {
        return administrativeState;
    }

    @Where(clause = RObjectReference.REFERENCE_TYPE + "=" + RResourceApproverRef.DISCRIMINATOR)
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @ForeignKey(name = "none")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RObjectReference> getApproverRef() {
        if (approverRef == null) {
            approverRef = new HashSet<RObjectReference>();
        }
        return approverRef;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getScripts() {
        return scripts;
    }

    @Embedded
    public RCapabilities getCapabilities() {
        return capabilities;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getConfiguration() {
        return configuration;
    }

    @Embedded
    public REmbeddedReference getConnectorRef() {
        return connectorRef;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getXmlSchema() {
        return xmlSchema;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getSchemaHandling() {
        return schemaHandling;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getSynchronization() {
        return synchronization;
    }

    public String getNamespace() {
        return namespace;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getConsistency() {
        return consistency;
    }

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getProjection() {
        return projection;
    }

    @Embedded
    public ROperationalState getOperationalState() {
        return operationalState;
    }

    @Embedded
    public RPolyString getName() {
        return name;
    }

    public void setAdministrativeState(RResourceAdministrativeState administrativeState) {
        this.administrativeState = administrativeState;
    }

    public void setApproverRef(Set<RObjectReference> approverRef) {
        this.approverRef = approverRef;
    }

    public void setName(RPolyString name) {
        this.name = name;
    }

    public void setOperationalState(ROperationalState operationalState) {
        this.operationalState = operationalState;
    }

    public void setConsistency(String consistency) {
        this.consistency = consistency;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public void setConnectorRef(REmbeddedReference connectorRef) {
        this.connectorRef = connectorRef;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public void setSchemaHandling(String schemaHandling) {
        this.schemaHandling = schemaHandling;
    }

    public void setSynchronization(String synchronization) {
        this.synchronization = synchronization;
    }

    public void setCapabilities(RCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public void setScripts(String scripts) {
        this.scripts = scripts;
    }

    public void setProjection(String projection) {
        this.projection = projection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        RResource rResource = (RResource) o;

        if (name != null ? !name.equals(rResource.name) : rResource.name != null)
            return false;
        if (capabilities != null ? !capabilities.equals(rResource.capabilities) : rResource.capabilities != null)
            return false;
        if (configuration != null ? !configuration.equals(rResource.configuration) : rResource.configuration != null)
            return false;
        if (connectorRef != null ? !connectorRef.equals(rResource.connectorRef) : rResource.connectorRef != null)
            return false;
        if (namespace != null ? !namespace.equals(rResource.namespace) : rResource.namespace != null)
            return false;
        if (schemaHandling != null ? !schemaHandling.equals(rResource.schemaHandling)
                : rResource.schemaHandling != null)
            return false;
        if (scripts != null ? !scripts.equals(rResource.scripts) : rResource.scripts != null)
            return false;
        if (synchronization != null ? !synchronization.equals(rResource.synchronization)
                : rResource.synchronization != null)
            return false;
        if (xmlSchema != null ? !xmlSchema.equals(rResource.xmlSchema) : rResource.xmlSchema != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
        result = 31 * result + (xmlSchema != null ? xmlSchema.hashCode() : 0);
        result = 31 * result + (schemaHandling != null ? schemaHandling.hashCode() : 0);
        result = 31 * result + (capabilities != null ? capabilities.hashCode() : 0);
        result = 31 * result + (scripts != null ? scripts.hashCode() : 0);
        result = 31 * result + (synchronization != null ? synchronization.hashCode() : 0);
        return result;
    }

    public static void copyToJAXB(RResource repo, ResourceType jaxb, PrismContext prismContext,
                                  Collection<SelectorOptions<GetOperationOptions>> options)
            throws DtoTranslationException {
        RObject.copyToJAXB(repo, jaxb, prismContext, options);

        jaxb.setName(RPolyString.copyToJAXB(repo.getName()));
        jaxb.setNamespace(repo.getNamespace());

        if (repo.getConnectorRef() != null) {
            jaxb.setConnectorRef(repo.getConnectorRef().toJAXB(prismContext));
        }

        try {
            jaxb.setConnectorConfiguration(RUtil.toJAXB(ResourceType.class, new ItemPath(
                    ResourceType.F_CONNECTOR_CONFIGURATION), repo.getConfiguration(), ConnectorConfigurationType.class,
                    prismContext));
            jaxb.setSchema(RUtil.toJAXB(ResourceType.class, new ItemPath(ResourceType.F_SCHEMA),
                    repo.getXmlSchema(), XmlSchemaType.class, prismContext));
            jaxb.setSchemaHandling(RUtil.toJAXB(ResourceType.class, new ItemPath(ResourceType.F_SCHEMA_HANDLING),
                    repo.getSchemaHandling(), SchemaHandlingType.class, prismContext));
            jaxb.setSynchronization(RUtil.toJAXB(ResourceType.class, new ItemPath(ResourceType.F_SYNCHRONIZATION),
                    repo.getSynchronization(), SynchronizationType.class, prismContext));
            if (repo.getCapabilities() != null) {
                jaxb.setCapabilities(repo.getCapabilities().toJAXB(prismContext));
            }
            jaxb.setScripts(RUtil.toJAXB(ResourceType.class, new ItemPath(ResourceType.F_SCRIPTS),
                    repo.getScripts(), OperationProvisioningScriptsType.class, prismContext));

            if (!isResourceBusinessConfigurationEmpty(repo)) {
                ResourceBusinessConfigurationType business = new ResourceBusinessConfigurationType();
                jaxb.setBusiness(business);
                if (repo.getAdministrativeState() != null) {
                    business.setAdministrativeState(repo.getAdministrativeState().getSchemaValue());
                }
                List<ObjectReferenceType> approvers = RUtil.safeSetReferencesToList(repo.getApproverRef(), prismContext);
                if (!approvers.isEmpty()) {
                    business.getApproverRef().addAll(approvers);
                }
            }
            if (repo.getOperationalState() != null) {
                jaxb.setOperationalState(repo.getOperationalState().toJAXB(jaxb,
                        new ItemPath(ResourceType.F_OPERATIONAL_STATE), prismContext));
            }
            jaxb.setConsistency(RUtil.toJAXB(ResourceType.class, new ItemPath(ResourceType.F_CONSISTENCY),
                    repo.getConsistency(), ResourceConsistencyType.class, prismContext));
            jaxb.setProjection(RUtil.toJAXB(ResourceType.class, new ItemPath(
                    ResourceType.F_PROJECTION), repo
                    .getProjection(), ProjectionPolicyType.class,
                    prismContext));

        } catch (Exception ex) {
            throw new DtoTranslationException(ex.getMessage(), ex);
        }
    }

    public static void copyFromJAXB(ResourceType jaxb, RResource repo, PrismContext prismContext)
            throws DtoTranslationException {
        RObject.copyFromJAXB(jaxb, repo, prismContext);

        repo.setName(RPolyString.copyFromJAXB(jaxb.getName()));
        repo.setNamespace(jaxb.getNamespace());
        repo.setConnectorRef(RUtil.jaxbRefToEmbeddedRepoRef(jaxb.getConnectorRef(), prismContext));

        if (jaxb.getConnector() != null) {
            LOGGER.warn("Connector from resource type won't be saved. It should be translated to connector reference.");
        }

        try {
            repo.setConfiguration(RUtil.toRepo(jaxb.getConnectorConfiguration(), prismContext));
            repo.setXmlSchema(RUtil.toRepo(jaxb.getSchema(), prismContext));
            repo.setSchemaHandling(RUtil.toRepo(jaxb.getSchemaHandling(), prismContext));
            repo.setSynchronization(RUtil.toRepo(jaxb.getSynchronization(), prismContext));
            if (jaxb.getCapabilities() != null) {
                RCapabilities cap = new RCapabilities();
                RCapabilities.copyFromJAXB(jaxb.getCapabilities(), cap, prismContext);
                repo.setCapabilities(cap);
            }
            // repo.setCapabilities(RUtil.toRepo(jaxb.getCapabilities(),
            // prismContext));
            // repo.setNativeCapabilities(RUtil.toRepo(jaxb.getNativeCapabilities(),
            // prismContext));
            repo.setScripts(RUtil.toRepo(jaxb.getScripts(), prismContext));
            repo.setConsistency(RUtil.toRepo(jaxb.getConsistency(), prismContext));
            if (jaxb.getBusiness() != null) {
                ResourceBusinessConfigurationType business = jaxb.getBusiness();
                repo.getApproverRef().addAll(RUtil.safeListReferenceToSet(business.getApproverRef(),
                        prismContext, repo, RReferenceOwner.RESOURCE_BUSINESS_CONFIGURATON_APPROVER));
                repo.setAdministrativeState(RUtil.getRepoEnumValue(business.getAdministrativeState(),
                        RResourceAdministrativeState.class));
            }
            if (jaxb.getOperationalState() != null) {
                ROperationalState repoOpState = new ROperationalState();
                ROperationalState.copyFromJAXB(jaxb.getOperationalState(), repoOpState, prismContext);
                repo.setOperationalState(repoOpState);
            }

            repo.setProjection(RUtil.toRepo(jaxb.getProjection(), prismContext));

        } catch (Exception ex) {
            throw new DtoTranslationException(ex.getMessage(), ex);
        }
    }

    private static boolean isResourceBusinessConfigurationEmpty(RResource repo) {
        return repo.getApproverRef().isEmpty() && repo.getAdministrativeState() == null;
    }

    @Override
    public ResourceType toJAXB(PrismContext prismContext, Collection<SelectorOptions<GetOperationOptions>> options)
            throws DtoTranslationException {
        ResourceType object = new ResourceType();
        RUtil.revive(object, prismContext);
        RResource.copyToJAXB(this, object, prismContext, options);

        return object;
    }
}

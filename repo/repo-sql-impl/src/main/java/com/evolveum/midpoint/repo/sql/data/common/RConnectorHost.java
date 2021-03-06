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
import com.evolveum.midpoint.repo.sql.data.common.embedded.RPolyString;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.repo.sql.util.RUtil;
import com.evolveum.midpoint.schema.GetOperationOptions;
import com.evolveum.midpoint.schema.SelectorOptions;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ConnectorHostType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ProtectedStringType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author lazyman
 */
@Entity
@ForeignKey(name = "fk_connector_host")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name_norm"}))
@org.hibernate.annotations.Table(appliesTo = "m_connector_host",
        indexes = {@Index(name = "iConnectorHostName", columnNames = "name_orig")})
public class RConnectorHost extends RObject<ConnectorHostType> {

    private RPolyString name;
    private String hostname;
    private String port;
    private String sharedSecret;
    private Boolean protectConnection;
    private Integer timeout;

    @Lob
    @Type(type = RUtil.LOB_STRING_TYPE)
    public String getSharedSecret() {
        return sharedSecret;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Column(nullable = true)
    public Boolean isProtectConnection() {
        return protectConnection;
    }

    @Embedded
    public RPolyString getName() {
        return name;
    }

    public void setName(RPolyString name) {
        this.name = name;
    }

    public void setProtectConnection(Boolean protectConnection) {
        this.protectConnection = protectConnection;
    }

    @Column(nullable = true)
    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RConnectorHost that = (RConnectorHost) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (protectConnection != null ? !protectConnection.equals(that.protectConnection) : that.protectConnection != null)
            return false;
        if (sharedSecret != null ? !sharedSecret.equals(that.sharedSecret) : that.sharedSecret != null) return false;
        if (timeout != null ? !timeout.equals(that.timeout) : that.timeout != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (sharedSecret != null ? sharedSecret.hashCode() : 0);
        result = 31 * result + (protectConnection != null ? protectConnection.hashCode() : 0);
        result = 31 * result + (timeout != null ? timeout.hashCode() : 0);
        return result;
    }

    public static void copyToJAXB(RConnectorHost repo, ConnectorHostType jaxb, PrismContext prismContext,
                                  Collection<SelectorOptions<GetOperationOptions>> options) throws
            DtoTranslationException {
        RObject.copyToJAXB(repo, jaxb, prismContext, options);

        jaxb.setName(RPolyString.copyToJAXB(repo.getName()));
        jaxb.setHostname(repo.getHostname());
        jaxb.setPort(repo.getPort());
        jaxb.setProtectConnection(repo.isProtectConnection());
        jaxb.setTimeout(repo.getTimeout());

        try {
            jaxb.setSharedSecret(RUtil.toJAXB(ConnectorHostType.class, new ItemPath(ConnectorHostType.F_SHARED_SECRET),
                    repo.getSharedSecret(), ProtectedStringType.class, prismContext));
        } catch (Exception ex) {
            new DtoTranslationException(ex.getMessage(), ex);
        }
    }

    public static void copyFromJAXB(ConnectorHostType jaxb, RConnectorHost repo, PrismContext prismContext) throws
            DtoTranslationException {
        RObject.copyFromJAXB(jaxb, repo, prismContext);

        repo.setName(RPolyString.copyFromJAXB(jaxb.getName()));
        repo.setHostname(jaxb.getHostname());
        repo.setPort(jaxb.getPort());
        repo.setTimeout(jaxb.getTimeout());
        repo.setProtectConnection(jaxb.isProtectConnection());

        try {
            repo.setSharedSecret(RUtil.toRepo(jaxb.getSharedSecret(), prismContext));
        } catch (Exception ex) {
            new DtoTranslationException(ex.getMessage(), ex);
        }
    }

    @Override
    public ConnectorHostType toJAXB(PrismContext prismContext, Collection<SelectorOptions<GetOperationOptions>> options)
            throws DtoTranslationException {
        ConnectorHostType object = new ConnectorHostType();
        RUtil.revive(object, prismContext);
        RConnectorHost.copyToJAXB(this, object, prismContext, options);

        return object;
    }
}

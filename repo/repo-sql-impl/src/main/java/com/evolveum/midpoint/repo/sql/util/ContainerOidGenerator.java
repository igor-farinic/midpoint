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

package com.evolveum.midpoint.repo.sql.util;

import com.evolveum.midpoint.repo.sql.data.common.RAnyContainer;
import com.evolveum.midpoint.repo.sql.data.common.RContainer;
import com.evolveum.midpoint.repo.sql.data.common.RObject;
import com.evolveum.midpoint.repo.sql.data.common.ROwnable;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lazyman
 */
public class ContainerOidGenerator implements IdentifierGenerator {

    private static final Trace LOGGER = TraceManager.getTrace(ContainerOidGenerator.class);

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        if (object instanceof RAnyContainer) {
            RAnyContainer any = (RAnyContainer) object;
            RContainer owner = any.getOwner();
            String oid = owner.getOid();
            if (oid == null) {
                oid = generate(owner);
                owner.setOid(oid);
            }
            LOGGER.trace("Created oid='{}' for any.", new Object[]{oid});
            return oid;
        }

        return generate(object);
    }

    private String generate(Object object) {
        RContainer container = null;
        if (object instanceof ROwnable) {
            container = ((ROwnable) object).getContainerOwner();
        } else if (object instanceof RObject) {
            container = (RContainer) object;
        }

        if (container == null) {
            throw new HibernateException("Couldn't create id for '"
                    + object.getClass().getSimpleName() + "' (should not happen).");
        }

        if (StringUtils.isNotEmpty(container.getOid())) {
            LOGGER.trace("Created oid='{}' for '{}'.", new Object[]{container.getOid(), toString(object)});
            return container.getOid();
        }

        String oid = UUID.randomUUID().toString();
        LOGGER.trace("Created oid='{}' for '{}'.", new Object[]{oid, toString(object)});
        return oid;
    }

    private String toString(Object object) {
        RContainer container = (RContainer) object;

        StringBuilder builder = new StringBuilder();
        builder.append(object.getClass().getSimpleName());
        builder.append("[");
        builder.append(container.getOid());
        builder.append(",");
        builder.append(container.getId());
        builder.append("]");

        return builder.toString();
    }
}

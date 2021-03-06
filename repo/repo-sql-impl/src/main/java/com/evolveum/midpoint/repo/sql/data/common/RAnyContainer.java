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

import com.evolveum.midpoint.prism.Item;
import com.evolveum.midpoint.prism.PrismContainerValue;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.repo.sql.data.common.any.*;
import com.evolveum.midpoint.repo.sql.data.common.id.RAnyContainerId;
import com.evolveum.midpoint.repo.sql.data.common.other.RContainerType;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.repo.sql.util.RUtil;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ExtensionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ShadowAttributesType;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lazyman
 */
@Entity
@IdClass(RAnyContainerId.class)
@Table(name = "m_any")
public class RAnyContainer implements Serializable {

    public static final String OWNER_TYPE = "owner_type";

    private RContainer owner;
    private String ownerOid;
    private Long ownerId;
    private RContainerType ownerType;

    private Set<RAnyString> strings;
    private Set<RAnyLong> longs;
    private Set<RAnyDate> dates;
    private Set<RAnyReference> references;
    private Set<RAnyClob> clobs;
    private Set<RAnyPolyString> polys;

    @ForeignKey(name = "none")
    @MapsId("owner")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "owner_id", referencedColumnName = "id"),
            @JoinColumn(name = "owner_oid", referencedColumnName = "oid")
    })
    public RContainer getOwner() {
        return owner;
    }

    @Column(name = "owner_id")
    public Long getOwnerId() {
        if (ownerId == null && owner != null) {
            ownerId = owner.getId();
        }
        return ownerId;
    }

    @Column(name = "owner_oid", length = RUtil.COLUMN_LENGTH_OID)
    public String getOwnerOid() {
        if (ownerOid == null && owner != null) {
            ownerOid = owner.getOid();
        }
        return ownerOid;
    }

    @Id
    @GeneratedValue(generator = "ContainerTypeGenerator")
    @GenericGenerator(name = "ContainerTypeGenerator",
            strategy = "com.evolveum.midpoint.repo.sql.util.ContainerTypeGenerator")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = OWNER_TYPE)
    public RContainerType getOwnerType() {
        return ownerType;
    }

    @OneToMany(mappedBy = RAnyClob.ANY_CONTAINER, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RAnyClob> getClobs() {
        if (clobs == null) {
            clobs = new HashSet<RAnyClob>();
        }
        return clobs;
    }

    @OneToMany(mappedBy = RAnyLong.ANY_CONTAINER, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RAnyLong> getLongs() {
        if (longs == null) {
            longs = new HashSet<RAnyLong>();
        }
        return longs;
    }

    @OneToMany(mappedBy = RAnyString.ANY_CONTAINER, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RAnyString> getStrings() {
        if (strings == null) {
            strings = new HashSet<RAnyString>();
        }
        return strings;
    }

    @OneToMany(mappedBy = RAnyDate.ANY_CONTAINER, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RAnyDate> getDates() {
        if (dates == null) {
            dates = new HashSet<RAnyDate>();
        }
        return dates;
    }

    @OneToMany(mappedBy = RAnyReference.ANY_CONTAINER, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RAnyReference> getReferences() {
        if (references == null) {
            references = new HashSet<RAnyReference>();
        }
        return references;
    }

    @OneToMany(mappedBy = RAnyPolyString.ANY_CONTAINER, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    public Set<RAnyPolyString> getPolys() {
        if (polys == null) {
            polys = new HashSet<RAnyPolyString>();
        }
        return polys;
    }

    public void setPolys(Set<RAnyPolyString> polys) {
        this.polys = polys;
    }

    public void setClobs(Set<RAnyClob> clobs) {
        this.clobs = clobs;
    }

    public void setReferences(Set<RAnyReference> references) {
        this.references = references;
    }

    public void setOwnerType(RContainerType ownerType) {
        this.ownerType = ownerType;
    }

    public void setDates(Set<RAnyDate> dates) {
        this.dates = dates;
    }

    public void setLongs(Set<RAnyLong> longs) {
        this.longs = longs;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public void setStrings(Set<RAnyString> strings) {
        this.strings = strings;
    }

    public void setOwner(RContainer owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RAnyContainer that = (RAnyContainer) o;

        if (clobs != null ? !clobs.equals(that.clobs) : that.clobs != null) return false;
        if (dates != null ? !dates.equals(that.dates) : that.dates != null) return false;
        if (longs != null ? !longs.equals(that.longs) : that.longs != null) return false;
        if (strings != null ? !strings.equals(that.strings) : that.strings != null) return false;
        if (references != null ? !references.equals(that.references) : that.references != null) return false;
        if (polys != null ? !polys.equals(that.polys) : that.polys != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ownerType != null ? ownerType.hashCode() : 0;

        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static void copyToJAXB(RAnyContainer repo, ShadowAttributesType jaxb,
                                  PrismContext prismContext) throws
            DtoTranslationException {
        Validate.notNull(repo, "Repo object must not be null.");
        Validate.notNull(jaxb, "JAXB object must not be null.");

        copyToJAXB(repo, jaxb.asPrismContainerValue(), prismContext);
    }

    public static void copyToJAXB(RAnyContainer repo, ExtensionType jaxb, PrismContext prismContext) throws
            DtoTranslationException {
        Validate.notNull(repo, "Repo object must not be null.");
        Validate.notNull(jaxb, "JAXB object must not be null.");

        copyToJAXB(repo, jaxb.asPrismContainerValue(), prismContext);
    }

    private static void copyToJAXB(RAnyContainer repo, PrismContainerValue containerValue,
                                   PrismContext prismContext) throws
            DtoTranslationException {
        RAnyConverter converter = new RAnyConverter(prismContext);

        convertValues(converter, containerValue, repo.getClobs());
        convertValues(converter, containerValue, repo.getDates());
        convertValues(converter, containerValue, repo.getLongs());
        convertValues(converter, containerValue, repo.getStrings());
        convertValues(converter, containerValue, repo.getReferences());
        convertValues(converter, containerValue, repo.getPolys());
    }

    private static <T extends RAnyValue> void convertValues(RAnyConverter converter, PrismContainerValue containerValue,
                                                                  Set<T> values) throws DtoTranslationException {
        if (values == null) {
            return;
        }

        for (RAnyValue value : values) {
            converter.convertFromRValue(value, containerValue);
        }
    }

    public static void copyFromJAXB(ShadowAttributesType jaxb, RAnyContainer repo,
                                    PrismContext prismContext) throws
            DtoTranslationException {
        Validate.notNull(repo, "Repo object must not be null.");
        Validate.notNull(jaxb, "JAXB object must not be null.");

        copyFromJAXB(jaxb.asPrismContainerValue(), repo, prismContext);
    }

    public static void copyFromJAXB(ExtensionType jaxb, RAnyContainer repo, PrismContext prismContext) throws
            DtoTranslationException {
        Validate.notNull(repo, "Repo object must not be null.");
        Validate.notNull(jaxb, "JAXB object must not be null.");

        copyFromJAXB(jaxb.asPrismContainerValue(), repo, prismContext);
    }

    private static void copyFromJAXB(PrismContainerValue containerValue, RAnyContainer repo,
                                     PrismContext prismContext) throws
            DtoTranslationException {
        RAnyConverter converter = new RAnyConverter(prismContext);

        Set<RAnyValue> values = new HashSet<RAnyValue>();
        try {
            List<Item<?>> items = containerValue.getItems();
            for (Item item : items) {
                values.addAll(converter.convertToRValue(item));
            }
        } catch (Exception ex) {
            throw new DtoTranslationException(ex.getMessage(), ex);
        }

        for (RAnyValue value : values) {
            value.setAnyContainer(repo);

            if (value instanceof RAnyClob) {
                repo.getClobs().add((RAnyClob) value);
            } else if (value instanceof RAnyDate) {
                repo.getDates().add((RAnyDate) value);
            } else if (value instanceof RAnyLong) {
                repo.getLongs().add((RAnyLong) value);
            } else if (value instanceof RAnyReference) {
                repo.getReferences().add((RAnyReference) value);
            } else if (value instanceof RAnyString) {
                repo.getStrings().add((RAnyString) value);
            } else if (value instanceof RAnyPolyString) {
                repo.getPolys().add((RAnyPolyString) value);
            }
        }
    }
}

package com.evolveum.midpoint.repo.sql.data.common;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PropertyPath;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.OperationalStateType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ResourceBusinessConfigurationType;

@Embeddable
public class ROperationalState {

	RAvailabilityStatusType lastAvailabilityStatus;
	
	public RAvailabilityStatusType getLastAvailabilityStatus() {
		return lastAvailabilityStatus;
	}
	
	public void setLastAvailabilityStatus(RAvailabilityStatusType lastAvailabilityStatus) {
		this.lastAvailabilityStatus = lastAvailabilityStatus;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ROperationalState that = (ROperationalState) o;

        if (lastAvailabilityStatus != null ? !lastAvailabilityStatus.equals(that.lastAvailabilityStatus) :
                that.lastAvailabilityStatus != null) return false;
   
        return true;
    }

    @Override
    public int hashCode() {
        int result = lastAvailabilityStatus != null ? lastAvailabilityStatus.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static void copyToJAXB(ROperationalState repo, OperationalStateType jaxb, ObjectType parent, PropertyPath path,
            PrismContext prismContext) throws DtoTranslationException {
        Validate.notNull(repo, "Repo object must not be null.");
        Validate.notNull(jaxb, "JAXB object must not be null.");

		try {
			if (repo.getLastAvailabilityStatus() != null) {
				jaxb.setLastAvailabilityStatus(repo.getLastAvailabilityStatus().getStatus());
			}
		} catch (Exception ex) {
            throw new DtoTranslationException(ex.getMessage(), ex);
        }
    }

    public static void copyFromJAXB(OperationalStateType jaxb, ROperationalState repo, PrismContext prismContext) throws
            DtoTranslationException {
        Validate.notNull(repo, "Repo object must not be null.");
        Validate.notNull(jaxb, "JAXB object must not be null.");

        try {
        	if (jaxb.getLastAvailabilityStatus() != null){
            	repo.setLastAvailabilityStatus(RAvailabilityStatusType.toRepoType(jaxb.getLastAvailabilityStatus()));
            }
        } catch (Exception ex) {
            throw new DtoTranslationException(ex.getMessage(), ex);
        }
    }

    public OperationalStateType toJAXB(ObjectType parent, PropertyPath path, PrismContext prismContext) throws
            DtoTranslationException {
        OperationalStateType operationalState = new OperationalStateType();
        ROperationalState.copyToJAXB(this, operationalState, parent, path, prismContext);
        return operationalState;
    }

}
package com.evolveum.midpoint.wf.processors.primary;

import com.evolveum.midpoint.prism.Objectable;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.wf.jobs.Job;
import com.evolveum.midpoint.wf.jobs.WfTaskUtil;
import com.evolveum.midpoint.wf.processors.primary.aspect.PrimaryChangeAspect;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectReferenceType;

import java.util.List;

/**
 * A job enhanced with PrimaryChangeProcessor-specific features.
 *
 * @author mederly
 */
public class PcpJob extends Job {

    PcpJob(Job original) {
        super(original);
    }

    public PrimaryChangeAspect getChangeAspect() {
        return getWfTaskUtil().getProcessWrapper(getTask(), getPcp().getProcessWrappers());
    }

    private PrimaryChangeProcessor getPcp() {
        return (PrimaryChangeProcessor) getChangeProcessor();
    }

    private WfTaskUtil getWfTaskUtil() {
        return getPcp().getWfTaskUtil();
    }

    public void storeResultingDeltas(List<ObjectDelta<Objectable>> deltas) throws SchemaException {
        getWfTaskUtil().storeResultingDeltas(deltas, getTask());
    }

    public void addApprovedBy(List<ObjectReferenceType> approvedBy) throws SchemaException {
        getWfTaskUtil().addApprovedBy(getTask(), approvedBy);
    }

    public List<ObjectDelta<Objectable>> retrieveDeltasToProcess() throws SchemaException {
        return getWfTaskUtil().retrieveDeltasToProcess(getTask());
    }
}

/*
 * Copyright (c) 2013 Evolveum
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
package com.evolveum.midpoint.model.intest.sync;

import static org.testng.AssertJUnit.assertTrue;
import static com.evolveum.midpoint.test.IntegrationTestTools.display;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import com.evolveum.icf.dummy.resource.BreakMode;
import com.evolveum.icf.dummy.resource.DummyAccount;
import com.evolveum.midpoint.audit.api.AuditEventRecord;
import com.evolveum.midpoint.audit.api.AuditEventStage;
import com.evolveum.midpoint.audit.api.AuditEventType;
import com.evolveum.midpoint.model.intest.AbstractInitializedModelIntegrationTest;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismProperty;
import com.evolveum.midpoint.prism.PrismPropertyValue;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.path.IdItemPathSegment;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.path.NameItemPathSegment;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.schema.result.OperationResultStatus;
import com.evolveum.midpoint.schema.util.MiscSchemaUtil;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.test.DummyResourceContoller;
import com.evolveum.midpoint.test.IntegrationTestTools;
import com.evolveum.midpoint.test.ProvisioningScriptSpec;
import com.evolveum.midpoint.test.util.TestUtil;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.MiscUtil;
import com.evolveum.midpoint.util.exception.CommunicationException;
import com.evolveum.midpoint.util.exception.ConfigurationException;
import com.evolveum.midpoint.util.exception.ObjectNotFoundException;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.exception.SecurityViolationException;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.AssignmentPolicyEnforcementType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.AssignmentType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ConstructionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.MappingStrengthType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ResourceAttributeDefinitionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.RoleType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.UserType;

/**
 * @author semancik
 *
 */
@ContextConfiguration(locations = {"classpath:ctx-model-intest-test-main.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class TestRecomputeTask extends AbstractInitializedModelIntegrationTest {
	
	private static final File TEST_DIR = new File("src/test/resources/sync");
	
	private static final File TASK_USER_RECOMPUTE_FILE = new File(TEST_DIR, "task-user-recompute.xml");
	private static final String TASK_USER_RECOMPUTE_OID = "91919191-76e0-59e2-86d6-3d4f02d3aaaa";
		
	@Override
	public void initSystem(Task initTask, OperationResult initResult) throws Exception {
		super.initSystem(initTask, initResult);
		assumeAssignmentPolicy(AssignmentPolicyEnforcementType.RELATIVE);
	}

	@Test
    public void test100Recompute() throws Exception {
		final String TEST_NAME = "test100Recompute";
        TestUtil.displayTestTile(this, TEST_NAME);

        // GIVEN
        Task task = createTask(TestRecomputeTask.class.getName() + "." + TEST_NAME);
        OperationResult result = task.getResult();
        
        // Preconditions
        assertUsers(5);
        assertNoDummyAccount(RESOURCE_DUMMY_RED_NAME, ACCOUNT_GUYBRUSH_DUMMY_USERNAME);
        assertNoDummyAccount(RESOURCE_DUMMY_RED_NAME, ACCOUNT_JACK_DUMMY_USERNAME);
        
        // Do some ordinary operations
        
        assignRole(USER_GUYBRUSH_OID, ROLE_PIRATE_OID, task, result);
        assignRole(USER_JACK_OID, ROLE_JUDGE_OID, task, result);
        
        result.computeStatus();
        TestUtil.assertSuccess(result);
        
        // Now do something evil
        
        // TODO: change definition of role "pirate". midPoint will no recompute automatically
        // the recompute task should do it
        
        ConstructionType redConstruction = new ConstructionType();
        ObjectReferenceType resourceRedRef = new ObjectReferenceType();
        resourceRedRef.setOid(RESOURCE_DUMMY_RED_OID);
		redConstruction.setResourceRef(resourceRedRef);
        ObjectDelta<RoleType> roleJudgeDelta = ObjectDelta.createModificationAddContainer(RoleType.class, ROLE_JUDGE_OID, 
        		new ItemPath(
        				new NameItemPathSegment(RoleType.F_INDUCEMENT),
        				new IdItemPathSegment(1111L),
        				new NameItemPathSegment(AssignmentType.F_CONSTRUCTION)),
        		prismContext, redConstruction);
        modelService.executeChanges(MiscSchemaUtil.createCollection(roleJudgeDelta), null, task, result);
        
        PrismObject<RoleType> rolePirate = modelService.getObject(RoleType.class, ROLE_PIRATE_OID, null, task, result);
        ItemPath attrItemPath = new ItemPath(
				new NameItemPathSegment(RoleType.F_INDUCEMENT),
				new IdItemPathSegment(1111L),
				new NameItemPathSegment(AssignmentType.F_CONSTRUCTION),
				new IdItemPathSegment(60004L),
				new NameItemPathSegment(ConstructionType.F_ATTRIBUTE));
        PrismProperty<ResourceAttributeDefinitionType> attributeProperty = rolePirate.findProperty(attrItemPath);
        assertNotNull("No attribute property in "+rolePirate);
        PrismPropertyValue<ResourceAttributeDefinitionType> oldAttrPVal = null;
        for (PrismPropertyValue<ResourceAttributeDefinitionType> pval: attributeProperty.getValues()) {
        	ResourceAttributeDefinitionType attrType = pval.getValue();
        	if (attrType.getRef().getLocalPart().equals(DummyResourceContoller.DUMMY_ACCOUNT_ATTRIBUTE_WEAPON_NAME)) {
        		oldAttrPVal = pval;
        	}
        }
        assertNotNull("Definition for weapon attribute not found in "+rolePirate);
        PrismPropertyValue<ResourceAttributeDefinitionType> newAttrPVal = oldAttrPVal.clone();
        JAXBElement<?> cutlassExpressionEvalJaxbElement = newAttrPVal.getValue().getOutbound().getExpression().getExpressionEvaluator().get(0);
        Element cutlassValueEvaluator = (Element) cutlassExpressionEvalJaxbElement.getValue();
        Element daggerValueEvaluator = DOMUtil.createElement(cutlassValueEvaluator.getOwnerDocument(), DOMUtil.getQName(cutlassValueEvaluator));
        daggerValueEvaluator.setTextContent("dagger");
        JAXBElement<?> daggerExpressionEvalJaxbElement = new JAXBElement<Object>(DOMUtil.getQName(daggerValueEvaluator), Object.class, daggerValueEvaluator);
        newAttrPVal.getValue().getOutbound().getExpression().getExpressionEvaluator().add(daggerExpressionEvalJaxbElement);
        newAttrPVal.getValue().getOutbound().setStrength(MappingStrengthType.STRONG);
        
        ObjectDelta<RoleType> rolePirateDelta = ObjectDelta.createModificationDeleteProperty(RoleType.class, ROLE_PIRATE_OID, 
        		attrItemPath, prismContext, oldAttrPVal.getValue());
        IntegrationTestTools.displayJaxb("AAAAAAAAAAA", newAttrPVal.getValue(), ConstructionType.F_ATTRIBUTE);
        display("BBBBBB", newAttrPVal.getValue().toString());
        rolePirateDelta.addModificationAddProperty(attrItemPath, newAttrPVal.getValue());

        display("Role pirate delta", rolePirateDelta);
		modelService.executeChanges(MiscSchemaUtil.createCollection(rolePirateDelta), null, task, result);
        
		// just to be sure
		rolePirate = modelService.getObject(RoleType.class, ROLE_PIRATE_OID, null, task, result);
		display("Role pirate after modify", rolePirate);
		IntegrationTestTools.displayXml("Role pirate after modify", rolePirate);
		
		assertDummyAccount(null, ACCOUNT_GUYBRUSH_DUMMY_USERNAME, "Guybrush Threepwood", true);
		assertNoDummyAccount(RESOURCE_DUMMY_RED_NAME, ACCOUNT_GUYBRUSH_DUMMY_USERNAME);
		
		assertDummyAccount(null, ACCOUNT_JACK_DUMMY_USERNAME, "Jack Sparrow", true);
		assertNoDummyAccount(RESOURCE_DUMMY_RED_NAME, ACCOUNT_JACK_DUMMY_USERNAME);
		
        result.computeStatus();
        TestUtil.assertSuccess(result);
        
		// WHEN
        TestUtil.displayWhen(TEST_NAME);
        addObject(TASK_USER_RECOMPUTE_FILE);
        
        dummyAuditService.clear();
        
        waitForTaskStart(TASK_USER_RECOMPUTE_OID, false);
		
        // WHEN
        TestUtil.displayWhen(TEST_NAME);
        
        waitForTaskFinish(TASK_USER_RECOMPUTE_OID, true, 40000);
        
        // THEN
        TestUtil.displayThen(TEST_NAME);
        
        List<PrismObject<UserType>> users = modelService.searchObjects(UserType.class, null, null, task, result);
        display("Users after recompute", users);
        
        assertDummyAccount(null, ACCOUNT_GUYBRUSH_DUMMY_USERNAME, "Guybrush Threepwood", true);
        assertDummyAccountAttribute(null, ACCOUNT_GUYBRUSH_DUMMY_USERNAME, 
        		DummyResourceContoller.DUMMY_ACCOUNT_ATTRIBUTE_WEAPON_NAME, "cutlass", "dagger");
        assertNoDummyAccount(RESOURCE_DUMMY_RED_NAME, ACCOUNT_GUYBRUSH_DUMMY_USERNAME);
        
        assertDummyAccount(null, ACCOUNT_JACK_DUMMY_USERNAME, "Jack Sparrow", true);
        assertDummyAccount(RESOURCE_DUMMY_RED_NAME, ACCOUNT_JACK_DUMMY_USERNAME, "Jack Sparrow", true);
        // TODO: asserts
        
        assertEquals("Unexpected number of users", 5, users.size());
        
        // Check audit
        display("Audit", dummyAuditService);
        
        List<AuditEventRecord> auditRecords = dummyAuditService.getRecords();
        
    	int i=0;
    	int modifications = 0;
    	for (; i < (auditRecords.size() - 1); i+=2) {
        	AuditEventRecord requestRecord = auditRecords.get(i);
        	assertNotNull("No request audit record ("+i+")", requestRecord);
        	assertEquals("Got this instead of request audit record ("+i+"): "+requestRecord, AuditEventStage.REQUEST, requestRecord.getEventStage());
        	assertTrue("Unexpected delta in request audit record "+requestRecord, requestRecord.getDeltas() == null || requestRecord.getDeltas().isEmpty());

        	AuditEventRecord executionRecord = auditRecords.get(i+1);
        	assertNotNull("No execution audit record ("+i+")", executionRecord);
        	assertEquals("Got this instead of execution audit record ("+i+"): "+executionRecord, AuditEventStage.EXECUTION, executionRecord.getEventStage());
        	
        	assertTrue("Empty deltas in execution audit record "+executionRecord, executionRecord.getDeltas() != null && ! executionRecord.getDeltas().isEmpty());
        	modifications++;
        	
        	// check next records
        	while (i < (auditRecords.size() - 2)) {
        		AuditEventRecord nextRecord = auditRecords.get(i+2);
        		if (nextRecord.getEventStage() == AuditEventStage.EXECUTION) {
        			// more than one execution record is OK
        			i++;
        		} else {
        			break;
        		}
        	}

        }
        assertEquals("Unexpected number of audit modifications", 5, modifications);

	}

}

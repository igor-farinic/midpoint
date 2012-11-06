/**
 * Copyright (c) 2011 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * Portions Copyrighted 2011 [name of copyright owner]
 */
package com.evolveum.midpoint.model;

import static org.testng.AssertJUnit.assertNotNull;
import static com.evolveum.midpoint.test.IntegrationTestTools.display;
import static com.evolveum.midpoint.test.IntegrationTestTools.displayTestTile;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.evolveum.icf.dummy.resource.DummyAccount;
import com.evolveum.midpoint.common.refinery.RefinedResourceSchema;
import com.evolveum.midpoint.common.refinery.ShadowDiscriminatorObjectDelta;
import com.evolveum.midpoint.model.AbstractModelIntegrationTest;
import com.evolveum.midpoint.model.api.ModelService;
import com.evolveum.midpoint.model.api.PolicyViolationException;
import com.evolveum.midpoint.model.api.context.ModelContext;
import com.evolveum.midpoint.model.api.context.SynchronizationPolicyDecision;
import com.evolveum.midpoint.prism.Containerable;
import com.evolveum.midpoint.prism.PrismContainer;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismReference;
import com.evolveum.midpoint.prism.PrismReferenceValue;
import com.evolveum.midpoint.prism.PropertyPath;
import com.evolveum.midpoint.prism.delta.ChangeType;
import com.evolveum.midpoint.prism.delta.ItemDelta;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.delta.ReferenceDelta;
import com.evolveum.midpoint.prism.util.PrismAsserts;
import com.evolveum.midpoint.prism.util.PrismTestUtil;
import com.evolveum.midpoint.schema.ObjectOperationOption;
import com.evolveum.midpoint.schema.ObjectOperationOptions;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.schema.holder.XPathHolder;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.schema.util.MiscSchemaUtil;
import com.evolveum.midpoint.schema.util.SchemaTestConstants;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.test.IntegrationTestTools;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.MiscUtil;
import com.evolveum.midpoint.util.exception.CommunicationException;
import com.evolveum.midpoint.util.exception.ConfigurationException;
import com.evolveum.midpoint.util.exception.ConsistencyViolationException;
import com.evolveum.midpoint.util.exception.ExpressionEvaluationException;
import com.evolveum.midpoint.util.exception.ObjectAlreadyExistsException;
import com.evolveum.midpoint.util.exception.ObjectNotFoundException;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.exception.SecurityViolationException;
import com.evolveum.midpoint.xml.ns._public.common.api_types_2.PropertyReferenceListType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.AccountShadowType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.AccountSynchronizationSettingsType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.AssignmentPolicyEnforcementType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.CredentialsType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.PasswordType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ValuePolicyType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ConnectorConfigurationType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ResourceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.UserType;

/**
 * @author semancik
 *
 */
@ContextConfiguration(locations = {"classpath:application-context-model.xml",
        "classpath:application-context-repository.xml",
        "classpath:application-context-repo-cache.xml",
        "classpath:application-context-configuration-test.xml",
        "classpath:application-context-provisioning.xml",
        "classpath:application-context-task.xml",
		"classpath:application-context-audit.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class TestImportRecon extends AbstractModelIntegrationTest {
		
	public TestImportRecon() throws JAXBException {
		super();
	}
	
	@Test
    public void test100ImportFromResourceDummy() throws Exception {
        displayTestTile(this, "test100ImportFromResourceDummy");

        // GIVEN
        Task task = createTask(TestImportRecon.class.getName() + ".test100ImportFromResourceDummy");
        OperationResult result = task.getResult();
        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
        
		// WHEN
        modelService.importAccountsFromResource(RESOURCE_DUMMY_OID, new QName(RESOURCE_DUMMY_NAMESPACE, "AccountObjectClass"), task, result);
		
        // THEN
        OperationResult subresult = result.getLastSubresult();
        IntegrationTestTools.assertInProgress("importAccountsFromResource result", subresult);
        
        waitForTaskFinish(task);
        
        // TODO
	}
	
//	@Test
//    public void test041SearchResources() throws Exception {
//        displayTestTile(this, "test041SearchResources");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test041SearchResources");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//		// WHEN
//        List<PrismObject<ResourceType>> resources = modelService.searchObjects(ResourceType.class, null, null, task, result);
//        
//		// THEN
//        assertNotNull("null rearch return", resources);
//        assertFalse("Empty rearch return", resources.isEmpty());
//        assertEquals("Unexpected number of resources found", 5, resources.size());
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("searchObjects result", result);
//
//        for (PrismObject<ResourceType> resource: resources) {
//        	assertResource(resource);
//        }
//	}
//	
//	private void assertResource(PrismObject<ResourceType> resource) throws JAXBException {
//		display("Resource", resource);
//		display("Resource def", resource.getDefinition());
//		PrismContainer<ConnectorConfigurationType> configurationContainer = resource.findContainer(ResourceType.F_CONNECTOR_CONFIGURATION);
//		assertNotNull("No Resource connector configuration def", configurationContainer);
//		display("Resource connector configuration def", configurationContainer.getDefinition());
//		display("Resource connector configuration def complex type def", configurationContainer.getDefinition().getComplexTypeDefinition());
//		assertNotNull("Empty Resource connector configuration def", configurationContainer.isEmpty());
//		assertEquals("Wrong compile-time class in Resource connector configuration in "+resource, ConnectorConfigurationType.class, 
//				configurationContainer.getCompileTimeClass());
//		
//		resource.checkConsistence(true, true);
//		
//		// Try to marshal using pure JAXB as a rough test that it is OK JAXB-wise
//		Element resourceDomElement = prismContext.getPrismJaxbProcessor().marshalObjectToDom(resource.asObjectable(), new QName(SchemaConstants.NS_C, "resource"),
//				DOMUtil.getDocument());
//		display("Resouce DOM element after JAXB marshall", resourceDomElement);
//	}
//		
//	@Test
//    public void test050GetUser() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test050GetUser");
//
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test050GetUser");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//
//        // WHEN
//        PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, null, task, result);
//        
//        // THEN
//        assertUserJack(userJack);
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        userJack.checkConsistence(true, true);
//	}
//	
//	@Test
//    public void test100ModifyUserAddAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test100ModifyUserAddAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test100ModifyUserAddAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//        PrismObject<AccountShadowType> account = PrismTestUtil.parseObject(new File(ACCOUNT_JACK_DUMMY_FILENAME));
//        
//        ObjectDelta<UserType> userDelta = ObjectDelta.createEmptyModifyDelta(UserType.class, USER_JACK_OID, prismContext);
//        PrismReferenceValue accountRefVal = new PrismReferenceValue();
//		accountRefVal.setObject(account);
//		ReferenceDelta accountDelta = ReferenceDelta.createModificationAdd(UserType.F_ACCOUNT_REF, getUserDefinition(), accountRefVal);
//		userDelta.addModification(accountDelta);
//		Collection<ObjectDelta<? extends ObjectType>> deltas = (Collection)MiscUtil.createCollection(userDelta);
//        
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		// Check accountRef
//		PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, null, task, result);
//        assertUserJack(userJack);
//        UserType userJackType = userJack.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 1, userJackType.getAccountRef().size());
//        ObjectReferenceType accountRefType = userJackType.getAccountRef().get(0);
//        accountOid = accountRefType.getOid();
//        assertFalse("No accountRef oid", StringUtils.isBlank(accountOid));
//        PrismReferenceValue accountRefValue = accountRefType.asReferenceValue();
//        assertEquals("OID mismatch in accountRefValue", accountOid, accountRefValue.getOid());
//        assertNull("Unexpected object in accountRefValue", accountRefValue.getObject());
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Jack Sparrow", true);
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//	}
//	
//	@Test
//    public void test101GetAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test101GetAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test101GetAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//		// WHEN
//		PrismObject<AccountShadowType> account = modelService.getObject(AccountShadowType.class, accountOid, null , task, result);
//		
//		display("Account", account);
//		display("Account def", account.getDefinition());
//		PrismContainer<Containerable> accountContainer = account.findContainer(AccountShadowType.F_ATTRIBUTES);
//		display("Account attributes def", accountContainer.getDefinition());
//		display("Account attributes def complex type def", accountContainer.getDefinition().getComplexTypeDefinition());
//        assertDummyShadowModel(account, accountOid, "jack", "Jack Sparrow");
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        account.checkConsistence(true, true);
//	}
//
//	@Test
//    public void test102GetAccountNoFetch() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test102GetAccountNoFetch");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test102GetAccountNoFetch");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//		Collection<ObjectOperationOptions> options = ObjectOperationOptions.createCollectionRoot(ObjectOperationOption.NO_FETCH);
//		
//		// WHEN
//		PrismObject<AccountShadowType> account = modelService.getObject(AccountShadowType.class, accountOid, options , task, result);
//		
//		display("Account", account);
//		display("Account def", account.getDefinition());
//		PrismContainer<Containerable> accountContainer = account.findContainer(AccountShadowType.F_ATTRIBUTES);
//		display("Account attributes def", accountContainer.getDefinition());
//		display("Account attributes def complex type def", accountContainer.getDefinition().getComplexTypeDefinition());
//        assertDummyShadowRepo(account, accountOid, "jack");
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        account.checkConsistence(true, true);
//	}
//	
//	@Test
//    public void test103GetAccountRaw() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test103GetAccountRaw");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test103GetAccountRaw");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//		Collection<ObjectOperationOptions> options = ObjectOperationOptions.createCollectionRoot(ObjectOperationOption.RAW);
//		
//		// WHEN
//		PrismObject<AccountShadowType> account = modelService.getObject(AccountShadowType.class, accountOid, options , task, result);
//		
//		display("Account", account);
//		display("Account def", account.getDefinition());
//		PrismContainer<Containerable> accountContainer = account.findContainer(AccountShadowType.F_ATTRIBUTES);
//		display("Account attributes def", accountContainer.getDefinition());
//		display("Account attributes def complex type def", accountContainer.getDefinition().getComplexTypeDefinition());
//        assertDummyShadowRepo(account, accountOid, "jack");
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        account.checkConsistence(true, true);
//	}
//
//	@Test
//    public void test108ModifyUserAddAccountAgain() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test108ModifyUserAddAccountAgain");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test108ModifyUserAddAccountAgain");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//        PrismObject<AccountShadowType> account = PrismTestUtil.parseObject(new File(ACCOUNT_JACK_DUMMY_FILENAME));
//        
//        ObjectDelta<UserType> userDelta = ObjectDelta.createEmptyModifyDelta(UserType.class, USER_JACK_OID, prismContext);
//        PrismReferenceValue accountRefVal = new PrismReferenceValue();
//		accountRefVal.setObject(account);
//		ReferenceDelta accountDelta = ReferenceDelta.createModificationAdd(UserType.F_ACCOUNT_REF, getUserDefinition(), accountRefVal);
//		userDelta.addModification(accountDelta);
//		Collection<ObjectDelta<? extends ObjectType>> deltas = (Collection)MiscUtil.createCollection(userDelta);
//        
//		try {
//			
//			// WHEN
//			modelService.executeChanges(deltas, null, task, result);
//			
//			// THEN
//			assert false : "Expected executeChanges operation to fail but it has obviously succeeded";
//		} catch (SchemaException e) {
//			// This is expected
//			// THEN
//			String message = e.getMessage();
//			assertMessageContains(message, "already contains account");
//			assertMessageContains(message, "default");
//		}
//		
//	}
//	
//	@Test
//    public void test109ModifyUserAddAccountAgain() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test109ModifyUserAddAccountAgain");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test109ModifyUserAddAccountAgain");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//        PrismObject<AccountShadowType> account = PrismTestUtil.parseObject(new File(ACCOUNT_JACK_DUMMY_FILENAME));
//        account.setOid(null);
//        
//        ObjectDelta<UserType> userDelta = ObjectDelta.createEmptyModifyDelta(UserType.class, USER_JACK_OID, prismContext);
//        PrismReferenceValue accountRefVal = new PrismReferenceValue();
//		accountRefVal.setObject(account);
//		ReferenceDelta accountDelta = ReferenceDelta.createModificationAdd(UserType.F_ACCOUNT_REF, getUserDefinition(), accountRefVal);
//		userDelta.addModification(accountDelta);
//		Collection<ObjectDelta<? extends ObjectType>> deltas = (Collection)MiscUtil.createCollection(userDelta);
//        
//		try {
//			
//			// WHEN
//			modelService.executeChanges(deltas, null, task, result);
//			
//			// THEN
//			assert false : "Expected executeChanges operation to fail but it has obviously succeeded";
//		} catch (SchemaException e) {
//			// This is expected
//			// THEN
//			String message = e.getMessage();
//			assertMessageContains(message, "already contains account");
//			assertMessageContains(message, "default");
//		}
//		
//	}
//
//	private void assertMessageContains(String message, String string) {
//		assert message.contains(string) : "Expected message to contain '"+string+"' but it does not; message: " + message;
//	}
//
//	@Test
//    public void test110GetUserResolveAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test110GetUserResolveAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test110GetUserResolveAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//
//        Collection<ObjectOperationOptions> options = 
//        	ObjectOperationOptions.createCollection(UserType.F_ACCOUNT, ObjectOperationOption.RESOLVE);
//        
//		// WHEN
//		PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, options , task, result);
//		
//        assertUserJack(userJack);
//        UserType userJackType = userJack.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 1, userJackType.getAccountRef().size());
//        ObjectReferenceType accountRefType = userJackType.getAccountRef().get(0);
//        String accountOid = accountRefType.getOid();
//        assertFalse("No accountRef oid", StringUtils.isBlank(accountOid));
//        
//        PrismReferenceValue accountRefValue = accountRefType.asReferenceValue();
//        assertEquals("OID mismatch in accountRefValue", accountOid, accountRefValue.getOid());
//        assertNotNull("Missing account object in accountRefValue", accountRefValue.getObject());
//
//        assertEquals("Unexpected number of accounts", 1, userJackType.getAccount().size());
//        AccountShadowType accountShadowType = userJackType.getAccount().get(0);
//        assertDummyShadowModel(accountShadowType.asPrismObject(), accountOid, "jack", "Jack Sparrow");
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        userJack.checkConsistence(true, true);
//	}
//
//
//	@Test
//    public void test111GetUserResolveAccountResource() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test111GetUserResolveAccountResource");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test111GetUserResolveAccountResource");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//
//        Collection<ObjectOperationOptions> options = 
//        	ObjectOperationOptions.createCollection(ObjectOperationOption.RESOLVE,
//        			new PropertyPath(UserType.F_ACCOUNT),
//    				new PropertyPath(UserType.F_ACCOUNT, AccountShadowType.F_RESOURCE)
//        	);
//        
//		// WHEN
//		PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, options , task, result);
//		
//        assertUserJack(userJack);
//        UserType userJackType = userJack.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 1, userJackType.getAccountRef().size());
//        ObjectReferenceType accountRefType = userJackType.getAccountRef().get(0);
//        String accountOid = accountRefType.getOid();
//        assertFalse("No accountRef oid", StringUtils.isBlank(accountOid));
//        
//        PrismReferenceValue accountRefValue = accountRefType.asReferenceValue();
//        assertEquals("OID mismatch in accountRefValue", accountOid, accountRefValue.getOid());
//        assertNotNull("Missing account object in accountRefValue", accountRefValue.getObject());
//
//        assertEquals("Unexpected number of accounts", 1, userJackType.getAccount().size());
//        AccountShadowType accountShadowType = userJackType.getAccount().get(0);
//        assertDummyShadowModel(accountShadowType.asPrismObject(), accountOid, "jack", "Jack Sparrow");
//        
//        assertNotNull("Resource in account was not resolved", accountShadowType.getResource());
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        userJack.checkConsistence(true, true);
//	}
//
//	@Test
//    public void test112GetUserResolveAccountNoFetch() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test112GetUserResolveAccountNoFetch");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test112GetUserResolveAccountNoFetch");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//
//        Collection<ObjectOperationOptions> options = 
//        	ObjectOperationOptions.createCollection(UserType.F_ACCOUNT, ObjectOperationOption.RESOLVE, ObjectOperationOption.NO_FETCH);
//        
//		// WHEN
//		PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, options , task, result);
//		
//        assertUserJack(userJack);
//        UserType userJackType = userJack.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 1, userJackType.getAccountRef().size());
//        ObjectReferenceType accountRefType = userJackType.getAccountRef().get(0);
//        String accountOid = accountRefType.getOid();
//        assertFalse("No accountRef oid", StringUtils.isBlank(accountOid));
//        
//        PrismReferenceValue accountRefValue = accountRefType.asReferenceValue();
//        assertEquals("OID mismatch in accountRefValue", accountOid, accountRefValue.getOid());
//        assertNotNull("Missing account object in accountRefValue", accountRefValue.getObject());
//
//        assertEquals("Unexpected number of accounts", 1, userJackType.getAccount().size());
//        AccountShadowType accountShadowType = userJackType.getAccount().get(0);
//        assertDummyShadowRepo(accountShadowType.asPrismObject(), accountOid, "jack");
//        
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("getObject result", result);
//        
//        userJack.checkConsistence(true, true);
//	}
//	
//	@Test
//    public void test119ModifyUserDeleteAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test119ModifyUserDeleteAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test119ModifyUserDeleteAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//
//        PrismObject<AccountShadowType> account = PrismTestUtil.parseObject(new File(ACCOUNT_JACK_DUMMY_FILENAME));
//        account.setOid(accountOid);
//        		
//		ObjectDelta<UserType> userDelta = ObjectDelta.createEmptyModifyDelta(UserType.class, USER_JACK_OID, prismContext);
//		PrismReferenceValue accountRefVal = new PrismReferenceValue();
//		accountRefVal.setObject(account);
//		ReferenceDelta accountDelta = ReferenceDelta.createModificationDelete(UserType.F_ACCOUNT_REF, getUserDefinition(), account);
//		userDelta.addModification(accountDelta);
//		Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(userDelta);
//        
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result, 2);
//        
//		// Check accountRef
//		PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, null, task, result);
//        assertUserJack(userJack);
//        UserType userJackType = userJack.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 0, userJackType.getAccountRef().size());
//        
//		// Check is shadow is gone
//        try {
//        	PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        	AssertJUnit.fail("Shadow "+accountOid+" still exists");
//        } catch (ObjectNotFoundException e) {
//        	// This is OK
//        }
//        
//        // Check if dummy resource account is gone
//        assertNoDummyAccount("jack");
//	}
//	
//	@Test
//    public void test120AddAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test120AddAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test120AddAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//        PrismObject<AccountShadowType> account = PrismTestUtil.parseObject(new File(ACCOUNT_JACK_DUMMY_FILENAME));
//        ObjectDelta<AccountShadowType> accountDelta = ObjectDelta.createAddDelta(account);
//        Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(accountDelta);
//        
//		// WHEN
//        modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//        accountOid = accountDelta.getOid();
//        assertNotNull("No account OID in resulting delta", accountOid);
//		// Check accountRef (should be none)
//		PrismObject<UserType> userJack = modelService.getObject(UserType.class, USER_JACK_OID, null, task, result);
//        assertUserJack(userJack);
//        UserType userJackType = userJack.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 0, userJackType.getAccountRef().size());
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Jack Sparrow", true);
//	}
//	
//	@Test
//    public void test121ModifyUserAddAccountRef() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test121ModifyUserAddAccountRef");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test121ModifyUserAddAccountRef");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//        ObjectDelta<UserType> userDelta = ObjectDelta.createEmptyModifyDelta(UserType.class, USER_JACK_OID, prismContext);
//        ReferenceDelta accountDelta = ReferenceDelta.createModificationAdd(UserType.F_ACCOUNT_REF, getUserDefinition(), accountOid);
//		userDelta.addModification(accountDelta);
//		Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(userDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		assertUserJack(userJack);
//        accountOid = getSingleUserAccountRef(userJack);
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Jack Sparrow", true);
//	}
//
//
//	
//	@Test
//    public void test128ModifyUserDeleteAccountRef() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test128ModifyUserDeleteAccountRef");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test128ModifyUserDeleteAccountRef");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//
//        PrismObject<AccountShadowType> account = PrismTestUtil.parseObject(new File(ACCOUNT_JACK_DUMMY_FILENAME));
//        
//        ObjectDelta<UserType> userDelta = ObjectDelta.createEmptyModifyDelta(UserType.class, USER_JACK_OID, prismContext);
//        PrismReferenceValue accountRefVal = new PrismReferenceValue();
//		accountRefVal.setObject(account);
//		ReferenceDelta accountDelta = ReferenceDelta.createModificationDelete(UserType.F_ACCOUNT_REF, getUserDefinition(), accountOid);
//		userDelta.addModification(accountDelta);
//		Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(userDelta);
//		        
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//        assertUserJack(userJack);
//		// Check accountRef
//        assertUserNoAccountRefs(userJack);
//		        
//		// Check shadow (if it is unchanged)
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account (if it is unchanged)
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Jack Sparrow");
//        
//        // Check account in dummy resource (if it is unchanged)
//        assertDummyAccount("jack", "Jack Sparrow", true);
//	}
//	
//	@Test
//    public void test129DeleteAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException, ConsistencyViolationException {
//        displayTestTile(this, "test129DeleteAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test129DeleteAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.NONE);
//        
//        ObjectDelta<AccountShadowType> accountDelta = ObjectDelta.createDeleteDelta(AccountShadowType.class, accountOid, prismContext);
//        Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(accountDelta);
//        
//		// WHEN
//        modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//        result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//        assertUserJack(userJack);
//		// Check accountRef
//        assertUserNoAccountRefs(userJack);
//        
//		// Check is shadow is gone
//        assertNoAccountShadow(accountOid);
//        
//        // Check if dummy resource account is gone
//        assertNoDummyAccount("jack");
//	}
//
//	
//	@Test
//    public void test130PreviewModifyUserJackAssignAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test130PreviewModifyUserJackAssignAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test130PreviewModifyUserJackAssignAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        Collection<ObjectDelta<? extends ObjectType>> deltas = new ArrayList<ObjectDelta<? extends ObjectType>>();
//        ObjectDelta<UserType> accountAssignmentUserDelta = createAccountAssignmentUserDelta(USER_JACK_OID, RESOURCE_DUMMY_OID, null, true);
//        deltas.add(accountAssignmentUserDelta);
//                
//		// WHEN
//        ModelContext<UserType,AccountShadowType> modelContext = modelInteractionService.previewChanges(deltas, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("previewChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		display("User after change execution", userJack);
//		assertUserJack(userJack);
//		// Check accountRef
//        assertUserNoAccountRefs(userJack);
//
//		// TODO: assert context
//		// TODO: assert context
//		// TODO: assert context
//        
//        assertResolvedResourceRefs(modelContext);
//        
//        // Check account in dummy resource
//        assertNoDummyAccount("jack");
//	}
//	
//	@Test
//    public void test131ModifyUserJackAssignAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test131ModifyUserJackAssignAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test131ModifyUserJackAssignAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        Collection<ObjectDelta<? extends ObjectType>> deltas = new ArrayList<ObjectDelta<? extends ObjectType>>();
//        ObjectDelta<UserType> accountAssignmentUserDelta = createAccountAssignmentUserDelta(USER_JACK_OID, RESOURCE_DUMMY_OID, null, true);
//        deltas.add(accountAssignmentUserDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		display("User after change execution", userJack);
//		assertUserJack(userJack);
//        accountOid = getSingleUserAccountRef(userJack);
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Jack Sparrow", true);
//	}
//	
//	@Test
//    public void test132ModifyAccountJackDummy() throws Exception {
//        displayTestTile(this, "test132ModifyAccountJackDummy");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test132ModifyAccountJackDummy");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        Collection<ObjectDelta<? extends ObjectType>> deltas = new ArrayList<ObjectDelta<? extends ObjectType>>();
//        ObjectDelta<AccountShadowType> accountDelta = ObjectDelta.createModificationReplaceProperty(AccountShadowType.class,
//        		accountOid, DUMMY_ACCOUNT_ATTRIBUTE_FULLNAME_PATH, prismContext, "Cpt. Jack Sparrow");
//        deltas.add(accountDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		display("User after change execution", userJack);
//		assertUserJack(userJack, "Cpt. Jack Sparrow", "Jack", "Sparrow");
//        accountOid = getSingleUserAccountRef(userJack);
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Cpt. Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Cpt. Jack Sparrow", true);
//	}
//	
//	@Test
//    public void test139ModifyUserJackUnassignAccount() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test139ModifyUserJackUnassignAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test139ModifyUserJackUnassignAccount");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        Collection<ObjectDelta<? extends ObjectType>> deltas = new ArrayList<ObjectDelta<? extends ObjectType>>();
//        ObjectDelta<UserType> accountAssignmentUserDelta = createAccountAssignmentUserDelta(USER_JACK_OID, RESOURCE_DUMMY_OID, null, false);
//        deltas.add(accountAssignmentUserDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		assertUserJack(userJack, "Cpt. Jack Sparrow", "Jack", "Sparrow");
//		// Check accountRef
//        assertUserNoAccountRefs(userJack);
//        
//        // Check is shadow is gone
//        assertNoAccountShadow(accountOid);
//        
//        // Check if dummy resource account is gone
//        assertNoDummyAccount("jack");
//	}
//	
//	@Test
//    public void test140ModifyUserJackAssignAccountAndModify() throws SchemaException, ObjectNotFoundException, ExpressionEvaluationException, 
//    		FileNotFoundException, JAXBException, CommunicationException, ConfigurationException, ObjectAlreadyExistsException, 
//    		PolicyViolationException, SecurityViolationException {
//        displayTestTile(this, "test140ModifyUserJackAssignAccountAndModify");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test140ModifyUserJackAssignAccountAndModify");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        Collection<ObjectDelta<? extends ObjectType>> deltas = new ArrayList<ObjectDelta<? extends ObjectType>>();
//        ObjectDelta<UserType> accountAssignmentUserDelta = createAccountAssignmentUserDelta(USER_JACK_OID, RESOURCE_DUMMY_OID, null, true);
//        ShadowDiscriminatorObjectDelta<AccountShadowType> accountDelta = ShadowDiscriminatorObjectDelta.createModificationReplaceProperty(AccountShadowType.class,
//        		RESOURCE_DUMMY_OID, null, DUMMY_ACCOUNT_ATTRIBUTE_FULLNAME_PATH, prismContext, "Cpt. Jack Sparrow");
//        deltas.add(accountDelta);
//        deltas.add(accountAssignmentUserDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		display("User after change execution", userJack);
//		assertUserJack(userJack, "Cpt. Jack Sparrow");
//        accountOid = getSingleUserAccountRef(userJack);
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Cpt. Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Cpt. Jack Sparrow", true);
//        DummyAccount dummyAccount = getDummyAccount(null, "jack");
//        assertNull("Unexpected loot", dummyAccount.getAttributeValue("loot", Integer.class));
//	}
//	
//	@Test
//    public void test145ModifyUserJack() throws Exception {
//        displayTestTile(this, "test145ModifyUserJack");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test145ModifyUserJack");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//                        
//		// WHEN
//        modifyUserReplace(USER_JACK_OID, UserType.F_FULL_NAME, task, result, "Magnificent Captain Jack Sparrow");
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		display("User after change execution", userJack);
//		assertUserJack(userJack, "Magnificent Captain Jack Sparrow");
//        accountOid = getSingleUserAccountRef(userJack);
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Magnificent Captain Jack Sparrow");
//        
//        // Check account in dummy resource
//        assertDummyAccount("jack", "Magnificent Captain Jack Sparrow", true);
//	}
//	
//	@Test
//    public void test146ModifyUserJackRaw() throws Exception {
//        displayTestTile(this, "test146ModifyUserJackRaw");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test146ModifyUserJackRaw");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        ObjectDelta<UserType> objectDelta = createModifyUserReplaceDelta(USER_JACK_OID, UserType.F_FULL_NAME,
//        		PrismTestUtil.createPolyString("Marvelous Captain Jack Sparrow"));
//        Collection<ObjectDelta<? extends ObjectType>> deltas = (Collection)MiscUtil.createCollection(objectDelta);
//                        
//		// WHEN
//		modelService.executeChanges(deltas, ObjectOperationOption.createCollection(ObjectOperationOption.RAW), task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//		display("User after change execution", userJack);
//		assertUserJack(userJack, "Marvelous Captain Jack Sparrow");
//        accountOid = getSingleUserAccountRef(userJack);
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "jack");
//        
//        // Check account - the original fullName should not be changed
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "jack", "Magnificent Captain Jack Sparrow");
//        
//        // Check account in dummy resource - the original fullName should not be changed
//        assertDummyAccount("jack", "Magnificent Captain Jack Sparrow", true);
//	}
//		
//	@Test
//    public void test149DeleteUserJack() throws Exception {
//        displayTestTile(this, "test149DeleteUserJack");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test149DeleteUserJack");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        ObjectDelta<UserType> userDelta = ObjectDelta.createDeleteDelta(UserType.class, USER_JACK_OID, prismContext);
//        Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(userDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		try {
//			PrismObject<UserType> userJack = getUser(USER_JACK_OID);
//			AssertJUnit.fail("Jack is still alive!");
//		} catch (ObjectNotFoundException ex) {
//			// This is OK
//		}
//        
//        // Check is shadow is gone
//        assertNoAccountShadow(accountOid);
//        
//        // Check if dummy resource account is gone
//        assertNoDummyAccount("jack");
//	}
//	
//	@Test
//    public void test150AddUserBlackbeardWithAccount() throws Exception {
//        displayTestTile(this, "test150AddUserBlackbeardWithAccount");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test150AddUserBlackbeardWithAccount");
//        // Use custom channel to trigger a special outbound mapping
//        task.setChannel("http://pirates.net/avast");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        PrismObject<UserType> user = PrismTestUtil.parseObject(new File(TEST_DIR, "user-blackbeard-account-dummy.xml"));
//        ObjectDelta<UserType> userDelta = ObjectDelta.createAddDelta(user);
//        Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(userDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userMorgan = modelService.getObject(UserType.class, USER_BLACKBEARD_OID, null, task, result);
//        UserType userMorganType = userMorgan.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 1, userMorganType.getAccountRef().size());
//        ObjectReferenceType accountRefType = userMorganType.getAccountRef().get(0);
//        String accountOid = accountRefType.getOid();
//        assertFalse("No accountRef oid", StringUtils.isBlank(accountOid));
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "blackbeard");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "blackbeard", "Edward Teach");
//        
//        // Check account in dummy resource
//        assertDummyAccount("blackbeard", "Edward Teach", true);
//        DummyAccount dummyAccount = getDummyAccount(null, "blackbeard");
//        assertEquals("Wrong loot", (Integer)10000, dummyAccount.getAttributeValue("loot", Integer.class));
//	}
//
//	
//	@Test
//    public void test210AddUserMorganWithAssignment() throws Exception {
//        displayTestTile(this, "test210AddUserMorganWithAssignment");
//
//        // GIVEN
//        Task task = taskManager.createTaskInstance(TestImportRecon.class.getName() + ".test210AddUserMorganWithAssignment");
//        OperationResult result = task.getResult();
//        assumeAssignmentPolicy(AssignmentPolicyEnforcementType.FULL);
//        
//        PrismObject<UserType> user = PrismTestUtil.parseObject(new File(TEST_DIR, "user-morgan-assignment-dummy.xml"));
//        ObjectDelta<UserType> userDelta = ObjectDelta.createAddDelta(user);
//        Collection<ObjectDelta<? extends ObjectType>> deltas = MiscSchemaUtil.createCollection(userDelta);
//                
//		// WHEN
//		modelService.executeChanges(deltas, null, task, result);
//		
//		// THEN
//		result.computeStatus();
//        IntegrationTestTools.assertSuccess("executeChanges result", result);
//        
//		PrismObject<UserType> userMorgan = modelService.getObject(UserType.class, USER_MORGAN_OID, null, task, result);
//        UserType userMorganType = userMorgan.asObjectable();
//        assertEquals("Unexpected number of accountRefs", 1, userMorganType.getAccountRef().size());
//        ObjectReferenceType accountRefType = userMorganType.getAccountRef().get(0);
//        String accountOid = accountRefType.getOid();
//        assertFalse("No accountRef oid", StringUtils.isBlank(accountOid));
//        
//		// Check shadow
//        PrismObject<AccountShadowType> accountShadow = repositoryService.getObject(AccountShadowType.class, accountOid, result);
//        assertDummyShadowRepo(accountShadow, accountOid, "morgan");
//        
//        // Check account
//        PrismObject<AccountShadowType> accountModel = modelService.getObject(AccountShadowType.class, accountOid, null, task, result);
//        assertDummyShadowModel(accountModel, accountOid, "morgan", "Sir Henry Morgan");
//        
//        // Check account in dummy resource
//        assertDummyAccount("morgan", "Sir Henry Morgan", true);
//	}
	

}
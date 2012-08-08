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
package com.evolveum.midpoint.model.lens.projector;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.evolveum.midpoint.common.crypto.EncryptionException;
import com.evolveum.midpoint.common.crypto.Protector;
import com.evolveum.midpoint.common.password.PasswordPolicyUtils;
import com.evolveum.midpoint.common.valueconstruction.ValueConstruction;
import com.evolveum.midpoint.common.valueconstruction.ValueConstructionFactory;
import com.evolveum.midpoint.model.api.PolicyViolationException;
import com.evolveum.midpoint.model.lens.LensContext;
import com.evolveum.midpoint.model.lens.LensFocusContext;
import com.evolveum.midpoint.prism.Item;
import com.evolveum.midpoint.prism.ItemDefinition;
import com.evolveum.midpoint.prism.PrismContainer;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismObjectDefinition;
import com.evolveum.midpoint.prism.PrismProperty;
import com.evolveum.midpoint.prism.PrismValue;
import com.evolveum.midpoint.prism.PropertyPath;
import com.evolveum.midpoint.prism.delta.ChangeType;
import com.evolveum.midpoint.prism.delta.ItemDelta;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.delta.PropertyDelta;
import com.evolveum.midpoint.repo.api.RepositoryService;
import com.evolveum.midpoint.schema.constants.ExpressionConstants;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.schema.holder.XPathHolder;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.schema.util.ObjectTypeUtil;
import com.evolveum.midpoint.util.exception.ExpressionEvaluationException;
import com.evolveum.midpoint.util.exception.ObjectNotFoundException;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.exception.SystemException;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_2.AccountShadowType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.CredentialsType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.PasswordPolicyType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.PasswordType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.PropertyConstructionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ProtectedStringType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.UserTemplateType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.UserType;
import com.evolveum.midpoint.xml.ns._public.common.common_2.ValueConstructionType;

/**
 * Processor to handle user template and possible also other user "policy" elements.
 *
 * @author Radovan Semancik
 *
 */
@Component
public class UserPolicyProcessor {

	private static final Trace LOGGER = TraceManager.getTrace(UserPolicyProcessor.class);

	@Autowired(required=true)
	private ValueConstructionFactory valueConstructionFactory;

	@Autowired(required=true)
	private PrismContext prismContext;
	
	@Autowired(required = true)
    @Qualifier("cacheRepositoryService")
    private transient RepositoryService cacheRepositoryService;
	
	@Autowired(required = true)
	Protector protector;

	<F extends ObjectType, P extends ObjectType> void processUserPolicy(LensContext<F,P> context, OperationResult result) throws ObjectNotFoundException,
            SchemaException, ExpressionEvaluationException, PolicyViolationException {

		LensFocusContext<F> focusContext = context.getFocusContext();
    	if (focusContext == null) {
    		return;
    	}
    	
    	if (focusContext.getObjectTypeClass() != UserType.class) {
    		// We can do this only for user.
    		return;
    	}
    	
		LensContext<UserType, AccountShadowType> usContext = (LensContext<UserType, AccountShadowType>) context;
    	//check user password if satisfies policies
    	checkPasswordPolicies(usContext, (LensFocusContext<UserType>)focusContext, result);
    	
		UserTemplateType userTemplate = determineUserTemplate(usContext, result);

		if (userTemplate == null) {
			// No applicable template
			return;
		}

		applyUserTemplate(usContext, userTemplate, result);
				
	}
	
	private void checkPasswordPolicies(LensContext<UserType,AccountShadowType> context, LensFocusContext<UserType> focusContext, OperationResult result) throws PolicyViolationException, SchemaException{
		
		PrismObject<UserType> user = null;
		
		ObjectDelta userDelta = focusContext.getDelta();
		
		if (userDelta == null){
			LOGGER.trace("Skipping processing password policies. User delta not specified.");
			return;
		}
		
		PrismProperty<PasswordType> password = null;
		if (ChangeType.ADD == userDelta.getChangeType()){
			user = focusContext.getDelta().getObjectToAdd();
			if (user != null){
				password = user.findProperty(SchemaConstants.PATH_PASSWORD_VALUE);
			}
		} else if (ChangeType.MODIFY == userDelta.getChangeType()){
			 PropertyDelta<PasswordType> passwordValueDelta = null;
		        if (userDelta != null) {
		        	passwordValueDelta = userDelta.findPropertyDelta(SchemaConstants.PATH_PASSWORD_VALUE);
		        	// Modification sanity check
		            if (userDelta.getChangeType() == ChangeType.MODIFY && passwordValueDelta != null && 
		            		(passwordValueDelta.isAdd() || passwordValueDelta.isDelete())) {
		            	throw new SchemaException("User password value cannot be added or deleted, it can only be replaced"); 
		            }
		            if (passwordValueDelta == null){
		            	LOGGER.trace("Skipping processing password policies. User delta does not contain password change.");
		            	return;
		            }
		            password = passwordValueDelta.getPropertyNew();
		        }
		}

		String passwordValue = determinePasswordValue(password);
		if (passwordValue == null || context.getGlobalPasswordPolicy() == null){
			LOGGER.trace("Skipping processing password policies. Password value or password policies not specified.");
			return;
		}

		boolean isValid = PasswordPolicyUtils
				.validatePassword(passwordValue, context.getGlobalPasswordPolicy(), result);

		if (!isValid) {
			throw new PolicyViolationException("Provided password does not satisfy password policies.");

		}

	}

	private String determinePasswordValue(PrismProperty<PasswordType> password) {
		// TODO: what to do if the provided password is null???
		if (password == null || password.getValue(ProtectedStringType.class) == null) {
			return null;
		}

		ProtectedStringType passValue = password.getValue(ProtectedStringType.class).getValue();

		if (passValue == null) {
			return null;
		}

		String passwordStr = passValue.getClearValue();

		if (passwordStr == null && passValue.getEncryptedData() != null) {
			// TODO: is this appropriate handling???
			try {
				passwordStr = protector.decryptString(passValue);
			} catch (EncryptionException ex) {
				throw new SystemException("Failed to process password for user: " , ex);
			}
		}

		return passwordStr;
	}
	

	private void applyUserTemplate(LensContext<UserType,AccountShadowType> context, UserTemplateType userTemplate, OperationResult result)
            throws ExpressionEvaluationException, ObjectNotFoundException, SchemaException {
		LensFocusContext<UserType> focusContext = context.getFocusContext();

		LOGGER.trace("Applying "+ObjectTypeUtil.toShortString(userTemplate)+" to "+focusContext.getObjectNew());

		ObjectDelta<UserType> userSecondaryDelta = focusContext.getWaveSecondaryDelta();
		for (PropertyConstructionType propConstr: userTemplate.getPropertyConstruction()) {
			XPathHolder propertyXPath = new XPathHolder(propConstr.getProperty());
			PropertyPath itemPath = propertyXPath.toPropertyPath();

			PrismObjectDefinition<UserType> userDefinition = getUserDefinition();
			ItemDefinition itemDefinition = userDefinition.findItemDefinition(itemPath);
			if (itemDefinition == null) {
				throw new SchemaException("The property "+itemPath+" is not a valid user property, defined in "
                        +ObjectTypeUtil.toShortString(userTemplate));
			}

			ValueConstructionType valueConstructionType = propConstr.getValueConstruction();
			// TODO: is the parentPath correct (null)?
			ValueConstruction valueConstruction = valueConstructionFactory.createValueConstruction(valueConstructionType,
					itemDefinition,
					"user template expression for "+itemDefinition.getName()+" while processing user " + focusContext.getObjectNew());

			PrismProperty existingValue = focusContext.getObjectNew().findProperty(itemPath);
			if (existingValue != null && !existingValue.isEmpty() && valueConstruction.isInitial()) {
				// This valueConstruction only applies if the property does not have a value yet.
				// ... but it does
				continue;
			}

			evaluateUserTemplateValueConstruction(valueConstruction, itemDefinition, context, result);

			Item output = valueConstruction.getOutput();
			ItemDelta itemDelta = output.createDelta(itemPath);

			if (itemDefinition.isMultiValue()) {
				itemDelta.addValuesToAdd(PrismValue.cloneCollection(output.getValues()));
			} else {
				itemDelta.setValuesToReplace(PrismValue.cloneCollection(output.getValues()));
			}

			if (userSecondaryDelta == null) {
				userSecondaryDelta = new ObjectDelta<UserType>(UserType.class, ChangeType.MODIFY);
				focusContext.setWaveSecondaryDelta(userSecondaryDelta);
			}
			userSecondaryDelta.addModification(itemDelta);
		}

	}

	private PrismObjectDefinition<UserType> getUserDefinition() {
		return prismContext.getSchemaRegistry().getObjectSchema().findObjectDefinitionByCompileTimeClass(UserType.class);
	}

	private void evaluateUserTemplateValueConstruction(ValueConstruction valueConstruction,
            ItemDefinition propertyDefinition, LensContext<UserType,AccountShadowType> context, OperationResult result)
            throws ExpressionEvaluationException, ObjectNotFoundException, SchemaException {

		valueConstruction.addVariableDefinition(ExpressionConstants.VAR_USER, context.getFocusContext().getObjectNew());
		// TODO: variables
		// TODO: root node

		valueConstruction.evaluate(result);

	}

	private UserTemplateType determineUserTemplate(LensContext<UserType,AccountShadowType> context, OperationResult result)
            throws ObjectNotFoundException, SchemaException {

		if (context.getUserTemplate() != null) {
			return context.getUserTemplate();
		}
		return null;
	}


}
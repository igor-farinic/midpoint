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

package com.evolveum.midpoint.repo.sql.testing;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Element;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismObjectDefinition;
import com.evolveum.midpoint.prism.delta.ItemDelta;
import com.evolveum.midpoint.prism.delta.PropertyDelta;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ResourceAttributeDefinitionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ResourceObjectTypeDefinitionType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ResourceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.SchemaHandlingType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.XmlSchemaType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.XmlSchemaType.Definition;

/**
 * @author semancik
 *
 */
public class ResourceCarefulAntUtil {

	private static Random rnd = new Random();
	
	/**
	 * @param prismContext
	 */
	public static void initAnts(List<CarefulAnt<ResourceType>> ants, final File resourceFile, final PrismContext prismContext) {
		final PrismObjectDefinition<ResourceType> resourceDef = prismContext.getSchemaRegistry().findObjectDefinitionByCompileTimeClass(ResourceType.class);
		ants.add(new CarefulAnt<ResourceType>() {
			@Override
			public ItemDelta<?> createDelta(int iteration) {
				return  PropertyDelta.createModificationReplaceProperty(ResourceType.F_DESCRIPTION, 
		    			resourceDef, "Blah "+iteration);
			}
			
			@Override
			public void assertModification(PrismObject<ResourceType> resource, int iteration) {
				assertEquals("Wrong descripion in iteration "+iteration, "Blah "+iteration, resource.asObjectable().getDescription());
			}
		});
    	
    	ants.add(new CarefulAnt<ResourceType>() {
    		SchemaHandlingType schemaHandling;
			@Override
			public ItemDelta<?> createDelta(int iteration) throws SchemaException {
				schemaHandling = createNewSchemaHandling(resourceFile, iteration, prismContext);
				return PropertyDelta.createModificationReplaceProperty(ResourceType.F_SCHEMA_HANDLING,
						resourceDef, schemaHandling);
			}	
			@Override
			public void assertModification(PrismObject<ResourceType> resource, int iteration) {
				assertEquals("Wrong schemaHandling in iteration "+iteration, schemaHandling, resource.asObjectable().getSchemaHandling());
			}
		});
    	
    	ants.add(new CarefulAnt<ResourceType>() {
    		Definition xmlSchemaDef;
			@Override
			public ItemDelta<?> createDelta(int iteration) throws SchemaException {
				xmlSchemaDef = createNewXmlSchemaDef(resourceFile, iteration, prismContext);
				return PropertyDelta.createModificationReplaceProperty(
						new ItemPath(ResourceType.F_SCHEMA, XmlSchemaType.F_DEFINITION),
						resourceDef, xmlSchemaDef);
			}	
			@Override
			public void assertModification(PrismObject<ResourceType> resource, int iteration) {
				List<Element> orgigElements = xmlSchemaDef.getAny();
				List<Element> newElements = resource.asObjectable().getSchema().getDefinition().getAny();
				assertEquals("Wrong number of elements in schema definition in iteration "+iteration, orgigElements.size(), newElements.size());
				// TODO look inside elements
			}
		});
	}
	
    private static SchemaHandlingType createNewSchemaHandling(File resourceFile, int iteration, PrismContext prismContext) throws SchemaException {
    	PrismObject<ResourceType> resource = prismContext.getPrismDomProcessor().parseObject(resourceFile);
    	SchemaHandlingType schemaHandling = resource.asObjectable().getSchemaHandling();
    	ResourceObjectTypeDefinitionType accountType = schemaHandling.getObjectType().iterator().next();
    	List<ResourceAttributeDefinitionType> attrDefs = accountType.getAttribute();
    	ResourceAttributeDefinitionType attributeDefinitionType = attrDefs.get(rnd.nextInt(attrDefs.size()));
    	attributeDefinitionType.setMaxOccurs(Integer.toString(iteration));
		return schemaHandling;
	}

    private static Definition createNewXmlSchemaDef(File resourceFile, int iteration, PrismContext prismContext) throws SchemaException {
    	PrismObject<ResourceType> resource = prismContext.getPrismDomProcessor().parseObject(resourceFile);
    	XmlSchemaType schema = resource.asObjectable().getSchema();
    	Definition def;
    	if (schema == null) {
    		def = new Definition();
    		def.getAny().add(DOMUtil.createElement(DOMUtil.XSD_SCHEMA_ELEMENT));
    	} else {
    		def = schema.getDefinition();
	    	// TODO: modify it somehow
    	}
    	return def;
	}


}

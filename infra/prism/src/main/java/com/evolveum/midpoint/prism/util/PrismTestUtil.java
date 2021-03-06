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
package com.evolveum.midpoint.prism.util;

import com.evolveum.midpoint.prism.Objectable;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismObjectDefinition;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.polystring.PolyString;
import com.evolveum.midpoint.prism.schema.SchemaRegistry;
import com.evolveum.midpoint.prism.xml.PrismJaxbProcessor;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.prism.xml.ns._public.types_2.PolyStringType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Class that statically instantiates the prism contexts and provides convenient static version of the PrismContext
 * and processor classes. 
 * 
 * This is usable for tests. DO NOT use this in the main code. Although it is placed in "main" for convenience,
 * is should only be used in tests.
 *
 * @author semancik
 */
public class PrismTestUtil {

    private static final QName DEFAULT_ELEMENT_NAME = new QName("http://midpoint.evolveum.com/xml/ns/test/whatever-1.xsd", "whatever");

    private static PrismContext prismContext;
    private static PrismContextFactory prismContextFactory;
    
    public static void resetPrismContext(PrismContextFactory newPrismContextFactory) throws SchemaException, SAXException, IOException {
    	if (prismContextFactory == newPrismContextFactory) {
    		// Exactly the same factory instance, nothing to do.
    		return;
    	}
    	setFactory(newPrismContextFactory);
    	resetPrismContext();
    }
    
	public static void setFactory(PrismContextFactory newPrismContextFactory) {
		PrismTestUtil.prismContextFactory = newPrismContextFactory;
	}

	public static void resetPrismContext() throws SchemaException, SAXException, IOException {
		prismContext = createInitializedPrismContext();
	}

	public static PrismContext createPrismContext() throws SchemaException, FileNotFoundException {
    	if (prismContextFactory == null) {
    		throw new IllegalStateException("Cannot create prism context, no prism factory is set");
    	}
        return prismContextFactory.createPrismContext();
    }

    public static PrismContext createInitializedPrismContext() throws SchemaException, SAXException, IOException {
    	PrismContext newPrismContext = createPrismContext();
    	newPrismContext.initialize();
        return newPrismContext;
    }
    
    public static PrismContext getPrismContext() {
    	if (prismContext == null) {
    		throw new IllegalStateException("Prism context is not set in PrismTestUtil. Maybe a missing call to resetPrismContext(..) in test initialization?");
    	}
    	return prismContext;
    }
    
    public static SchemaRegistry getSchemaRegistry() {
    	return prismContext.getSchemaRegistry();
    }
    
    // ==========================
    // == parsing
    // ==========================
    
    public static <T extends Objectable> PrismObject<T> parseObject(File file) throws SchemaException {
    	return getPrismContext().getPrismDomProcessor().parseObject(file);
    }
    
    public static <T extends Objectable> PrismObject<T> parseObject(String xmlString) throws SchemaException {
    	return getPrismContext().parseObject(xmlString);
    }
    
    public static <T extends Objectable> PrismObject<T> parseObject(Element element) throws SchemaException {
    	return getPrismContext().parseObject(element);
    }
    
    public static List<PrismObject<? extends Objectable>> parseObjects(File file) throws SchemaException {
    	return getPrismContext().getPrismDomProcessor().parseObjects(file);
    }
    
    public static <T extends Objectable> ObjectDelta<T> parseDelta(File file) throws SchemaException {
    	// TODO
//    	return getPrismContext().getPrismDomProcessor().parseObject(file);
    	return null;
    }

    // ==========================
    // == Serializing
    // ==========================

    public static String serializeObjectToString(PrismObject<? extends Objectable> object) throws SchemaException {
    	return getPrismContext().getPrismDomProcessor().serializeObjectToString(object);
    }
    
    // ==========================
    // == JAXB
    // ==========================
    
    public static void marshalElementToDom(JAXBElement<?> jaxbElement, Node parentNode) throws JAXBException {
        getPrismJaxbProcessor().marshalElementToDom(jaxbElement, parentNode);
    }

    public static <T> JAXBElement<T> unmarshalElement(String xmlString, Class<T> type) throws JAXBException, SchemaException {
        return getPrismJaxbProcessor().unmarshalElement(xmlString, type);
    }
    
    public static <T> T unmarshalObject(File file, Class<T> type) throws JAXBException, SchemaException, FileNotFoundException {
    	return getPrismJaxbProcessor().unmarshalObject(file, type);
    }
    
    public static <T> T unmarshalObject(String stringXml, Class<T> type) throws JAXBException, SchemaException {
    	return getPrismJaxbProcessor().unmarshalObject(stringXml, type);
    }
    
    public static <T> JAXBElement<T> unmarshalElement(File xmlFile, Class<T> type) throws JAXBException, SchemaException, FileNotFoundException {
        return getPrismJaxbProcessor().unmarshalElement(xmlFile, type);
    }
    
    public static <T> Element marshalObjectToDom(T jaxbObject, QName elementQName, Document doc) throws JAXBException {
    	return getPrismJaxbProcessor().marshalObjectToDom(jaxbObject, elementQName, doc);
    }
    
    public static Element toDomElement(Object element) throws JAXBException {
    	return getPrismJaxbProcessor().toDomElement(element);
    }
    
    public static Element toDomElement(Object jaxbElement, Document doc) throws JAXBException {
    	return getPrismJaxbProcessor().toDomElement(jaxbElement, doc);
    }
    
    public static Element toDomElement(Object jaxbElement, Document doc, boolean adopt, boolean clone, boolean deep) throws JAXBException {
    	return getPrismJaxbProcessor().toDomElement(jaxbElement, doc, adopt, clone, deep);
    }

    public static String marshalToString(Objectable objectable) throws JAXBException {
        return getPrismJaxbProcessor().marshalToString(objectable);
    }

	public static String marshalElementToString(JAXBElement<?> jaxbElement) throws JAXBException {
        return getPrismJaxbProcessor().marshalElementToString(jaxbElement);
    }
    
    // Works both on JAXB and DOM elements
    public static String marshalElementToString(Object element) throws JAXBException {
        return getPrismJaxbProcessor().marshalElementToString(element);
    }

    // Compatibility
    public static String marshalWrap(Object jaxbObject) throws JAXBException {
        JAXBElement<Object> jaxbElement = new JAXBElement<Object>(DEFAULT_ELEMENT_NAME, (Class) jaxbObject.getClass(), jaxbObject);
        return marshalElementToString(jaxbElement);
    }

	private static PrismJaxbProcessor getPrismJaxbProcessor() {
		if (prismContext == null) {
			throw new IllegalStateException("No prism context in Prism test util");
		}
		PrismJaxbProcessor prismJaxbProcessor = prismContext.getPrismJaxbProcessor();
		if (prismJaxbProcessor == null) {
			throw new IllegalStateException("No prism JAXB processor in Prism test util");
		}
		return prismJaxbProcessor;
	}

	public static <T extends Objectable> PrismObjectDefinition<T> getObjectDefinition(Class<T> compileTimeClass) {
		return getSchemaRegistry().findObjectDefinitionByCompileTimeClass(compileTimeClass);
	}

	public static PolyString createPolyString(String orig) {
		PolyString polyString = new PolyString(orig);
		polyString.recompute(getPrismContext().getDefaultPolyStringNormalizer());
		return polyString;
	}

	public static PolyStringType createPolyStringType(String string) {
		return new PolyStringType(createPolyString(string));
	}

}

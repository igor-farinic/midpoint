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
package com.evolveum.midpoint.model.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.evolveum.prism.xml.ns._public.types_2.PolyStringType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.CredentialsType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.PasswordType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ProtectedStringType;
import com.evolveum.midpoint.xml.ns._public.model.model_1_wsdl.ModelPortType;
import com.evolveum.midpoint.xml.ns._public.model.model_1_wsdl.ModelService;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectType;

/**
 * @author Radovan Semancik
 *
 */
public class ModelClientUtil {
	
	// XML constants
	public static final String NS_COMMON = "http://midpoint.evolveum.com/xml/ns/public/common/common-2a";
	public static final QName COMMON_PATH = new QName(NS_COMMON, "path");
	public static final QName COMMON_VALUE = new QName(NS_COMMON, "value");
	public static final QName COMMON_ASSIGNMENT = new QName(NS_COMMON, "assignment");
	
	public static final String NS_TYPES = "http://prism.evolveum.com/xml/ns/public/types-2";
	private static final QName TYPES_POLYSTRING_ORIG = new QName(NS_TYPES, "orig");
	
	private static final DocumentBuilder domDocumentBuilder;
	
	public static JAXBContext instantiateJaxbContext() throws JAXBException {
		return JAXBContext.newInstance("com.evolveum.midpoint.xml.ns._public.common.api_types_2:" +
				"com.evolveum.midpoint.xml.ns._public.common.common_2a:" +
				"com.evolveum.midpoint.xml.ns._public.common.fault_1:" +
				"com.evolveum.midpoint.xml.ns._public.connector.icf_1.connector_schema_2:" +
				"com.evolveum.midpoint.xml.ns._public.connector.icf_1.resource_schema_2:" +
				"com.evolveum.midpoint.xml.ns._public.resource.capabilities_2:" +
				"com.evolveum.midpoint.xml.ns.model.workflow.common_forms_2:" +
                "com.evolveum.midpoint.xml.ns.model.workflow.process_instance_state_2:" +
				"com.evolveum.prism.xml.ns._public.annotation_2:" +
				"com.evolveum.prism.xml.ns._public.query_2:" +
				"com.evolveum.prism.xml.ns._public.types_2:" +
				"org.w3._2000._09.xmldsig:" +
				"org.w3._2001._04.xmlenc");
	}
	
	public static Element createPathElement(String stringPath, Document doc) {
		String pathDeclaration = "declare default namespace '" + NS_COMMON + "'; " + stringPath;
		return createTextElement(COMMON_PATH, pathDeclaration, doc);
	}
	
	public static PolyStringType createPolyStringType(String string, Document doc) {
		PolyStringType polyStringType = new PolyStringType();
		Element origElement = createTextElement(TYPES_POLYSTRING_ORIG, string, doc);
		polyStringType.getContent().add(origElement);
		return polyStringType;
	}
	
	public static Element createTextElement(QName qname, String value, Document doc) {
		Element element = doc.createElementNS(qname.getNamespaceURI(), qname.getLocalPart());
		element.setTextContent(value);
		return element;
	}
	
	public static CredentialsType createPasswordCredentials(String password) {
		CredentialsType credentialsType = new CredentialsType();
		credentialsType.setPassword(createPasswordType(password));
		return credentialsType;
	}
	
	public static PasswordType createPasswordType(String password) {
		PasswordType passwordType = new PasswordType();
		passwordType.setValue(createProtectedString(password));
		return passwordType;
	}

	public static ProtectedStringType createProtectedString(String clearValue) {
		ProtectedStringType protectedString = new ProtectedStringType();
		protectedString.setClearValue(clearValue);
		return protectedString;
	}

	public static <T> JAXBElement<T> toJaxbElement(QName name, T value) {
		return new JAXBElement<T>(name, (Class<T>) value.getClass(), value);
	}

	public static Document getDocumnent() {
		return domDocumentBuilder.newDocument();
	}

	public static String getTypeUri(Class<? extends ObjectType> type) {
//		QName typeQName = JAXBUtil.getTypeQName(type);
//		String typeUri = QNameUtil.qNameToUri(typeQName);
		String typeUri = NS_COMMON + "#" + type.getSimpleName();
		return typeUri;
	}
	
	public static QName getTypeQName(Class<? extends ObjectType> type) {
//		QName typeQName = JAXBUtil.getTypeQName(type);
		QName typeQName = new QName(NS_COMMON, type.getSimpleName());
		return typeQName;
	}
	
	public static Element parseElement(String stringXml) throws SAXException, IOException {
		Document document = domDocumentBuilder.parse(IOUtils.toInputStream(stringXml, "utf-8"));
		return getFirstChildElement(document);
	}
	
	public static Element getFirstChildElement(Node parent) {
		if (parent == null || parent.getChildNodes() == null) {
			return null;
		}

		NodeList nodes = parent.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node child = nodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) child;
			}
		}

		return null;
	}
	
	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			domDocumentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			throw new IllegalStateException("Error creating XML document " + ex.getMessage());
		}
	}

}

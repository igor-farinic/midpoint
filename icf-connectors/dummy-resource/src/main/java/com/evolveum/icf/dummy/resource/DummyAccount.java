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
package com.evolveum.icf.dummy.resource;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evolveum.midpoint.util.DebugDumpable;
import com.evolveum.midpoint.util.DebugUtil;

/**
 * @author Radovan Semancik
 *
 */
public class DummyAccount extends DummyObject {
	
	public static final String ATTR_FULLNAME_NAME = "fullname";
	public static final String ATTR_DESCRIPTION_NAME = "description";
	public static final String ATTR_INTERESTS_NAME = "interests";
	public static final String ATTR_PRIVILEGES_NAME = "privileges";
	
	private String password = null;

	public DummyAccount() {
		super();
	}

	public DummyAccount(String username) {
		super(username);
	}
		
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	protected DummyObjectClass getObjectClass() throws ConnectException, FileNotFoundException {
		return resource.getAccountObjectClass();
	}

	@Override
	public String getShortTypeName() {
		return "account";
	}

	@Override
	public String toStringContent() {
		return super.toStringContent() + ", password=" + password; 
	}

	@Override
	public String debugDump() {
		return debugDump(0);
	}

	@Override
	protected void extendDebugDump(StringBuilder sb, int indent) {
		DebugUtil.debugDumpWithLabelToStringLn(sb, "Password", password, indent + 1);
	}
	
}

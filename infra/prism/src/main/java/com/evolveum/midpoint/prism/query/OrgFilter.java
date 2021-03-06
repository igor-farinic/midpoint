/*
 * Copyright (c) 2010-2014 Evolveum
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

package com.evolveum.midpoint.prism.query;

import org.w3c.dom.Element;

import com.evolveum.midpoint.prism.Objectable;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.PrismPropertyValue;
import com.evolveum.midpoint.prism.PrismReferenceValue;
import com.evolveum.midpoint.prism.match.MatchingRuleRegistry;
import com.evolveum.midpoint.util.DebugUtil;

public class OrgFilter extends ObjectFilter {

	private PrismReferenceValue baseOrgRef;
	private Integer minDepth;
	private Integer maxDepth;
	private boolean root;

	public OrgFilter(PrismReferenceValue baseOrgRef, Integer minDepth, Integer maxDepth) {
		this.baseOrgRef = baseOrgRef;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
	}
	
	public OrgFilter() {
		// TODO Auto-generated constructor stub
	}

	public static OrgFilter createOrg(PrismReferenceValue baseOrgRef, Integer minDepth, Integer maxDepth) {
		return new OrgFilter(baseOrgRef, minDepth, maxDepth);
	}

	public static OrgFilter createOrg(String baseOrgOid, Integer minDepth, Integer maxDepth) {
		return new OrgFilter(new PrismReferenceValue(baseOrgOid), minDepth, maxDepth);
	}

	public static OrgFilter createOrg(String baseOrgRef) {
		return new OrgFilter(new PrismReferenceValue(baseOrgRef), null, null);
	}
	
	public static OrgFilter createRootOrg(){
		OrgFilter filter = new OrgFilter();
		filter.setRoot(true);
		return filter;
		
	}
	
	public PrismReferenceValue getOrgRef() {
		return baseOrgRef;
	}

	public void setOrgRef(PrismReferenceValue baseOrgRef) {
		this.baseOrgRef = baseOrgRef;
	}

	public Integer getMinDepth() {
		return minDepth;
	}

	public void setMinDepth(Integer minDepth) {
		this.minDepth = minDepth;
	}

	public Integer getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public void setRoot(boolean root) {
		this.root = root;
	}
	
	public boolean isRoot() {
		return root;
	}
	
	@Override
	public OrgFilter clone() {
		return new OrgFilter(getOrgRef(), getMinDepth(), getMaxDepth());
	}

	@Override
	public String debugDump() {
		return debugDump(0);
	}

	@Override
	public String debugDump(int indent) {
		StringBuilder sb = new StringBuilder();
		DebugUtil.indentDebugDump(sb, indent);
		sb.append("ORG: \n");
		if (getOrgRef() != null) {
			sb.append(getOrgRef().debugDump(indent + 1));
			sb.append("\n");
		} else {
			DebugUtil.indentDebugDump(sb, indent + 1);
			sb.append("null\n");
		}

		if (getMinDepth() != null) {
			DebugUtil.indentDebugDump(sb, indent + 1);
			sb.append(getMaxDepth());
		}
		if (getMaxDepth() != null) {
			DebugUtil.indentDebugDump(sb, indent + 1);
			sb.append(getMaxDepth());
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ORG: ");
		if (getOrgRef() != null){
			sb.append(getOrgRef().toString());
			sb.append(", ");
		}
		if (getMinDepth() != null){
			sb.append(getMinDepth());
			sb.append(", ");
		}
		if (getMaxDepth() != null){
			sb.append(getMaxDepth());
		}
		return sb.toString();
	}

	@Override
	public <T extends Objectable> boolean match(PrismObject<T> object, MatchingRuleRegistry matchingRuleRegistry) {
		// TODO Auto-generated method stub
		return false;
	}


}

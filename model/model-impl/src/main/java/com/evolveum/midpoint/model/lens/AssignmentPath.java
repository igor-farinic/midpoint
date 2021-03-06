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
package com.evolveum.midpoint.model.lens;

import java.util.ArrayList;
import java.util.List;

import com.evolveum.midpoint.xml.ns._public.common.common_2a.AssignmentType;

/**
 * @author semancik
 *
 */
public class AssignmentPath {
	
	private List<AssignmentPathSegment> segments;

	public AssignmentPath() {
		segments = createNewSegments();
	}
	
	AssignmentPath(AssignmentType assignmentType) {
		this.segments = createNewSegments();
		segments.add(new AssignmentPathSegment(assignmentType, null));
	}

	private List<AssignmentPathSegment> createNewSegments() {
		return new ArrayList<AssignmentPathSegment>();
	}

	public List<AssignmentPathSegment> getSegments() {
		return segments;
	}
	
	public void add(AssignmentPathSegment segment) {
		segments.add(segment);
	}
	
	public void remove(AssignmentPathSegment segment) {
		segments.remove(segment);
	}

	
	public AssignmentType getFirstAssignment() {
		return segments.get(0).getAssignmentType();
	}
	
	public boolean isEmpty() {
		return segments.isEmpty();
	}
	
	public int getEvaluationOrder() {
		if (isEmpty()) {
			return 0;
		} else {
			return segments.get(segments.size()-1).getEvaluationOrder();
		}
	}
	
	public AssignmentPathSegment last() {
		if (isEmpty()) {
			return null;
		} else {
			return segments.get(segments.size()-1);
		}
	}
	
	/**
	 * Shallow clone.
	 */
	public AssignmentPath clone() {
		AssignmentPath clone = new AssignmentPath();
		clone.segments.addAll(this.segments);
		return clone;
	}

	@Override
	public String toString() {
		return "AssignmentPath(" + segments + ")";
	}
	
	

}

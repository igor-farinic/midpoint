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
package com.evolveum.midpoint.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.prism.path.ItemPath;

/**
 * @author semancik
 *
 */
public class SelectorOptions<T> {
	
	private ObjectSelector selector;
	private T options;
	
	public SelectorOptions(ObjectSelector selector, T options) {
		super();
		this.selector = selector;
		this.options = options;
	}
	
	public SelectorOptions(T options) {
		super();
		this.selector = null;
		this.options = options;
	}

	public ObjectSelector getSelector() {
		return selector;
	}

	public T getOptions() {
		return options;
	}
			
	public static <T> SelectorOptions<T> create(ItemPath path, T options) {
		return new SelectorOptions<T>(new ObjectSelector(path), options);
	}
	
	public static <T> SelectorOptions<T> create(QName pathQName, T options) {
		return new SelectorOptions<T>(new ObjectSelector(new ItemPath(pathQName)), options);
	}
		
	public static <T> Collection<SelectorOptions<T>> createCollection(ItemPath path, T options) {
		Collection<SelectorOptions<T>> optionsCollection = new ArrayList<SelectorOptions<T>>(1);
		optionsCollection.add(create(path, options));
		return optionsCollection;
	}

	public static <T> Collection<SelectorOptions<T>> createCollection(QName pathQName, T options) {
		Collection<SelectorOptions<T>> optionsCollection = new ArrayList<SelectorOptions<T>>(1);
		optionsCollection.add(create(pathQName, options));
		return optionsCollection;
	}

	public static <T> Collection<SelectorOptions<T>> createCollection(T options) {
		Collection<SelectorOptions<T>> optionsCollection = new ArrayList<SelectorOptions<T>>(1);
		optionsCollection.add(new SelectorOptions<T>(options));
		return optionsCollection;
	}

	public static <T> Collection<SelectorOptions<T>> createCollection(T options, ItemPath... paths) {
		Collection<SelectorOptions<T>> optionsCollection = new ArrayList<SelectorOptions<T>>(paths.length);
		for (ItemPath path: paths) {
			optionsCollection.add(create(path, options));
		}
		return optionsCollection;
	}

	public static <T> Collection<SelectorOptions<T>> createCollection(T options, QName... pathQNames) {
		Collection<SelectorOptions<T>> optionsCollection = new ArrayList<SelectorOptions<T>>(pathQNames.length);
		for (QName qname: pathQNames) {
			optionsCollection.add(create(qname, options));
		}
		return optionsCollection;
	}

	/**
	 * Returns options that apply to the "root" object. I.e. options that have null selector, null path, empty path, ...
	 */
	public static <T> T findRootOptions(Collection<SelectorOptions<T>> options) {
		if (options == null) {
			return null;
		}
		for (SelectorOptions<T> oooption: options) {
			if (oooption.isRoot()) {
				return oooption.getOptions();
			}
		}
		return null;
	}

	private boolean isRoot() {
		if (selector == null) {
			return true;
		}
		if (selector.getPath() == null || selector.getPath().isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((selector == null) ? 0 : selector.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectorOptions other = (SelectorOptions) obj;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (selector == null) {
			if (other.selector != null)
				return false;
		} else if (!selector.equals(other.selector))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ObjectOperationOptions(" + selector + ": " + options + ")";
	}

}
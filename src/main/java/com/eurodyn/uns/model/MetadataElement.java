/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Unified Notification System
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Sasha Milosavljevic (ED) 
 */

package com.eurodyn.uns.model;

import java.io.Serializable;

import com.eurodyn.uns.util.HashCodeUtil;

public class MetadataElement implements Comparable, Serializable {

	private Integer id;

	private String name;

	private static final long serialVersionUID = 6183619606841264376L;

	public MetadataElement() {
	}

	public MetadataElement(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalName() {
		int index = name.lastIndexOf("#");
		return  (index > 0)?name.substring(index + 1):name.substring(name.lastIndexOf("/") + 1);  
	}

	public int compareTo(Object o) {
		if (o != null && o instanceof MetadataElement) {
			MetadataElement me = (MetadataElement) o;
			return (getLocalName() == null ? -1 : getLocalName().compareTo(me.getLocalName()));
		} else
			return 1;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof MetadataElement))
			return false;
		final MetadataElement me = (MetadataElement) other;
		if (!me.getName().equals(getName()))
			return false;
		return true;
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, name);
		return result;
	}

}

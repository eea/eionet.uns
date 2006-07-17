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
 *    Original code: Nedeljko Pavlovic (ED) 
 */

package com.eurodyn.uns.model;

import java.io.Serializable;

/**
 * @hibernate.class table="EEA_ROLE" lazy="false"
 */
public class Role implements Serializable {

	private static final long serialVersionUID = -3976232324025037068L;

	private Integer id;

	private String name;

	private String fullDn;

	public Role() {
	}

	public Role(Integer id) {
	}

	public String getFullDn() {
		return fullDn;
	}

	public void setFullDn(String fullDn) {
		this.fullDn = fullDn;
	}

	public String getLocalName() {
		String localName = name.substring(name.indexOf("=") + 1);
		int i = localName.indexOf(",");
		if (i > 0) {
			localName = localName.substring(0, i);
		}

		return localName;
	}

	/**
	 * @hibernate.property column="EXT_ID" length="50"
	 * @return String
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @hibernate.id column="ID" generator-class="native"
	 */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Role))
			return false;

		Role castOther = (Role) other;

		return (this.getName().equals(castOther.getName()));
	}

	public int hashCode() {
		return this.getName().hashCode();
	}

}

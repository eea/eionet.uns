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

public class DeliveryType implements Serializable {

	private static final long serialVersionUID = -4294827145445178090L;

	public static final int EMAIL = 1;
	public static final int JABBER = 2;
	public static final int RSS = 3;
	public static final int WDB = 4;
	
	// Fields

	private Integer id;

	private String name;

	// Constructors

	/** default constructor */
	public DeliveryType() {
	}

	/** minimal constructor */
	public DeliveryType(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public DeliveryType(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof DeliveryType))
			return false;
		final DeliveryType dt = (DeliveryType) other;
		if (!dt.getId().equals(getId()))
			return false;
		// if ( !dt.getName().equals( getName() ) ) return false;
		return true;
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, id);
		// result = HashCodeUtil.hash( result, name);
		return result;
	}

}

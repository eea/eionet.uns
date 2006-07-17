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
 * @hibernate.class table="STYLESHEET" lazy="false"
 */
public class Stylesheet implements Serializable {

	private static final long serialVersionUID = 1096385920108846821L;

	private Integer id;

	private String name;

	private String description;

	private String content;

	private Boolean editOnly;

	private Integer channelsCount;

	public Stylesheet() {

	}

	public Stylesheet(int id) {
		this.id = new Integer(id);
	}

	/**
	 * @hibernate.property column="CONTENT" not-null="true" length="65535" sql-type="text"
	 * @return String
	 */
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @hibernate.property column="DESCRIPTION" length="255"
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	/**
	 * @hibernate.property column="NAME" length="50" not-null="true" unique="true"
	 * @return String
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return name + " - " + description;
	}

	/**
	 * @hibernate.property formula="( SELECT (SELECT COUNT(*) FROM CHANNEL c WHERE s.ID = c.STYLESHEET_ID) FROM STYLESHEET s WHERE s.ID = id )"
	 * @return Integer
	 */
	public Integer getChannelsCount() {
		return channelsCount;
	}

	public void setChannelsCount(Integer channelsCount) {
		this.channelsCount = channelsCount;
	}

	public Boolean getEditOnly() {
		return this.editOnly;
	}

	public void setEditOnly(Boolean editOnly) {
		this.editOnly = editOnly;
	}

}

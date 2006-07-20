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

package com.eurodyn.uns.util.rdf;

import java.io.Serializable;
import java.util.Date;

import com.eurodyn.uns.model.User;

public interface IChannel extends Serializable {

	public Integer getId();

	public void setId(Integer id);

	public String getContent();

	public void setContent(String content);

	public String getDescription();

	public void setDescription(String description);

	public String getFeedUrl();

	public void setFeedUrl(String feedUrl);

	public String getTitle();

	public void setTitle(String title);

	public Integer getRefreshDelay();

	public void setRefreshDelay(Integer refreshDelay);

	public String getMode();

	public void setMode(String mode);

	public String getSecondaryId();

	public void setSecondaryId(String secondaryId);

	public User getUser();

	public void setUser(User user);
	
	public Date getLastHarvestDate();

	public void setLastHarvestDate(Date lastHarvestDate);

}

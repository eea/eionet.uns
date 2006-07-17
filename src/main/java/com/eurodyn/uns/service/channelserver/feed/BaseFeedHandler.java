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
package com.eurodyn.uns.service.channelserver.feed;

import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.channelserver.DisabledException;
import com.eurodyn.uns.service.channelserver.NotFoundException;

public abstract class BaseFeedHandler {
    
    
    public abstract void handleRequest(Dto request,short action) throws DisabledException, NotFoundException, Exception;

}

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
 *    Original code: Dusan Popovic (ED) 
 */

package com.eurodyn.uns.util.cache;

import java.util.Date;

public class CacheItem {
    protected Object key;

    protected Object content;

    protected Date lastHarvestDate;

    private CacheItem() {
    }

    /**
     * Constructs a CacheItem object
     * 
     * @param key
     * @param timeToLive
     *              ms to keep this in the cache
     * @param content
     *              The content being cached
     */
    public CacheItem(Object key, Object content, Date lastHarvestDate) {
        this.key = key;
        this.content = content;
        this.lastHarvestDate = lastHarvestDate;
    }

    /**
     * Set the content in the cache
     * 
     * @param content
     *              the content being cached
     */
    public void setContent(Object content) {
        this.content = content;
    }

    /**
     * Get the content
     * 
     * @return the content being cached
     */
    public Object getContent() {
        return this.content;
    }

    /**
     * @return Returns the key.
     */
    public Object getKey() {
        return key;
    }

    /**
     * @param key
     *              The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    public Date getLastHarvestDate() {
        return lastHarvestDate;
    }

}

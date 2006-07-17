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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.eurodyn.uns.util.common.WDSLogger;

public class MemoryCache implements Comparator {
    private static final WDSLogger logger = WDSLogger.getLogger(MemoryCache.class);

    private int maxSize = 100;

    private int evictionPercentage = 10;

    private TreeMap cache = null;

    private Object lock = new Object();


    /**
     * Spring constructor injection
     *  
     */
    public MemoryCache(int maxSize, int evictionPercentage) {
        cache = new TreeMap();
        this.maxSize = maxSize;
        this.evictionPercentage = evictionPercentage;
    }


    public int getMaxSize() {
        return maxSize;
    }


    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }


    public int getEvictionPercentage() {
        return this.evictionPercentage;
    }


    public void put(Object key, Object document, long timeToLive) {
        CacheItem entry = new CacheItem(key, document, timeToLive);
        if (cache.size() > getMaxSize()) {
            evict();
        }
        synchronized (lock) {
            cache.put(key, entry);
        }
        logger.debug("Transformed content put in cache! Transform: " + key);

    }


    /**
     * The eviction policy will keep n items in the cache, and then start evicting
     * x items ordered-by least used first. 
     * n = max size of cache
     * x = (eviction_percentage/100) * n
     *
     */
    protected void evict() {
        logger.debug("Calling evict... cacheSize: " + cache.size() + " maxSize: " + getMaxSize());
        synchronized (lock) {
            if (this.getMaxSize() >= cache.size()) {
                return;
            }

            List list = new LinkedList(cache.values());
            Collections.sort(list, this);

            int count = 0;
            int limit = (getMaxSize() * getEvictionPercentage()) / 100;
            if (limit <= 0)
                limit = 1;

            for (Iterator it = list.iterator(); it.hasNext();) {
                if (count >= limit) {
                    break;
                }

                CacheItem entry = (CacheItem) it.next();
                logger.debug("Evicting: " + entry.getKey());
                cache.remove(entry.getKey());

                count++;
            }
        }
    }


    public Object remove(String key) {
        CacheItem entry = (CacheItem) cache.get(key);
        if (entry == null) {
            return null;
        }
        synchronized (lock) {
            entry = (CacheItem) cache.remove(key);
        }
        return entry;

    }


    public CacheItem get(Object key) {
        CacheItem entry = (CacheItem) cache.get(key);
        if (entry == null) {
            return null;
        }
        long now = new Date().getTime();
        long lifeTime = entry.getTimeToLive() * 1000;
        if ((entry.getLastAccessed() + lifeTime) < now) {
            logger.debug("Transformed content expired for key: "+key);
            return null; // expire it
        }
        logger.debug("Transformed content found in cache! Transform: " + key);
        return entry;
    }

    

    public Object getContent(String key) {
        CacheItem entry = (CacheItem) get(key);
        if (entry != null) {
            return entry.getContent();
        }
        return null;
    }


    public int compare(Object o1, Object o2) {
        CacheItem e1 = (CacheItem) o1;
        CacheItem e2 = (CacheItem) o2;
        if (e1.getLastAccessed() < e2.getLastAccessed()) {
            return -1;
        } else if (e1.getLastAccessed() == e2.getLastAccessed()) {
            return 0;
        }
        return 1;
    }


    public String constructKey(String url, String stylesheet) {
        return url + ":" + stylesheet;
    }


    public void clearCache() {
        cache.clear();
    }

    public Set getKeys(){
    	return cache.keySet();
    }
}

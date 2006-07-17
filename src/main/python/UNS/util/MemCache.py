# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# The Original Code is Reportnet Unified Notification Service
#
# The Initial Owner of the Original Code is European Environment
# Agency (EEA).  Portions created by European Dynamics (ED) company are
# Copyright (C) by European Environment Agency.  All
# Rights Reserved.
#
# Contributor(s):
#   Original code: Nedeljko Pavlovic (ED)



import time, threading

class MemCache:
    """ A cache for application objects """

    def __init__(self):
        """ Initialize a new instance """
        self.cache = {}
        self.cache_lock = threading.Lock()


    def put(self, key , content, time_to_live=None):
        """  """
        key = key.lower()
        entry = CacheItem(key, content, time_to_live);
        self.cache_lock.acquire()
        self.cache[key] = entry
        self.cache_lock.release()


    def get(self, key):
        """ """
        try: key = key.lower()
        except AttributeError: return None

        entry = self.cache.get(key)
        if not entry: return None
        
        now = time.time()
        lifeTime = entry.getTimeToLive()
        if entry.getLastAccessed() + lifeTime < now: return None # expire it
        return entry



    def remove(self, id):
        """  """
        id = id.lower()
        self.cache_lock.acquire()
        if self.cache.has_key(id):  del self.cache[id]
        self.cache_lock.release()


    def clear(self):
        """ Clears the cache """
        self.cache = {}
        
    def getCache(self):
        """ Gets valid cache records """
        valid = []
        cache_keys = self.cache.keys()
        now = time.time()
        for key in cache_keys:
            item=self.get(key)
            if item: valid.append(object)
        return valid
    

class CacheItem:
    """ """
    def __init__(self, key, content, time_to_live=10 * 60):
        self.key=key
        self.time_to_live=time_to_live*1.0
        self.lastAccessed=time.time()
        self.content=content

    def setContent(self, content):
        self.content=content

    def getContent(self):
        return self.content
    
    def getTimeToLive(self):
        return self.time_to_live
    
    def getLastAccessed(self):
        return self.lastAccessed


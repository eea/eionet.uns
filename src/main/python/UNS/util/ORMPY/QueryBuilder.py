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

from SqlFunction import SqlFunction

class QueryBuilder:
    """ 
    TODO: Need to be refactored to avoid duplicating of code, 
    currently it is not high priority
    """
    def buildInsertStatment(self, obj):
        names=[]
        params=[]
        l=[]
        for key in obj.getAttributes():
            if len(names)==0:
                names.append(key)
                if isinstance(obj[key], SqlFunction): params.append(obj[key].getFunction())
                else:
                    params.append('%s')
                    l.append(obj[key])
            else:
                names.append(',')
                names.append(key)
                params.append(',')
                if isinstance(obj[key], SqlFunction): params.append(obj[key].getFunction())
                else:
                    params.append('%s')
                    l.append(obj[key])
        statment=['INSERT INTO ', obj.table, '(', ') VALUES(', ')'];
        statment[3:3]=names
        statment[-1:-1]=params
        t=''.join(statment), tuple(l)
        return t
        
    
    def buildDeleteStatment(self,obj):
        statment="DELETE FROM "+obj.table+" WHERE PAIRS"
        pairs=None
        l=[]
        index=0
        for key in obj.pk:
            l.append(obj[key[0]])
            index+=1
            if(pairs is None):
                pairs=key[0]+"=%s"
            else:
                pairs= pairs + " AND "+key[0]+"=%s"
        statment=statment.replace("PAIRS",pairs)
        t=statment, tuple(l)
        return t    
        
    
    def buildUpdateStatment(self,obj):
        """ Builds update query   """
        pairs=[]
        l=[]
        for key in obj.getAttributes():
            if len(pairs)==0:
                pairs.append(key)
                pairs.append("=")
                if isinstance(obj[key], SqlFunction): pairs.append(obj[key].getFunction())
                else:
                    pairs.append('%s')
                    l.append(obj[key])
            else:
                pairs.append(", ")
                pairs.append(key)
                pairs.append("=")
                if isinstance(obj[key], SqlFunction): pairs.append(obj[key].getFunction())
                else:
                    pairs.append('%s')
                    l.append(obj[key])
        tt=self.buildPKCondition(obj)
        statment=['UPDATE ', obj.table ,' SET ', ' WHERE ', tt[0]]
        statment[3:3]=pairs
        l+=tt[1]
        t=''.join(statment), tuple(l)
        return t
        
   
    def buildFindAll(self, obj, attrs=None):
        if attrs is None:
            columns=self.allColumns(obj)
        else:
            columns=self.__customAttributes(attrs)
        return ''.join(['SELECT ',columns,' FROM ',obj.table])
        
        
    def buildFindByPKStatment(self, obj, attrs=None):
        if attrs is None:
            columns=self.allColumns(obj)
        else:
            columns=self.__customAttributes(attrs)
        tt=self.buildPKCondition(obj)
        statment=['SELECT ', columns, ' FROM ', obj.table, ' WHERE ',tt[0]]
        t=''.join(statment), tt[1]
        return t
        
    
    def buildPKCondition(self,obj):
        pairs=None
        l=[]
        for key in obj.pk:
            l.append(obj[key[0]])
            if(pairs is None):
                pairs=key[0]+"=%s"
            else:
                pairs= pairs + " AND "+key[0]+"=%s"
        return tuple([pairs,l])
    
    
    def allColumns(self,obj):
        return ', '.join(obj.getAttributes())
    
    def __customAttributes(self, attrs):
        return ', '.join(attrs)
            
            
        
        

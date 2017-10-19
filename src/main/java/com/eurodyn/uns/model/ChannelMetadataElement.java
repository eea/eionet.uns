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

public class ChannelMetadataElement implements Comparable,Serializable{

    
    private static final long serialVersionUID = 1862510090982883644L;  
    private Boolean visible;
    private Boolean filtered;
    private Integer appearanceOrder;
    private Boolean obsolete;
    private MetadataElement metadataElement;
    
    public MetadataElement getMetadataElement() {
        return metadataElement;
    }
    public void setMetadataElement(MetadataElement metadataElement) {
        this.metadataElement = metadataElement;
    }
    public Integer getAppearanceOrder() {
        return appearanceOrder;
    }
    public void setAppearanceOrder(Integer appearanceOrder) {
        this.appearanceOrder = appearanceOrder;
    }
    public Boolean isFiltered() {
        return filtered;
    }

    public Boolean getFiltered() {
        return filtered;
    }
    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }
    public Boolean isObsolete() {
        return obsolete;
    }

    public Boolean getObsolete() {
        return obsolete;
    }
    public void setObsolete(Boolean obsolete) {
        this.obsolete = obsolete;
    }
    
    public Boolean isVisible() {
        return visible;
    }
    
    public Boolean getVisible() {
        return visible;
    }
    
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof ChannelMetadataElement))
            return false;
        final ChannelMetadataElement cme = (ChannelMetadataElement) other;
        
        if (!cme.getMetadataElement().equals(getMetadataElement()))
            return false;
        
        return true;
    }

    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, metadataElement);
        return result;
    }

    

    public int compareTo(Object o) {
        if (o != null && o instanceof ChannelMetadataElement) {
            ChannelMetadataElement cme = (ChannelMetadataElement) o;
            return (appearanceOrder==null ? -1 : appearanceOrder.compareTo(cme.getAppearanceOrder()));
        }
        else
            return 1;
    }

    
}

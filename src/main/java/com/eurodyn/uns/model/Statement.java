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

import com.eurodyn.uns.util.HashCodeUtil;

public class Statement implements java.io.Serializable {

    private static final long serialVersionUID = 852475451895441178L;

    private MetadataElement metadataElement;

    private Integer id;

    private String value;

    private String property;

    public Statement() {
    }

    public Statement(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MetadataElement getMetadataElement() {
        return metadataElement;
    }

    public void setMetadataElement(MetadataElement metadataElement) {
        this.metadataElement = metadataElement;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Statement))
            return false;
        final Statement st = (Statement) other;
        if (!st.getProperty().equals(getProperty()))
            return false;
        if (!st.getValue().equals(getValue()))
            return false;
        return true;
    }

    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, property);
        result = HashCodeUtil.hash(result, value);
        return result;
    }

}
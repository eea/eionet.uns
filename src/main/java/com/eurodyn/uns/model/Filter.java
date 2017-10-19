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
import java.util.HashSet;
import java.util.Set;

import com.eurodyn.uns.util.HashCodeUtil;

public class Filter implements Serializable {

    // Fields
    private static final long serialVersionUID = -8427166850946922455L;

    private Integer id;

    private Subscription subscription;

    private Set statements = new HashSet(0);

    private boolean editMode;

    // Constructors

    /** default constructor */
    public Filter() {
    }

    /** minimal constructor */
    public Filter(Integer id, Subscription subscription) {
        this.id = id;
        this.subscription = subscription;
    }

    /** full constructor */
    public Filter(Integer id, Subscription subscription, Set statements) {
        this.id = id;
        this.subscription = subscription;
        this.statements = statements;
    }

    // Property accessors

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Subscription getSubscription() {
        return this.subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Set getStatements() {
        return this.statements;
    }

    public void setStatements(Set statements) {
        this.statements = statements;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Filter))
            return false;
        final Filter filt = (Filter) other;
        if (!filt.getStatements().equals(getStatements()))
            return false;
        return true;
    }

    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, statements);
        return result;
    }

}

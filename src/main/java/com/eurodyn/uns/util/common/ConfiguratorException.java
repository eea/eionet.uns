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

package com.eurodyn.uns.util.common;

/**
 * Exception for throwing configuration handling errors.
 *
 * @author Jaanus
 */
public class ConfiguratorException extends Exception {

    /** Serail ID. */
    private static final long serialVersionUID = -1234301207938511605L;

    /**
     * Default constructor.
     */
    public ConfiguratorException() {
        // Nothing to do here.
    }

    /**
     * Calls super-class constructor with same arguments.
     *
     * @param msg Exception message.
     */
    public ConfiguratorException(String msg) {
        super(msg);
    }

    /**
     * Calls super-class constructor with same arguments.
     *
     * @param msg Exception message.
     * @param cause The causing exception.
     */
    public ConfiguratorException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

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

package com.eurodyn.uns.dao.ldap;

import com.eurodyn.uns.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;


public abstract class BaseLdapDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseLdapDao.class);

    public static final int PAGE_SIZE = 1000;

    protected static String baseDn;

    static {
        baseDn = Properties.getStringProperty("ldap.context");
    }

    protected DirContext getDirContext() throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, Properties.getStringProperty("ldap.url"));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, Properties.getStringProperty("ldap.principal"));
        env.put(Context.SECURITY_CREDENTIALS, Properties.getStringProperty("ldap.password"));
        DirContext ctx = new InitialDirContext(env);
        return ctx;
    }

    protected LdapContext getPagedLdapContext() throws NamingException, IOException {
        Hashtable env = new Hashtable();
        env.put(LdapContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(LdapContext.PROVIDER_URL, Properties.getStringProperty("ldap.url"));
        env.put(LdapContext.SECURITY_AUTHENTICATION, "simple");
        env.put(LdapContext.SECURITY_PRINCIPAL, Properties.getStringProperty("ldap.principal"));
        env.put(LdapContext.SECURITY_CREDENTIALS, Properties.getStringProperty("ldap.password"));
        LdapContext ctx = new InitialLdapContext(env, null);
        ctx.setRequestControls(new Control[]{
                new PagedResultsControl(PAGE_SIZE, Control.CRITICAL)
        });
        return ctx;
    }

    protected void closeContext(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                // do nothing
            }
        }
    }

}

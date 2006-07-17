package com.eurodyn.uns.model;

import java.io.Serializable;

/**
 * <p>Title: AbstractDto</p>
 * <p>Description: Interface that should be implemented by any class(bean)
 * that need to return its class name</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: European Dynamics</p>
 * @version 1.0
 */
public abstract class AbstractDto implements Serializable {

   // the main identifier method for all value objects
   public abstract String getDtoClassName();
}

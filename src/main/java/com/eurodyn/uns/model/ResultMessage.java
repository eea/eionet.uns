package com.eurodyn.uns.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a system message key with a List of parameters. This will be created during execution of command and wrapped by ResultDTO. At Web tier this will be converted to Struts ActionError.
 * 
 */
public class ResultMessage  {

	private final String key;

	private final List paramList = new LinkedList();

	/**
	 * Creates an instance.
	 */
	public ResultMessage(String key) {
		this.key = key;
	}

	/**
	 * Add a parameter to this ResultMesage. At Web tier this will be used as a key to look up a localized parameter name. If there is no key for the parameter, it will be used as is.
	 */
	public void addParam(Object param) {
		paramList.add(param);
	}

	public Object[] getParams() {
		return paramList.toArray();
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		return "ResultMessage: " + key + paramList;
	}
}

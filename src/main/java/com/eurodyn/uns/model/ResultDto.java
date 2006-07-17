package com.eurodyn.uns.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An DTO for passing result values of command from Business tier to Web tier. Also holds the following special values:<BR>
 * <BR> - forward: a keyword to indicate a page to jump (mostly for Struts' action mapping)<BR> - a List of ResultMessages: keys and parameters to show internationalized message to users (those will be converted to Strut's ActionErrors).
 */
public class ResultDto extends Dto {

	private static final String FORWARD_SUCCESS = "Success";

	private static final String FORWARD_FAILURE = "Failure";

	private final List resultMessageList = new LinkedList();

	private String forward = FORWARD_SUCCESS;

	public boolean isClearingForm = false;

	/**
	 * Sets forward keyword that will be passed to DefaultForm to determine next page.
	 * 
	 * @param forward
	 *              forward name
	 */
	public void setForward(String forward) {
		this.forward = forward;
	}

	/**
	 * Returns a forward keyword.
	 */
	public String getForward() {
		return forward;
	}

	/**
	 * Tells is the forward is set as 'failure'.
	 */
	public boolean isFailure() {
		return forward.equals(FORWARD_FAILURE);
	}

	/**
	 * Sets the forward as 'failure'. This may cause DefaultAction to get back to the previous page.
	 */
	public void setResultAsFailure() {
		forward = FORWARD_FAILURE;
	}

	/**
	 * Adds a ResultMessage to ResultMessage List of this DTO. The ResultMessage List can be retrived on JSP page by referring 'msgs' key of this DTO. e.g. form.msgs
	 * 
	 * @param msgKey
	 *              ResultMessage to add
	 * @see ResultMessage
	 */
	public void addResultMessage(ResultMessage msgKey) {
		resultMessageList.add(msgKey);
	}

	/**
	 * Adds a ResultMessage to resultDTO.
	 * 
	 * @see ResultMessage
	 */
	public void addResultMessage(String key) {
		ResultMessage msgKey = new ResultMessage(key);
		addResultMessage(msgKey);
	}

	/**
	 * Adds a ResultMessage (with a param key) to resultDTO.
	 * 
	 * @see ResultMessage
	 */
	public void addResultMessage(String key, Object param0) {
		ResultMessage msgKey = new ResultMessage(key);
		msgKey.addParam(param0);
		addResultMessage(msgKey);
	}

	/**
	 * Adds a ResultMessage (with a param key) to resultDTO.
	 * 
	 * @see ResultMessage
	 */
	public void addResultMessage(String key, Object param0, Object param1) {
		ResultMessage msgKey = new ResultMessage(key);
		msgKey.addParam(param0);
		msgKey.addParam(param1);
		addResultMessage(msgKey);
	}

	/**
	 * Tells if this ResultDTO has any DTOMessages.
	 */
	public boolean hasResultMessage() {
		return (!resultMessageList.isEmpty());
	}

	/**
	 * Returns an Iterator of DTOMessages.
	 */
	public Iterator getResultMessages() {
		return resultMessageList.iterator();
	}

	public String toString() {
		return super.toString() + " MSG: " + resultMessageList;
	}

}

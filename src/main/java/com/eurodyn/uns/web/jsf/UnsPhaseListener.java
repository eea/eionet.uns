package com.eurodyn.uns.web.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class UnsPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 8833602239376172818L;

	public void afterPhase(PhaseEvent event) {
	}

	public void beforePhase(PhaseEvent event) {
		event.getFacesContext().getExternalContext().getRequestMap().put(PhaseId.RENDER_RESPONSE, Boolean.TRUE);
	}

	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
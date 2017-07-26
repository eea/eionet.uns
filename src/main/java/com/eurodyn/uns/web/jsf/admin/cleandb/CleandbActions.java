package com.eurodyn.uns.web.jsf.admin.cleandb;

import com.eurodyn.uns.service.facades.EventMetadataFacade;

import com.eurodyn.uns.web.jsf.BaseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleandbActions extends BaseBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CleandbActions.class);
    protected EventMetadataFacade eventMetadataFacade = new EventMetadataFacade();
    
    public String clean() {
        try {
            eventMetadataFacade.deleteOldEvents();
            addInfoMessage(null, "msg.deleteSuccess", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;

    }
}
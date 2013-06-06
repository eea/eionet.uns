package com.eurodyn.uns.web.jsf.admin.cleandb;

import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.BaseBean;

public class CleandbActions extends BaseBean {
    
    private static final WDSLogger logger = WDSLogger.getLogger(CleandbActions.class);
    protected EventMetadataFacade eventMetadataFacade = new EventMetadataFacade();
    
    public String clean() {
        try {
            eventMetadataFacade.deleteOldEvents();
            addInfoMessage(null, "msg.deleteSuccess", null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return null;

    }
}
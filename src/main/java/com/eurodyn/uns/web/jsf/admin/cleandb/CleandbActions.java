package com.eurodyn.uns.web.jsf.admin.cleandb;

import com.eurodyn.uns.service.facades.EventMetadataFacade;

import com.eurodyn.uns.web.jsf.BaseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CleandbActions extends BaseBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CleandbActions.class);
    protected EventMetadataFacade eventMetadataFacade = new EventMetadataFacade();

    public String clean() {

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        try {
            int count =  eventMetadataFacade.deleteOldEvents();
            if (count>0) {
                addInfoMessage(null, "msg.deleteSuccess", null);
            } else {
                addInfoMessage(null, "msg.deleteFailure", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        addInfoMessage(null, "msg.finishUpdate", null);
        return null;
    }
}
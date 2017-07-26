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

package com.eurodyn.uns.service.facades;

import java.util.List;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Stylesheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XslFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(XslFacade.class);
    private static XslFacade instance = null;
    private DAOFactory daoFactory;

    public XslFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }

    public ResultDto getStylesheets() {
        return getSortedStylesheets("name", null);
    }

    public ResultDto getStylesheets(Dto dto) {
        String orderProperty = dto.getAsString("orderProperty");
        if(orderProperty == null || orderProperty.length()==0)
            orderProperty = "name";     
        String order = dto.getAsString("order");
        return getSortedStylesheets(orderProperty, order);
    }
    
    public ResultDto getSortedStylesheets(String orderProperty, String order) {
        ResultDto result=new ResultDto();
        List stylesheets = null;
        try {
            stylesheets = daoFactory.getStylesheetDao().findAllStylesheets(orderProperty, order);
            result.put("list",stylesheets);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return result;
    }
    
    public Stylesheet getStylesheet(Integer id) {
        Stylesheet xsl = null;
        try {
            xsl = daoFactory.getStylesheetDao().findByPK(id);
            //XmlContext x = new XmlContext();
            //x.setWellFormednessChecking();
            //x.checkFromInputStream(new ByteArrayInputStream(xsl.getContent().getBytes()));
            //xsl.setContent(x.serializeToString());
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return xsl;
    }

    public boolean updateStylesheet(Stylesheet xsl) {
        boolean ret = false;
        try {
            daoFactory.getStylesheetDao().updateStylesheet(xsl);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }

    public boolean createStylesheet(Stylesheet xsl) {
        boolean ret = false;
        try {
            daoFactory.getStylesheetDao().createStylesheet(xsl);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }

    public ResultDto deleteStylesheet(Dto params) throws Exception{
        
        ResultDto result=new ResultDto();
        try {
            Stylesheet xsl = getStylesheet(params.getAsInteger("xslId"));
            if (xsl == null) {
                result.addResultMessage("label.xsl.error.unexisting");
            } else if (xsl.getChannelsCount().intValue() > 0) {
                result.addResultMessage("label.xsl.error.deletec");
            } else { 
                deleteStylesheetl(xsl);
                result.addResultMessage("label.xsl.success.delete", xsl.getName());
            }
        } catch (DAOException e) {
            result.addResultMessage("label.xsl.error.deletec");
        } catch (Exception e) {
            result.addResultMessage("label.xsl.error.deletec");
        }
        return result;
    }

    public boolean deleteStylesheetl(Stylesheet xsl) throws Exception {
        boolean ret = false;
        try {
            daoFactory.getStylesheetDao().deleteStylesheetl(xsl);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
            throw(e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            throw(e);
        }
        return ret;
    }

    public static XslFacade getInstance() {
        if (null == instance) {
            synchronized (XslFacade.class) {
                if (null == instance) {
                    try {
                        instance = new XslFacade();
                    } catch (Exception ce) {
                    }
                }
            }
        }
        return instance;
    }

}

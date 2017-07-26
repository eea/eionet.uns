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
import com.eurodyn.uns.model.MetadataElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MetadataElementFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataElementFacade.class);
    private DAOFactory daoFactory;

    public MetadataElementFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }

    public ResultDto getMetadataElements() {
        return getSortedMetadataElements("name", null);
    }

    public ResultDto getMetadataElements(Dto dto) {
        String orderProperty = dto.getAsString("orderProperty");
        if(orderProperty == null || orderProperty.length()==0)
            orderProperty = "name";     
        String order = dto.getAsString("order");
        return getSortedMetadataElements(orderProperty, order);
    }
    
    public ResultDto getSortedMetadataElements(String orderProperty, String order) {
        ResultDto result=new ResultDto();
        List stylesheets = null;
        try {
            stylesheets = daoFactory.getMetadataElementDao().findAllMetadataElements(orderProperty, order);
            result.put("list",stylesheets);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return result;
    }
    
    public MetadataElement getMetadataElement(Integer id) {
        MetadataElement metadataElement = null;
        try {
            metadataElement = daoFactory.getMetadataElementDao().findByPK(id);
            //XmlContext x = new XmlContext();
            //x.setWellFormednessChecking();
            //x.checkFromInputStream(new ByteArrayInputStream(metadataElement.getContent().getBytes()));
            //metadataElement.setContent(x.serializeToString());
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return metadataElement;
    }

    public MetadataElement findByName(String name) {
        MetadataElement metadataElement = null;
        try {
            metadataElement = daoFactory.getMetadataElementDao().findByName(name);
            //XmlContext x = new XmlContext();
            //x.setWellFormednessChecking();
            //x.checkFromInputStream(new ByteArrayInputStream(metadataElement.getContent().getBytes()));
            //metadataElement.setContent(x.serializeToString());
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return metadataElement;
    }
    
    public boolean updateMetadataElement(MetadataElement metadataElement) {
        boolean ret = false;
        try {
            daoFactory.getMetadataElementDao().updateMetadataElement(metadataElement);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }

    public boolean createMetadataElement(MetadataElement metadataElement) {
        boolean ret = false;
        try {
            daoFactory.getMetadataElementDao().createMetadataElement(metadataElement);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }


    public boolean deleteMetadataElement(MetadataElement metadataElement) throws Exception {
        boolean ret = false;
        try {
            daoFactory.getMetadataElementDao().deleteMetadataElement(metadataElement);
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


}

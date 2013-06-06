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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.util.common.WDSLogger;

public class DeliveryTypeFacade {

    private static final WDSLogger logger = WDSLogger.getLogger(DeliveryTypeFacade.class);
    private DAOFactory daoFactory;

    public DeliveryTypeFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }

    public ResultDto getDeliveryTypes() {
        return getSortedDeliveryTypes("name", null);
    }

    
    public Map getDeliveryTypesMap() {
        Map deliveryTypesMap  = new HashMap();
        List deliveryTypes = (List) getSortedDeliveryTypes("name", null).get("list");        
        if (deliveryTypes != null){
            for (Iterator iter = deliveryTypes.iterator(); iter.hasNext();) {
                DeliveryType dt = (DeliveryType) iter.next();
                deliveryTypesMap.put(dt.getName(),dt);
            }           
        }        
        return deliveryTypesMap;
    }

    
    
    public ResultDto getDeliveryTypes(Dto dto) {
        String orderProperty = dto.getAsString("orderProperty");
        if(orderProperty == null || orderProperty.length()==0)
            orderProperty = "name";     
        String order = dto.getAsString("order");
        return getSortedDeliveryTypes(orderProperty, order);
    }
    
    public ResultDto getSortedDeliveryTypes(String orderProperty, String order) {
        ResultDto result=new ResultDto();
        List stylesheets = null;
        try {
            stylesheets = daoFactory.getDeliveryTypeDao().findAllDeliveryTypes(orderProperty, order);
            result.put("list",stylesheets);
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return result;
    }
    
    public DeliveryType getDeliveryType(Integer id) {
        DeliveryType deliveryType = null;
        try {
            deliveryType = daoFactory.getDeliveryTypeDao().findByPK(id);
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return deliveryType;
    }

    public DeliveryType findByName(String name) {
        DeliveryType deliveryType = null;
        try {
            deliveryType = daoFactory.getDeliveryTypeDao().findByName(name);
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return deliveryType;
    }
    
    public boolean updateDeliveryType(DeliveryType deliveryType) {
        boolean ret = false;
        try {
            daoFactory.getDeliveryTypeDao().updateDeliveryType(deliveryType);
            ret = true;
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return ret;
    }

    public boolean createDeliveryType(DeliveryType deliveryType) {
        boolean ret = false;
        try {
            daoFactory.getDeliveryTypeDao().createDeliveryType(deliveryType);
            ret = true;
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return ret;
    }


    public boolean deleteDeliveryType(DeliveryType deliveryType) throws Exception {
        boolean ret = false;
        try {
            daoFactory.getDeliveryTypeDao().deleteDeliveryType(deliveryType);
            ret = true;
        } catch (DAOException e) {
            logger.error(e);
            throw(e);
        } catch (Exception e) {
            logger.fatalError(e);
            throw(e);
        }
        return ret;
    }


}

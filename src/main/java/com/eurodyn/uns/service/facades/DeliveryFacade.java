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

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Delivery;
import com.eurodyn.uns.util.common.WDSLogger;

public class DeliveryFacade {

    private static final WDSLogger logger = WDSLogger.getLogger(DeliveryFacade.class);
    private DAOFactory daoFactory;

    public DeliveryFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }
        
    public boolean updateDelivery(Delivery delivery) {
        boolean ret = false;
        try {
            daoFactory.getDeliveryDao().updateDelivery(delivery);
            ret = true;
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return ret;
    }

    public boolean createDelivery(Delivery delivery) {
        boolean ret = false;
        try {
            daoFactory.getDeliveryDao().createDelivery(delivery);
            ret = true;
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return ret;
    }

}

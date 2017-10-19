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

package com.eurodyn.uns.service.channelserver.feed;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.eurodyn.uns.Properties;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IChannelDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.DisabledException;
import com.eurodyn.uns.service.channelserver.NotFoundException;
import com.eurodyn.uns.util.DateUtil;

import com.eurodyn.uns.util.common.ConfiguratorException;

import com.eurodyn.uns.util.rdf.IChannel;
import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent tie responsible for handling of PUSH channels.
 * 
 */
public class PushHandler extends BaseFeedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushHandler.class);
    private BaseFeedHandler successor;
    private IChannelDao channelDao;
    private static String pathPrefix;

    private static final String dcNS = "http://wdb.eionet.europa.eu/elements#";
    public static int DEFAULT_LIFETIME = 60 * 24 * 60;
    
    static {
        pathPrefix = Properties.getStringProperty("uns.home") + File.separatorChar + "rdf" + File.separatorChar;
        DEFAULT_LIFETIME=Integer.parseInt(Properties.getStringProperty("pushchannel.default_lifetime"));
    }

    public PushHandler() {
    }

    public PushHandler(BaseFeedHandler successor) {
        this.successor = successor;
        channelDao = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE).getChannelDao();
    }

    public void handleRequest(Dto request, short action) throws DisabledException, NotFoundException, Exception {
        if (action == BaseChannelServer.PUSH) {
            if (request.get("secondaryId") == null) {
                IChannel channel = (IChannel) request.get("channel");
                getContent(channel);
            } else {
                push((String) request.get("secondaryId"), (User) request.get("user"), (String) request.get("RDF"));
            }
        } else if (successor != null) {
            successor.handleRequest(request, action);
        }
    }

    private void getContent(IChannel channel) {

    }

    private void push(String secondaryId, User user, String rdf) throws DisabledException, NotFoundException, Exception {
        Channel c = channelDao.findChannel(secondaryId);
        if (c == null){
            LOGGER.debug("Channel not found:" + secondaryId);
            throw new NotFoundException();
        }
        if (c.getStatus() == null || c.getStatus().intValue() == 0){
            LOGGER.debug("Channel is disabled:" + secondaryId);
            throw new DisabledException();
        }
        String userpath = (user != null ? "_" + user.getExternalId() : "");
        String persistence = pathPrefix + secondaryId + userpath + "_" + "data.rdf";
        LOGGER.debug(persistence);
        Model main = ModelFactory.createDefaultModel();
        Model incoming = ModelFactory.createDefaultModel();
        InputStream in1 = FileManager.get().open(persistence);
        ByteArrayInputStream bis = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
        main.read(in1, "");
        incoming.read(bis, "");

        checkDataLifeTime(c, user, main);
        
        //logger.debug("================ PERSISTENSE ================");
        //log(main);
        //logger.debug("================ INCOMING ================");
        //log(incoming);

        Model intersection = incoming.intersection(main);

        //logger.debug("================ DIFFERENCE ================");
        //log(intersection);

        removeExisting(main, intersection);
        prepareIncomming(incoming);
        
        Model result = main.union(incoming);
        result.setNsPrefix( "wdb", dcNS );

        //logger.debug("================ RESULT ================");
        //log(result);

        FileOutputStream fos = new FileOutputStream(persistence);
        result.write(fos, "RDF/XML-ABBREV");
        fos.flush();
        fos.close();
        LOGGER.debug(c.getTitle());
    }

    /**
     * Removes resources founded in the incoming model from the persistent model
     * @param current        Presisten model of the target channel
     * @param intersection  Statments intersection between persisten model and incomming model
     */
    private void removeExisting(Model current, Model intersection) {
        ResIterator res = intersection.listSubjects();
        while (res.hasNext()) {
            Resource r = res.nextResource();
            try {
                current.removeAll(r, null, null);
            } catch (Exception e) {
                LOGGER.error("Error", e);
            }
        }
    }

    /**
     * Insertes dc:created element into the incoming model.
     * This element will be used for identifying outdated data. 
     * @param incoming
     * @throws Exception
     */
    private void prepareIncomming(Model incoming) throws Exception {
        ResIterator res = incoming.listSubjects();
        while (res.hasNext()) {
            Resource r = res.nextResource();
            Property p = incoming.createProperty(dcNS + "created");
            r.addProperty(p, DateUtil.messageFormatWithTime(new Date()));
        }

    }

    /**
     * Loggs model by using WDS log utility.
     * @param model Model whose content is going to be logged.
     */
//  private void log(Model model) {
//      ByteArrayOutputStream bos = new ByteArrayOutputStream();
//      model.write(bos, "RDF/XML-ABBREV");
//      logger.debug(bos.toString());
//  }

    
    static public void checkDataLifeTime(Channel channel, User user, File file) {
        if(file.exists()){
            ModelMem model = new ModelMem();
            model.read(FileManager.get().open(file.getAbsolutePath()), "");
            checkDataLifeTime(channel, user, model);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                model.write(fos, "RDF/XML-ABBREV");
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                LOGGER.error("Error", e);
            }
        }
    }

    static public void checkDataLifeTime(Channel channel, User user, Model model) {
//      UserChannel uc = findUserChannel(user, channel);
//      int lifetime = DEFAULT_LIFETIME;
//      if(uc != null){
//          Integer lt = uc.getDataLifeTime();
//          if (lt != null)
//              lifetime = lt.intValue();
//      }
//      Calendar calendar = new GregorianCalendar();
//      calendar.add(Calendar.MINUTE, -lifetime);
//      Date fromDate = calendar.getTime();
//        
//      Property p = model.createProperty(dcNS + "created");        
//      for (ResIterator iterator = model.listSubjects(); iterator.hasNext();) {
//          Resource r = iterator.nextResource();
//          Statement created = r.getProperty(p);
//          if(created != null){
//              String strDate = created.getString();
//              Date date=null;
//              try {
//                  date = DateUtil.dateFormatWithTime(strDate);
//                  if (date.before(fromDate))
//                      model.removeAll(r, null, null);
//              } catch (ParseException e) {
//                  logger.error(e);
//              }
//          }
//      }
    }
    
//  static private UserChannel findUserChannel(User user, Channel channel) {
//      if(user != null){
//          Set uc = user.getUserchannels();
//          for (Iterator iter = uc.iterator(); iter.hasNext();) {
//              UserChannel element = (UserChannel) iter.next();
//              if(element.getChannel().equals(channel))
//                  return element;
//          }
//      }
//      return null;
//  }
}

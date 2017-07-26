package com.eurodyn.uns.web.servlets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.jdbc.JdbcFeedDao;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.User;

import com.eurodyn.uns.web.jsf.LoginBean;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

import eionet.directory.DirectoryServiceIF;
import eionet.directory.modules.DirectoryService25Impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RssFeedServlet extends HttpServlet {

    private static final long serialVersionUID = -5125237705976348016L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean secured = true;
            User user = null;
            
            String auth = request.getHeader("Authorization");
            
            if (auth == null) {
                user = LoginBean.getUser(request);
                if (user == null) secured = false;
            } else {
                String username = allowUser(auth);
                if (username == null) {
                    secured = false;
                } else {
                    user = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE).getUserDao().findUser(username);
                    if (user == null) secured = false;
                }
            }
            
            if (secured) {
                StringBuilder sb = new StringBuilder("<rss:channel><rss:title>Unified Notification System</rss:title><rss:items><rdf:Seq>");
                Model rdf = ModelFactory.createDefaultModel();

                JdbcFeedDao jdbcFeedDao = new JdbcFeedDao();
                rdf.setNsPrefix("rss", "http://purl.org/rss/1.0/");
                rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
                rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
                
                Map things = jdbcFeedDao.findAllUserEvents(user);
                Collection thingsList = things.values();
                
                for (Iterator iterator = thingsList.iterator(); iterator.hasNext();) {
                    RDFThing rdfThing = (RDFThing) iterator.next();
                    sb.append("<rdf:li rdf:resource=\"");
                    sb.append(rdfThing.getExt_id());
                    sb.append("\"/>");
                }
                sb.append("</rdf:Seq></rss:items></rss:channel>");
                
                for (Iterator iterator = thingsList.iterator(); iterator.hasNext();) {
                    RDFThing rdfThing = (RDFThing) iterator.next();
                    Resource item = rdf.createResource(rdfThing.getExt_id(), RSS.item);
                    boolean titleCheck = CheckTitle(rdfThing.getMetadata().entrySet().iterator());
                    Iterator iter = rdfThing.getMetadata().entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pairs2 = (Map.Entry) iter.next();
                        String pred = (String) pairs2.getKey();
                        ArrayList values = (ArrayList) pairs2.getValue();
                        Property pr;
                        for (int i = 0; i < values.size(); i++) {
                            if (!titleCheck && pred.equalsIgnoreCase("http://purl.org/dc/elements/1.1/title")) {
                                pr=RSS.title;
                            } else {
                                pr=ResourceFactory.createProperty(pred);
                            }
                            item.addProperty(pr, (String) values.get(i));
                        }
                    }
                }       
            
                StringBuilder resultOut = new StringBuilder();
                StringWriter out = new StringWriter();
                RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
                writer.write(rdf, new BufferedWriter(out), null);
                resultOut.append(out.toString());
            
                int ind = resultOut.indexOf(">");
                resultOut.insert(ind+1, sb.toString());
            
                response.setContentType("application/xml");
                PrintWriter outWriter = response.getWriter();
                outWriter.print(resultOut.toString());
                outWriter.flush();
                outWriter.close();
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "BASIC realm=\"RSSfeed\"");
            }
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }
    
    private boolean CheckTitle(Iterator iter) {
        boolean result=false;
        while (iter.hasNext()) {
            Map.Entry pairs2 = (Map.Entry) iter.next();
            String pred = (String) pairs2.getKey();
            ArrayList values = (ArrayList) pairs2.getValue();
            for (int i = 0; i < values.size(); i++) {
                if ("http://purl.org/rss/1.0/title".equalsIgnoreCase(pred)) {
                    result = true;
                    break;
                }
            }
            if (result) break;
        }
        return result;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
    
    
    protected String allowUser(String auth) throws IOException {
        LOGGER.debug("===================================");
        if (auth == null) return null;
        if (!auth.toUpperCase().startsWith("BASIC ")) return null;
        String userpassEncoded = auth.substring(6);

        String userpassDecoded = new String(Base64.decodeBase64(userpassEncoded.getBytes()));
        LOGGER.debug(userpassDecoded);
        
        StringTokenizer userAndPass=new StringTokenizer(userpassDecoded,":");
        String username=userAndPass.nextToken();
        String password=userAndPass.nextToken();

        LOGGER.debug(username);
        LOGGER.debug(password);
        
        try {
            DirectoryServiceIF directoryService = new DirectoryService25Impl();
            directoryService.sessionLogin(username, password);
        } catch(Exception e) {
            LOGGER.error("Error", e);
            username=null;
        }
        
        // Check our user list to see if that user and password are "allowed"
        return username;
      }

    
    
}

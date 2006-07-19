package com.eurodyn.uns.web.servlets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eurodyn.uns.dao.jdbc.JdbcFeedDao;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.LoginBean;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

public class RssFeedServlet extends HttpServlet {

	private static final long serialVersionUID = -5125237705976348016L;

	private static final WDSLogger logger = WDSLogger.getLogger(RssFeedServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)

	{
		try {
			String result = "";
			Model rdf = ModelFactory.createDefaultModel();

			JdbcFeedDao jdbcFeedDao = new JdbcFeedDao();
			rdf.setNsPrefix("rss", "http://purl.org/rss/1.0/");
			rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
			rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
			Resource rssChannel = rdf.createResource(RSS.channel);
			rssChannel.addProperty(RSS.title, "Unified Notification System ");

			User user = LoginBean.getUser(request);

			if(user != null){
				Map things = jdbcFeedDao.findAllUserEvents(user);
				Collection thingsList = things.values();
				for (Iterator iterator = thingsList.iterator(); iterator.hasNext();) {
					RDFThing rdfThing = (RDFThing) iterator.next();
					Resource item = rdf.createResource(rdfThing.getExt_id(), ResourceFactory.createResource(rdfThing.getType()));
					Iterator iter = rdfThing.getMetadata().entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry pairs2 = (Map.Entry) iter.next();
						item.addProperty(ResourceFactory.createProperty((String) pairs2.getKey()), (String) pairs2.getValue());
					}

				}				
			}
			
			StringWriter out = new StringWriter();
			RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
			writer.write(rdf, new BufferedWriter(out), null);
			result = out.toString();

			response.setContentType("application/xml");

			PrintWriter outWriter = response.getWriter();
			outWriter.print(result);
			outWriter.flush();
			outWriter.close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}
}

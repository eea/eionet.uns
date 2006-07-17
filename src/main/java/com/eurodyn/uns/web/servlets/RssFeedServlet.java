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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

public class RssFeedServlet extends HttpServlet
{
   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)

   {
      try {
		String result = "";
		Model rdf = ModelFactory.createDefaultModel();

		JdbcFeedDao jdbcFeedDao = new JdbcFeedDao();
		rdf.setNsPrefix("rss", "http://purl.org/rss/1.0/");
		rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
		rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
		Resource rssChannel = rdf.createResource(RSS.channel);
		rssChannel.addProperty(RSS.title, "Unified Notification System 2");
		//rssChannel.addProperty(RSS.link, channel.getFeedUrl() != null ? channel.getFeedUrl() : "http://testChannel.com");
		//rssChannel.addProperty(RSS.description, channel.getDescription());

		User user = new User();
		user.setId(new Integer(8));
		try {
			Map things = jdbcFeedDao.findAllUserEvents(user);
			//System.out.println("things" + things);
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringWriter out = new StringWriter();
		RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
		writer.write(rdf, new BufferedWriter(out), null);
		result = out.toString();
		
		//System.out.println(" result" + result);
		
		response.setContentType("application/xml");
		
		PrintWriter outWriter = response.getWriter();;

		//outWriter = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
		outWriter.print(result);
		outWriter.flush();
		outWriter.close();
		
		


	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
                     throws IOException, ServletException
   {
      doGet(request,response);
   }
}

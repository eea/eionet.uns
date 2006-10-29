package com.eurodyn.uns.web.jsf.rss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.service.facades.FeedFacade;
import com.eurodyn.uns.util.xml.TransformException;
import com.eurodyn.uns.util.xml.XSLTransformer;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

public class RssReaderForm extends BaseBean {

	FeedFacade feedFacade;

	public RssReaderForm() {
		feedFacade = new FeedFacade();
	}

	protected List channels;

	protected Channel channel;

	public List getChannels() {
		return channels;
	}

	public void setChannels(List channels) {
		this.channels = channels;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	protected String renderedEvents;

	protected List events;

	public Collection getEvents() {
		return events;

	}

	public String getRenderedEvents() {
		return renderedEvents;
	}

	protected void populateThings(Subscription subs) throws TransformException {
		List properties = new ArrayList();
		Map parameters = new HashMap();
		Channel channel = subs.getChannel();
		if (channel.getTransformation() == null) {
			List elements = new ArrayList(channel.getMetadataElements());
			Collections.sort(elements);
			if (elements != null) {
				for (Iterator iter = elements.iterator(); iter.hasNext();) {
					ChannelMetadataElement element = (ChannelMetadataElement) iter.next();
					if (element.isVisible().booleanValue())
						properties.add(element.getMetadataElement().getName());
				}
			} else {
				properties.add("http://www.w3.org/2000/01/rdf-schema#label");
			}
		} else {
			parameters.put("openinpopup", "true");
			parameters.put("showdescription", "true");
			parameters.put("showtitle", "true");
		}

		Map things = feedFacade.findUserEvents(subs);
		Iterator iterator = things.values().iterator();
		while (iterator.hasNext()) {
			RDFThing rdfThing = (RDFThing) iterator.next();

			if (channel.getTransformation() != null) {
				String rssContent = toRss(channel, rdfThing);
				InputSource source = new InputSource(new BufferedReader(new StringReader(rssContent)));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				XSLTransformer transform = new XSLTransformer();
				transform.transform(channel.getTransformation().getName(), channel.getTransformation().getContent(), source, baos, parameters);
				String transformedContent = baos.toString();
				rdfThing.setTransformedContent(transformedContent);

				channel.getTransformedEvents().put(rdfThing.getEventId(), rdfThing);
			} else {
				String transformedContent = renderVisbleElements(rdfThing, properties);
				rdfThing.setTransformedContent(transformedContent);
				channel.getTransformedEvents().put(rdfThing.getEventId(), rdfThing);
			}

		}
	}

	private String renderVisbleElements(RDFThing thing, List properties) {
		StringBuffer result = new StringBuffer();

		result.append("<ul>\n");

		String subject = (String) thing.getExt_id();

		result.append("\t<li>\n");
		Map metadata = thing.getMetadata();
		for (int i = 0; i < properties.size(); i++) {
			String property = (String) properties.get(i);
			ArrayList values = (ArrayList) metadata.get(property);
			if (values == null) continue;
			for (int j = 0; j < values.size(); j++) {
				String value = (String) values.get(j);
				if (i == 0) {
					result.append("<span>");
					result.append(getLabel(property).toUpperCase()).append(": ");
					result.append("</span>");
					result.append("<a href=\"").append(subject).append("\" target=\"_blank\">").append(value).append("</a>\n");
					result.append("\n");
				} else if (value != null) {
					result.append("<p>");
					result.append("<span>");
					result.append(getLabel(property).toUpperCase()).append(": ");
					result.append("</span>");
					result.append(value);
					result.append("</p>\n");
				}
			}
		}
		result.append("\t</li>\n");
		result.append("</ul>\n");

		return result.toString();
	}

	private String getLabel(String uri) {
		String result = "";
		int i1 = uri.lastIndexOf("#");
		int i2 = uri.lastIndexOf("/");
		if (i1 < 0) {
			result = uri.substring(i2 + 1);
		} else {
			result = uri.substring(i1 + 1);
		}
		return result;
	}

	private String toRss(Channel channel, RDFThing rdfThing) {
		String result = "";
		Model rdf = ModelFactory.createDefaultModel();

		rdf.setNsPrefix("rss", "http://purl.org/rss/1.0/");
		rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
		rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
		Resource rssChannel = rdf.createResource(RSS.channel);
		rssChannel.addProperty(RSS.title, channel.getTitle());
		rssChannel.addProperty(RSS.link, channel.getFeedUrl() != null ? channel.getFeedUrl() : "http://testChannel.com");
		rssChannel.addProperty(RSS.description, channel.getDescription());

		Resource item = rdf.createResource(rdfThing.getExt_id(), ResourceFactory.createResource(rdfThing.getType()));

		Iterator iter = rdfThing.getMetadata().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry pairs2 = (Map.Entry) iter.next();
			String pred = (String) pairs2.getKey();
			ArrayList values = (ArrayList) pairs2.getValue();
			for (int i = 0; i < values.size(); i++) {
				item.addProperty(ResourceFactory.createProperty(pred), (String) values.get(i));
			}
			//item.addProperty(ResourceFactory.createProperty((String) pairs2.getKey()), (String) pairs2.getValue());
		}
		StringWriter out = new StringWriter();
		RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
		writer.write(rdf, new BufferedWriter(out), null);
		result = out.toString();

		return result;

	}

}

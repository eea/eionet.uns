package com.eurodyn.uns.service.daemons.notificator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;

import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.util.common.WDSLogger;

public class PrepareText {
	
	private static final WDSLogger logger = WDSLogger.getLogger(PrepareText.class);
	
	private static String title_pred1 = "http://purl.org/rss/1.0/title";
	private static String title_pred2 = "http://purl.org/dc/elements/1.1/title";
	
	
	public static HashMap prepare(NotificationTemplate template, Event event, Subscription subscription, String homeUrl) throws Exception {
		HashMap ret = new HashMap();
		try{
			String event_title = "";
			
			Map event_md = event.getEventMetadata();
			for(Iterator it = event_md.keySet().iterator(); it.hasNext();){
				String key = (String)it.next();
				EventMetadata em = (EventMetadata)event_md.get(key);
				String property = em.getProperty();
				String val = em.getValue();
				if(property != null && (property.equals(title_pred1) || property.equals(title_pred2)))
					event_title = val;
			}
			String subj = template.getSubject();
			Date creation_date = event.getCreationDate();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			String ev_creation_date = formatter.format(creation_date);
			
			String plain_text = template.getPlainText();
			String html_text = null;
			if(subscription.getUser().getPreferHtml().booleanValue())
				html_text = template.getHtmlText();
			Map content = new HashMap();
			content.put("plain", plain_text);
			content.put("html", html_text);
			
			for(Iterator it = content.keySet().iterator(); it.hasNext(); ){
				String key = (String) it.next();
				String text = (String)content.get(key);
				
				boolean isHtml = key.equals("html");
				if(text != null){
					text = text.replaceAll("\\$EVENT.DATE", ev_creation_date);
					text = text.replaceAll("\\$USER", subscription.getUser().getFullName());
					text = text.replaceAll("\\$EVENT.TITLE", event_title);
					text = text.replaceAll("\\$EVENT.CHANNEL", subscription.getChannel().getTitle());
					if(text.contains("$UNSUBSCRIBE_LINK") || text.contains("$UNSUSCRIBE_LINK")){
						String unsub_link = homeUrl+"/subscriptions/unsubscribe.jsf?subsc="+subscription.getSecondaryId();
						if(isHtml)
							unsub_link = "<a href=\""+unsub_link+"\">"+unsub_link+"</a>";
						text = text.replaceAll("\\$UNSUBSCRIBE_LINK", unsub_link);
						text = text.replaceAll("\\$UNSUSCRIBE_LINK", unsub_link);
					}
					String nl = isHtml ? "<br/>" : "\n";
					StringBuffer event_body = new StringBuffer();
					for(Iterator event_it = event_md.keySet().iterator(); event_it.hasNext();){
						String event_key = (String)event_it.next();
						EventMetadata em = (EventMetadata)event_md.get(event_key);
						String prop = em.getProperty();
						String val = em.getValue();
						if(val == null){
							val = "";
						}
						
						event_body.append(getLocalName(prop)).append(": ");
						if(isHtml){
							val = val.replaceAll("\n", "<br/>");
							if(val.matches("https?://")){
								val = "<a href=\""+val+"\">"+val+"</a>";
							}
						}
						event_body.append(val).append(nl);
					}
					text = text.replaceAll("\\$EVENT", event_body.toString());
					content.put(key, text);
				}
			}
			
			subj = subj.replaceAll("\\$EVENT.CHANNEL", subscription.getChannel().getTitle());
			subj = subj.replaceAll("\\$EVENT.TITLE", event_title);
			subj = subj.replaceAll("\\$USER", subscription.getUser().getFullName());
			subj = subj.replaceAll("\\$EVENT.DATE", ev_creation_date);
			
			PyStringMap user = new PyStringMap();
			user.__setitem__("fullName", new PyString(subscription.getUser().getFullName()));
			user.__setitem__("externalId", new PyString(subscription.getUser().getExternalId()));
			
			PyStringMap channel = new PyStringMap();
			channel.__setitem__("title", new PyString(subscription.getChannel().getTitle()));
			
			PyStringMap pysubscription = new PyStringMap();
			pysubscription.__setitem__("channel", channel);
			pysubscription.__setitem__("user", user);
			
			PyStringMap event_metadata = new PyStringMap();
			PyStringMap metadata_dict = new PyStringMap();
			PyList metadata_list = new PyList();
			
			Map multipleMap = new HashMap();
			
			for(Iterator it = event_md.keySet().iterator(); it.hasNext();){
				String key = (String)it.next();
				EventMetadata em = (EventMetadata)event_md.get(key);
				String property = em.getProperty();
				String val = em.getValue();
				if(val == null){
					val = "";
				}
				
				event_metadata.__setitem__(property, new PyString(val));
				PyObject dict_val = metadata_dict.get(new PyString(property), new PyString());
				String dval = dict_val.toString() + val;
				if(property != null && isMultiple(property, event_md)){
					PyList list = new PyList();
					if(multipleMap.containsKey(property)){
						list = (PyList) multipleMap.get(property);
					}
					if(list != null){
						list.add(new PyString(dval));
						multipleMap.put(property, list);
					}
				} else {
					metadata_dict.__setitem__(property, new PyString(dval));
				}
				metadata_list.add(new PyString(dval));
			}
			for(Iterator it = multipleMap.keySet().iterator(); it.hasNext();){
				String key = (String)it.next();
				PyList list = (PyList)multipleMap.get(key);
				metadata_dict.__setitem__(key, list);
			}
			
			PyStringMap pyevent = new PyStringMap();
			pyevent.__setitem__("date", new PyString(ev_creation_date));
			pyevent.__setitem__("title", new PyString(event_title));
			pyevent.__setitem__("metadata", event_metadata);
			
			PyStringMap templ_namespace = new PyStringMap();
			templ_namespace.__setitem__("subscription", pysubscription);
			templ_namespace.__setitem__("event", pyevent);
			templ_namespace.__setitem__("metadata_dict", metadata_dict);
			templ_namespace.__setitem__("metadata_list", metadata_list);
			
			String plain = (String)content.get("plain");
			if(plain != null){
				plain = RenderText.executeTemplate(plain, templ_namespace, false);
				ret.put("plain", plain);
			}
			
			String html = (String)content.get("html");
			if(html != null){
				html = RenderText.executeTemplate(html, templ_namespace, true);
				ret.put("html", html);
			}
			
			ret.put("subj", subj);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception("Error occured when prepearing notification text: "+e.toString());
		}
		return ret;
	}
	
	private static boolean isMultiple(String predicate, Map event_md){
		int i = 0;
		for(Iterator it = event_md.keySet().iterator(); it.hasNext();){
			String key = (String)it.next();
			EventMetadata em = (EventMetadata)event_md.get(key);
			String property = em.getProperty();
			if(property != null && property.equals(predicate)){
				i = i + 1;
			}
			if(i > 1){
				return true;
			}
		}
		return false;
	}
	
	private static String getLocalName(String predicate){
		String ret = "";
		String[] temp = predicate.split("#");
		if(temp.length < 2){
			temp = predicate.split("/");
			ret = temp[temp.length - 1];
		} else {
			ret = temp[1];
		}
		return ret;
	}
	
}

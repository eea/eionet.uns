package com.eurodyn.uns.web.jsf.admin.templates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Stylesheet;
import com.eurodyn.uns.service.delegates.ChannelServerDelegate;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.XslFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.xml.IXmlCtx;
import com.eurodyn.uns.util.xml.XmlContext;
import com.eurodyn.uns.util.xml.XmlException;

public class DashTemplateActions extends DashTemplateForm {


	private static final WDSLogger logger = WDSLogger.getLogger(DashTemplateActions.class);
	
	public DashTemplateActions() {
		try {
			xslFacade = XslFacade.getInstance();
			channelFacade = new ChannelFacade();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
	}

	
	public String edit() {
		if(reset){
			stylesheet = new Stylesheet();
			reset = false;
		}
		return "editDashTemplate";
	}
	

	public String prepareTest() {

		try {
			testChannels = (List) channelFacade.getChannels().get("list");
			setId(null);
			testChannel = new Channel();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return "testChannelsList";
	}
	
	
	
	public String upload() {
		try {
			if (checkXML(upFile))
				stylesheet.setContent(new String(upFile.getBytes(), "UTF8"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return null;
	}

	public String save() {
				
		try {
			if (stylesheet.getId() == null){
				xslFacade.createStylesheet(stylesheet);	
				addInfoMessage(null, "messages.stylesheet.success.create", new Object[] { stylesheet.getName() });					
			}
			else{
				xslFacade.updateStylesheet(stylesheet);
				addInfoMessage(null, "messages.stylesheet.success.update", new Object[] { stylesheet.getName() });
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		
		
		return "dashTemplates";
	}

	public String remove() {
		try {
			xslFacade.deleteStylesheetl(stylesheet);
			addInfoMessage(null, "messages.stylesheet.success.delete", new Object[] { stylesheet.getName() });
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

		return "dashTemplates";
	}


	public String download() {
		try {

		
			HttpServletResponse response = getResponse();
			response.setHeader("Content-Disposition", "atachement; filename=\"" + stylesheet.getName() + "\"");
			response.setHeader("Content-Length", "" + stylesheet.getContent().getBytes().length);
			response.setContentType("text/xsl");
			PrintWriter out;

			out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
			out.print(stylesheet.getContent());
			out.flush();
			out.close();
			getFacesContext().responseComplete();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}
		return null;

	}


	public String testChannel() {

		try {
			if (testChannel.getFeedUrl().equals(""))
				testChannel = channelFacade.getChannel(getId());
			else
				testChannel.setMode("PULL");
			
			testChannel.setTransformation(stylesheet);
			testChannel.setContent(null);
			String result = ChannelServerDelegate.instance.testNewChannel(testChannel);
			if (result != null && result.length() > 12) {
				if (result.indexOf("</svg>") > 0) {
					result = "<div style=\"overflow:auto; width: 100%; height:180px\">";
					result += "<img src=\"../svg\" alt=\"Generated SVG\" />";
					result += "</div>";
				}
			} else {
				result = "<p class=\"nocontent\">CONTENT IS NOT AVAILABLE !</p>";
			}
			testChannel.setContent(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addSystemErrorMessage();
		}

		return "testStylesheet";
	}

	private boolean checkXML(UploadedFile file) throws IOException {
		boolean valid = true;
		
		try {
			IXmlCtx x = new XmlContext();
			x.setWellFormednessChecking();
			x.checkFromInputStream(new ByteArrayInputStream(file.getBytes()));
		} catch (XmlException e) {
			addErrorMessage(null,"errors.xsl",null);
			valid = false;
		}
		
		return valid;
	}

	
	public void changeAfterTest(ActionEvent event){
		afterTest="editDashTemplate";
	}

	
}

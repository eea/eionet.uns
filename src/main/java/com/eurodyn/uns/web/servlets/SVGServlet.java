package com.eurodyn.uns.web.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.xml.sax.InputSource;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.Stylesheet;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.EEAChannelServer;
import com.eurodyn.uns.service.channelserver.feed.PullHandler;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.service.facades.XslFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.xml.XSLTransformer;

public class SVGServlet extends HttpServlet {

	private static final WDSLogger logger = WDSLogger.getLogger(SVGServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ByteArrayInputStream aaa = null;
		try {
			System.out.println("Test");
			BaseChannelServer cs = new EEAChannelServer();
			String idStr = request.getParameter("subs_id");
			String cc = null;
			if (idStr != null) {
				// Existing channel
				SubscriptionFacade subscriptionFacade = new SubscriptionFacade();
				Integer subscriptionId = Integer.valueOf(idStr);
				if (subscriptionId.intValue() == 0){
					ChannelFacade cf = new ChannelFacade();
					Channel ch = cf.getChannel(new Integer(1));
					PullHandler pullHandler = new PullHandler(null);
					Dto dto = new Dto();
					pullHandler.pull(ch,dto);
					cc = dto.get("CONTENT").toString();

				}else{
					Subscription subsc = subscriptionFacade.getSubscription(subscriptionId);
					if (subsc == null) {
						logger.error("SVG2JPG: Invalid Subscription ID");
						return;
					}
					cc = cs.getChannelContent(subsc,true);

				}
				
//				System.out.println("==================");
//				System.out.println(cc);
//				System.out.println("==================");
				aaa = new ByteArrayInputStream(cc.getBytes());
			} else {
				// Non existing channel TEST function
				Channel channel = (Channel) request.getSession().getAttribute("admin.channels.testchannel");
				XSLTransformer transform = new XSLTransformer();
				String url = "http://rod.eionet.eu.int/events.rss";
				InputSource source = new InputSource(url);
				source.setSystemId(url);
				source.setEncoding("UTF-8");

				Map parameters = new HashMap();
				parameters.put("openinpopup", "true");
				parameters.put("showdescription", "true");
				parameters.put("showtitle", "true");

				Stylesheet xsl = XslFacade.getInstance().getStylesheet(channel.getTransformation().getId());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				transform.transform(xsl.getName(), xsl.getContent(), source, baos, parameters);
				aaa = new ByteArrayInputStream(baos.toByteArray());
			}

			response.setContentType("image/jpeg");			
			JPEGTranscoder t = new JPEGTranscoder();
			t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
			TranscoderInput input = new TranscoderInput(aaa);
			ServletOutputStream ostream = response.getOutputStream();
			TranscoderOutput output = new TranscoderOutput(ostream);
			t.transcode(input, output);
			ostream.flush();
			ostream.close();

		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}
}

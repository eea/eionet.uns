package com.eurodyn.uns.web.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.EEAChannelServer;
import com.eurodyn.uns.service.channelserver.feed.DatabaseHandler;
import com.eurodyn.uns.service.channelserver.feed.PullHandler;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.util.common.WDSLogger;

public class SVGServlet extends HttpServlet {

    private static final long serialVersionUID = 3017627366103614298L;

    private static final WDSLogger logger = WDSLogger.getLogger(SVGServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        ByteArrayInputStream aaa = null;
        String cc = null;
        try {
            BaseChannelServer cs = new EEAChannelServer();
            String idStr = request.getParameter("subs_id");
            if (idStr != null) {
                // Existing channel
                SubscriptionFacade subscriptionFacade = new SubscriptionFacade();
                Integer subscriptionId = Integer.valueOf(idStr);
                if (subscriptionId.intValue() == 0) {
                    ChannelFacade cf = new ChannelFacade();
                    Channel ch = cf.getChannel(new Integer(1));
                    PullHandler pullHandler = new PullHandler(null);
                    Dto dto = new Dto();
                    pullHandler.pull(ch, dto);
                    cc = dto.get("CONTENT").toString();

                } else {
                    Subscription subsc = subscriptionFacade.getSubscription(subscriptionId);
                    if (subsc == null) {
                        logger.error("SVG2JPG: Invalid Subscription ID");
                        return;
                    }
                    cc = cs.getChannelContent(subsc, true);

                }

                aaa = new ByteArrayInputStream(cc.getBytes());
            } else {
                // Non existing channel TEST function
                Channel channel = (Channel) request.getSession().getAttribute("testChannel");
                DatabaseHandler databaseHandler = new DatabaseHandler(null);
                Dto dto = new Dto();
                dto.put("channel", channel);
                databaseHandler.handleRequest(dto, BaseChannelServer.DATABASE);
                cc = dto.get("CONTENT").toString();

                if ((cc == null || cc.length() == 0) && channel.getFeedUrl() != null) {
                    PullHandler pullHandler = new PullHandler(null);
                    dto = new Dto();
                    pullHandler.pull(channel, dto);
                    cc = dto.get("CONTENT").toString();
                }
                aaa = new ByteArrayInputStream(cc.getBytes());

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

package com.eurodyn.uns.service.channelserver.feed;


import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.util.rdf.RssChannelsProcessor;
import com.eurodyn.uns.util.rdf.RdfContext;

public class QueryHandler extends BaseFeedHandler {
    
    private BaseFeedHandler successor;
    
    public QueryHandler() {
    }


    public QueryHandler(BaseFeedHandler successor) {
        this.successor = successor;
    }

    public void handleRequest(Dto request, short action) throws Exception {
        if (action==BaseChannelServer.QUERY) {
            collectDetails(request);
        } else if (successor != null) {
            successor.handleRequest(request, action);
        }
    }

    public void collectDetails(Dto request) throws Exception {
        Channel channel = (Channel) request.get("channel");
        RdfContext rdfctx=new RdfContext(channel);
        request.put("PROPERTIES", rdfctx.getData(new RssChannelsProcessor()));
    }

}

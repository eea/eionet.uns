package com.eurodyn.uns.web.jsf.admin.channels;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.MetadataElement;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.MetadataElementFacade;
import com.eurodyn.uns.web.jsf.BaseBean;

public abstract class BaseChannelBean extends BaseBean {

    protected MetadataElementFacade metadataElementFacade;
    
    protected EventMetadataFacade eventMetadataFacade;
    
    protected ChannelFacade channelFacade;
    
    protected Channel channel;
    
    protected List channelMetadataElements;
    
    public BaseChannelBean() {}
    
    
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }


    public String getSubMenu() {
        return channel.getMode().equals("PULL") ? "pullChannels" : "pushChannels";
    }

    public List getChannelMetadataElements() {
        return channelMetadataElements;
    }

    public void setChannelMetadataElements(List channelMetadataElements) {
        this.channelMetadataElements = channelMetadataElements;
    }
    
    protected void setUpChannelMetadataElements(Set properties){
        
        boolean updatedChannel = false;
        List channelMetadataElements = channel.getMetadataElements();

        for (Iterator iter = properties.iterator(); iter.hasNext();) {
            String metadataElementName = (String) iter.next();
            
            boolean hasMetadataElement = false;
            for (Iterator iter1 = channelMetadataElements.iterator(); iter1.hasNext();) {
                ChannelMetadataElement cme = (ChannelMetadataElement) iter1.next();
                if (cme.getMetadataElement().getName().equals(metadataElementName)) {
                    hasMetadataElement = true;
                    break;
                }
            }
            if (!hasMetadataElement) {
                MetadataElement me = metadataElementFacade.findByName(metadataElementName);
                if (me == null) {
                    me = new MetadataElement();
                    me.setName(metadataElementName);
                    metadataElementFacade.createMetadataElement(me);
                    me = metadataElementFacade.findByName(metadataElementName);
                }

                ChannelMetadataElement cme = new ChannelMetadataElement();
                cme.setMetadataElement(me);
                cme.setVisible(Boolean.TRUE);
                cme.setAppearanceOrder(new Integer(100));
                cme.setObsolete(Boolean.FALSE);
                cme.setFiltered(Boolean.FALSE);
                channelMetadataElements.add(cme);
                updatedChannel = true;
            }
        }
        channel.setMetadataElements(channelMetadataElements);
        if (channel.getId().intValue() != -1 && updatedChannel){
            channelFacade.updateChannel(channel);
        }
        
    }
    

}

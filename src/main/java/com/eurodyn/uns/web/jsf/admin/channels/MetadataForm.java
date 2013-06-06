package com.eurodyn.uns.web.jsf.admin.channels;

import java.util.List;

import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.MetadataElementFacade;
import com.eurodyn.uns.web.jsf.SortableTable;

public abstract class MetadataForm extends BaseChannelBean {

    protected String searchValue;

    protected String searchMetadataElement;

    protected SortableTable st = new SortableTable("metadataElement.localName");
    protected SortableTable st1 = new SortableTable("value");

    public SortableTable getSt1() {
        return st1;
    }
    

    
    protected ChannelMetadataElement channelMetadataElement;

    protected EventMetadata eventMetadataValue;

    protected List metadataElementValues;

    protected void initForm() {
        channelFacade = new ChannelFacade();
        metadataElementFacade = new MetadataElementFacade();
        eventMetadataFacade = new EventMetadataFacade();
    }

    public SortableTable getSt() {
        return st;
    }
    

    public List getChannelMetadataElements() {
        return st.sort(channelMetadataElements);
    }

    
    public List getMetadataElementValues() {
        return st1.sort(metadataElementValues);
    }

    public void setMetadataElementValues(List metadataElementValues) {
        if (this.metadataElementValues == null)
            this.metadataElementValues = metadataElementValues;
    }
    
    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public void setSearchMetadataElement(String searchMetadataElement) {
        this.searchMetadataElement = searchMetadataElement;
    }

    public String getSearchMetadataElement() {
        return searchMetadataElement;
    }
    
    public void setEventMetadataValue(EventMetadata eventMetadataValue) {
        this.eventMetadataValue = eventMetadataValue;
    }

    
    public ChannelMetadataElement getChannelMetadataElement() {
        return channelMetadataElement;
    }
    
    public void setChannelMetadataElement(ChannelMetadataElement channelMetadataElement) {
        this.channelMetadataElement = channelMetadataElement;
    }


    public List getChannelMetadataElementsItems() {
        return toSelectItems(channel,"metadataElements", "metadataElement.name", "metadataElement.localName",false);

    }

    
}

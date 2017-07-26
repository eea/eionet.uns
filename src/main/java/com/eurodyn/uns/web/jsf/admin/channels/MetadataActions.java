package com.eurodyn.uns.web.jsf.admin.channels;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataActions extends MetadataForm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelActions.class);

    public MetadataActions() {

        try {
            initForm();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

    }

    public String prepareMetadataElements() {
        try {
            channel = channelFacade.getChannel(channel.getId());
            setUpChannelMetadataElements(eventMetadataFacade.findChannelProperties(channel));
            channelMetadataElements = channel.getMetadataElements();
            metadataElementValues = new ArrayList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return "metadataElements";
    }

    public String removeChannelMetadataElement() {
        try {
            String elementName = channelMetadataElement.getMetadataElement().getName();
            eventMetadataFacade.deleteEventMetadataByProperty(channel, elementName);
            channel.getMetadataElements().remove(channelMetadataElement);
            channelFacade.updateChannel(channel);
            metadataElementValues = null;
            eventMetadataFacade.deleteFilterStatement(channel,new Statement(elementName,null));
            addInfoMessage(null, "messages.metadata.element.success.delete", new Object[] { elementName,channel.getTitle() });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return "metadataElements";
    }

    public String searchMetadataElementValues() {
        try {
            if(!reset){ // reset is callad only for sorting purpose
                ResultDto rDto = eventMetadataFacade.findEventMetadataWithValue(channel, searchMetadataElement, searchValue);
                if (rDto.isFailure())
                    addErrorMessage(null, "message.searchMetadata.tooManyValues", new Object[] { rDto.get("limit").toString() });
                metadataElementValues = (List) rDto.get("list");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return "metadataElements";
    }

    public String removeEventMetadataValue() {
        try {
            String value = eventMetadataValue.getValue();
            metadataElementValues.remove(eventMetadataValue);
            eventMetadataFacade.deleteEventMetadataByValue(channel, value);
            eventMetadataFacade.deleteFilterStatement(channel,new Statement(null,value));
            addInfoMessage(null, "messages.metadata.value.success.delete", new Object[] { value,channel.getTitle() });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return "metadataElements";
    }
        
}

package com.eurodyn.uns.web.jsf.admin.templates;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.web.jsf.BaseBean;

public class NotificationTemplateForm extends BaseBean {

    protected NotificationTemplateFacade notificationTemplateFacade = null;

    protected ChannelFacade channelFacade = null;
    
    protected EventMetadataFacade eventMetadataFacade;

    protected List testEventsList ;
    
    protected NotificationTemplate notificationTemplate;
    
    protected String resultText;

    protected String resultHtml;

    protected Event testEvent;
    
    public NotificationTemplateForm() {
    }


    public NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }
    
    public void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    
    protected String getLocalName(String name) {
        return name.substring(name.lastIndexOf("/") + 1);
    }
    
    

    public String getResultText() {
        return resultText;
    }


    public String getResultHtml() {
        return resultHtml;
    }

    
    public List getTestEventsItems() {
        return toSelectItems(testEventsList, "id", "channel.title");
    }
    
    protected String afterTest = "notificationTemplates";

    public String afterTest(){
        return afterTest;
    }

    public void changeAfterTest(ActionEvent event){
        afterTest="notificationTemplate";
    }
    

}

package com.eurodyn.uns.web.jsf.admin.templates;

import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Stylesheet;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.XslFacade;
import com.eurodyn.uns.web.jsf.BaseBean;

public class DashTemplateForm extends BaseBean {

    protected XslFacade xslFacade;

    protected UploadedFile upFile;

    protected Channel testChannel;

    protected Stylesheet stylesheet;

    protected ChannelFacade channelFacade;
    
    protected List testChannels;

    protected String afterTest = "dashTemplates";
    
    public DashTemplateForm() {
    }


    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }

    public UploadedFile getUpFile() {
        return upFile;
    }

    public void setUpFile(UploadedFile file) {
        this.upFile = file;
    }

    public List getTestChannelsItems() {
        return toSelectItems(testChannels, "id", "title");
    }

    public Channel getTestChannel() {
        return testChannel;
    }

    public void setTestChannel(Channel testChannel) {
        this.testChannel = testChannel;
    }


    public String afterTest(){
        return afterTest;
    }
    

    
}

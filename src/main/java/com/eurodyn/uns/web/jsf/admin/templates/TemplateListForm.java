package com.eurodyn.uns.web.jsf.admin.templates;

import java.util.List;

import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.service.facades.XslFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.eurodyn.uns.web.jsf.SortableTable;

public class TemplateListForm extends BaseBean {

    private static final WDSLogger logger = WDSLogger.getLogger(TemplateListForm.class);

    private XslFacade xslFacade;

    private NotificationTemplateFacade notificationTemplateFacade;

    private List notificationTemplates;

    private List stylesheets;

    public TemplateListForm() {
        st = new SortableTable("name");
        xslFacade = new XslFacade();
        notificationTemplateFacade = new NotificationTemplateFacade();
    }

    public boolean isPreparedNotificationTemplates() {
        try {
            if (isRenderPhase()) {
                if (notificationTemplates == null || reset) {
                    Dto dto = new Dto();
                    String order = st.isAscending() ? "asc" : "desc";
                    dto.put("orderProperty", st.getSort());
                    dto.put("order", order);
                    // getExternalContext().getSessionMap().remove("notificationTemplateBean");
                    reset = false;
                    notificationTemplates = (List) notificationTemplateFacade.getNotificationTemplates(dto).get("list");
                }
                st.sort(notificationTemplates);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

    public boolean isPreparedStylesheets() {
        try {
            if (isRenderPhase()) {
                if (stylesheets == null || reset) {
                    Dto dto = new Dto();
                    String order = st.isAscending() ? "asc" : "desc";
                    dto.put("orderProperty", st.getSort());
                    dto.put("order", order);
                    // getExternalContext().getSessionMap().remove("dashTemplate");
                    stylesheets = (List) xslFacade.getStylesheets(dto).get("list");
                    reset = false;
                }
                st.sort(stylesheets);

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

    public List getNotificationTemplates() {        
        return notificationTemplates;
    }

    public void setNotificationTemplates(List notificationTemplates) {
        this.notificationTemplates = notificationTemplates;
    }

    public List getStylesheets() {      
        return stylesheets;
    }

    public void setStylesheets(List stylesheets) {
        this.stylesheets = stylesheets;
    }

}

package com.eurodyn.uns.web.jsf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;

import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseBean.class);

    protected static final Integer EMAIL = new Integer(1);

    protected static final Integer JABBER = new Integer(2);
    
    protected static final Integer WDB = new Integer(3);

    protected static final Integer RSSFEED = new Integer(4);


    protected static Map deliveryTypesMap;

    static {
        try {

            deliveryTypesMap = new HashMap(4);
            deliveryTypesMap.put("EMAIL", new DeliveryType(EMAIL, "EMAIL"));
            deliveryTypesMap.put("JABBER", new DeliveryType(JABBER, "JABBER"));
            deliveryTypesMap.put("RSS", new DeliveryType(RSSFEED, "RSS"));
            deliveryTypesMap.put("WDB", new DeliveryType(WDB, "WDB"));

            FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("EMAIL", EMAIL);
            FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("JABBER", JABBER);
            FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("RSS", RSSFEED);
            FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("WDB", WDB);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    protected SortableTable st;

    public SortableTable getSt() {
        return st;
    }

    protected boolean reset = false;

    public void reset(ActionEvent event) {
        reset = true;
    }

    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    protected Application getApplication() {
        return FacesContext.getCurrentInstance().getApplication();
    }

    protected ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    protected HttpSession getSession() {
        return (HttpSession) getExternalContext().getSession(true);
    }

    protected HttpServletResponse getResponse() {
        return (HttpServletResponse) getExternalContext().getResponse();
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) getExternalContext().getRequest();
    }

    protected String getParameter(String name) {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getParameter(name);
    }

    protected Object getRequestAttribute(String name) {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getAttribute(name);
    }

    protected void redirect(String path) {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        try {
            response.sendRedirect(path);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    protected void redirectToDashboard() {
        redirect(getRequest().getContextPath() + "/dash/" + getUser().getExternalId() + "/dashboard.jsf");
    }

    protected void redirectToRssReader() {
        redirect(getRequest().getContextPath() + "/rss/" + getUser().getExternalId() + "/rssReader.jsf");
    }

    protected void forward(String path) {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        try {
            FacesContext.getCurrentInstance().responseComplete();
            request.getRequestDispatcher(path).forward(request, response);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String substituteParams(Locale locale, String msgtext, Object params[]) {
        String localizedStr = null;

        if (params == null || msgtext == null) {
            return msgtext;
        }
        StringBuffer b = new StringBuffer(100);
        MessageFormat mf = new MessageFormat(msgtext);
        if (locale != null) {
            mf.setLocale(locale);
            b.append(mf.format(params));
            localizedStr = b.toString();
        }
        return localizedStr;
    }

    protected String getMessage(String messageId, Object params[]) {
        String message = "";
        ResourceBundle bundle = getBundle();
        try {
            message = bundle.getString(messageId);
        } catch (MissingResourceException e) {
        }

        if (params != null) {
            message = substituteParams(bundle.getLocale(), message, params);
        }
        return message;

    }

    protected ResourceBundle getBundle() {

        Locale locale = null;
        FacesContext context = FacesContext.getCurrentInstance();
        // context.getViewRoot() may not have been initialized at this
        // point.
        if (context != null && context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
            if (locale == null) {
                locale = Locale.getDefault();
            }
        } else {
            locale = Locale.getDefault();
        }

        String bundleName = context.getApplication().getMessageBundle();
        return ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName));
    }

    protected static ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = fallbackClass.getClass().getClassLoader();
        }
        return loader;
    }

    protected void addInfoMessage(String clientId, String msg, Object[] params) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, getMessage(msg, params), null));
    }

    protected void addWarningMessage(String clientId, String msg, Object[] params) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, getMessage(msg, params), null));
    }

    protected void addErrorMessage(String clientId, String msg, Object[] params) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, getMessage(msg, params), null));
    }

    protected void addSystemErrorMessage() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "System error", null));
    }

    protected void addErrorMessagePlain(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    protected String encodeBase64(String value) {
        try {
            byte[] bytes = Base64.encodeBase64(value.getBytes());
            value = new String(bytes);
            value = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    protected String decodeBase64(String value) {
        try {
            value = URLDecoder.decode(value, "UTF-8");
            value = new String(Base64.decodeBase64(value.getBytes()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        return value;
    }

    protected List toSelectItems(Object object, String collectionProperty, String fieldValue, String fieldLabel, boolean localize) {

        List result = null;
        try {
            Collection list = (Collection) PropertyUtils.getProperty(object, collectionProperty);
            result = toSelectItems(list, fieldValue, fieldLabel, localize);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
            result = new ArrayList(0);
        }
        return result;
    }

    protected List toSelectItems(Collection list, String fieldValue, String fieldLabel) {
        return toSelectItems(list, fieldValue, fieldLabel, false);
    }

    protected List toSelectItems(Collection list, String fieldValue, String fieldLabel, boolean localize) {
        List values = new ArrayList();
        if (!(list != null && list.size() > 0))
            return values;

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            String label = null;
            String value = null;
            try {
                label = BeanUtils.getProperty(object, fieldLabel);
                if (localize) {
                    label = getMessage(label, null);
                }
                if (fieldValue != null)
                    value = BeanUtils.getProperty(object, fieldValue);
            } catch (IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                LOGGER.error(e.getMessage(), e);
            }

            SelectItem selectItem = new SelectItem(fieldValue != null ? value : object, label);
            values.add(selectItem);
        }

        return values;
    }

    protected List toConvertedSelectItems(Object object, String collectionProperty, String fieldLabel, boolean localize) {

        List result = null;
        try {
            Collection list = (Collection) PropertyUtils.getProperty(object, collectionProperty);
            result = toSelectItems(list, null, fieldLabel, localize);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
            result = new ArrayList(0);
        }
        return result;
    }

    public static List toSelectItems(Collection list) {
        List values = new ArrayList();
        if (list == null)
            return values;

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            SelectItem selectItem = new SelectItem();
            selectItem.setLabel(object.toString());
            selectItem.setValue(object.toString());
            values.add(selectItem);
        }

        return values;
    }

    public static List toConditionalSelectItems(Collection list, String fieldValue, String fieldLabel, String conditionField, Boolean condition) {

        List values = new ArrayList();
        if (list == null)
            return values;

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            String label = null;
            String value = null;
            boolean addItem = false;
            try {
                label = BeanUtils.getProperty(object, fieldLabel);
                value = BeanUtils.getProperty(object, fieldValue);
                addItem = ((Boolean) PropertyUtils.getProperty(object, conditionField)).equals(condition);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            if (addItem) {
                SelectItem selectItem = new SelectItem();
                selectItem.setLabel(label);
                selectItem.setValue(value);
                values.add(selectItem);
            }

        }
        return values;
    }

    public User getUser() {
        return (User) getSession().getAttribute("user");
    }

    protected User getUser(boolean reload) {

        User rUser = null;
        User user = (User) getSession().getAttribute("user");

        if (reload) {
            UserFacade userFacade = new UserFacade();
            rUser = userFacade.getUser(user.getExternalId(), false);
            rUser.setLoggedIn(user.isLoggedIn());
            getSession().removeAttribute("user");
            getSession().setAttribute("user", rUser);
        }
        return rUser;
    }

    protected boolean isRenderPhase() {
        if (getExternalContext().getRequestMap().get(PhaseId.RENDER_RESPONSE) != null)
            return true;
        return false;
    }

}

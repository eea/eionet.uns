package com.eurodyn.uns.web.jsf;

import com.eurodyn.uns.util.common.WDSLogger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BreadCrumbBean extends BaseBean {

    private static Map breadcrumbsMap = new HashMap();

    private static final WDSLogger logger = WDSLogger.getLogger(BreadCrumbBean.class);

    Stack breadCrumbStack = new Stack();

    public String getBreadCumbs() {

        StringBuffer breadcrumbs = new StringBuffer();
        try {
            HttpServletRequest req = getRequest();
            int port = req.getServerPort();
            String contextPath = req.getScheme() + "://" + req.getServerName() + (port != 0 ? (":" + port) : "") + req.getContextPath(); // "http://localhost:8080/UNS";//request.getContextPath();
            String url = getFacesContext().getViewRoot().getViewId();
            String key = url.substring(url.lastIndexOf("/") + 1);
            String[] breadcrumb = (String[]) breadcrumbsMap.get(key);

            if (req.getMethod().equals("GET")) {
                breadCrumbStack.removeAllElements();
                breadCrumbStack.push(breadcrumbsMap.get("home.jsp"));
                if (breadcrumb[0] != null) {
                    breadCrumbStack.push(breadcrumbsMap.get(breadcrumb[0]));
                }
            } else {
                String[] stackBc = (String[]) breadCrumbStack.peek();
                if (stackBc[2] == null) {
                    breadCrumbStack.pop();
                }
            }

            if (!breadCrumbStack.contains(breadcrumb)) {
                breadCrumbStack.push(breadcrumb);
            }

            int breadCrumbsSize = breadCrumbStack.size();

            for (int i = 0; i < breadCrumbsSize; i++) {

                String[] bc = (String[]) breadCrumbStack.get(i);

                if (i + 1 < breadCrumbsSize) {
                    breadcrumbs.append(renderElement(bc, contextPath));
                } else
                    breadcrumbs.append(renderLastElement(bc[1]));

            }

        } catch (Exception e) {
            logger.error(e);
        }

        return breadcrumbs.toString();
    }

    private String renderElement(String[] breadcrumb, String contextPath) {
        return "<div class=\"breadcrumbitem\" ><a href=\"" + contextPath + breadcrumb[2] + "\" >" + breadcrumb[1] + "</a></div>";
    }

    private String renderLastElement(String value) {
        return "<div class=\"breadcrumbitemlast\">" + value + "</div>";
    }

    static {
        breadcrumbsMap.put("dashboard.jsp", new String[] {null, "Dashboards", null });
        breadcrumbsMap.put("rssReader.jsp", new String[] {null, "Rss reader", null });
        breadcrumbsMap.put("login.jsp", new String[] {null, "UNS", null });
        breadcrumbsMap.put("home.jsp", new String[] {null, "UNS", "/home.jsf" });

        // config
        breadcrumbsMap.put("config", new String[] {null, "Config", "/admin/config/general.jsf" });
        breadcrumbsMap.put("mail.jsp", new String[] {"config", "Mail administration", null });
        breadcrumbsMap.put("ldap.jsp", new String[] {"config", "Ldap administration", null });
        breadcrumbsMap.put("jabber.jsp", new String[] {"config", "Jabber administration", null });
        breadcrumbsMap.put("general.jsp", new String[] {"config", "General administration", null });
        breadcrumbsMap.put("database.jsp", new String[] {"config", "Database administration", null });

        // channels
        breadcrumbsMap.put("pullChannels.jsp", new String[] {null, "Pull channels", "/admin/channels/pullChannels.jsf" });
        breadcrumbsMap.put("pushChannels.jsp", new String[] {null, "Push channels", "/admin/channels/pushChannels.jsf" });
        breadcrumbsMap.put("channel.jsp", new String[] {null, "Edit channel", null });
        breadcrumbsMap.put("metadataElements.jsp", new String[] {null, "Edit channel", null });
        breadcrumbsMap.put("choosable.jsp", new String[] {null, "Edit channel", null });
        breadcrumbsMap.put("channelUrl.jsp", new String[] {null, "Edit channel", null });
        breadcrumbsMap.put("channelTemplates.jsp", new String[] {null, "Edit channel", null });
        breadcrumbsMap.put("channelPreview.jsp", new String[] {null, "Edit channel", null });
        breadcrumbsMap.put("subscribers.jsp", new String[] {null, "Subscribers", null });

        //cleandb
        breadcrumbsMap.put("cleandb.jsp", new String[] {null, "Clean database", null });

        // reports
        breadcrumbsMap.put("throughput_criteria.jsp", new String[] {null, "Generate report", "/admin/reports/throughput_criteria.jsf" });
        breadcrumbsMap.put("report_criteria.jsp", new String[] {null, "Generate report", "/admin/reports/report_criteria.jsf" });
        breadcrumbsMap.put("notification_throughput.jsp", new String[] {null, "Throughput of notifications", null });
        breadcrumbsMap.put("failed_notifications.jsp", new String[] {"report_criteria.jsp", "Failed notifications", null });
        breadcrumbsMap.put("notifications_report.jsp", new String[] {null , "Notifications report", null });

        // templates
        breadcrumbsMap.put("dashTemplates.jsp", new String[] {null, "Stylesheets", "/admin/templates/dashTemplates.jsf" });
        breadcrumbsMap.put("dashTemplate.jsp", new String[] {null, "Edit stylesheet", null});
        breadcrumbsMap.put("testStylesheet.jsp", new String[] {null, "Edit stylesheet", null});
        breadcrumbsMap.put("testChannelsList.jsp", new String[] {null, "Edit stylesheet", null});

        breadcrumbsMap.put("notificationTemplates.jsp", new String[] {null, "Notification Templates", "/admin/templates/notificationTemplates.jsf" });
        breadcrumbsMap.put("notificationTemplate.jsp", new String[] {null, "Edit notification Templates", null});
        breadcrumbsMap.put("testNotifChannelsList.jsp", new String[] {null, "Edit notification Templates", null});
        breadcrumbsMap.put("notificationTemplateTestResult.jsp", new String[] {null, "Edit notification Templates", null});

        // subscriptions
        breadcrumbsMap.put("subscriptions", new String[] {null, "Subscriptions", "/subscriptions/subscriptions.jsf" });
        breadcrumbsMap.put("subscriptions.jsp", new String[] {"subscriptions", "Your subscriptions", null });
        breadcrumbsMap.put("unsubscribe.jsp", new String[] {"subscriptions", "Unsubscribe", null });
        breadcrumbsMap.put("subscription.jsp", new String[] {"subscriptions", "Edit subscription", null });
        breadcrumbsMap.put("subscription.jsp", new String[] {"subscriptions", "Edit subscription", null });
        breadcrumbsMap.put("availableChannels.jsp", new String[] {"subscriptions", "Available channels", null });
        breadcrumbsMap.put("userPreferences.jsp", new String[] {"subscriptions", "Your preferences", null });

        // xmlrpc user
        breadcrumbsMap.put("rpcUserChannels.jsp", new String[] {null, "Your RPC channels", "/xmlrpc/rpcUserChannels.jsf" });
        breadcrumbsMap.put("rpcChannel.jsp", new String[] {null, "Edit channel", null});

    }
}

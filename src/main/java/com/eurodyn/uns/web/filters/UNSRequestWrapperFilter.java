package com.eurodyn.uns.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;

public class UNSRequestWrapperFilter implements Filter {

    public void init(FilterConfig arg0) throws ServletException {
    }

    /**
     * Provides SecuredServletRequest if session is available. Otherwise,
     * provides original HttpServletRequest.
     * 
     * @see SecuredServletRequest
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        // check if session is available

        final HttpServletRequest httpReq = (HttpServletRequest) req;
        final HttpSession session = httpReq.getSession(false);

        final HttpServletRequest newReq;
        if (session != null) {
            newReq = createSecuredServletRequest(httpReq, session);
        } else {
            newReq = httpReq;
        }

        chain.doFilter(newReq, res);
    }

    protected SecuredServletRequest createSecuredServletRequest(final HttpServletRequest httpReq, final HttpSession session) {

        return new SecuredServletRequest(httpReq, session);
    }

    public void destroy() {
        // do nothing
    }

    protected class SecuredServletRequest extends HttpServletRequestWrapper {

        HttpServletRequest request = null;

        String remoteUser = null;

        public SecuredServletRequest(HttpServletRequest request, HttpSession session) {
            super(request);
            this.request = request;
        }

        public String getRemoteUser() {
            String exernalId = request.getRemoteUser();
            if (exernalId == null) {
                HttpSession session = request.getSession();
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    if (user != null && user.isLoggedIn()) {
                        exernalId = user.getExternalId();
                    }
                }
            }
            return exernalId;
        }

        public boolean isUserInRole(String roleName) {
            String userName = getRemoteUser();
            if (userName != null && UserFacade.hasPerm(userName, "/" + roleName, "x")) {
                return true;
            } else {
                return false;
            }

        }

    }

}

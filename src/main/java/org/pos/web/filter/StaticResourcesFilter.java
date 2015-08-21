package org.pos.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * It is configured to serve resources from the blank.html
 * </p>
 */
public class StaticResourcesFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String requestURI = httpRequest.getRequestURI();
        requestURI = StringUtils.substringAfter(requestURI, contextPath);
        if (StringUtils.equals("/blank", requestURI)) {
            requestURI = "/blank.html";
        }
        request.getRequestDispatcher(requestURI).forward(request, response);
    }
}

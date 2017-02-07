/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.web;

import com.kattysoft.core.NoAccessRightsException;
import com.kattysoft.core.SokolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 12.01.2017
 */
public class ErrorFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ErrorFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            String message = "";
            int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            if (e instanceof SokolException) {
                message = e.getMessage();
            } else if (e instanceof NoAccessRightsException) {
                message = e.getMessage();
                status = HttpServletResponse.SC_FORBIDDEN;
            } else if (e.getCause() != null && e.getCause() instanceof SokolException) {
                message = e.getCause().getMessage();
            } else if (e.getCause() != null && e.getCause() instanceof NoAccessRightsException) {
                message = e.getCause().getMessage();
                status = HttpServletResponse.SC_FORBIDDEN;
            } else {
                throw e;
            }
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print("{\"error\": \"" + message.replaceAll("\"", "\\\"") + "\"}");
            ((HttpServletResponse) response).setStatus(status);
        }
    }

    @Override
    public void destroy() {

    }
}

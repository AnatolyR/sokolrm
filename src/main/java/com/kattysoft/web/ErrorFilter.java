/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kattysoft.web;

import com.kattysoft.core.NoAccessRightsException;
import com.kattysoft.core.SokolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
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
            log.error("Error processing rest call", e);
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print("{\"error\": \"" + message.replaceAll("\"", "\\\"") + "\"}");
            ((HttpServletResponse) response).setStatus(status);
        }
    }

    @Override
    public void destroy() {

    }
}

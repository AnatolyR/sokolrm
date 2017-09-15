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

import com.kattysoft.core.UserService;
import com.kattysoft.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 30.04.2016
 */
public class LoginFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        userService = applicationContext.getBean(UserService.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = ((HttpServletRequest) request);
        HttpServletResponse res = ((HttpServletResponse) response);

        res.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession ses = req.getSession();

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String path = (servletPath != null ? servletPath : "") + (pathInfo != null ? pathInfo : "");

        if ("post".equalsIgnoreCase(req.getMethod()) && path.endsWith("login")) {
            try {
                String login = req.getParameter("user");
                String password = req.getParameter("password");
                User user = authenticate(login, password);
                if (user != null) {
                    req.getSession().setAttribute("user", user);
                    res.getWriter().write("true");
                } else {
                    res.getWriter().write("false");
                }
            } catch (Exception e) {
                log.error("Can not authenticate", e);
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (path.equals("/login")) {
            req.getRequestDispatcher("login.html").forward(req, res);
        } else if (path.equals("/help")) {
            req.getRequestDispatcher("help.html").forward(req, res);
        } else if (path.endsWith("logout")) {
            req.getSession().invalidate();
            res.sendRedirect("login");
        } else if (ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
            userService.setCurrentUser(user);
            if (path.startsWith("/app/")) {
                log.info("[WEB][{}] {} {}", user.getLogin(), path.substring(5));
            }
            chain.doFilter(request, response);
            userService.setCurrentUser(null);
        } else if (path.startsWith("/js/") || path.startsWith("/css/")) {
            chain.doFilter(request, response);
        } else if (path.equals("/") || path.startsWith("/index.html")) {
            res.sendRedirect("login");
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }

    private User authenticate(String login, String password) {
        User user = userService.getUserByLoginAndPassword(login, password);
        return user;
    }

    @Override
    public void destroy() {

    }
}

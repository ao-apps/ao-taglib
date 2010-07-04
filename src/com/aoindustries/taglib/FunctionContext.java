/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FunctionContext implements Filter {

    private static final String INIT_ERROR_MESSAGE = "Function context not initialized.  Please install FunctionContext filter to web.xml";

    private static ThreadLocal<ServletContext> servletContextTL = new ThreadLocal<ServletContext>();
    private static ThreadLocal<HttpServletRequest> requestTL = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<HttpServletResponse> responseTL = new ThreadLocal<HttpServletResponse>();

    public static ServletContext getServletContext() {
        ServletContext servletContext = servletContextTL.get();
        if(servletContext==null) throw new IllegalStateException(INIT_ERROR_MESSAGE);
        return servletContext;
    }

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = requestTL.get();
        if(request==null) throw new IllegalStateException(INIT_ERROR_MESSAGE);
        return request;
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = responseTL.get();
        if(response==null) throw new IllegalStateException(INIT_ERROR_MESSAGE);
        return response;
    }

    private ServletContext filterServletContext;

    @Override
    public void init(FilterConfig config) throws ServletException {
        filterServletContext = config.getServletContext();
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        if(
            (request instanceof HttpServletRequest)
            && (response instanceof HttpServletResponse)
        ) {
            try {
                servletContextTL.set(filterServletContext);
                requestTL.set((HttpServletRequest)request);
                responseTL.set((HttpServletResponse)response);
                chain.doFilter(request, response);
            } finally {
                servletContextTL.remove();
                requestTL.remove();
                responseTL.remove();
            }
        } else {
            throw new ServletException("Not using HttpServletRequest and HttpServletResponse");
        }
    }
    
    @Override
    public void destroy() {
        filterServletContext = null;
    }
}

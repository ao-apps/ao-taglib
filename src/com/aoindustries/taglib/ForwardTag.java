/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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

import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.servlet.http.ServletUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class ForwardTag extends SimpleTagSupport implements ParamsAttribute {

    private String page;
    private boolean clearParams = false;
    private HttpParametersMap params;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public boolean getClearParams() {
        return clearParams;
    }

    public void setClearParams(boolean clearParams) {
        this.clearParams = clearParams;
    }

    @Override
    public HttpParameters getParams() {
        return params==null ? EmptyParameters.getInstance() : params;
    }

    @Override
    public void addParam(String name, String value) {
        if(params==null) params = new HttpParametersMap();
        params.addParameter(name, value);
    }

    @Override
    public void doTag() throws IOException, SkipPageException, JspException {
        JspFragment body = getJspBody();
        if(body!=null) body.invoke(null);
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        try {
            // Make relative to current JSP page
            String contextRelativePath = ServletUtil.getAbsolutePath(request, page);
            RequestDispatcher dispatcher = pageContext.getServletContext().getRequestDispatcher(contextRelativePath);
            final Map<String,String[]> parameters;
            if(clearParams) {
                if(params==null) {
                    // No parameters
                    parameters = Collections.emptyMap();
                } else {
                    // Only use new parameters
                    Map<String,List<String>> newMap = params.getParameterMap();
                    Map<String,String[]> newParameters = new LinkedHashMap<String,String[]>(newMap.size()*4/3+1);
                    for(Map.Entry<String,List<String>> entry : newMap.entrySet()) {
                        String name = entry.getKey();
                        List<String> newValues = entry.getValue();
                        newParameters.put(
                            name,
                            newValues.toArray(new String[newValues.size()])
                        );
                    }
                    parameters = Collections.unmodifiableMap(newParameters);
                }
            } else {
                if(params==null) {
                    // Only use old parameters
                    @SuppressWarnings("unchecked")
                    Map<String,String[]> oldMap = request.getParameterMap();
                    parameters = oldMap;
                } else {
                    // Merge all parameters
                    Map<String,List<String>> newMap = params.getParameterMap();
                    @SuppressWarnings("unchecked")
                    Map<String,String[]> oldMap = request.getParameterMap();
                    Map<String,String[]> newParameters = new LinkedHashMap<String,String[]>((newMap.size() + oldMap.size())*4/3+1);
                    for(Map.Entry<String,List<String>> entry : newMap.entrySet()) {
                        String name = entry.getKey();
                        List<String> newValues = entry.getValue();
                        String[] oldValues = oldMap.get(name);
                        String[] merged;
                        if(oldValues==null) {
                            // No need to merge values
                            merged = newValues.toArray(new String[newValues.size()]);
                        } else {
                            // Merge values into single String[]
                            merged = new String[newValues.size() + oldValues.length];
                            String[] result = newValues.toArray(merged);
                            assert merged==result;
                            System.arraycopy(oldValues, 0, merged, newValues.size(), oldValues.length);
                        }
                        newParameters.put(name, merged);
                    }
                    // Add any old parameters that were not merged
                    for(Map.Entry<String,String[]> entry : oldMap.entrySet()) {
                        String name = entry.getKey();
                        if(!newMap.containsKey(name)) newParameters.put(name, entry.getValue());
                    }
                    parameters = Collections.unmodifiableMap(newParameters);
                }
            }
            if(!clearParams) {
                // Add all request parameters
            }
            dispatcher.forward(
                new HttpServletRequestWrapper(request) {
                    @Override
                    public String getParameter(String name) {
                        String[] values = parameters.get(name);
                        return values==null || values.length==0 ? null : values[0];
                    }

                    @Override
                    public Map getParameterMap() {
                        return parameters;
                    }

                    @Override
                    public Enumeration getParameterNames() {
                        return Collections.enumeration(parameters.keySet());
                    }

                    @Override
                    public String[] getParameterValues(String name) {
                        return parameters.get(name);
                    }
                },
                response
            );
        } catch(ServletException e) {
            throw new JspException(e);
        }
        throw new SkipPageException();
    }
}

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * The parent implementation for both the forward and include tags.
 *
 * @author  AO Industries, Inc.
 */
abstract class DispatchTag extends SimpleTagSupport implements PageAttribute, ParamsAttribute {

    private String page;
    private boolean clearParams = false;
    private HttpParametersMap params;

    @Override
    public String getPage() {
        return page;
    }

    @Override
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
    public void doTag() throws IOException, JspException {
        PageContext pageContext = (PageContext)getJspContext();
        JspFragment body = getJspBody();
        if(body!=null) body.invoke(getJspFragmentWriter(pageContext.getOut()));
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        // Make relative to current JSP page
        if(page==null) throw new AttributeRequiredException("page");
        String contextRelativePath = ServletUtil.getAbsolutePath(request, page);
        RequestDispatcher dispatcher = pageContext.getServletContext().getRequestDispatcher(contextRelativePath);
        final Map<String,String[]> parameters;
        if(clearParams) {
            if(params==null) {
                // No parameters
                if(request.getParameterMap().isEmpty()) {
                    // No need to wrap, use old request
                    dispatch(dispatcher, request, response);
                    return;
                }
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
            // Add all request parameters
            if(params==null) {
                // Only use old parameters - use existing request, no need to wrap
                //@SuppressWarnings("unchecked")
                //Map<String,String[]> oldMap = request.getParameterMap();
                //parameters = oldMap;
                dispatch(dispatcher, request, response);
                return;
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
        dispatch(
            dispatcher,
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
    }

    /**
     * Gets the write to use for the JspFragment.  By default it uses the same
     * writer as this tag.
     */
    Writer getJspFragmentWriter(JspWriter out) {
        return out;
    }

    abstract void dispatch(RequestDispatcher dispatcher, ServletRequest request, ServletResponse response) throws IOException, JspException;
}

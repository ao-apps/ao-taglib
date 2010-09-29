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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class RedirectTag extends SimpleTagSupport implements HrefAttribute, ParamsAttribute, TypeAttribute {

    public static boolean isValidType(String type) {
        return
            "moved_permanently".equals(type)
            || "permanent".equals(type)
            || "301".equals(type)
            || "found".equals(type)
            || "temporary".equals(type)
            || "302".equals(type)
            || "see_other".equals(type)
            || "303".equals(type)
        ;
    }

    private String href;
    private SortedMap<String,List<String>> params;
    private String type = "found";

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public Map<String,List<String>> getParams() {
        if(params==null) return Collections.emptyMap();
        return params;
    }

    @Override
    public void addParam(String name, String value) {
        if(params==null) params = new TreeMap<String,List<String>>();
        List<String> values = params.get(name);
        if(values==null) params.put(name, values = new ArrayList<String>());
        values.add(value);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) throws JspException {
        if(!isValidType(type)) throw new JspException(ApplicationResources.accessor.getMessage("RedirectTag.type.invalid", type));
        this.type = type;
    }

    @Override
    public void doTag() throws IOException, SkipPageException, JspException {
        JspFragment body = getJspBody();
        if(body!=null) body.invoke(null);
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        if(href.startsWith("/")) {
            String contextPath = request.getContextPath();
            if(contextPath.length()>0) href = contextPath+href;
        }
        href = HrefTag.addParams(href, params);
        String encodedHref = response.encodeRedirectURL(href);
        if("moved_permanently".equals(type) || "permanent".equals(type) || "301".equals(type)) {
            response.setHeader("Location", encodedHref);
            response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
        } else if("found".equals(type) || "temporary".equals(type) || "302".equals(type)) {
            response.sendRedirect(encodedHref);
        } else if("see_other".equals(type) || "303".equals(type)) {
            response.setHeader("Location", encodedHref);
            response.sendError(HttpServletResponse.SC_SEE_OTHER);
        } else {
            throw new AssertionError("Unexpected value for type: "+type);
        }
        throw new SkipPageException();
    }
}

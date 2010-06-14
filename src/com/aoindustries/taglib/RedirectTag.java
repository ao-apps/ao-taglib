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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class RedirectTag extends SimpleTagSupport implements HrefAttribute, TypeAttribute {

    public static boolean isValidType(String type) {
        return
            "temporary".equals(type)
            || "permanent".equals(type)
        ;
    }

    private String href;
    private String type = "temporary";

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
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
    public void doTag() throws IOException, SkipPageException {
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        if(href.startsWith("/")) {
            String contextPath = request.getContextPath();
            if(contextPath.length()>0) href = contextPath+href;
        }
        String encodedHref = response.encodeRedirectURL(href);
        if("temporary".equals(type)) {
            response.sendRedirect(encodedHref);
        } else if("permanent".equals(type)) {
            response.setHeader("Location", encodedHref);
            response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
        } else {
            throw new AssertionError("Unexpected value for type: "+type);
        }
        throw new SkipPageException();
    }
}

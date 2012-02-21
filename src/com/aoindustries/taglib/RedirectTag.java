/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011  AO Industries, Inc.
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

import com.aoindustries.encoding.NewEncodingUtils;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.servlet.http.ServletUtil;
import java.io.IOException;
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
            || "moved_temporarily".equals(type)
            || "found".equals(type)
            || "temporary".equals(type)
            || "302".equals(type)
            || "see_other".equals(type)
            || "303".equals(type)
        ;
    }

    private String href;
    private HttpParametersMap params;
    private String type;

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
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

        //System.err.println("DEBUG: RedirectTag.doTag: 1: href="+href);

        // Convert page-relative paths to context-relative path, resolving ./ and ../
        href = ServletUtil.getAbsolutePath(request, href);
        //System.err.println("DEBUG: RedirectTag.doTag: 2: href="+href);

        // Add any parameters to the URL
        href = HttpParametersUtils.addParams(href, params);
        //System.err.println("DEBUG: RedirectTag.doTag: 3: href="+href);

        // Encode URL path elements (like Japanese filenames)
        href = NewEncodingUtils.encodeUrlPath(href);
        //System.err.println("DEBUG: RedirectTag.doTag: 4: href="+href);

        // Perform URL rewriting
        href = response.encodeRedirectURL(href);
        //System.err.println("DEBUG: RedirectTag.doTag: 5: href="+href);

        // Convert to absolute path if needed.  This will also add the context path.
        if(href.startsWith("/")) {
            href = ServletUtil.getAbsoluteURL(request, href);
            //System.err.println("DEBUG: RedirectTag.doTag: 6: href="+href);
        }
        /*
        // Add in the context path for context-relative paths
        if(href.startsWith("/")) {
            String contextPath = request.getContextPath();
            if(contextPath.length()>0) href = contextPath+href;
        }
        //System.err.println("DEBUG: RedirectTag.doTag: 3: href="+href);
         */

        if(type==null) throw new AttributeRequiredException("type");
        if("301".equals(type) || "moved_permanently".equals(type) || "permanent".equals(type)) {
            response.setHeader("Location", href);
            response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
        } else if("302".equals(type) || "moved_temporarily".equals(type) || "found".equals(type) || "temporary".equals(type)) {
            response.setHeader("Location", href);
            response.sendError(HttpServletResponse.SC_MOVED_TEMPORARILY);
            //response.sendRedirect(encodedHref);
        } else if("303".equals(type) || "see_other".equals(type)) {
            response.setHeader("Location", href);
            response.sendError(HttpServletResponse.SC_SEE_OTHER);
        } else {
            throw new AssertionError("Unexpected value for type: "+type);
        }
        throw new SkipPageException();
    }
}

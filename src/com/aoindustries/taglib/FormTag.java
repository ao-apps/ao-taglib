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

import com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.NewEncodingUtils;
import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class FormTag extends AutoEncodingBufferedTag implements MethodAttribute, IdAttribute, ActionAttribute, OnsubmitAttribute {

    public static boolean isValidMethod(String method) {
        return
            "get".equals(method)
            || "post".equals(method)
        ;
    }

    private String method = "get";
    private String id;
    private String action;
    private String onsubmit;

    public MediaType getContentType() {
        return MediaType.XHTML;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) throws JspException {
        if(!isValidMethod(method)) throw new JspException(ApplicationResourcesAccessor.getMessage(Locale.getDefault(), "FormTag.method.invalid", method));
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOnsubmit() {
        return onsubmit;
    }

    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        out.write("<form method=\"");
        out.write(method);
        out.write('"');
        if(id!=null) {
            out.write(" id=\"");
            EncodingUtils.encodeXmlAttribute(id, out);
            out.write('"');
        }
        if(action!=null) {
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
            out.write(" action=\"");
            if(action.startsWith("/")) {
                String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
                if(contextPath.length()>0) action = contextPath+action;
            }
            out.write(
                EncodingUtils.encodeXmlAttribute(
                    response.encodeURL(
                        NewEncodingUtils.encodeURL(action)
                    )
                )
            );
            out.write('"');
        }
        if(onsubmit!=null) {
            out.write(" onsubmit=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onsubmit, out);
            out.write('"');
        }
        out.write('>');
        out.write(capturedBody.toString());
        out.write("</form>");
    }
}

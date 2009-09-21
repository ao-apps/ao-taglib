/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class ATag extends AutoEncodingBufferedTag implements HrefAttribute, ClassAttribute, OnclickAttribute {

    private String href;
    private String clazz;
    private String onclick;

    public MediaType getContentType() {
        return MediaType.XHTML;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        out.write("<a");
        if(href!=null) {
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
            out.write(" href=\"");
            if(href.startsWith("/")) {
                String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
                if(contextPath.length()>0) href = contextPath+href;
            }
            out.write(
                response.encodeURL(
                    EncodingUtils.encodeXmlAttribute(
                        EncodingUtils.encodeURL(href)
                    )
                )
            );
            out.write('"');
        }
        if(clazz!=null) {
            out.write(" class=\"");
            EncodingUtils.encodeXmlAttribute(clazz, out);
            out.write('"');
        }
        if(onclick!=null) {
            out.write(" onclick=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onclick, out);
            out.write('"');
        }
        out.write('>');
        out.write(capturedBody.toString());
        out.write("</a>");
    }
}

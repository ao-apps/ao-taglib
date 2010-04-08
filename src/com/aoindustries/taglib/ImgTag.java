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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.NewEncodingUtils;
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
public class ImgTag extends AutoEncodingBufferedTag implements SrcAttribute, WidthAttribute, HeightAttribute, AltAttribute, ClassAttribute, StyleAttribute {

    private String src;
    private String width;
    private String height;
    private String alt;
    private String clazz;
    private String style;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        if(src==null) src = capturedBody.toString().trim();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        if(width==null) throw new JspException(ApplicationResourcesAccessor.getMessage(response.getLocale(), "ImgTag.width.required"));
        if(height==null) throw new JspException(ApplicationResourcesAccessor.getMessage(response.getLocale(), "ImgTag.height.required"));
        if(alt==null) throw new JspException(ApplicationResourcesAccessor.getMessage(response.getLocale(), "ImgTag.alt.required"));
        out.write("<img src=\"");
        if(src.startsWith("/")) {
            String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
            if(contextPath.length()>0) src = contextPath+src;
        }
        out.write(
            EncodingUtils.encodeXmlAttribute(
                response.encodeURL(
                    NewEncodingUtils.encodeURL(src)
                )
            )
        );
        out.write("\" width=\"");
        EncodingUtils.encodeXmlAttribute(width, out);
        out.write("\" height=\"");
        EncodingUtils.encodeXmlAttribute(height, out);
        out.write("\" alt=\"");
        EncodingUtils.encodeXmlAttribute(alt, out);
        out.write('"');
        if(clazz!=null) {
            out.write(" class=\"");
            EncodingUtils.encodeXmlAttribute(clazz, out);
            out.write('"');
        }
        if(style!=null) {
            out.write(" style=\"");
            EncodingUtils.encodeXmlAttribute(style, out);
            out.write('"');
        }
        out.write(" />");
    }
}

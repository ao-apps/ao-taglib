/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
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
public class ImgTag extends AutoEncodingBufferedTag implements SrcAttribute, ParamsAttribute, WidthAttribute, HeightAttribute, AltAttribute, TitleAttribute, ClassAttribute, StyleAttribute {

    private String src;
    private HttpParametersMap params;
    private String width;
    private String height;
    private String alt;
    private String title;
    private String clazz;
    private String style;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public String getSrc() {
        return src;
    }

    @Override
    public void setSrc(String src) {
        this.src = src;
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
    public String getWidth() {
        return width;
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String getHeight() {
        return height;
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String getAlt() {
        return alt;
    }

    @Override
    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        if(src==null) src = capturedBody.toString().trim();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        if(width==null) throw new JspException(ApplicationResources.accessor.getMessage("ImgTag.width.required"));
        if(height==null) throw new JspException(ApplicationResources.accessor.getMessage("ImgTag.height.required"));
        if(alt==null) throw new JspException(ApplicationResources.accessor.getMessage("ImgTag.alt.required"));
        out.write("<img src=\"");
        if(src.startsWith("/")) {
            String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
            if(contextPath.length()>0) src = contextPath+src;
        }
        src = HrefTag.addParams(src, params);
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
        if(title!=null) {
            out.write("\" title=\"");
            EncodingUtils.encodeXmlAttribute(title, out);
            out.write('"');
        }
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

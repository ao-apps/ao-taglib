/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013  AO Industries, Inc.
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
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.servlet.jsp.LocalizedJspException;
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
public class ATag
	extends AutoEncodingBufferedTag
	implements
		HrefAttribute,
		ParamsAttribute,
		HreflangAttribute,
		RelAttribute,
		TypeAttribute,
		TargetAttribute,
		TitleAttribute,
		ClassAttribute,
		StyleAttribute,
		OnclickAttribute,
		OnmouseoverAttribute,
		OnmouseoutAttribute
{

    private String href;
    private MutableHttpParameters params;
	private String hreflang;
	private String rel;
	private String type;
	private String target;
    private String title;
    private String clazz;
    private String style;
    private String onclick;
    private String onmouseover;
    private String onmouseout;

    @Override
    public MediaType getContentType() {
        return MediaType.XHTML;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

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
    public String getHreflang() {
        return hreflang;
    }

	@Override
	public void setHreflang(String hreflang) {
		this.hreflang = hreflang;
	}

	@Override
    public String getRel() {
        return rel;
    }

    @Override
    public void setRel(String rel) {
        this.rel = rel;
    }

	@Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

	@Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
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
    public String getOnclick() {
        return onclick;
    }

    @Override
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    @Override
    public String getOnmouseover() {
        return onmouseover;
    }

    @Override
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    @Override
    public String getOnmouseout() {
        return onmouseout;
    }

    @Override
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

	/**
	 * Writes an href attribute with parameters.
	 */
	static void writeHref(Writer out, PageContext pageContext, String href, MutableHttpParameters params) throws JspException, IOException {
        if(href!=null) {
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
            out.write(" href=\"");
            if(href.startsWith("/")) {
                String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
                if(contextPath.length()>0) href = contextPath+href;
            }
            href = HttpParametersUtils.addParams(href, params);
			EncodingUtils.encodeXmlAttribute(
				response.encodeURL(
					NewEncodingUtils.encodeUrlPath(href)
				),
				out
			);
            out.write('"');
        } else {
            if(params!=null) throw new LocalizedJspException(ApplicationResources.accessor, "ATag.doTag.paramsWithoutHref");
        }
	}

	@Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        out.write("<a");
		writeHref(out, pageContext, href, params);
        if(hreflang!=null) {
            out.write(" hreflang=\"");
            EncodingUtils.encodeXmlAttribute(hreflang, out);
            out.write('"');
        }
        if(rel!=null) {
            out.write(" rel=\"");
            EncodingUtils.encodeXmlAttribute(rel, out);
            out.write('"');
        }
        if(type!=null) {
            out.write(" type=\"");
            EncodingUtils.encodeXmlAttribute(type, out);
            out.write('"');
        }
        if(target!=null) {
            out.write(" target=\"");
            EncodingUtils.encodeXmlAttribute(target, out);
            out.write('"');
        }
        if(title!=null) {
            out.write(" title=\"");
            EncodingUtils.encodeXmlAttribute(title, out);
            out.write('"');
        }
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
        if(onclick!=null) {
            out.write(" onclick=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onclick, out);
            out.write('"');
        }
        if(onmouseover!=null) {
            out.write(" onmouseover=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onmouseover, out);
            out.write('"');
        }
        if(onmouseout!=null) {
            out.write(" onmouseout=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onmouseout, out);
            out.write('"');
        }
        out.write('>');
        capturedBody.writeTo(out);
        out.write("</a>");
    }
}

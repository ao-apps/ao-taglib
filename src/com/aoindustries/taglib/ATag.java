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

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.NewEncodingUtils;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.servlet.jsp.LocalizedJspException;
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
	private Object hreflang;
	private Object rel;
	private String type;
	private String target;
    private String title;
    private Object clazz;
    private String style;
    private Object onclick;
    private Object onmouseover;
    private Object onmouseout;

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
    public Object getHreflang() {
        return hreflang;
    }

	@Override
	public void setHreflang(Object hreflang) {
		this.hreflang = hreflang;
	}

	@Override
    public Object getRel() {
        return rel;
    }

    @Override
    public void setRel(Object rel) {
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
    public Object getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(Object clazz) {
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
    public Object getOnclick() {
        return onclick;
    }

    @Override
    public void setOnclick(Object onclick) {
        this.onclick = onclick;
    }

    @Override
    public Object getOnmouseover() {
        return onmouseover;
    }

    @Override
    public void setOnmouseover(Object onmouseover) {
        this.onmouseover = onmouseover;
    }

    @Override
    public Object getOnmouseout() {
        return onmouseout;
    }

    @Override
    public void setOnmouseout(Object onmouseout) {
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
			encodeTextInXhtmlAttribute(
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
			Coercion.write(hreflang, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(rel!=null) {
			out.write(" rel=\"");
			Coercion.write(rel, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(type!=null) {
			out.write(" type=\"");
			encodeTextInXhtmlAttribute(type, out);
			out.write('"');
		}
		if(target!=null) {
			out.write(" target=\"");
			encodeTextInXhtmlAttribute(target, out);
			out.write('"');
		}
		if(title!=null) {
			out.write(" title=\"");
			encodeTextInXhtmlAttribute(title, out);
			out.write('"');
		}
		if(clazz!=null) {
			out.write(" class=\"");
			Coercion.write(clazz, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			encodeTextInXhtmlAttribute(style, out);
			out.write('"');
		}
		if(onclick!=null) {
			out.write(" onclick=\"");
			Coercion.write(onclick, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onmouseover!=null) {
			out.write(" onmouseover=\"");
			Coercion.write(onmouseover, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onmouseout!=null) {
			out.write(" onmouseout=\"");
			Coercion.write(onmouseout, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write('>');
		capturedBody.writeTo(out);
		out.write("</a>");
    }
}

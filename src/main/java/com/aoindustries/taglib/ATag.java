/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-taglib.
 *
 * ao-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.Coercion;
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.encodeTextInXhtml;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class ATag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
		IdAttribute,
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

	private Object id;
	private String href;
	private MutableHttpParameters params;
	private boolean hrefAbsolute;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;
	private Object hreflang;
	private Object rel;
	private Object type;
	private Object target;
	private Object title;
	private Object clazz;
	private Object style;
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
	public Object getId() {
		return id;
	}

	@Override
	public void setId(Object id) {
		try {
			// JSP EL converts nulls to empty string
			this.id = Coercion.isEmpty(id) ? null : id;
		} catch(IOException e) {
			throw new WrappedException(e);
		}
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

	public boolean getHrefAbsolute() {
		return hrefAbsolute;
	}

	public void setHrefAbsolute(boolean hrefAbsolute) {
		this.hrefAbsolute = hrefAbsolute;
	}

	public String getAddLastModified() {
		return addLastModified.getLowerName();
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = LastModifiedServlet.AddLastModifiedWhen.valueOfLowerName(addLastModified);
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
	public Object getType() {
		return type;
	}

	@Override
	public void setType(Object type) {
		this.type = type;
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public void setTarget(Object target) {
		this.target = target;
	}

	@Override
	public Object getTitle() {
		return title;
	}

	@Override
	public void setTitle(Object title) {
		this.title = title;
	}

	@Override
	public Object getClazz() {
		return clazz;
	}

	@Override
	public void setClazz(Object clazz) {
		try {
			// JSP EL converts nulls to empty string
			this.clazz = Coercion.isEmpty(clazz) ? null : clazz;
		} catch(IOException e) {
			throw new WrappedException(e);
		}
	}

	@Override
	public Object getStyle() {
		return style;
	}

	@Override
	public void setStyle(Object style) {
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

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(
			uri==null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			ParamUtils.setDynamicAttribute(this, uri, localName, value);
		} else {
			throw new LocalizedJspTagException(
				accessor,
				"error.unexpectedDynamicAttribute",
				localName,
				ParamUtils.PARAM_ATTRIBUTE_PREFIX+"*"
			);
		}
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		out.write("<a");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		UrlUtils.writeHref(getJspContext(), out, href, params, hrefAbsolute, addLastModified);
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
			Coercion.write(type, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(target!=null) {
			out.write(" target=\"");
			Coercion.write(target, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(title!=null) {
			out.write(" title=\"");
			MarkupUtils.writeWithMarkup(title, MarkupType.TEXT, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(clazz!=null) {
			out.write(" class=\"");
			Coercion.write(clazz, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
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
		BufferResult trimmedBody = capturedBody.trim();
		if(trimmedBody.getLength()==0) {
			// When the body is empty after trimming, display the href itself
			if(href!=null) {
				PageContext pageContext = (PageContext)getJspContext();
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				if(href.startsWith("mailto:")) {
					encodeTextInXhtml(href.substring(7), out);
				} else if(href.startsWith("telnet:")) {
					encodeTextInXhtml(href.substring(7), out);
				} else if(href.startsWith("tel:")) {
					encodeTextInXhtml(href.substring(4), out);
				} else {
					encodeTextInXhtml(
						ServletUtil.getAbsolutePath(Dispatcher.getCurrentPagePath(request), href),
						out
					);
				}
			}
		} else {
			MarkupUtils.writeWithMarkup(trimmedBody, MarkupType.XHTML, out);
		}
		out.write("</a>");
	}
}

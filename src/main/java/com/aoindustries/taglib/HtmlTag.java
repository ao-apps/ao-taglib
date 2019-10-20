/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016, 2017, 2019  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.servlet.ServletUtil;
import com.aoindustries.servlet.http.Html;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

public class HtmlTag extends AutoEncodingFilteredTag {

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	// TODO: charset here, WebPage, Page, PageServer, PageTag, Skin, Layout, Theme

	private Html.Serialization serialization;
	public void setSerialization(String serialization) {
		if(serialization == null) {
			this.serialization = null;
		} else {
			serialization = serialization.trim();
			this.serialization = (serialization.isEmpty() || "auto".equalsIgnoreCase(serialization)) ? null : Html.Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
		}
	}

	private Html.Doctype doctype;
	public void setDoctype(String doctype) {
		if(doctype == null) {
			this.doctype = null;
		} else {
			doctype = doctype.trim();
			this.doctype = (doctype.isEmpty() || "default".equalsIgnoreCase(doctype)) ? null : Html.Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
		}
	}

	private String clazz;
	public void setClazz(String clazz) {
		this.clazz = AttributeUtils.trimNullIfEmpty(clazz);
	}

	private String oldIeClass;
	public void setOldIeClass(String oldIeClass) {
		this.oldIeClass = AttributeUtils.trimNullIfEmpty(oldIeClass);
	}

	public static void beginHtmlTag(Locale locale, Appendable out, Html.Serialization serialization, String clazz) throws IOException {
		out.append("<html");
		if(serialization == Html.Serialization.XML) {
			out.append(" xmlns=\"http://www.w3.org/1999/xhtml\"");
		}
		if(clazz!=null) {
			out.append(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.append('"');
		}
		if(locale != null) {
			String lang = locale.toLanguageTag();
			if(!lang.isEmpty()) {
				out.append(" lang=\"");
				encodeTextInXhtmlAttribute(lang, out);
				out.append('"');
				if(serialization == Html.Serialization.XML) {
					out.append(" xml:lang=\"");
					encodeTextInXhtmlAttribute(lang, out);
					out.append('"');
				}
			}
		}
		out.append('>');
	}

	public static void beginHtmlTag(ServletResponse response, Appendable out, Html.Serialization serialization, String clazz) throws IOException {
		beginHtmlTag(response.getLocale(), out, serialization, clazz);
	}

	public static void endHtmlTag(Appendable out) throws IOException {
		out.append("</html>");
	}

	@Override
	protected void doTag(Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		ServletContext servletContext = pageContext.getServletContext();
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

		Html.Serialization currentSerialization = serialization;
		Html.Serialization oldSerialization;
		boolean setSerialization;
		if(currentSerialization == null) {
			currentSerialization = Html.Serialization.get(servletContext, request);
			oldSerialization = null;
			setSerialization = false;
		} else {
			oldSerialization = Html.Serialization.replace(request, currentSerialization);
			setSerialization = true;
		}
		try {
			Html.Doctype currentDoctype = doctype;
			Html.Doctype oldDoctype;
			boolean setDoctype;
			if(currentDoctype == null) {
				currentDoctype = Html.Doctype.get(servletContext, request);
				oldDoctype = null;
				setDoctype = false;
			} else {
				oldDoctype = Html.Doctype.replace(request, currentDoctype);
				setDoctype = true;
			}
			try {
				ServletResponse response = pageContext.getResponse();
				// Clear the output buffer
				response.resetBuffer();
				// Set the content type
				final String documentEncoding = Html.ENCODING.name();
				ServletUtil.setContentType(response, currentSerialization.getContentType(), documentEncoding);
				// Write doctype
				currentDoctype.xmlDeclaration(currentSerialization, documentEncoding, out);
				currentDoctype.doctype(currentSerialization, out);
				// Write <html>
				if(oldIeClass!=null) {
					out.write("<!--[if lte IE 8]>");
					beginHtmlTag(response, out, currentSerialization, clazz==null ? oldIeClass : (clazz + " " + oldIeClass));
					out.write("<![endif]-->\n"
							+ "<!--[if gt IE 8]><!-->");
					beginHtmlTag(response, out, currentSerialization, clazz);
					out.write("<!--<![endif]-->");
				} else {
					beginHtmlTag(response, out, currentSerialization, clazz);
				}
				super.doTag(out);
				endHtmlTag(out);
			} catch(ServletException e) {
				throw new JspTagException(e);
			} finally {
				if(setDoctype) Html.Doctype.set(request, oldDoctype);
			}
		} finally {
			if(setSerialization) Html.Serialization.set(request, oldSerialization);
		}
	}
}

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
import com.aoindustries.servlet.http.Html;
import com.aoindustries.servlet.http.Html.DocType;
import com.aoindustries.servlet.http.Html.Serialization;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.ServletContext;
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

	// TODO: a way to register doctype on the current request
	private DocType doctype;
	public void setDoctype(String doctype) {
		if(doctype == null) {
			this.doctype = null;
		} else {
			doctype = doctype.trim();
			this.doctype = (doctype.isEmpty() || "default".equalsIgnoreCase(doctype)) ? null : DocType.valueOf(doctype.toLowerCase(Locale.ROOT));
		}
	}

	// TODO: Add a way to register the serialization on the current request, or just base on response encoding always?
	// TODO: Would HtmlTag use this registered value as its default, to be consistent with any pre-processing that assumed this serialization?
	private Serialization serialization;
	public void setSerialization(String serialization) {
		if(serialization == null) {
			this.serialization = null;
		} else {
			serialization = serialization.trim();
			this.serialization = (serialization.isEmpty() || "auto".equalsIgnoreCase(serialization)) ? null : Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
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

	public static void beginHtmlTag(ServletResponse response, Writer out, Serialization serialization, String clazz) throws IOException {
		out.write("<html");
		if(serialization == Serialization.XHTML) {
			out.write(" xmlns=\"http://www.w3.org/1999/xhtml\"");
		}
		if(clazz!=null) {
			out.write(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.write('"');
		}
		Locale locale = response.getLocale();
		if(locale!=null) {
			String language = locale.getLanguage();
			if(language.length()>0) {
				String country = locale.getCountry();
				out.write(" lang=\"");
				out.write(language);
				if(country.length()>0) {
					out.write('-');
					out.write(country);
				}
				out.write('"');
				if(serialization == Serialization.XHTML) {
					out.write(" xml:lang=\"");
					out.write(language);
					if(country.length()>0) {
						out.write('-');
						out.write(country);
					}
					out.write('"');
				}
			}
		}
		out.write('>');
	}

	public static void endHtmlTag(Writer out) throws IOException {
		out.write("</html>");
	}

	@Override
	protected void doTag(Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		ServletContext servletContext = pageContext.getServletContext();
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		ServletResponse response = pageContext.getResponse();

		// Clear the output buffer
		response.resetBuffer();

		// Set the content type
		Serialization currentSerialization = serialization;
		if(currentSerialization == null) currentSerialization = Serialization.select(servletContext, request);
		String contentType = currentSerialization.getContentType();
		response.setContentType(contentType);
		final String documentEncoding = Html.ENCODING.name();
		response.setCharacterEncoding(documentEncoding); // Seems required by Jetty, otherwise the response stayed iso-8859-1 and could not handle unicode characters
		String actualContentType = response.getContentType();
		if(actualContentType != null) {
			int semiPos = actualContentType.indexOf(';');
			if(semiPos != -1) actualContentType = actualContentType.substring(0, semiPos);
		}
		if(!contentType.equals(actualContentType)) throw new JspTagException("Unable to set content type, response already committed? contentType=" + contentType + ", actualContentType=" + actualContentType);

		DocType currentDocType = doctype;
		if(currentDocType == null) {
			currentDocType = DocType.get(servletContext, request);
		}
		DocType.set(request, currentDocType);
		currentDocType.appendXmlDeclarationLine(currentSerialization, documentEncoding, out);
		out.write(currentDocType.getDocTypeLine(currentSerialization));
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
	}
}

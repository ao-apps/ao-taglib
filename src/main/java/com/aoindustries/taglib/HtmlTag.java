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

	private DocType doctype = DocType.strict;
	public void setDoctype(String doctype) {
		this.doctype = doctype==null ? null : DocType.valueOf(doctype.trim());
	}

	// TODO: Change to an attribute "serialization" with "html", "xhtml", or "auto" (the default)
	private boolean forceHtml = false;
	public void setForceHtml(boolean forceHtml) {
		this.forceHtml = forceHtml;
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
		ServletResponse response = pageContext.getResponse();

		// Clear the output buffer
		response.resetBuffer();

		// Set the content type
		Serialization serialization = forceHtml ? Serialization.HTML : Serialization.select(pageContext.getServletContext(), (HttpServletRequest)pageContext.getRequest());
		String contentType = serialization.getContentType();
		response.setContentType(contentType);
		final String documentEncoding = Html.ENCODING.name();
		response.setCharacterEncoding(documentEncoding); // Seems required by Jetty, otherwise the response stayed iso-8859-1 and could not handle unicode characters
		String actualContentType = response.getContentType();
		if(actualContentType != null) {
			int semiPos = actualContentType.indexOf(';');
			if(semiPos != -1) actualContentType = actualContentType.substring(0, semiPos);
		}
		if(!contentType.equals(actualContentType)) throw new JspTagException("Unable to set content type, response already committed? contentType=" + contentType + ", actualContentType=" + actualContentType);

		doctype.appendXmlDeclarationLine(serialization, documentEncoding, out);
		out.write(doctype.getDocTypeLine(serialization));
		if(oldIeClass!=null) {
			out.write("<!--[if lte IE 8]>");
			beginHtmlTag(response, out, serialization, clazz==null ? oldIeClass : (clazz + " " + oldIeClass));
			out.write("<![endif]-->\n"
					+ "<!--[if gt IE 8]><!-->");
			beginHtmlTag(response, out, serialization, clazz);
			out.write("<!--<![endif]-->");
		} else {
			beginHtmlTag(response, out, serialization, clazz);
		}
		super.doTag(out);
		endHtmlTag(out);
	}
}

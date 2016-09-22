/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2011, 2013, 2015, 2016  AO Industries, Inc.
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
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class MetaTag
	extends AutoEncodingBufferedTag
	implements
		NameAttribute,
		ContentAttribute
{

	private Object name;
	private Object httpEquiv;
	private Object charset;
	private Object content;

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setName(Object name) throws JspTagException {
		try {
			this.name = Coercion.nullIfEmpty(name);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	public void setHttpEquiv(Object httpEquiv) throws JspTagException {
		try {
			this.httpEquiv = Coercion.nullIfEmpty(httpEquiv);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	public void setCharset(Object charset) throws JspTagException {
		try {
			this.charset = Coercion.nullIfEmpty(charset);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setContent(Object content) throws JspTagException {
		try {
			this.content = Coercion.nullIfEmpty(content);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		JspTag parent = findAncestorWithClass(this, MetasAttribute.class);
		if(content==null) setContent(capturedBody.trim());
		if(parent!=null) {
			((MetasAttribute)parent).addMeta(
				new Meta(
					Coercion.toString(name),
					Coercion.toString(httpEquiv),
					Coercion.toString(charset),
					Coercion.toString(content)
				)
			);
		} else {
			// Write the meta tag directly here
			out.write("<meta");
			if(name != null) {
				out.write(" name=\"");
				Coercion.write(name, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(httpEquiv != null) {
				out.write(" http-equiv=\"");
				Coercion.write(httpEquiv, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(charset != null) {
				out.write(" charset=\"");
				Coercion.write(charset, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(content != null) {
				out.write(" content=\"");
				Coercion.write(content, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			out.write(" />");
		}
	}
}

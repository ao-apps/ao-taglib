/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013, 2015  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.util.WrappedException;
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

	/**
	 * Avoid empty string attributes.
	 */
	private static Object nullIfEmpty(Object o) {
		if(o instanceof String) {
			String s = (String)o;
			if(s.isEmpty()) return null;
		} else if(o instanceof BufferResult) {
			try {
				BufferResult br = (BufferResult)o;
				if(br.getLength() == 0) return null;
			} catch(IOException e) {
				throw new WrappedException(e);
			}
		}
		return o;
	}

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
    public Object getName() {
        return name;
    }

    @Override
    public void setName(Object name) {
		this.name = nullIfEmpty(name);
    }

    public Object getHttpEquiv() {
        return httpEquiv;
    }

    public void setHttpEquiv(Object httpEquiv) {
        this.httpEquiv = nullIfEmpty(httpEquiv);
	}

	public Object getCharset() {
        return charset;
    }

    public void setCharset(Object charset) {
        this.charset = nullIfEmpty(charset);
    }

	@Override
    public Object getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
		this.content = nullIfEmpty(content);
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

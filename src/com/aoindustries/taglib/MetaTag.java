/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
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

    private Object httpEquiv;
    private Object name;
    private Object content;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public Object getHttpEquiv() {
        return httpEquiv;
    }

    public void setHttpEquiv(Object httpEquiv) {
        this.httpEquiv = httpEquiv;
    }

    @Override
    public Object getName() {
        return name;
    }

    @Override
    public void setName(Object name) {
		this.name = name;
    }

    @Override
    public Object getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
		this.content = content;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		JspTag parent = findAncestorWithClass(this, MetasAttribute.class);
		if(content==null) setContent(capturedBody.trim());
		// Create meta in all cases for its validation
		if(parent!=null) {
			((MetasAttribute)parent).addMeta(
				new Meta(
					Coercion.toString(name),
					Coercion.toString(httpEquiv),
					Coercion.toString(content)
				)
			);
		} else {
			// Write the meta tag directly here
			out.write("<meta");
			if(httpEquiv!=null) {
				out.write(" http-equiv=\"");
				Coercion.write(httpEquiv, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(name!=null) {
				out.write(" name=\"");
				Coercion.write(name, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			out.write(" content=\"");
			Coercion.write(content, textInXhtmlAttributeEncoder, out);
			out.write("\" />");
		}
    }
}

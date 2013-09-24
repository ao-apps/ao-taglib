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
import com.aoindustries.encoding.TextInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.util.ref.ReferenceUtils;
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

    private String httpEquiv;
    private String name;
    private Object content;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getHttpEquiv() {
        return httpEquiv;
    }

    public void setHttpEquiv(String httpEquiv) {
        this.httpEquiv = httpEquiv;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
		this.content = ReferenceUtils.replace(this.content, content);
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		try {
			JspTag parent = findAncestorWithClass(this, MetasAttribute.class);
			if(content==null) setContent(capturedBody.trim());
			// Create meta in all cases for its validation
			if(parent!=null) {
				Meta meta = new Meta(
					name,
					httpEquiv,
					Coercion.toString(content)
				);
				((MetasAttribute)parent).addMeta(meta);
			} else {
				// Write the meta tag directly here
				out.write("<meta");
				if(httpEquiv!=null && httpEquiv.length()>0) {
					out.write(" http-equiv=\"");
					encodeTextInXhtmlAttribute(httpEquiv, out);
					out.write('"');
				}
				if(name!=null && name.length()>0) {
					out.write(" name=\"");
					encodeTextInXhtmlAttribute(name, out);
					out.write('"');
				}
				out.write(" content=\"");
				Coercion.toString(
					content,
					TextInXhtmlAttributeEncoder.getInstance(),
					out
				);
				out.write("\" />");
			}
		} finally {
			content = ReferenceUtils.release(content);
		}
    }
}

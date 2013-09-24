/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.util.ref.ReferenceUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class OptionTag
	extends AutoEncodingBufferedTag
	implements
		ValueAttribute,
		SelectedAttribute,
		DisabledAttribute
{

    private boolean valueSet;
    private Object value;
    private boolean selected;
    private boolean disabled;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.valueSet = true;
		this.value = ReferenceUtils.replace(this.value, value);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		try {
			capturedBody.trim();
			if(!valueSet) setValue(capturedBody);
			out.write("<option value=\"");
			Coercion.write(value, textInXhtmlAttributeEncoder, out);
			out.write('"');
			if(selected) out.write(" selected=\"selected\"");
			if(disabled) out.write(" disabled=\"disabled\"");
			out.write('>');
			Coercion.write(capturedBody, textInXhtmlEncoder, out);
			out.write("</option>");
		} finally {
			value = ReferenceUtils.release(value);
		}
    }
}

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.Coercion;
import com.aoindustries.io.Writable;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class OutTag
	extends AutoEncodingNullTag
	implements
		ValueAttribute,
		TypeAttribute
{

	private Object value;
	private Object def;
	private Object type = MediaType.TEXT;
    private MediaType mediaType = MediaType.TEXT;

	@Override
    public MediaType getOutputType() {
        return mediaType;
    }

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	public Object getDefault() {
		return def;
	}
	
	public void setDefault(Object def) {
		this.def = def;
	}

	@Override
    public Object getType() {
        return type;
    }

	@Override
    public void setType(Object type) throws JspTagException {
		MediaType newMediaType;
		if(type instanceof MediaType) {
			newMediaType = (MediaType)type;
		} else {
			String typeStr = Coercion.toString(type);
			newMediaType = MediaType.getMediaTypeByName(typeStr);
			if(newMediaType==null) {
				try {
					newMediaType = MediaType.getMediaTypeForContentType(typeStr);
				} catch(MediaException e) {
					throw new JspTagException(e);
				}
			}
		}
		this.type = type;
		this.mediaType = newMediaType;
    }

	private String toStringResult;
	private BundleLookupMarkup lookupMarkup;

	@Override
	protected void writePrefix(MediaType containerType, Writer out) throws IOException {
		Object effectiveValue = value!=null ? value : def;
		if(
			!(effectiveValue instanceof Writable)
			|| ((Writable)effectiveValue).isFastToString()
		) {
			toStringResult = Coercion.toString(effectiveValue);
			// Look for any message markup
			BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
			if(threadContext!=null) {
				lookupMarkup = threadContext.getLookupMarkup(toStringResult);
			} else {
				lookupMarkup = null;
			}
			if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(containerType.getMarkupType(), out);
		}
	}

	@Override
    protected void doTag(Writer out) throws JspTagException, IOException {
		if(toStringResult!=null) {
			out.write(toStringResult);
		} else if(value!=null) {
			Coercion.write(value, out);
		} else if(def!=null) {
			Coercion.write(def, out);
		}
    }

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws IOException {
		if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(containerType.getMarkupType(), out);
	}
}

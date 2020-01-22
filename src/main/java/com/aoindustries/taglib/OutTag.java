/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.Writable;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspTagException;
import org.w3c.dom.Node;

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
	private ValueExpression def;
	private boolean defValueSet;
	private Object defValue;
	private MediaType mediaType = MediaType.TEXT;

	@Override
	public MediaType getOutputType() {
		return mediaType;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	public void setDefault(ValueExpression def) {
		this.def = def;
		this.defValueSet = false;
		this.defValue = null;
	}

	private Object getDefault() {
		if(def == null) return null;
		if(defValueSet) return defValue;
		Object _value = def.getValue(getJspContext().getELContext());
		defValue = _value;
		defValueSet = true;
		return _value;
	}

	/**
	 * <p>
	 * TODO: Support a type of "auto" (not the default - use with care) that
	 * takes the media type from the object being written, if it implements
	 * an interface that has a <code>getOutputType()</code> method.
	 * If no <code>getOutputType()</code> method in auto mode, default to
	 * {@link MediaType#TEXT} or throw exception?  Other objects with
	 * a <code>getOutputType()</code> method should also implement this new
	 * method?  Other places with <code>setType(Object)</code> might support
	 * the same auto-mode.  Or would we allow a object passed into "type"
	 * attribute to implement this interface?  Or a different "typeOf" attribute?
	 * </p>
	 */
	@Override
	public void setType(Object type) throws JspTagException {
		MediaType newMediaType;
		if(type instanceof MediaType) {
			newMediaType = (MediaType)type;
		} else {
			String typeStr = Coercion.toString(type).trim();
			newMediaType = MediaType.getMediaTypeByName(typeStr);
			if(newMediaType==null) {
				try {
					newMediaType = MediaType.getMediaTypeForContentType(typeStr);
				} catch(MediaException e) {
					throw new JspTagException(e);
				}
			}
		}
		this.mediaType = newMediaType;
	}

	private MarkupType markupType;
	private String toStringResult;
	private BundleLookupMarkup lookupMarkup;

	@Override
	protected void writePrefix(MediaType containerType, Writer out) throws IOException {
		Object effectiveValue = (value != null) ? value : getDefault();
		if(effectiveValue != null) {
			markupType = containerType.getMarkupType();
			BundleLookupThreadContext threadContext;
			if(
				markupType != null
				&& markupType != MarkupType.NONE
				&& (threadContext = BundleLookupThreadContext.getThreadContext(false)) != null
				// Avoid intermediate String from Writable
				&& (
					!(effectiveValue instanceof Writable)
					|| ((Writable)effectiveValue).isFastToString()
				)
				// Other types that will not be converted to String for bundle lookups
				&& !(value instanceof char[])
				&& !(value instanceof Node)
			) {
				toStringResult = Coercion.toString(effectiveValue);
				// Look for any message markup
				lookupMarkup = threadContext.getLookupMarkup(toStringResult);
				if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
			}
		}
	}

	@Override
	protected void doTag(Writer out) throws JspTagException, IOException {
		if(toStringResult != null) {
			out.write(toStringResult);
		} else if(value != null) {
			Coercion.write(value, out);
		} else {
			Object _default = getDefault();
			if(_default != null) {
				Coercion.write(_default, out);
			}
		}
	}

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws IOException {
		if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
	}
}

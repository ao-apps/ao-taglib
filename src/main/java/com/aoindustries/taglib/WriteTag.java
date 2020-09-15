/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.io.Writable;
import com.aoindustries.lang.Strings;
import com.aoindustries.lang.Throwables;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import org.w3c.dom.Node;

/**
 * @author  AO Industries, Inc.
 */
public class WriteTag
	extends AutoEncodingNullTag
	implements
		NameAttribute,
		TypeAttribute
{

	private String scope;
	private String name;
	private String property;
	private String method = "toString";
	private MediaType mediaType = MediaType.TEXT;

	@Override
	public MediaType getOutputType() {
		return mediaType;
	}

	public void setScope(String scope) {
		this.scope = scope.trim();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public void setType(String type) throws JspTagException {
		String typeStr = Strings.trim(type);
		MediaType newMediaType = MediaType.getMediaTypeByName(typeStr);
		if(newMediaType==null) {
			try {
				newMediaType = MediaType.getMediaTypeForContentType(typeStr);
			} catch(UnsupportedEncodingException e) {
				throw new JspTagException(e);
			}
		}
		this.mediaType = newMediaType;
	}

	// One or neither of these values will be set after writePrefix, but not both
	private MarkupType markupType;
	private String toStringResult;
	private BundleLookupMarkup lookupMarkup;
	private Object value;

	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	protected void writePrefix(MediaType containerType, Writer out) throws JspTagException, IOException {
		try {
			if(name == null) throw new AttributeRequiredException("name");

			// Find the bean to write
			Object bean = PropertyUtils.findObject(
				(PageContext)getJspContext(),
				scope,
				name,
				property,
				true,
				false
			);

			// Print the value
			if(bean != null) {
				markupType = containerType.getMarkupType();
				// Avoid reflection when possible
				if("toString".equals(method)) {
					BundleLookupThreadContext threadContext;
					if(
						markupType == null
						|| markupType == MarkupType.NONE
						|| (threadContext = BundleLookupThreadContext.getThreadContext(false)) == null
						// Avoid intermediate String from Writable
						|| (
							bean instanceof Writable
							&& !((Writable)bean).isFastToString()
						)
						// Other types that will not be converted to String for bundle lookups
						|| value instanceof char[]
						|| value instanceof Node
					) {
						// Stream with coercion in doTag
						value = bean;
					} else {
						toStringResult = Coercion.toString(bean);
						// Look for any message markup
						lookupMarkup = threadContext.getLookupMarkup(toStringResult);
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
					}
				} else {
					try {
						BundleLookupThreadContext threadContext;
						Method refMethod = bean.getClass().getMethod(method);
						Object retVal = refMethod.invoke(bean);
						if(
							retVal == null
							|| markupType == null
							|| markupType == MarkupType.NONE
							|| (threadContext = BundleLookupThreadContext.getThreadContext(false)) == null
							// Avoid intermediate String from Writable
							|| (
								retVal instanceof Writable
								&& !((Writable)retVal).isFastToString()
							)
							// Other types that will not be converted to String for bundle lookups
							|| value instanceof char[]
							|| value instanceof Node
						) {
							// Stream with coercion in doTag
							value = retVal;
						} else {
							toStringResult = Coercion.toString(retVal);
							// Look for any message markup
							lookupMarkup = threadContext.getLookupMarkup(toStringResult);
							if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
						}
					} catch(NoSuchMethodException err) {
						throw new LocalizedJspTagException(ApplicationResources.accessor, "WriteTag.unableToFindMethod", method);
					} catch(InvocationTargetException e) {
						// Unwrap cause for more direct stack traces
						Throwable cause = e.getCause();
						throw (cause == null) ? e : cause;
					}
				}
			}
		} catch(Throwable t) {
			if(t instanceof IOException) throw (IOException)t;
			throw Throwables.wrap(t, JspTagException.class, JspTagException::new);
		}
	}

	@Override
	protected void doTag(Writer out) throws JspTagException, IOException {
		if(toStringResult != null) {
			out.write(toStringResult);
		} else if(value != null) {
			Coercion.write(value, out);
		}
	}

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws IOException {
		if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
	}
}

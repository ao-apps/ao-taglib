/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.EncodingNullTag;
import com.aoapps.hodgepodge.i18n.BundleLookupMarkup;
import com.aoapps.hodgepodge.i18n.BundleLookupThreadContext;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.Writable;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import org.w3c.dom.Node;

/**
 * @author  AO Industries, Inc.
 */
// TODO: Remove or deprecate?  This is old struts-style, and obsoleted by JSTL + EL.
public class WriteTag extends EncodingNullTag
	implements
		NameAttribute,
		TypeAttribute
{

/* SimpleTag only: */
	public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, WriteTag.class);
/**/

	public WriteTag() {
		init();
	}

	@Override
	public MediaType getOutputType() {
		return mediaType;
	}

/* BodyTag only:
	private static final long serialVersionUID = 1L;
/**/

	private String scope;
	public void setScope(String scope) {
		this.scope = scope.trim();
	}

	private String name;
	@Override
	public void setName(String name) {
		this.name = name;
	}

	private String property;
	public void setProperty(String property) {
		this.property = property;
	}

	private String method;
	public void setMethod(String method) {
		this.method = method;
	}

	private MediaType mediaType;
	@Override
	public void setType(String type) {
		String typeStr = Strings.trim(type);
		MediaType newMediaType = MediaType.getMediaTypeByName(typeStr);
		if(newMediaType==null) {
			try {
				newMediaType = MediaType.getMediaTypeForContentType(typeStr);
			} catch(UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e);
			}
		}
		this.mediaType = newMediaType;
	}

	// One or neither of these values will be set after writePrefix, but not both
	private MarkupType markupType;
	private String toStringResult;
	private BundleLookupMarkup lookupMarkup;
	private Object value;

	private void init() {
		scope = null;
		name = null;
		property = null;
		method = "toString";
		mediaType = MediaType.TEXT;
		markupType = null;
		toStringResult = null;
		lookupMarkup = null;
		value = null;
	}

	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	protected void writePrefix(MediaType containerType, Writer out) throws JspException, IOException {
		try {
/* SimpleTag only: */
			PageContext pageContext = (PageContext)getJspContext();
/**/
			if(name == null) throw new AttributeRequiredException("name");

			// Find the bean to write
			Object bean = PropertyUtils.findObject(
				pageContext,
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
						|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
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
							|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
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
						throw new LocalizedJspTagException(RESOURCES, "unableToFindMethod", method);
					} catch(InvocationTargetException e) {
						// Unwrap cause for more direct stack traces
						Throwable cause = e.getCause();
						throw (cause == null) ? e : cause;
					}
				}
			}
		} catch(IOException e) {
			throw e;
		} catch(Throwable t) {
			throw Throwables.wrap(t, JspTagException.class, JspTagException::new);
		}
	}

	@Override
/* BodyTag only:
	protected int doEndTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only: */
	protected void doTag(Writer out) throws JspException, IOException {
/**/
		if(toStringResult != null) {
			out.write(toStringResult);
		} else if(value != null) {
			Coercion.write(value, out, true);
		}
/* BodyTag only:
		return EVAL_PAGE;
/**/
	}

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws JspException, IOException {
		if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
	}

/* BodyTag only:
	@Override
	public void doFinally() {
		try {
			init();
		} finally {
			super.doFinally();
		}
	}
/**/
}

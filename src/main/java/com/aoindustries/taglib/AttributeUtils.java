/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019  AO Industries, Inc.
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
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public final class AttributeUtils  {

	/**
	 * Finds the attribute parent tag of the provided class (or subclass).
	 *
	 * @return  the parent tag
	 * @exception  NeedAttributeParentException  if parent not found
	 */
	public static <T> T findAttributeParent(String fromTagName, JspTag from, String attribute, Class<? extends T> clazz) throws NeedAttributeParentException {
		T parent = clazz.cast(SimpleTagSupport.findAncestorWithClass(from, clazz));
		if(parent==null) throw new NeedAttributeParentException(fromTagName, attribute);
		return parent;
	}

	/**
	 * Evaluates an expression then casts to the provided type.
	 */
	public static <T> T resolveValue(ValueExpression expression, Class<T> type, ELContext elContext) {
		if(expression == null) {
			return null;
		} else {
			return type.cast(expression.getValue(elContext));
		}
	}

	/**
	 * Casts or evaluates an expression then casts to the provided type.
	 */
	public static <T> T resolveValue(Object value, Class<T> type, ELContext elContext) {
		if(value == null) {
			return null;
		} else if(value instanceof ValueExpression) {
			return resolveValue((ValueExpression)value, type, elContext);
		} else {
			return type.cast(value);
		}
	}

	/**
	 * @see  Coercion#nullIfEmpty(java.lang.Object)
	 */
	public static <T> T nullIfEmpty(T value) throws JspTagException {
		try {
			return Coercion.nullIfEmpty(value);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	/**
	 * @see  StringUtility#nullIfEmpty(java.lang.String)
	 */
	public static String nullIfEmpty(String value) {
		return StringUtility.nullIfEmpty(value);
	}

	/**
	 * @see  Coercion#trimNullIfEmpty(java.lang.Object)
	 */
	public static Object trimNullIfEmpty(Object value) throws JspTagException {
		try {
			return Coercion.trimNullIfEmpty(value);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	/**
	 * Trims a value, returning {@code null} if empty after trimming.
	 */
	public static String trimNullIfEmpty(String value) {
		if(value != null) {
			value = value.trim();
			if(value.isEmpty()) value = null;
		}
		return value;
	}

	/**
	 * Make no instances.
	 */
	private AttributeUtils() {
	}
}

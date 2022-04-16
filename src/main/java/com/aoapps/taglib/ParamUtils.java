/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.NullArgumentException;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspTag;

/**
 * Helper utility for handling parameter values.
 *
 * @author  AO Industries, Inc.
 */
public final class ParamUtils {

	/** Make no instances. */
	private ParamUtils() {throw new AssertionError();}

	/**
	 * The prefix for <code>param.*</code> dynamic attributes.
	 */
	public static final String PARAM_ATTRIBUTE_PREFIX = "param.";

	/**
	 * Adds one parameter to the first parent of the given tag that implements <code>ParamsAttribute</code>.
	 * If value is null, the parameter is not added.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @param  fromTagName  the name of the tag searching from
	 * @param  from         the tag to search from
	 * @param  name         the name of the parameter (required)
	 * @param  value        the value of the parameter
	 *
	 * @see  AttributeUtils#requireAttributeParent(java.lang.String, javax.servlet.jsp.tagext.JspTag, java.lang.String, java.lang.Class)
	 */
	public static void addParam(
		String fromTagName,
		JspTag from,
		String name,
		Object value
	) throws JspTagException {
		addParam(
			AttributeUtils.requireAttributeParent(fromTagName, from, "params", ParamsAttribute.class),
			name,
			value
		);
	}

	/**
	 * Adds one parameter to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, the parameter is not added.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 * @param  value            the value of the parameter
	 */
	public static void addParam(
		ParamsAttribute paramsAttribute,
		String name,
		Object value
	) {
		NullArgumentException.checkNotNull(name, "name");
		if(value != null) paramsAttribute.addParam(name, value);
	}

	/**
	 * Adds a set of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addIterableParams(
		ParamsAttribute paramsAttribute,
		String name,
		Iterable<?> values
	) {
		NullArgumentException.checkNotNull(name, "name");
		if(values != null) {
			addIteratorParams(paramsAttribute, name, values.iterator());
		}
	}

	/**
	 * Adds a set of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addIteratorParams(
		ParamsAttribute paramsAttribute,
		String name,
		Iterator<?> values
	) {
		NullArgumentException.checkNotNull(name, "name");
		if(values != null) {
			while(values.hasNext()) {
				addParam(paramsAttribute, name, values.next());
			}
		}
	}

	/**
	 * Adds a set of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addEnumerationParams(
		ParamsAttribute paramsAttribute,
		String name,
		Enumeration<?> values
	) throws JspTagException {
		NullArgumentException.checkNotNull(name, "name");
		if(values != null) {
			while(values.hasMoreElements()) {
				addParam(paramsAttribute, name, values.nextElement());
			}
		}
	}

	/**
	 * Adds an array of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addArrayParams(
		ParamsAttribute paramsAttribute,
		String name,
		Object values
	) throws JspTagException {
		NullArgumentException.checkNotNull(name, "name");
		if(values != null) {
			int len = Array.getLength(values);
			for(int c = 0; c < len; c++) {
				addParam(paramsAttribute, name, Array.get(values, c));
			}
		}
	}

	/**
	 * Adds the <code>param.*</code> {@linkplain DynamicAttributes dynamic attributes}.
	 * Handles Iterable, Iterator, Enumeration, arrays, and direct coercion.
	 * <p>
	 * The conversion to string may be deferred, or the value may be streamed instead of being
	 * converted to a string.  It is incorrect to change the state of the provided value; doing
	 * so may or may not affect the value of the resulting parameter.
	 * </p>
	 *
	 * @return  {@code true} when added, or {@code false} when attribute not expected and has not been added.
	 *
	 * @see  DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public static boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns, ParamsAttribute paramsAttribute) throws JspTagException {
		if(
			uri == null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			if(value != null) {
				String paramName = localName.substring(PARAM_ATTRIBUTE_PREFIX.length());
				if(value instanceof Iterable<?>) {
					addIterableParams(
						paramsAttribute,
						paramName,
						(Iterable<?>)value
					);
				} else if(value instanceof Iterator<?>) {
					addIteratorParams(
						paramsAttribute,
						paramName,
						(Iterator<?>)value
					);
				} else if(value instanceof Enumeration<?>) {
					addEnumerationParams(
						paramsAttribute,
						paramName,
						(Enumeration<?>)value
					);
				} else if(value.getClass().isArray()) {
					addArrayParams(
						paramsAttribute,
						paramName,
						value
					);
				} else {
					addParam(paramsAttribute, paramName, value);
				}
			}
			return true;
		} else {
			expectedPatterns.add(ParamUtils.PARAM_ATTRIBUTE_PREFIX + "*");
			return false;
		}
	}
}

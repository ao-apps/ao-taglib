/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import com.aoindustries.io.Coercion;
import com.aoindustries.lang.NullArgumentException;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

/**
 * Helper utility for handling parameter values.
 *
 * @author  AO Industries, Inc.
 */
final public class ParamUtils {

	/**
	 * The prefix for parameter attributes.
	 */
	public static final String PARAM_ATTRIBUTE_PREFIX = "param.";

	/**
	 * Adds one parameter to the first parent of the given tag that implements <code>ParamsAttribute</code>.
	 * If value is null, the parameter is not added.
	 * 
	 * @param  fromTagName  the name of the tag searching from
	 * @param  from         the tag to search from
	 * @param  name         the name of the parameter (required)
	 * @param  value        the value of the parameter, will be coerced to String
	 *
	 * @see  AttributeUtils#findAttributeParent(java.lang.String, javax.servlet.jsp.tagext.JspTag, java.lang.String, java.lang.Class)
	 */
	public static void addParam(
		String fromTagName,
		JspTag from,
		String name,
		Object value
	) throws JspException {
		addParam(
			AttributeUtils.findAttributeParent(fromTagName, from, "params", ParamsAttribute.class),
			name,
			value
		);
	}

	/**
	 * Adds one parameter to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, the parameter is not added.
	 * 
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 * @param  value            the value of the parameter, will be coerced to String
	 */
	public static void addParam(
		ParamsAttribute paramsAttribute,
		String name,
		Object value
	) throws JspException {
		NullArgumentException.checkNotNull(name, "name");
		if(value!=null) {
			paramsAttribute.addParam(
				name,
				Coercion.toString(value)
			);
		}
	}

	/**
	 * Adds a set of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * 
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addIterableParams(
		ParamsAttribute paramsAttribute,
		String name,
		Iterable<?> values
	) throws JspException {
		NullArgumentException.checkNotNull(name, "name");
		if(values!=null) {
			addIteratorParams(
				paramsAttribute,
				name,
				values.iterator()
			);
		}
	}

	/**
	 * Adds a set of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * 
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addIteratorParams(
		ParamsAttribute paramsAttribute,
		String name,
		Iterator<?> values
	) throws JspException {
		NullArgumentException.checkNotNull(name, "name");
		if(values!=null) {
			while(values.hasNext()) {
				Object elem = values.next();
				if(elem!=null) {
					addParam(
						paramsAttribute,
						name,
						Coercion.toString(elem)
					);
				}
			}
		}
	}

	/**
	 * Adds a set of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * 
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addEnumerationParams(
		ParamsAttribute paramsAttribute,
		String name,
		Enumeration<?> values
	) throws JspException {
		NullArgumentException.checkNotNull(name, "name");
		if(values!=null) {
			while(values.hasMoreElements()) {
				Object elem = values.nextElement();
				if(elem!=null) {
					addParam(
						paramsAttribute,
						name,
						Coercion.toString(elem)
					);
				}
			}
		}
	}

	/**
	 * Adds an array of parameters to the given <code>ParamsAttribute</code> parent tag.
	 * If value is null, no parameters are added.
	 * If any element is null, the parameter is not added for the element.
	 * 
	 * @param  paramsAttribute  the parent tag that will receive the parameters
	 * @param  name             the name of the parameter (required)
	 */
	public static void addArrayParams(
		ParamsAttribute paramsAttribute,
		String name,
		Object values
	) throws JspException {
		NullArgumentException.checkNotNull(name, "name");
		if(values!=null) {
			int len = Array.getLength(values);
			for(int c=0; c<len; c++) {
				Object elem = Array.get(values, c);
				if(elem!=null) {
					addParam(
						paramsAttribute,
						name,
						Coercion.toString(elem)
					);
				}
			}
		}
	}

	/**
	 * Sets the dynamic param.* attributes.
	 * Handles Iterable, Iterator, Enumeration, arrays, and direct coercion.
	 *
	 * @throws  JspException  if any dynamic parameter other than "param.*" is given
	 */
	public static void setDynamicAttribute(
		ParamsAttribute paramsAttribute,
		String uri,
		String localName,
		Object value
	) throws JspException {
		assert uri==null;
		assert localName.startsWith(PARAM_ATTRIBUTE_PREFIX);
		if(value!=null) {
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
				addParam(
					paramsAttribute,
					paramName,
					Coercion.toString(value)
				);
			}
		}
	}

	/**
	 * Make no instances.
	 */
	private ParamUtils() {
	}
}

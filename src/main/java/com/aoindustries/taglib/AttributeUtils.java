/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.lang.Strings;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
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
	public static Object nullIfEmpty(Object value) throws JspTagException {
		try {
			return Coercion.nullIfEmpty(value);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	/**
	 * @see  Strings#nullIfEmpty(java.lang.String)
	 */
	public static String nullIfEmpty(String value) {
		return Strings.nullIfEmpty(value);
	}

	/**
	 * @see  Coercion#trim(java.lang.Object)
	 */
	public static Object trim(Object value) throws JspTagException {
		try {
			return Coercion.trim(value);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
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
	 * @see  Strings#trimNullIfEmpty(java.lang.String)
	 */
	public static String trimNullIfEmpty(String value) {
		return Strings.trimNullIfEmpty(value);
	}

	public static boolean isAllDigits(String value) {
		for(int i = 0, len = value.length(); i < len; i++) {
			char ch = value.charAt(i);
			if(ch < '0' || ch > '9') return false;
		}
		return true;
	}

	public static boolean isZero(String value) {
		for(int i = 0, len = value.length(); i < len; i++) {
			char ch = value.charAt(i);
			if(ch != '0') return false;
		}
		return true;
	}

	/**
	 * Appends a width style, while automatically appending "px" to any non-zero integer.
	 * Encoding for XML attribute context is performed.
	 *
	 * @return  {@code true} when printed the style
	 */
	public static boolean appendWidthStyle(String width, Appendable out) throws IOException {
		width = trimNullIfEmpty(width);
		if(width != null) {
			out.append("width:");
			encodeTextInXhtmlAttribute(width, out);
			if(isAllDigits(width) && !isZero(width)) {
				out.append("px");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets a width style, while automatically appending "px" to any non-zero integer.
	 * No encoding is performed.
	 *
	 * @return  The style or {@code null} when none
	 */
	public static String getWidthStyle(String width) {
		width = trimNullIfEmpty(width);
		if(width != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("width:").append(width);
			if(isAllDigits(width) && !isZero(width)) {
				sb.append("px");
			}
			return sb.toString();
		} else {
			return null;
		}
	}

	/**
	 * Checks a validation result.
	 *
	 * @return  The value when valid
	 * @throws  JspTagException  When invalid, supporting {@link LocalizedJspTagException} when validationResult
	 *                           is a {@link InvalidResult}
	 */
	// TODO: Use Attributes version once setters throw IllegalArgumentException instead of JspTagException
	public static <T> T validate(T value, ValidationResult validationResult) throws JspTagException {
		if(validationResult.isValid()) {
			return value;
		} else {
			if(validationResult instanceof InvalidResult) {
				InvalidResult invalidResult = (InvalidResult)validationResult;
				throw new LocalizedJspTagException(
					invalidResult.getAccessor(),
					invalidResult.getKey(),
					invalidResult.getArgs()
				);
			} else {
				throw new JspTagException(validationResult.toString());
			}
		}
	}

	/**
	 * Validates a value using the provided validator.
	 *
	 * @return  The value when valid
	 * @throws  JspTagException  When invalid, supporting {@link LocalizedJspTagException} when validationResult
	 *                           is a {@link InvalidResult}
	 */
	// TODO: Use Attributes version once setters throw IllegalArgumentException instead of JspTagException
	public static <T> T validate(T value, Function<? super T,ValidationResult> validator) throws JspTagException {
		return validate(value, validator.apply(value));
	}

	/**
	 * Creates the exception for dynamic attribute failed.  Does not throw it.
	 */
	public static JspTagException newDynamicAttributeFailedException(String uri, String localName, Object value, List<String> expectedPatterns) {
		if(expectedPatterns == null || expectedPatterns.isEmpty()) {
			return new LocalizedJspTagException(
				accessor,
				"error.unexpectedDynamicAttribute0",
				localName
			);
		} else {
			int size = expectedPatterns.size();
			if(size == 1) {
				return new LocalizedJspTagException(
					accessor,
					"error.unexpectedDynamicAttribute1",
					localName,
					expectedPatterns.get(0)
				);
			} else if(size == 2) {
				return new LocalizedJspTagException(
					accessor,
					"error.unexpectedDynamicAttribute2",
					localName,
					expectedPatterns.get(0),
					expectedPatterns.get(1)
				);
			} else {
				// Quote and comma-separate arbitrary length list here
				StringBuilder pre = new StringBuilder();
				for(int i = 0; i < (size - 1); i++) {
					if(pre.length() != 0) pre.append(", ");
					pre.append('"').append(expectedPatterns.get(i)).append('"');
				}
				return new LocalizedJspTagException(
					accessor,
					"error.unexpectedDynamicAttributeN",
					localName,
					pre,
					'"' + expectedPatterns.get(size - 1) + '"'
				);
			}
		}
	}

	/**
	 * Make no instances.
	 */
	private AttributeUtils() {
	}
}

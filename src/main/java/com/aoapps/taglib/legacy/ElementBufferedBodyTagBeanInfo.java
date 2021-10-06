/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020, 2021  AO Industries, Inc.
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
package com.aoapps.taglib.legacy;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author  AO Industries, Inc.
 */
public class ElementBufferedBodyTagBeanInfo extends SimpleBeanInfo {

	private static final PropertyDescriptor[] properties;
	static {
		try {
			properties = new PropertyDescriptor[] {
				new PropertyDescriptor("class", ElementBufferedBodyTag.class, "getClazz", "setClazz"),
				new PropertyDescriptor("data",  ElementBufferedBodyTag.class, "getData",  "setData"),
				new PropertyDescriptor("dir",   ElementBufferedBodyTag.class, "getDir",   "setDir"),
				new PropertyDescriptor("style", ElementBufferedBodyTag.class, "getStyle", "setStyle"),
			};
		} catch(IntrospectionException err) {
			throw new ExceptionInInitializerError(err);
		}
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Not copying array for performance
	public PropertyDescriptor[] getPropertyDescriptors () {
		return properties;
	}

	/**
	 * Include base class.
	 */
	@Override
	public BeanInfo[] getAdditionalBeanInfo() {
		try {
			return new BeanInfo[] {
				Introspector.getBeanInfo(ElementBufferedBodyTag.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}

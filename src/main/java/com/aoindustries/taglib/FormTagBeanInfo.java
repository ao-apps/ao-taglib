/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2017  AO Industries, Inc.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author  AO Industries, Inc.
 */
public class FormTagBeanInfo extends SimpleBeanInfo {

	private static class PropertiesLock {}
	private static final PropertiesLock propertiesLock = new PropertiesLock();
	private static PropertyDescriptor[] properties;

	@Override
	public PropertyDescriptor[] getPropertyDescriptors () {
		try {
			synchronized(propertiesLock) {
				PropertyDescriptor[] props = properties;
				if(props==null) {
					props = new PropertyDescriptor[] {
						// From base class: new PropertyDescriptor("contentType", FormTag.class, "getContentType", null),
						// From base class: new PropertyDescriptor("outputType", FormTag.class, "getOutputType", null),
						new PropertyDescriptor("method", FormTag.class, null, "setMethod"),
						new PropertyDescriptor("id", FormTag.class, null, "setId"),
						new PropertyDescriptor("action", FormTag.class, null, "setAction"),
						new PropertyDescriptor("target", FormTag.class, null, "setTarget"),
						new PropertyDescriptor("enctype", FormTag.class, null, "setEnctype"),
						new PropertyDescriptor("class", FormTag.class, "getClazz", "setClazz"),
						new PropertyDescriptor("style", FormTag.class, null, "setStyle"),
						new PropertyDescriptor("onsubmit", FormTag.class, null, "setOnsubmit")
					};
					properties = props;
				}
				return props;
			}
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}

	/**
	 * Include base class.
	 */
	@Override
	public BeanInfo[] getAdditionalBeanInfo() {
		try {
			return new BeanInfo[] {
				Introspector.getBeanInfo(FormTag.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}
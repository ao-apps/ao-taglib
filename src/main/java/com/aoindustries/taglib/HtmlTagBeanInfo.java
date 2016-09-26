/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2013, 2015, 2016  AO Industries, Inc.
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
public class HtmlTagBeanInfo extends SimpleBeanInfo {

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
						// From base class: new PropertyDescriptor("contentType", HtmlTag.class, "getContentType", null),
						new PropertyDescriptor("doctype", HtmlTag.class, null, "setDoctype"),
						new PropertyDescriptor("forceHtml", HtmlTag.class, null, "setForceHtml"),
						new PropertyDescriptor("class", HtmlTag.class, null, "setClazz"),
						new PropertyDescriptor("oldIeClass", HtmlTag.class, null, "setOldIeClass")
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
				Introspector.getBeanInfo(HtmlTag.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}

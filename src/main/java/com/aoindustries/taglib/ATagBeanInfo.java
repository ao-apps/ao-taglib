/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
public class ATagBeanInfo extends SimpleBeanInfo {

	private static volatile PropertyDescriptor[] properties;

	@Override
	public PropertyDescriptor[] getPropertyDescriptors () {
		try {
			PropertyDescriptor[] props = properties;
			if(props == null) {
				props = new PropertyDescriptor[] {
					// From base class: new PropertyDescriptor("contentType", ATag.class, "getContentType", null),
					// From base class: new PropertyDescriptor("outputType", ATag.class, "getOutputType", null),
					new PropertyDescriptor("id", ATag.class, null, "setId"),
					new PropertyDescriptor("href", ATag.class, null, "setHref"),
					new PropertyDescriptor("absolute", ATag.class, null, "setAbsolute"),
					new PropertyDescriptor("canonical", ATag.class, null, "setCanonical"),
					new PropertyDescriptor("addLastModified", ATag.class, null, "setAddLastModified"),
					new PropertyDescriptor("hreflang", ATag.class, null, "setHreflang"),
					new PropertyDescriptor("rel", ATag.class, null, "setRel"),
					new PropertyDescriptor("type", ATag.class, null, "setType"),
					new PropertyDescriptor("target", ATag.class, null, "setTarget"),
					new PropertyDescriptor("title", ATag.class, null, "setTitle"),
					new PropertyDescriptor("class", ATag.class, "getClazz", "setClazz"),
					new PropertyDescriptor("style", ATag.class, null, "setStyle"),
					new PropertyDescriptor("onclick", ATag.class, null, "setOnclick"),
					new PropertyDescriptor("onmouseover", ATag.class, null, "setOnmouseover"),
					new PropertyDescriptor("onmouseout", ATag.class, null, "setOnmouseout")
				};
				properties = props;
			}
			return props; // Not copying array for performance
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
				Introspector.getBeanInfo(ATag.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}

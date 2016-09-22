/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2014, 2015, 2016  AO Industries, Inc.
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
public class ImgTagBeanInfo extends SimpleBeanInfo {

	private static volatile PropertyDescriptor[] properties = null;

	@Override
	public PropertyDescriptor[] getPropertyDescriptors () {
		try {
			PropertyDescriptor[] props = properties;
			if(props==null) {
				props = new PropertyDescriptor[] {
					// From base class: new PropertyDescriptor("contentType", ImgTag.class, "getContentType", null),
					// From base class: new PropertyDescriptor("outputType", ImgTag.class, "getOutputType", null),
					new PropertyDescriptor("id", ImgTag.class, null, "setId"),
					new PropertyDescriptor("src", ImgTag.class, null, "setSrc"),
					new PropertyDescriptor("srcAbsolute", ImgTag.class, null, "setSrcAbsolute"),
					new PropertyDescriptor("addLastModified", ImgTag.class, null, "setAddLastModified"),
					new PropertyDescriptor("width", ImgTag.class, null, "setWidth"),
					new PropertyDescriptor("height", ImgTag.class, null, "setHeight"),
					new PropertyDescriptor("alt", ImgTag.class, null, "setAlt"),
					new PropertyDescriptor("title", ImgTag.class, null, "setTitle"),
					new PropertyDescriptor("class", ImgTag.class, "getClazz", "setClazz"),
					new PropertyDescriptor("style", ImgTag.class, null, "setStyle")
				};
				properties = props;
			}
			return props;
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
				Introspector.getBeanInfo(ImgTag.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}

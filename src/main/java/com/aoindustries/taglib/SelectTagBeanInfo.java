/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020  AO Industries, Inc.
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
public class SelectTagBeanInfo extends SimpleBeanInfo {

	private static volatile PropertyDescriptor[] properties = null;

	@Override
	public PropertyDescriptor[] getPropertyDescriptors () {
		try {
			PropertyDescriptor[] props = properties;
			if(props == null) {
				props = new PropertyDescriptor[] {
					// From base class: new PropertyDescriptor("contentType", SelectTag.class, "getContentType", null),
					// From base class: new PropertyDescriptor("outputType", SelectTag.class, "getOutputType", null),
					new PropertyDescriptor("id",         SelectTag.class, null,       "setId"),
					new PropertyDescriptor("name",       SelectTag.class, null,       "setName"),
					new PropertyDescriptor("class",      SelectTag.class, "getClazz", "setClazz"),
					new PropertyDescriptor("style",      SelectTag.class, null,       "setStyle"),
					new PropertyDescriptor("disabled",   SelectTag.class, null,       "setDisabled"),
					new PropertyDescriptor("size",       SelectTag.class, null,       "setSize"),
					new PropertyDescriptor("onchange",   SelectTag.class, null,       "setOnchange"),
					new PropertyDescriptor("onfocus",    SelectTag.class, null,       "setOnfocus"),
					new PropertyDescriptor("onblur",     SelectTag.class, null,       "setOnblur"),
					new PropertyDescriptor("onkeypress", SelectTag.class, null,       "setOnkeypress"),
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
				Introspector.getBeanInfo(SelectTag.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}

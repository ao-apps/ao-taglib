/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2015, 2016  AO Industries, Inc.
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
public class InputTagBeanInfo extends SimpleBeanInfo {

    private static volatile PropertyDescriptor[] properties = null;

    @Override
    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor[] props = properties;
            if(props==null) {
                props = new PropertyDescriptor[] {
                    // From base class: new PropertyDescriptor("contentType", InputTag.class, "getContentType", null),
                    // From base class: new PropertyDescriptor("outputType", InputTag.class, "getOutputType", null),
                    new PropertyDescriptor("id", InputTag.class),
                    new PropertyDescriptor("type", InputTag.class),
                    new PropertyDescriptor("name", InputTag.class),
                    new PropertyDescriptor("value", InputTag.class),
                    new PropertyDescriptor("onclick", InputTag.class),
                    new PropertyDescriptor("onchange", InputTag.class),
                    new PropertyDescriptor("onfocus", InputTag.class),
                    new PropertyDescriptor("onblur", InputTag.class),
                    new PropertyDescriptor("onkeypress", InputTag.class),
                    new PropertyDescriptor("size", InputTag.class),
                    new PropertyDescriptor("maxlength", InputTag.class),
                    new PropertyDescriptor("readonly", InputTag.class),
                    new PropertyDescriptor("disabled", InputTag.class),
                    new PropertyDescriptor("class", InputTag.class, "getClazz", "setClazz"),
                    new PropertyDescriptor("style", InputTag.class),
                    new PropertyDescriptor("checked", InputTag.class),
                    new PropertyDescriptor("tabindex", InputTag.class)
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
				Introspector.getBeanInfo(InputTag.class.getSuperclass())
			};
        } catch(IntrospectionException err) {
			throw new AssertionError(err);
        }
	}
}

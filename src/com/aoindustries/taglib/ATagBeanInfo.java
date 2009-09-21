/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  AO Industries, Inc.
 */
public class ATagBeanInfo extends SimpleBeanInfo {

    private static final Logger logger = Logger.getLogger(ATagBeanInfo.class.getName());

    private static volatile PropertyDescriptor[] properties = null;

    @Override
    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor[] props = properties;
            if(props==null) {
                props = new PropertyDescriptor[] {
                    new PropertyDescriptor("href", ATag.class),
                    new PropertyDescriptor("class", ATag.class, "getClazz", "setClazz"),
                    new PropertyDescriptor("style", ATag.class),
                    new PropertyDescriptor("onclick", ATag.class)
                };
                properties = props;
            }
            return props;
        } catch(IntrospectionException err) {
            logger.log(Level.SEVERE, null, err);
            return null;
        }
    }
}

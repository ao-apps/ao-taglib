/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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

import java.lang.reflect.InvocationTargetException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Resolves objects from scope, name, and property values.
 *
 * @author  AO Industries, Inc.
 */
public class PropertyUtils {

    private PropertyUtils() {
    }

    /**
     * Gets the object given its scope, name, and optional property.
     *
     * @param  scope  scope should be one of these acceptable values:
     *                 <ul>
     *                   <li><code>null</code></li>
     *                   <li><code>"page"</code></li>
     *                   <li><code>"request"</code></li>
     *                   <li><code>"session"</code></li>
     *                   <li><code>"application"</code></li>
     *                 </ul>
     * @param beanRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
     *                     throw a <code>JspException</code> with an appropriate localized message.
     * @param valueRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
     *                      throw a <code>JspException</code> with an appropriate localized message.
     *
     * @return  the resolved <code>Object</code> or <code>null</code> if not found.
     */
    public static Object findObject(PageContext pageContext, String scope, String name, String property, boolean beanRequired, boolean valueRequired) throws JspException {
        try {
            // Check the name
            if(name==null) throw new AttributeRequiredException("name");

            // Find the bean
            Object bean;
            if(scope==null) bean = pageContext.findAttribute(name);
            else if("page".equals(scope)) bean = pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
            else if("request".equals(scope)) bean = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
            else if("session".equals(scope)) bean = pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
            else if("application".equals(scope)) bean = pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
            else throw new JspException(ApplicationResources.accessor.getMessage("PropertyUtils.scope.invalid", scope));

            // Check required
            if(bean==null) {
                if(beanRequired) {
                    // null and required
                    if(scope==null) throw new JspException(ApplicationResources.accessor.getMessage("PropertyUtils.bean.required.nullScope", name));
                    else throw new JspException(ApplicationResources.accessor.getMessage("PropertyUtils.bean.required.scope", name, scope));
                } else {
                    // null and not required
                    return null;
                }
            } else {
                if(property==null) {
                    // No property lookup, use the bean directly
                    return bean;
                } else {
                    // Find the property
                    Object value = org.apache.commons.beanutils.PropertyUtils.getProperty(bean, property);
                    if(valueRequired && value==null) {
                        // null and required
                        if(scope==null) throw new JspException(ApplicationResources.accessor.getMessage("PropertyUtils.value.required.nullScope", property, name));
                        else throw new JspException(ApplicationResources.accessor.getMessage("PropertyUtils.value.required.scope", property, name, scope));
                    }
                    return value;
                }
            }
        } catch(IllegalAccessException err) {
            throw new JspException(err);
        } catch(InvocationTargetException err) {
            throw new JspException(err);
        } catch(NoSuchMethodException err) {
            throw new JspException(err);
        }
    }
}

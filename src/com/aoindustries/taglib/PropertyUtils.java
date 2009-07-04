package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
     * @param isRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
     *                    throw a <code>JspException</code> with an appropriate localized message.
     *
     * @return  the resolved <code>Object</code> or <code>null</code> if not found.
     */
    public static Object findObject(PageContext pageContext, String scope, String name, String property, boolean isRequired) throws JspException {
        try {
            // Check the name
            if(name==null) throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getRequest().getLocale(), "PropertyUtils.name.required"));

            // Find the bean
            Object bean;
            if(scope==null) bean = pageContext.findAttribute(name);
            else if("page".equals(scope)) bean = pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
            else if("request".equals(scope)) bean = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
            else if("session".equals(scope)) bean = pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
            else if("application".equals(scope)) bean = pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
            else throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getRequest().getLocale(), "PropertyUtils.scope.invalid", scope));

            // Check required
            if(bean==null) {
                if(isRequired) {
                    // null and required
                    if(scope==null) throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getRequest().getLocale(), "PropertyUtils.bean.required.nullScope", name));
                    else throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getRequest().getLocale(), "PropertyUtils.bean.required.scope", name, scope));
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
                    if(isRequired && value==null) {
                        // null and required
                        if(scope==null) throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getRequest().getLocale(), "PropertyUtils.value.required.nullScope", property, name));
                        else throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getRequest().getLocale(), "PropertyUtils.value.required.scope", property, name, scope));
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

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2013  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.Coercion;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class ParamsTag
	extends AutoEncodingNullTag
	implements NameAttribute
{

    private Object name;
    private String exclude = null;
	private WildcardPatternMatcher excludeMatcher = WildcardPatternMatcher.getMatchNone();
    private Object values;

    @Override
    public MediaType getOutputType() {
        return null;
    }

    @Override
    public Object getName() {
        return name;
    }

    @Override
    public void setName(Object name) {
		this.name = name;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
		this.excludeMatcher = WildcardPatternMatcher.getInstance(exclude);
    }

	public void setValues(Object values) {
		this.values = values;
    }

    @Override
    protected void doTag(Writer out) throws JspException, IOException {
		ParamsAttribute paramsAttribute = AttributeUtils.findAttributeParent("params", this, "params", ParamsAttribute.class);
		if(values!=null) {
			if(name==null) {
				if(values instanceof Map<?,?>) {
					// Get from Map with exclude
					for(Map.Entry<?,?> entry : ((Map<?,?>)values).entrySet()) {
						Object entryKey = entry.getKey();
						if(entryKey!=null) {
							String paramName = Coercion.toString(entryKey);
							if(!excludeMatcher.isMatch(paramName)) {
								Object entryValue = entry.getValue();
								if(entryValue instanceof Iterable<?>) {
									ParamUtils.addIterableParams(
										paramsAttribute,
										paramName,
										(Iterable<?>)entryValue
									);
								} else if(entryValue instanceof Iterator<?>) {
									ParamUtils.addIteratorParams(
										paramsAttribute,
										paramName,
										(Iterator<?>)entryValue
									);
								} else if(entryValue.getClass().isArray()) {
									ParamUtils.addArrayParams(
										paramsAttribute,
										paramName,
										entryValue
									);
								} else {
									ParamUtils.addParam(paramsAttribute, paramName, entryValue);
								}
							}
						}
					}
				} else if(values instanceof HttpParameters) {
					// Get from HttpParameters with exclude
					HttpParameters httpParams = (HttpParameters)values;
					Iterator<String> paramNames = httpParams.getParameterNames();
					while(paramNames.hasNext()) {
						String paramName = paramNames.next();
						if(
							paramName!=null
							&& !excludeMatcher.isMatch(paramName)
						) {
							List<String> paramValues = httpParams.getParameterValues(paramName);
							if(paramValues!=null) {
								for(String paramValue : paramValues) {
									ParamUtils.addParam(paramsAttribute, paramName, paramValue);
								}
							}
						}
					}
				} else {
					throw new LocalizedJspException(ApplicationResources.accessor, "ParamsTag.mapRequiredWithName");
				}
			} else {
				// Exclude not allowed
				if(!excludeMatcher.isEmpty()) {
					throw new LocalizedJspException(ApplicationResources.accessor, "ParamsTag.excludesNotAllowedWithName");
				}
				final String paramName = Coercion.toString(name);
				if(values instanceof Iterable<?>) {
					ParamUtils.addIterableParams(
						paramsAttribute,
						paramName,
						(Iterable<?>)values
					);
				} else if(values instanceof Iterator<?>) {
					ParamUtils.addIteratorParams(
						paramsAttribute,
						paramName,
						(Iterator<?>)values
					);
				} else if(values.getClass().isArray()) {
					ParamUtils.addArrayParams(
						paramsAttribute,
						paramName,
						values
					);
				} else if(
					values instanceof Map<?,?>
					|| values instanceof HttpParameters
				) {
					throw new LocalizedJspException(ApplicationResources.accessor, "ParamsTag.mapWithNameNotAllowed");
				} else {
					throw new LocalizedJspException(ApplicationResources.accessor, "ParamsTag.values.unexpectedType", values.getClass().getName());
				}
			}
		}
    }
}

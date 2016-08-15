/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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

import com.aoindustries.lang.LocalizedIllegalArgumentException;
import com.aoindustries.servlet.http.Dispatcher;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.WildcardPatternMatcher;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.jsp.JspTagException;

/**
 * A dispatch tag with arguments.
 *
 * @author  AO Industries, Inc.
 */
abstract class ArgDispatchTag
	extends DispatchTag
	implements ArgsAttribute
{

	/**
	 * The prefix for argument attributes.
	 */
	private static final String ARG_ATTRIBUTE_PREFIX = Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME + ".";

	private String clearParams = null;
	private WildcardPatternMatcher clearParamsMatcher = WildcardPatternMatcher.getMatchNone();
	private Map<String,Object> args;

	public String getClearParams() {
		return clearParams;
	}

	public void setClearParams(String clearParams) {
		this.clearParams = clearParams;
		this.clearParamsMatcher = WildcardPatternMatcher.getInstance(clearParams);
	}

	@Override
	protected WildcardPatternMatcher getClearParamsMatcher() {
		return clearParamsMatcher;
	}

	@Override
	public Map<String,?> getArgs() {
		if(args==null) return Collections.emptyMap();
		return Collections.unmodifiableMap(args);
	}

	@Override
	public void addArg(String name, Object value) throws IllegalArgumentException {
		if(args==null) {
			args = new LinkedHashMap<String,Object>();
		} else if(args.containsKey(name)) {
			throw new LocalizedIllegalArgumentException(accessor, "DispatchTag.addArg.duplicateArgument", name);
		}
		args.put(name, value);
	}

	@Override
	protected String getDynamicAttributeExceptionKey() {
		return "error.unexpectedDynamicAttribute2";
	}

	@Override
	protected Serializable[] getDynamicAttributeExceptionArgs(String localName) {
		return new Serializable[] {
			localName,
			ARG_ATTRIBUTE_PREFIX+"*",
			ParamUtils.PARAM_ATTRIBUTE_PREFIX+"*"
		};
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(
			uri==null
			&& localName.startsWith(ARG_ATTRIBUTE_PREFIX)
		) {
			addArg(localName.substring(ARG_ATTRIBUTE_PREFIX.length()), value);
		} else {
			super.setDynamicAttribute(uri, localName, value);
		}
	}
}

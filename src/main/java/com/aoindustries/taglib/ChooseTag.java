/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2016  AO Industries, Inc.
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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class ChooseTag
	extends TagSupport
{

	private static final long serialVersionUID = 1L;

	private boolean hasWhen;
	private boolean matched;
	private boolean hasOtherwise;

	private void init() {
		hasWhen = false;
		matched = false;
		hasOtherwise = false;
	}

	public ChooseTag() {
		init();
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	void onWhen() throws JspTagException {
		if(hasOtherwise) {
			throw new JspTagException("<ao:when> may not follow <ao:otherwise>");
		} else {
			hasWhen = true;
		}
	}

	boolean getMatched() {
		return matched;
	}

	void setMatched() {
		assert !matched;
		matched = true;
	}

	void onOtherwise() throws JspTagException {
		if(hasOtherwise) {
			throw new JspTagException("Only one <ao:otherwise> allowed");
		} else {
			hasOtherwise = true;
		}
	}

	@Override
	public int doEndTag() throws JspTagException {
		try {
			if(!hasWhen) {
				throw new JspTagException("<ao:choose> requires at least one nested <ao:when>");
			}
			return EVAL_PAGE;
		} finally {
			init();
		}
	}
}

/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2016, 2017, 2020, 2021  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * @author  AO Industries, Inc.
 */
public class ChooseTag extends TagSupport implements TryCatchFinally {

	public static final String TAG_NAME = "<ao:choose>";

	private static final long serialVersionUID = 1L;

	public ChooseTag() {
		init();
	}

	private boolean hasWhen;
	private boolean matched;
	private boolean hasOtherwise;

	private void init() {
		hasWhen = false;
		matched = false;
		hasOtherwise = false;
	}

	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_INCLUDE;
	}

	void onWhen() throws JspTagException {
		if(hasOtherwise) {
			throw new JspTagException(WhenTag.TAG_NAME + " may not follow " + OtherwiseTag.TAG_NAME);
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
			throw new JspTagException("Only one " + OtherwiseTag.TAG_NAME + " allowed");
		} else {
			hasOtherwise = true;
		}
	}

	@Override
	public int doEndTag() throws JspException {
		if(!hasWhen) {
			throw new JspTagException(TAG_NAME + " requires at least one nested " + WhenTag.TAG_NAME);
		}
		return EVAL_PAGE;
	}

	@Override
	public void doCatch(Throwable t) throws Throwable {
		throw t;
	}

	@Override
	public void doFinally() {
		init();
	}
}

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

import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;
import static javax.servlet.jsp.tagext.IterationTag.EVAL_BODY_AGAIN;
import static javax.servlet.jsp.tagext.Tag.EVAL_PAGE;
import static javax.servlet.jsp.tagext.Tag.SKIP_BODY;
import static javax.servlet.jsp.tagext.Tag.SKIP_PAGE;

/**
 * Helper utilities for working with {@link BodyTag}.
 *
 * @author  AO Industries, Inc.
 */
final class BodyTagUtils  {

	static int checkAfterBodyReturn(int afterBodyReturn) throws JspTagException {
		if(
			afterBodyReturn != SKIP_BODY
			&& afterBodyReturn != EVAL_BODY_AGAIN
		) throw new LocalizedJspTagException(
			ApplicationResources.accessor,
			"BodyTagUtils.checkAfterBodyReturn.invalid",
			afterBodyReturn
		);
		return afterBodyReturn;
	}

	static int checkEndTagReturn(int endTagReturn) throws JspTagException {
		if(
			endTagReturn != EVAL_PAGE
			&& endTagReturn != SKIP_PAGE
		) throw new LocalizedJspTagException(
			ApplicationResources.accessor,
			"BodyTagUtils.checkEndTagReturn.invalid",
			endTagReturn
		);
		return endTagReturn;
	}

	/**
	 * Make no instances.
	 */
	private BodyTagUtils() {
	}
}

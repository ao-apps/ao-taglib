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

import java.io.IOException;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class WhenTag
	extends SimpleTagSupport
{

	private ValueExpression test;
	public void setTest(ValueExpression test) {
		this.test = test;
	}

	@Override
	public void doTag() throws JspException, IOException {
		JspTag parent = getParent();
		if(!(parent instanceof ChooseTag)) {
			throw new JspTagException("<ao:when> must be directly nested within <ao:choose>");
		}
		ChooseTag chooseTag = (ChooseTag)parent;
		chooseTag.onWhen();
		if(!chooseTag.getMatched()) {
			Boolean matched = (Boolean)test.getValue(getJspContext().getELContext());
			if(matched != null && matched) {
				chooseTag.setMatched();
				JspFragment body = getJspBody();
				if(body != null) {
					body.invoke(null);
				}
			}
		}
	}
}

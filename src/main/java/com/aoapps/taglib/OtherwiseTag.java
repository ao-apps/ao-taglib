/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class OtherwiseTag extends TagSupport {

  public static final String TAG_NAME = "<ao:otherwise>";

  private static final long serialVersionUID = 1L;

  @Override
  public int doStartTag() throws JspException {
    JspTag parent = getParent();
    if (!(parent instanceof ChooseTag)) {
      throw new JspTagException(TAG_NAME + " must be directly nested within " + ChooseTag.TAG_NAME);
    }
    ChooseTag chooseTag = (ChooseTag) parent;
    chooseTag.onOtherwise();
    return chooseTag.getMatched() ? SKIP_BODY : EVAL_BODY_INCLUDE;
  }
}

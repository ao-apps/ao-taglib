/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.taglib.legacy;

import com.aoapps.encoding.MediaType;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.taglib.GlobalAttributesUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class BrTag extends ElementNullBodyTag {

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

/* BodyTag only: */
  private static final long serialVersionUID = 1L;
/**/

  @Override
/* BodyTag only: */
  protected int doEndTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
  protected void doTag(Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext)getJspContext();
/**/
    GlobalAttributesUtils.doGlobalAttributes(
      global,
      new DocumentEE(
        pageContext.getServletContext(),
        (HttpServletRequest)pageContext.getRequest(),
        (HttpServletResponse)pageContext.getResponse(),
        out,
        false, // Do not add extra newlines to JSP
        false  // Do not add extra indentation to JSP
      ).br()
    ).__();
/* BodyTag only: */
    return EVAL_PAGE;
/**/
  }
}

/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2012, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.encoding.taglib.legacy.EncodingBufferedBodyTag;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.servlet.http.Includer;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class SendErrorTag extends EncodingBufferedBodyTag {

  public SendErrorTag() {
    init();
  }

  @Override
  public MediaType getContentType() {
    return MediaType.TEXT;
  }

  @Override
  public MediaType getOutputType() {
    return null;
  }

/* BodyTag only: */
  private static final long serialVersionUID = 1L;
/**/

  private int status;
  public void setStatus(int status) {
    this.status = status;
  }

  private String message;
  public void setMessage(String message) {
    this.message = message;
  }

  private void init() {
    status = 0;
    message = null;
  }

  @Override
/* BodyTag only: */
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext)getJspContext();
/**/
    if (message == null) {
      message = capturedBody.trim().toString();
    }

    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

    if (message == null || message.isEmpty()) {
      Includer.sendError(request, response, status);
    } else {
      Includer.sendError(request, response, status, message);
    }

    Includer.setPageSkipped(request);
/* BodyTag only: */
    return SKIP_PAGE;
/**/
/* SimpleTag only:
    throw com.aoapps.servlet.ServletUtil.SKIP_PAGE_EXCEPTION;
/**/
  }

/* BodyTag only: */
  @Override
  public void doFinally() {
    try {
      init();
    } finally {
      super.doFinally();
    }
  }
/**/
}

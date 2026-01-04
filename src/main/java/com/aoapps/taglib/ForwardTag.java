/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2025, 2026  AO Industries, Inc.
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

import com.aoapps.lang.attribute.Attribute;
import com.aoapps.servlet.ServletUtil;
import com.aoapps.servlet.http.Includer;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  AO Industries, Inc.
 */
// TODO: ForwardBodyTag and ForwardSimpleTag?
public class ForwardTag extends ArgDispatchTag {

  private static final Logger logger = Logger.getLogger(ForwardTag.class.getName());

  /**
   * Dispatch as forward.
   */
  @Override
  @SuppressWarnings("deprecation")
  void dispatch(RequestDispatcher dispatcher, JspWriter out, HttpServletRequest request, HttpServletResponse response) throws JspException, IOException {
    if (!response.isCommitted()) {
      try (Attribute.OldValue oldForwarded = FORWARDED_REQUEST_ATTRIBUTE.context(request).init(true)) {
        try {
          // Clear the previous JSP out buffer
          out.clear();
          dispatcher.forward(request, response);
        } catch (ServletException e) {
          throw new JspTagException(e);
        }
      }
    } else {
      logger.log(Level.FINE, "Not forwarding due to response already committed: request.servletPath = {0}, dispatcher = {1}",
          new Object[] {request.getServletPath(), dispatcher});
    }
    Includer.setPageSkipped(request);
    throw ServletUtil.SKIP_PAGE_EXCEPTION;
  }
}

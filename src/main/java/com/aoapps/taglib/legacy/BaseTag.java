/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.net.URIEncoder;
import com.aoapps.servlet.filter.EncodeURIFilter;
import com.aoapps.servlet.http.Dispatcher;
import com.aoapps.servlet.http.HttpServletUtil;
import com.aoapps.taglib.GlobalAttributesUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class BaseTag extends ElementNullBodyTag {

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

/* BodyTag only: */
  private static final long serialVersionUID = 1L;
/**/

  @Override
  @SuppressWarnings("StringEquality")
/* BodyTag only: */
  protected int doEndTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
  protected void doTag(Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext)getJspContext();
/**/
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    String originalPath = Dispatcher.getOriginalPagePath(request); // Before forward
    String currentPath = request.getServletPath(); // After forward
    if (
      // Quick check for common case (string identity equals intentional)
      originalPath != currentPath
    ) {
      // When the paths do not match, request has been forwarded to a different directory and base is required
      int originalLastSlash = originalPath.lastIndexOf('/');
      int currentLastSlash = currentPath.lastIndexOf('/');
      // Only include base when in different directories
      if (
        originalLastSlash != currentLastSlash
        || !originalPath.regionMatches(0, currentPath, 0, originalLastSlash)
      ) {
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        DocumentEE document = new DocumentEE(
          pageContext.getServletContext(),
          request,
          response,
          out,
          false, // Do not add extra newlines to JSP
          false  // Do not add extra indentation to JSP
        );

        // Note: This does not directly do response encodeURL because URL rewriting would interfere with the intent of the base tag

        String url = HttpServletUtil.getAbsoluteURL(request, currentPath.substring(0, currentLastSlash + 1));

        // TODO: Should we create a registry for things that want to modify the base URL,
        //       instead of this direct connection between BaseTag and EncodeURIFilter?
        EncodeURIFilter encodeURIFilter = EncodeURIFilter.getActiveFilter(request);
        if (encodeURIFilter != null) {
          url = encodeURIFilter.encode(
            url,
            document.encodingContext.getDoctype(),
            response.getCharacterEncoding()
          );
        } else {
          // Encoding US-ASCII
          // TODO: Implement this in ao:base instead (along with other URL implementations)?
          url = URIEncoder.encodeURI(url);
        }
        GlobalAttributesUtils.doGlobalAttributes(global, document.base())
          .href(url)
          .__();
      }
    }
/* BodyTag only: */
    return EVAL_PAGE;
/**/
  }
}

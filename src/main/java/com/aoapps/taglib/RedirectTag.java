/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2024  AO Industries, Inc.
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

import com.aoapps.hodgepodge.util.WildcardPatternMatcher;
import com.aoapps.html.any.attributes.url.Href;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.lang.attribute.Attribute;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.ServletUtil;
import com.aoapps.servlet.http.Includer;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.servlet.lastmodified.LastModifiedUtil;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;

/**
 * @author  AO Industries, Inc.
 */
// TODO: RedirectBodyTag and RedirectSimpleTag?
public class RedirectTag extends DispatchTag
    implements HrefAttribute {

  private static final Logger logger = Logger.getLogger(RedirectTag.class.getName());

  static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, RedirectTag.class);

  /**
   * The maximum length of a URL allowed for redirect.
   *
   * <p>Matching limit of Internet Explorer: <a href="http://support.microsoft.com/kb/208427">http://support.microsoft.com/kb/208427</a></p>
   *
   * @see <a href="http://www.boutell.com/newfaq/misc/urllength.html">WWW FAQs: What is the maximum length of a URL?</a>
   */
  private static final int MAXIMUM_GET_REQUEST_LENGTH = 2048;

  public static boolean isValidStatusCode(String statusCode) {
    return
        "moved_permanently".equals(statusCode)
            || "permanent".equals(statusCode)
            || "301".equals(statusCode)
            || "moved_temporarily".equals(statusCode)
            || "found".equals(statusCode)
            || "temporary".equals(statusCode)
            || "302".equals(statusCode)
            || "see_other".equals(statusCode)
            || "303".equals(statusCode);
  }

  private String statusCode;
  private String href;
  private boolean absolute = true;
  private boolean canonical;
  private AddLastModified addLastModified = AddLastModified.AUTO;

  public void setStatusCode(String statusCode) {
    statusCode = Strings.trim(statusCode);
    if (!isValidStatusCode(statusCode)) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "statusCode.invalid", statusCode);
    }
    this.statusCode = statusCode;
  }

  @Override
  public void setHref(String href) {
    this.href = Href.href.normalize(href);
  }

  public void setAbsolute(boolean absolute) {
    this.absolute = absolute;
  }

  public void setCanonical(boolean canonical) {
    this.canonical = canonical;
  }

  public void setAddLastModified(String addLastModified) {
    this.addLastModified = AddLastModified.valueOfLowerName(Strings.trim(addLastModified).toLowerCase(Locale.ROOT));
  }

  @Override
  protected WildcardPatternMatcher getClearParamsMatcher() {
    return WildcardPatternMatcher.matchAll();
  }

  /**
   * args will be set to <code>null</code> to simulate initial request conditions of a redirect
   * as well as possible.
   */
  @Override
  protected Map<String, ?> getArgs() {
    return null;
  }

  @Override
  protected void doTag(String servletPath) throws IOException, JspException, SkipPageException {
    final int status;
    if (statusCode == null) {
      throw new AttributeRequiredException("statusCode");
    }
    if (
        "301".equals(statusCode)
            || "moved_permanently".equals(statusCode)
            || "permanent".equals(statusCode)
    ) {
      status = HttpServletResponse.SC_MOVED_PERMANENTLY;
    } else if (
        "302".equals(statusCode)
            || "moved_temporarily".equals(statusCode)
            || "found".equals(statusCode)
            || "temporary".equals(statusCode)
    ) {
      status = HttpServletResponse.SC_MOVED_TEMPORARILY;
    } else if (
        "303".equals(statusCode)
            || "see_other".equals(statusCode)
    ) {
      status = HttpServletResponse.SC_SEE_OTHER;
    } else {
      throw new AssertionError("Unexpected value for statusCode: " + statusCode);
    }

    final PageContext pageContext = (PageContext) getJspContext();
    final ServletContext servletContext = pageContext.getServletContext();
    final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    final HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

    // Add any parameters to the URL
    String myHref = href;
    if (myHref == null) {
      // Default to page when href not given
      myHref = page;
    }
    if (myHref == null) {
      throw new AttributeRequiredException("href");
    }

    // Get the full URL that will be used for the redirect
    String location = LastModifiedUtil.buildRedirectURL(
        servletContext,
        request,
        response,
        servletPath,
        myHref,
        params,
        addLastModified,
        absolute,
        canonical
    );
    boolean isTooLong = location.length() > MAXIMUM_GET_REQUEST_LENGTH;
    if (!isTooLong || page == null) {
      if (isTooLong) {
        // Warn about location too long
        if (logger.isLoggable(Level.WARNING)) {
          logger.warning(
              RESOURCES.getMessage(
                  "locationTooLongWarning",
                  MAXIMUM_GET_REQUEST_LENGTH,
                  location.length(),
                  location.substring(0, 100)
              )
          );
        }
      }

      Includer.setLocation(request, response, location);
      Includer.sendError(request, response, status);
      Includer.setPageSkipped(request);
      throw ServletUtil.SKIP_PAGE_EXCEPTION;
    } else {
      // Set no-cache header for 302 and 303 redirect
      if (
          status == HttpServletResponse.SC_MOVED_TEMPORARILY
              || status == HttpServletResponse.SC_SEE_OTHER
      ) {
        response.setHeader("cache-control", "no-cache");
      }
    }
  }

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

/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

import static com.aoapps.servlet.filter.FunctionContext.getRequest;
import static com.aoapps.servlet.filter.FunctionContext.getResponse;
import static com.aoapps.servlet.filter.FunctionContext.getServletContext;

import com.aoapps.hodgepodge.util.Tuple2;
import com.aoapps.lang.NullArgumentException;
import com.aoapps.lang.Projects;
import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Locales;
import com.aoapps.net.URIEncoder;
import com.aoapps.servlet.ServletContextCache;
import com.aoapps.servlet.attribute.ScopeEE;
import com.aoapps.servlet.filter.FunctionContext;
import com.aoapps.servlet.http.Dispatcher;
import com.aoapps.servlet.http.HttpServletUtil;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.servlet.lastmodified.LastModifiedServlet;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

/**
 * Tag library function implementations.
 */
public final class Functions {

  /** Make no instances. */
  private Functions() {
    throw new AssertionError();
  }

  private static final Logger logger = Logger.getLogger(Functions.class.getName());

  /**
   * Gets the lastModified or {@code 0} when not known.
   *
   * @see  LastModifiedServlet#getLastModified(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, java.lang.String)
   */
  public static long getLastModified(String url) throws MalformedURLException, URISyntaxException {
    HttpServletRequest request = getRequest();
    // Get the context-relative path (resolves relative paths)
    String resourcePath = HttpServletUtil.getAbsolutePath(request, url);
    if (resourcePath.startsWith("/")) {
      URI resourcePathURI = new URI(
          URIEncoder.encodeURI(
              resourcePath
          )
      );
      return LastModifiedServlet.getLastModified(
          getServletContext(),
          request,
          resourcePathURI.getPath()
      );
    }
    return 0;
  }

  public static String addLastModified(String url) throws MalformedURLException {
    HttpServletRequest request = getRequest();
    return LastModifiedServlet.addLastModified(
        getServletContext(),
        request,
        Dispatcher.getCurrentPagePath(request),
        url,
        AddLastModified.TRUE
    );
  }

  public static String encodeURL(String url) {
    return getResponse().encodeURL(URIEncoder.encodeURI(url));
  }

  /**
   * @see  HttpServletUtil#getAbsolutePath(javax.servlet.http.HttpServletRequest, java.lang.String)
   */
  public static String getAbsolutePath(String relPath) throws MalformedURLException {
    return HttpServletUtil.getAbsolutePath(getRequest(), relPath);
  }

  /**
   * @see  HttpServletUtil#getAbsoluteURL(javax.servlet.http.HttpServletRequest, java.lang.String)
   */
  public static String getAbsoluteURL(String relPath) {
    return HttpServletUtil.getAbsoluteURL(getRequest(), relPath);
  }

  public static String getDecimalTimeLength(Long millis) {
    return millis == null ? null : Strings.getDecimalTimeLengthString(millis);
  }

  /**
   * Application-scope cached key.
   */
  private static final ScopeEE.Application.Attribute<ConcurrentMap<String, Tuple2<Long, String>>> RESOURCE_PROJECT_VERSIONS_APPLICATION_KEY =
      ScopeEE.APPLICATION.attribute(Functions.class.getName() + ".getProjectVersion.resourceProjectVersions");

  /**
   * A distinct String instance used to represent not found, since {@link ConcurrentHashMap} does not support
   * {@code null} values.
   */
  @SuppressWarnings("RedundantStringConstructorCall")
  private static final String NOT_FOUND = new String("<<<NOT_FOUND>>>");

  private static final ConcurrentMap<Tuple2<String, String>, String> classPathProjectVersions = new ConcurrentHashMap<>();

  @SuppressWarnings("StringEquality")
  public static String getProjectVersion(String lib, String groupId, String artifactId, String def) throws IOException {
    ServletContext servletContext = getServletContext();
    HttpServletRequest request = getRequest();
    // Make lib relative to the current page
    String absLib = HttpServletUtil.getAbsolutePath(request, lib);
    String resourceName;
      {
        StringBuilder sb = new StringBuilder();
        sb.append(absLib);
        int len = sb.length();
        if (len == 0 || sb.charAt(len - 1) != '/') {
          sb.append('/');
        }
        sb.append("META-INF/maven/").append(groupId).append('/').append(artifactId).append("/pom.properties");
        resourceName = sb.toString();
      }
    // Find the current resource modified time
    long lastModified = ServletContextCache.getLastModified(servletContext, resourceName);
    if (logger.isLoggable(Level.FINER)) {
      logger.finer(
          "lib.........: \"" + lib + "\"\n"
              + "groupId.....: \"" + groupId + "\"\n"
              + "artifactId..: \"" + artifactId + "\"\n"
              + "def.........: \"" + def + "\"\n"
              + "absLib......: \"" + absLib + "\"\n"
              + "resourceName: \"" + resourceName + "\"\n"
              + "lastModified: \"" + (lastModified == 0 ? "Unknown" : new Date(lastModified)) + "\"\n"
      );
    }
    if (lastModified != 0) {
      // Get the application-scope cache
      ConcurrentMap<String, Tuple2<Long, String>> resourceProjectVersions =
          RESOURCE_PROJECT_VERSIONS_APPLICATION_KEY.context(servletContext)
              .computeIfAbsent(name -> new ConcurrentHashMap<>());
      // Find any cache entry
      Tuple2<Long, String> cached = resourceProjectVersions.get(resourceName);
      String result;
      if (cached != null && cached.getElement1() == lastModified) {
        result = cached.getElement2();
        if (logger.isLoggable(Level.FINER)) {
          logger.finer(
              "Found in resourceProjectVersions cache: version \"" + result
                  + "\" from \"" + resourceName + '"'
          );
        }
        if (result == NOT_FOUND) {
          result = null;
        }
      } else {
        try (InputStream in = servletContext.getResourceAsStream(resourceName)) {
          result = Projects.readVersion(resourceName, in, groupId, artifactId);
        }
        if (logger.isLoggable(Level.FINE)) {
          logger.fine(
              "Store in resourceProjectVersions cache: version \"" + ((result == null) ? NOT_FOUND : result)
                  + "\" from \"" + resourceName + '"'
          );
        }
        // Cache result
        resourceProjectVersions.put(
            resourceName,
            new Tuple2<>(
                lastModified,
                (result == null) ? NOT_FOUND : result
            )
        );
      }
      // Return if found
      if (result != null) {
        return result;
      }
    }
    // Check for cached in classpath (cached found or not found)
    Tuple2<String, String> classPathKey = new Tuple2<>(groupId, artifactId);
    String result = classPathProjectVersions.get(classPathKey);
    if (result != null) {
      if (logger.isLoggable(Level.FINER)) {
        logger.finer(
            "Found in classPathProjectVersions cache: version \"" + result
                + "\" from \"" + groupId + ':' + artifactId + '"'
        );
      }
    } else {
      // Search classpath
      result = Projects.getVersion(groupId, artifactId);
      if (logger.isLoggable(Level.FINE)) {
        logger.fine(
            "Store in classPathProjectVersions cache: version \"" + ((result == null) ? NOT_FOUND : result)
                + "\" from \"" + groupId + ':' + artifactId + '"'
        );
      }
      // Store result as permanent cache entry
      classPathProjectVersions.put(
          classPathKey,
          (result == null) ? NOT_FOUND : result
      );
    }
    // Return default if not found
    return (result == null) ? def : result;
  }

  /**
   * @see DispatchTag#isForwarded(javax.servlet.ServletRequest)
   */
  @SuppressWarnings("deprecation")
  public static boolean isForwarded() {
    return DispatchTag.isForwarded(FunctionContext.getRequest());
  }

  public static boolean isRtl() {
    return Locales.isRightToLeft(FunctionContext.getResponse().getLocale());
  }

  public static String join(Iterable<?> iter, String separator) {
    if (iter == null) {
      return null;
    }
    return Strings.join(iter, separator);
  }

  public static String message(String key) throws JspTagException {
    NullArgumentException.checkNotNull(key, "key");
    BundleTag bundleTag = BundleTag.getBundleTag(getRequest());
    if (bundleTag == null) {
      throw new LocalizedJspTagException(MessageTag.RESOURCES, "requiredParentTagNotFound", "bundle");
    }
    String prefix = bundleTag.getPrefix();
    return bundleTag.getResources().getMessage(
        prefix == null || prefix.isEmpty() ? key : prefix.concat(key)
    );
  }

  public static boolean resourceExists(String path) throws MalformedURLException {
    return ServletContextCache.getResource(FunctionContext.getServletContext(), path) != null;
  }
}

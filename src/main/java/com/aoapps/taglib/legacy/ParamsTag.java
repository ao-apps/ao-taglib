/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import static com.aoapps.taglib.ParamsTag.RESOURCES;
import static com.aoapps.taglib.ParamsTag.TAG_NAME;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.legacy.EncodingNullBodyTag;
import com.aoapps.hodgepodge.util.WildcardPatternMatcher;
import com.aoapps.lang.Coercion;
import com.aoapps.net.URIParameters;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.NameAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
// TODO: Support output when not inside a containing tag? (or implement applet and object tags to avoid errors of params accidentally passed to client)
public class ParamsTag extends EncodingNullBodyTag
    implements NameAttribute {

  /* SimpleTag only:
    public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, ParamsTag.class);

    public static final String TAG_NAME = "<ao:params>";
  /**/

  public ParamsTag() {
    init();
  }

  @Override
  public MediaType getOutputType() {
    return null;
  }

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;
  /**/

  private String name;

  @Override
  public void setName(String name) {
    this.name = name;
  }

  // private String exclude;
  private WildcardPatternMatcher excludeMatcher;

  public void setExclude(String exclude) {
    // this.exclude = exclude;
    this.excludeMatcher = WildcardPatternMatcher.compile(exclude);
  }

  private Object values;

  public void setValues(Object values) {
    this.values = values;
  }

  private void init() {
    name = null;
    // exclude = null;
    excludeMatcher = WildcardPatternMatcher.matchNone();
    values = null;
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
      protected void doTag(Writer out) throws JspException, IOException {
    /**/
    // TODO: These supported types match Html.java and Param.java in ao-fluent-html?
    ParamsAttribute paramsAttribute = AttributeUtils.requireAttributeParent(TAG_NAME, this, "params", ParamsAttribute.class);
    if (values != null) {
      if (name == null) {
        if (values instanceof Map<?, ?>) {
          // Get from Map with exclude
          for (Map.Entry<?, ?> entry : ((Map<?, ?>) values).entrySet()) {
            Object entryKey = entry.getKey();
            if (entryKey != null) {
              String paramName = Coercion.toString(entryKey);
              if (!excludeMatcher.isMatch(paramName)) {
                Object entryValue = entry.getValue();
                if (entryValue instanceof Iterable<?>) {
                  ParamUtils.addIterableParams(
                      paramsAttribute,
                      paramName,
                      (Iterable<?>) entryValue
                  );
                } else if (entryValue instanceof Iterator<?>) {
                  ParamUtils.addIteratorParams(
                      paramsAttribute,
                      paramName,
                      (Iterator<?>) entryValue
                  );
                } else if (entryValue instanceof Enumeration<?>) {
                  ParamUtils.addEnumerationParams(
                      paramsAttribute,
                      paramName,
                      (Enumeration<?>) entryValue
                  );
                } else if (entryValue != null && entryValue.getClass().isArray()) {
                  ParamUtils.addArrayParams(
                      paramsAttribute,
                      paramName,
                      entryValue
                  );
                } else {
                  ParamUtils.addParam(paramsAttribute, paramName, entryValue);
                }
              }
            }
          }
        } else if (values instanceof URIParameters) {
          // Get from HttpParameters with exclude
          URIParameters uriParams = (URIParameters) values;
          Iterator<String> paramNames = uriParams.getParameterNames();
          while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            if (!excludeMatcher.isMatch(paramName)) {
              List<String> paramValues = uriParams.getParameterValues(paramName);
              if (paramValues != null) {
                for (String paramValue : paramValues) {
                  ParamUtils.addParam(paramsAttribute, paramName, paramValue);
                }
              }
            }
          }
        } else {
          throw new LocalizedJspTagException(RESOURCES, "mapRequiredWithName");
        }
      } else {
        // Exclude not allowed
        if (!excludeMatcher.isEmpty()) {
          throw new LocalizedJspTagException(RESOURCES, "excludesNotAllowedWithName");
        }
        if (values instanceof Iterable<?>) {
          ParamUtils.addIterableParams(
              paramsAttribute,
              name,
              (Iterable<?>) values
          );
        } else if (values instanceof Iterator<?>) {
          ParamUtils.addIteratorParams(
              paramsAttribute,
              name,
              (Iterator<?>) values
          );
        } else if (values instanceof Enumeration<?>) {
          ParamUtils.addEnumerationParams(
              paramsAttribute,
              name,
              (Enumeration<?>) values
          );
        } else if (values.getClass().isArray()) {
          ParamUtils.addArrayParams(
              paramsAttribute,
              name,
              values
          );
        } else if (
            values instanceof Map<?, ?>
                || values instanceof URIParameters
        ) {
          throw new LocalizedJspTagException(RESOURCES, "mapWithNameNotAllowed");
        } else {
          throw new LocalizedJspTagException(RESOURCES, "values.unexpectedType", values.getClass().getName());
        }
      }
    }
    /* BodyTag only: */
    return EVAL_PAGE;
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

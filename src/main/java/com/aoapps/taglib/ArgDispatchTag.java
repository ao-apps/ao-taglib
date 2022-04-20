/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.http.Dispatcher;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspTagException;

/**
 * A dispatch tag with arguments.
 *
 * @author  AO Industries, Inc.
 */
// TODO: ArgDispatchBodyTag and ArgDispatchSimpleTag?
abstract class ArgDispatchTag extends DispatchTag
  implements ArgsAttribute
{

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, ArgDispatchTag.class);

  /**
   * The prefix for argument attributes.
   */
  private static final String ARG_ATTRIBUTE_PREFIX = Dispatcher.ARG_REQUEST_ATTRIBUTE.getName() + ".";

  private WildcardPatternMatcher clearParamsMatcher = WildcardPatternMatcher.matchNone();
  private Map<String, Object> args;

  public void setClearParams(String clearParams) {
    this.clearParamsMatcher = WildcardPatternMatcher.compile(clearParams);
  }

  @Override
  protected WildcardPatternMatcher getClearParamsMatcher() {
    return clearParamsMatcher;
  }

  @Override
  protected Map<String, ?> getArgs() {
    if (args == null) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(args);
  }

  @Override
  public void addArg(String name, Object value) {
    if (args == null) {
      args = new LinkedHashMap<>();
    } else if (args.containsKey(name)) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "addArg.duplicateArgument", name);
    }
    args.put(name, value);
  }

  /**
   * @see  #addArg(java.lang.String, java.lang.Object)
   */
  @Override
  protected boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns) throws JspTagException {
    if (super.addDynamicAttribute(uri, localName, value, expectedPatterns)) {
      return true;
    } else if (
      uri == null
      && localName.startsWith(ARG_ATTRIBUTE_PREFIX)
    ) {
      addArg(localName.substring(ARG_ATTRIBUTE_PREFIX.length()), value);
      return true;
    } else {
      expectedPatterns.add(ARG_ATTRIBUTE_PREFIX + "*");
      return false;
    }
  }
}

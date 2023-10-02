/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import static com.aoapps.taglib.MessageTag.RESOURCES;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.legacy.EncodingNullBodyTag;
import com.aoapps.hodgepodge.i18n.BundleLookupMarkup;
import com.aoapps.hodgepodge.i18n.BundleLookupThreadContext;
import com.aoapps.html.any.attributes.text.Type;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.BundleTag;
import com.aoapps.taglib.MessageArgsAttribute;
import com.aoapps.taglib.TypeAttribute;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class MessageTag extends EncodingNullBodyTag
    implements
    DynamicAttributes,
    TypeAttribute,
    MessageArgsAttribute {

  /* SimpleTag only:
    public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, MessageTag.class);
  /**/

  public MessageTag() {
    init();
  }

  @Override
  public MediaType getOutputType() {
    return mediaType;
  }

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;
  /**/

  private String bundle;

  public void setBundle(String bundle) {
    this.bundle = bundle;
  }

  private String key;

  public void setKey(String key) {
    this.key = key;
  }

  private MediaType mediaType;

  @Override
  public void setType(Object type) throws IOException {
    String typeStr = Coercion.toString(Type.type.normalize(type));
    MediaType newMediaType = MediaType.getMediaTypeByName(typeStr);
    if (newMediaType == null) {
      try {
        newMediaType = MediaType.getMediaTypeForContentType(typeStr);
      } catch (UnsupportedEncodingException e) {
        throw new IllegalArgumentException(e);
      }
    }
    this.mediaType = newMediaType;
  }

  private BitSet messageArgsSet;
  private List<Object> messageArgs;

  @Override
  public void addMessageArg(Object value) {
    // Create lists on first use
    if (messageArgs == null) {
      messageArgsSet = new BitSet();
      messageArgs = new ArrayList<>();
    }
    // Find the first index that is not used
    int index = messageArgsSet.nextClearBit(0);
    messageArgsSet.set(index);
    if (index >= messageArgs.size()) {
      assert index == messageArgs.size();
      messageArgs.add(value);
    } else {
      messageArgs.set(index, value);
    }
  }

  public void setArg0(Object value) {
    insertMessageArg("arg0", 0, value);
  }

  public void setArg1(Object value) {
    insertMessageArg("arg1", 1, value);
  }

  public void setArg2(Object value) {
    insertMessageArg("arg2", 2, value);
  }

  public void setArg3(Object value) {
    insertMessageArg("arg3", 3, value);
  }

  @Override
  public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
    if (uri == null && localName.startsWith("arg")) {
      try {
        String numSubstring = localName.substring(3);
        int index = Integer.parseInt(numSubstring);
        // Do not allow "arg00" in place of "arg0"
        if (!numSubstring.equals(Integer.toString(index))) {
          throw new LocalizedJspTagException(AttributeUtils.RESOURCES, "unexpectedDynamicAttribute1", localName, "arg*");
        }
        insertMessageArg(localName, index, value);
      } catch (NumberFormatException err) {
        throw new LocalizedJspTagException(err, AttributeUtils.RESOURCES, "unexpectedDynamicAttribute1", localName, "arg*");
      }
    } else {
      throw new LocalizedJspTagException(AttributeUtils.RESOURCES, "unexpectedDynamicAttribute1", localName, "arg*");
    }
  }

  private void insertMessageArg(String localName, int index, Object value) throws IllegalArgumentException {
    // Create lists on first use
    if (messageArgs == null) {
      messageArgsSet = new BitSet();
      messageArgs = new ArrayList<>();
    }
    // Must not already be set
    if (messageArgsSet.get(index)) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "duplicateArgument", localName);
    }
    messageArgsSet.set(index);
    if (index >= messageArgs.size()) {
      while (messageArgs.size() < index) {
        messageArgs.add(null);
      }
      assert index == messageArgs.size();
      messageArgs.add(value);
    } else {
      if (messageArgs.set(index, value) != null) {
        throw new AssertionError();
      }
    }
  }

  private String lookupResult;
  private BundleLookupMarkup lookupMarkup;

  private void init() {
    bundle = null;
    key = null;
    mediaType = MediaType.TEXT;
    if (messageArgsSet != null) {
      messageArgsSet.clear();
    }
    if (messageArgs != null) {
      messageArgs.clear();
    }
    lookupResult = null;
    lookupMarkup = null;
  }

  @Override
  @SuppressWarnings("deprecation")
  protected void writePrefix(MediaType containerType, Writer out) throws JspException, IOException {
    Resources resources;
    String combinedKey;
    if (bundle != null) {
      resources = Resources.getResources(bundle);
      combinedKey = key;
    } else {
      // Find parent bundle
      /* SimpleTag only:
      PageContext pageContext = (PageContext)getJspContext();
      /**/
      BundleTag bundleTag = BundleTag.getBundleTag(pageContext.getRequest());
      if (bundleTag == null) {
        throw new LocalizedJspTagException(RESOURCES, "requiredParentTagNotFound", "bundle");
      }
      resources = bundleTag.getResources();
      String prefix = bundleTag.getPrefix();
      combinedKey = prefix == null || prefix.isEmpty() ? key : prefix.concat(key);
    }
    // Lookup the message value
    if (messageArgs == null) {
      lookupResult = resources.getMessage(combinedKey);
    } else {
      // Error if gap in message args (any not set in range)
      int firstClear = messageArgsSet.nextClearBit(0);
      if (firstClear < messageArgs.size()) {
        throw new LocalizedJspTagException(RESOURCES, "argumentMissing", firstClear);
      }
      lookupResult = resources.getMessage(combinedKey, messageArgs.toArray());
    }
    // Look for any message markup
    BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext();
    if (threadContext != null) {
      lookupMarkup = threadContext.getLookupMarkup(lookupResult);
    }
    if (lookupMarkup != null) {
      lookupMarkup.appendPrefixTo(containerType.getMarkupType(), out);
    }
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
      protected void doTag(Writer out) throws JspException, IOException {
    /**/
    out.write(lookupResult);
    /* BodyTag only: */
    return EVAL_PAGE;
    /**/
  }

  @Override
  protected void writeSuffix(MediaType containerType, Writer out) throws JspException, IOException {
    if (lookupMarkup != null) {
      lookupMarkup.appendSuffixTo(containerType.getMarkupType(), out);
    }
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

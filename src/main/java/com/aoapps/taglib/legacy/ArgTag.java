/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import static com.aoapps.taglib.ArgTag.TAG_NAME;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.legacy.EncodingBufferedBodyTag;
import com.aoapps.html.any.attributes.text.Name;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.taglib.ArgsAttribute;
import com.aoapps.taglib.AttributeRequiredException;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.NameAttribute;
import com.aoapps.taglib.ValueAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class ArgTag extends EncodingBufferedBodyTag
    implements
    NameAttribute,
    ValueAttribute {

  /* SimpleTag only:
    public static final String TAG_NAME = "<ao:arg>";
  /**/

  public ArgTag() {
    init();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
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

  private transient String name;

  @Override
  public void setName(Object name) {
    name = Name.name.normalize(name);
    this.name = (name == null) ? null : Coercion.toString(name);
  }

  private transient Object value;

  @Override
  public void setValue(Object value) {
    this.value = value;
  }

  private void init() {
    name = null;
    value = null;
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
      protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    /**/
    if (name == null) {
      throw new AttributeRequiredException("name");
    }
    if (value == null) {
      setValue(capturedBody.trim());
    }
    AttributeUtils.requireAttributeParent(TAG_NAME, this, "args", ArgsAttribute.class)
        .addArg(name, value);
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

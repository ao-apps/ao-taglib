/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.encoding.MediaType;
import com.aoapps.lang.Strings;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTagTEI extends ElementTagTEI {

  @Override
  protected void validate(TagData data, List<ValidationMessage> messages) {
    super.validate(data, messages);
    Object typeAttr = data.getAttribute("type");
    if (
        typeAttr != null
            && typeAttr != TagData.REQUEST_TIME_VALUE
    ) {
      String type = Strings.trimNullIfEmpty((String) typeAttr); // TODO: normalizeType
      if (type != null) {
        try {
          MediaType mediaType = MediaType.getMediaTypeForContentType(type);
          if (mediaType != MediaType.JAVASCRIPT) {
            messages.add(
                new ValidationMessage(
                    data.getId(),
                    ScriptTag.RESOURCES.getMessage("unsupportedMediaType", type)
                )
            );
          }
        } catch (UnsupportedEncodingException err) {
          messages.add(
              new ValidationMessage(data.getId(), err.getMessage())
          );
        }
      }
    }
  }
}

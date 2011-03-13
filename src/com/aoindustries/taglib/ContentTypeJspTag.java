/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.ValidMediaInput;
import com.aoindustries.encoding.MediaType;
import javax.servlet.jsp.tagext.JspTag;

/**
 * Indicates that a tag contains a specific type of content in order to allow the
 * correct encoding of data between various types.  Any nested <code>tags</code>
 * must encode their data properly for this content type.  Likewise, this tag
 * must encode its output, including the output of its nested tags, properly for
 * the content type of its nearest <code>ContentTypeJspTag</code> parent or the
 * content type of the <code>HttpServletResponse</code> if no such parent is
 * found.
 *
 * @author  AO Industries, Inc.
 */
public interface ContentTypeJspTag extends JspTag, ValidMediaInput {

    /**
     * Gets the type of data that is contained by this tag.
     */
    MediaType getContentType();
}

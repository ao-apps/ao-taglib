package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaType;
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
public interface ContentTypeJspTag extends JspTag {

    /**
     * Gets the type of data that is contained by this tag.
     */
    MediaType getContentType();
}

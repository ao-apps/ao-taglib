package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaType;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class PreTag extends AutoEncodingTag {

    private static final long serialVersionUID = 1L;

    public MediaType getContentType() {
        return MediaType.XHTML_PRE;
    }

    @Override
    public int doAutoEncodingStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doAutoEncodingEndTag() throws JspException {
        return EVAL_PAGE;
    }
}

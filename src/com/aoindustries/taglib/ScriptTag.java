package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaException;
import com.aoindustries.media.MediaType;
import java.util.Locale;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag extends AutoEncodingTag {

    private static final long serialVersionUID = 1L;

    private MediaType type;

    @Override
    protected void init() {
        type = MediaType.PLAINTEXT;
    }

    public MediaType getContentType() {
        return type;
    }

    @Override
    public int doAutoEncodingStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doAutoEncodingEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public String getType() {
        return type.getMediaType();
    }

    public void setType(String type) throws MediaException {
        Locale locale = Locale.getDefault();
        MediaType newType = MediaType.getMediaType(locale, type);
        if(newType!=MediaType.JAVASCRIPT) throw new MediaException(ApplicationResourcesAccessor.getMessage(locale, "ScriptTag.unsupportedMediaType", type));
        this.type = newType;
    }
}

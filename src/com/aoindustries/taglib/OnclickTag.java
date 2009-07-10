package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.StringBuilderWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class OnclickTag extends AutoEncodingBufferedTag {

    public MediaType getContentType() {
        return MediaType.JAVASCRIPT;
    }

    public MediaType getOutputType() {
        return null;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        JspTag parent = getParent();
        if(parent==null || !(parent instanceof OnclickAttribute)) {
            PageContext pageContext = (PageContext)getJspContext();
            Locale userLocale = pageContext.getResponse().getLocale();
            throw new JspException(ApplicationResourcesAccessor.getMessage(userLocale, "OnclickTag.needOnclickAttributeParent"));
        }
        OnclickAttribute onclickAttribute = (OnclickAttribute)parent;
        onclickAttribute.setOnclick(capturedBody.toString());
    }
}

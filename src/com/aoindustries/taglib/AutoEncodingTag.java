package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaEncoder;
import com.aoindustries.media.MediaException;
import com.aoindustries.media.MediaType;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>
 * An implementation of <code>Tag</code> that automatically encodes
 * its output correctly given its context.  To determine its context, it finds
 * its nearest ancestore that also implements <code>ContentTypeJspTag</code>.  It
 * then uses the content type of that tag to perform proper encoding.  If it
 * fails to find any such parent, it uses the content type of the
 * <code>HttpServletResponse</code>.
 * </p>
 * <p>
 * In addition, this implements an <code>init</code> method to ease the resetting
 * of field states between tag invocations because some JSP environments reuse
 * tag instances.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class AutoEncodingTag extends TagSupport implements ContentTypeJspTag {

    private MediaEncoder mediaEncoder;

    public AutoEncodingTag() {
        doInit();
    }

    private void doInit() {
        try {
            init();
        } finally {
            mediaEncoder = null;
        }
    }

    /**
     * <p>
     * (Re)initializes all fields of this tag to prepare it for its next use.
     * This is called during construction and at doEndTag.  Any tags that have
     * attributes that are not required should clear them or set their default
     * values in the init method because tag instances may be reused by some JSP
     * environments.
     * </p>
     * <p>
     * This default implementation does nothing.  There is no need to invoke
     * <code>super.init()</code> when overriding this method.
     * </p>
     */
    protected void init() {
    }

    /**
     * @see ContentTypeJspTag#getContentType()
     */
    public abstract MediaType getContentType();

    @Override
    final public int doStartTag() throws JspException {
        try {
            // Find the content type of the nearest parent
            ContentTypeJspTag parent = (ContentTypeJspTag)findAncestorWithClass(this, ContentTypeJspTag.class);
            Locale userLocale = pageContext.getResponse().getLocale();
            MediaType outputContentType = parent!=null ? parent.getContentType() : MediaType.getMediaType(userLocale, pageContext.getResponse().getContentType());
            MediaType myContentType = getContentType();
            mediaEncoder = MediaEncoder.getMediaEncoder(userLocale, myContentType, outputContentType, pageContext.getOut());
            if(mediaEncoder!=null) {
                pageContext.pushBody(mediaEncoder);
                mediaEncoder.writePrefix();
            }
            return doAutoEncodingStartTag();
        } catch(IOException err) {
            throw new JspException(err);
        } catch(MediaException err) {
            throw new JspException(err);
        }
    }

    /**
     * Once the out JspWriter has been replaced to output the proper content
     * type, this version of doStartTag is called.
     *
     * @return This default implementation returns SKIP_BODY.
     * @throws javax.servlet.jsp.JspException
     */
    public int doAutoEncodingStartTag() throws JspException {
        return SKIP_BODY;
    }

    /**
     * This version of doEndTag is called before the JspWriter is restored to
     * its original object.
     *
     * @return This default implementation returns EVAL_PAGE.
     * @throws javax.servlet.jsp.JspException
     */
    public int doAutoEncodingEndTag() throws JspException {
        return EVAL_PAGE;
    }

    @Override
    final public int doEndTag() throws JspException {
        try {
            return doAutoEncodingEndTag();
        } finally {
            try {
                if(mediaEncoder!=null) {
                    try {
                        mediaEncoder.writeSuffix();
                    } catch(IOException err) {
                        throw new JspException(err);
                    } finally {
                        pageContext.popBody();
                    }
                }
            } finally {
                doInit();
            }
        }
    }
}

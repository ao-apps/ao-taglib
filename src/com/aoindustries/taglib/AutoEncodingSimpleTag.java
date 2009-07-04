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
import java.io.Writer;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <p>
 * An implementation of <code>SimpleTag</code> that automatically encodes
 * its output correctly given its context.  To determine its context, it finds
 * its nearest ancestore that also implements <code>ContentTypeJspTag</code>.  It
 * then uses the content type of that tag to perform proper encoding.  If it
 * fails to find any such parent, it uses the content type of the
 * <code>HttpServletResponse</code>.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class AutoEncodingSimpleTag extends SimpleTagSupport implements ContentTypeJspTag {

    /**
     * @see ContentTypeJspTag#getContentType()
     */
    public abstract MediaType getContentType();

    @Override
    final public void doTag() throws JspException, IOException {
        try {
            // Find our context
            PageContext pageContext = (PageContext)getJspContext();
            // Find the content type of the nearest parent
            ContentTypeJspTag parent = (ContentTypeJspTag)findAncestorWithClass(this, ContentTypeJspTag.class);
            Locale userLocale = pageContext.getResponse().getLocale();
            MediaType outputContentType = parent!=null ? parent.getContentType() : MediaType.getMediaType(userLocale, pageContext.getResponse().getContentType());
            MediaType myContentType = getContentType();
            // Find the encoder
            JspWriter out = pageContext.getOut();
            MediaEncoder mediaEncoder = MediaEncoder.getMediaEncoder(userLocale, myContentType, outputContentType, out);
            if(mediaEncoder!=null) {
                // Filter content
                mediaEncoder.writePrefix();
                invokeAutoEncoding(mediaEncoder);
                mediaEncoder.writeSuffix();
            } else {
                // No filter
                invokeAutoEncoding(out);
            }
        } catch(MediaException err) {
            throw new JspException(err);
        }
    }

    /**
     * Once the out JspWriter has been replaced to output the proper content
     * type, this version of invoke is called.
     *
     * @return This default implementation invokes the jsp body, if present.
     * @throws javax.servlet.jsp.JspException
     */
    public void invokeAutoEncoding(Writer out) throws JspException, IOException {
        JspFragment body = getJspBody();
        if(body!=null) body.invoke(out);
    }
}

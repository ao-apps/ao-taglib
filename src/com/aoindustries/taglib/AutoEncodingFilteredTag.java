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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.ValidMediaInput;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.MediaValidator;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <p>
 * An implementation of <code>SimpleTag</code> that automatically validates its
 * content and automatically encodes its output correctly given its context.
 * It also validates its own output when used in a non-validating context.  For
 * higher performance, it filters the output from its body instead of buffering.
 * </p>
 * <p>
 * The content validation is primarily focused on making sure the contained data
 * is properly encoded.  This is to avoid data corruption or intermingling of
 * data and code.  It does not go through great lengths such as ensuring that
 * XHTML Strict is valid or JavaScript will run correctly.
 * </p>
 * <p>
 * In additional to checking that its contents are well behaved, it also is
 * well behaved for its container by properly encoding its output for its
 * context.  To determine its context, it finds its nearest ancestor that also
 * implements <code>ContentTypeJspTag</code>.  It then uses the content type
 * of that tag to perform proper encoding.  If it fails to find any such parent,
 * it uses the content type of the <code>HttpServletResponse</code>.
 * </p>
 * <p>
 * Finally, if no parent <code>ContentTypeJspTag</code> is found, this will
 * validate its own output against the content type of the
 * <code>HttpServletResponse</code> to make sure it is well-behaved.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class AutoEncodingFilteredTag extends SimpleTagSupport implements ContentTypeJspTag {

    /**
     * @see ContentTypeJspTag#getContentType()
     */
    @Override
    public abstract MediaType getContentType();

    private ValidMediaInput inputValidator;

    /**
     * The validator is stored to allow nested tags to check if their output
     * is already being filtered on this tags input.  When this occurs they
     * skip the validation of their own output.
     */
    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return inputValidator!=null && inputValidator.isValidatingMediaInputType(inputType);
    }

    @Override
    final public void doTag() throws JspException, IOException {
        try {
            PageContext pageContext = (PageContext)getJspContext();
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
            Writer out = pageContext.getOut();
            ContentTypeJspTag parent = (ContentTypeJspTag)findAncestorWithClass(this, ContentTypeJspTag.class);
            MediaType myContentType = getContentType();

            // Determine the container's content type and see if our output is already validated
            MediaType containerContentType;
            if(parent!=null) {
                // Use the output type of the parent
                containerContentType = parent.getContentType();
                // Make sure the output is compatibly validated.  It is a bug in the parent to not validate its input consistent with its content type
                if(!parent.isValidatingMediaInputType(containerContentType)) {
                    throw new JspException(
                        ApplicationResources.accessor.getMessage(
                            "AutoEncodingFilterTag.parentIncompatibleValidation",
                            parent.getClass().getName(),
                            containerContentType.getMediaType()
                        )
                    );
                }
            } else {
                // Use the content type of the response
                containerContentType = MediaType.getMediaType(response.getContentType());
            }

            // Find the encoder
            MediaEncoder mediaEncoder = MediaEncoder.getMediaEncoder(response, myContentType, containerContentType, out);
            if(mediaEncoder!=null) {
                setMediaEncoderOptions(mediaEncoder);
                // Encode the content.  The encoder is also the validator for our input and guarantees valid output for our parent.
                mediaEncoder.writePrefix();
                try {
                    inputValidator = mediaEncoder;
                    invokeAutoEncoding(mediaEncoder);
                } finally {
                    mediaEncoder.writeSuffix();
                }
            } else {
                // Not using an encoder, validate our own content
                MediaValidator validator = MediaValidator.getMediaValidator(myContentType, out);
                inputValidator = validator;
                invokeAutoEncoding(validator);
            }
        } catch(MediaException err) {
            throw new JspException(err);
        }
    }

    /**
     * Sets the media encoder options.  This is how subclass tag attributes
     * can effect the encoding.
     */
    protected void setMediaEncoderOptions(MediaEncoder mediaEncoder) {
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

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
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
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.MediaValidator;
import com.aoindustries.encoding.ValidMediaInput;
import com.aoindustries.io.NullWriter;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <p>
 * The exhibits all of the behavior of <code>AutoEncodingFilteredTag</code> with
 * the only exception being that it discards all content.  When the direct output
 * of the body will not be used, this will increase efficiency by discarding all
 * write operations immediately.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class AutoEncodingNullTag extends SimpleTagSupport {

    /**
     * Gets the output type of this tag.  This is used to determine the correct
     * encoder.  If the tag never has any output this should return <code>null</code>.
     * When <code>null</code> is returned, any output will result in an error.
     */
    public abstract MediaType getOutputType();

    /**
     * Is only validating TEXT, which basically means no validation rules.
     */
    /*
    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return inputType==MediaType.TEXT;
    }*/

    @Override
    final public void doTag() throws JspException, IOException {
        try {
            MediaType myOutputType = getOutputType();
            if(myOutputType==null) {
                // No output, error if anything written.
                doTag(FailOnWriteWriter.getInstance());
            } else {
                final PageContext pageContext = (PageContext)getJspContext();
                final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
                final Writer out = pageContext.getOut();

                final MediaType parentContentType = ThreadEncodingContext.contentType.get();
                final ValidMediaInput parentValidMediaInput = ThreadEncodingContext.validMediaInput.get();

                // Determine the container's content type
                MediaType containerContentType;
                if(parentContentType!=null) {
                    // Use the output type of the parent
                    containerContentType = parentContentType;
                } else {
                    // Use the content type of the response
                    containerContentType = MediaType.getMediaType(response.getContentType());
                }
                // Find the encoder
                MediaEncoder mediaEncoder = MediaEncoder.getMediaEncoder(response, myOutputType, containerContentType, out);
                if(mediaEncoder!=null) {
                    setMediaEncoderOptions(mediaEncoder);
                    // Encode out output.  The encoder guarantees valid output for our parent.
                    mediaEncoder.writePrefix();
                    try {
                        ThreadEncodingContext.contentType.set(myOutputType);
                        ThreadEncodingContext.validMediaInput.set(mediaEncoder);
                        try {
                            doTag(mediaEncoder);
                        } finally {
                            // Restore previous encoding context that is used for our output
                            ThreadEncodingContext.contentType.set(parentContentType);
                            ThreadEncodingContext.validMediaInput.set(parentValidMediaInput);
                        }
                    } finally {
                        mediaEncoder.writeSuffix();
                    }
                } else {
                    // If parentValidMediaInput exists, the parent should already be validating our output type.
                    if(parentValidMediaInput!=null) {
                        // Make sure the output is compatibly validated.  It is a bug in the parent to not validate its input consistent with its content type
                        if(!parentValidMediaInput.isValidatingMediaInputType(containerContentType)) {
                            throw new LocalizedJspException(
                                ApplicationResources.accessor,
                                "AutoEncodingFilterTag.parentIncompatibleValidation",
                                parentValidMediaInput.getClass().getName(),
                                containerContentType.getMediaType()
                            );
                        }
                        ThreadEncodingContext.contentType.set(myOutputType);
                        try {
                            doTag(out);
                        } finally {
                            ThreadEncodingContext.contentType.set(parentContentType);
                        }
                    } else {
                        // Not using an encoder and no parent, validate our own output.
                        MediaValidator validator = MediaValidator.getMediaValidator(myOutputType, out);
                        ThreadEncodingContext.contentType.set(myOutputType);
                        ThreadEncodingContext.validMediaInput.set(validator);
                        try {
                            doTag(validator);
                        } finally {
                            ThreadEncodingContext.contentType.set(parentContentType);
                            ThreadEncodingContext.validMediaInput.set(parentValidMediaInput);
                        }
                    }
                }
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
     * @return This default implementation invokes the jsp body, if present, discarding all output of the body.
     * @throws javax.servlet.jsp.JspException
     */
    protected void doTag(Writer out) throws JspException, IOException {
        JspFragment body = getJspBody();
        if(body!=null) body.invoke(NullWriter.getInstance());
    }
}

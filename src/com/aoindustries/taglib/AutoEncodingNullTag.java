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
import com.aoindustries.encoding.MediaWriter;
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
 * Automatically encodes its output based on tag context while discarding all
 * content.  When the direct output of the body will not be used, this will
 * increase efficiency by discarding all write operations immediately.
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
			// The output type cannot be determined until the body of the tag is invoked, because nested tags may
			// alter the resulting type.  We invoke the body first to accommodate nested tags.
			JspFragment body = getJspBody();
			if(body!=null) body.invoke(NullWriter.getInstance());
			
            MediaType myOutputType = getOutputType();
            if(myOutputType==null) {
                // No output, error if anything written.
				// prefix skipped
                doTag(FailOnWriteWriter.getInstance());
				// suffix skipped
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
				
				// Determine the validator for the parent type.  This is to make sure prefix and suffix are valid.
				Writer containerValidator;
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
					// Already validated
					containerValidator = out;
				} else {
					// Need to add validator
					containerValidator = MediaValidator.getMediaValidator(containerContentType, out);
				}
				
				// Write any prefix
				writePrefix(containerContentType, containerValidator);

				// Find the encoder
                MediaEncoder mediaEncoder = MediaEncoder.getInstance(response, myOutputType, containerContentType);
                if(mediaEncoder!=null) {
                    setMediaEncoderOptions(mediaEncoder);
                    // Encode our output.  The encoder guarantees valid output for our parent.
					MediaWriter mediaWriter = new MediaWriter(mediaEncoder, out);
                    mediaWriter.writePrefix();
                    try {
                        ThreadEncodingContext.contentType.set(myOutputType);
                        ThreadEncodingContext.validMediaInput.set(mediaWriter);
                        try {
                            doTag(mediaWriter);
                        } finally {
                            // Restore previous encoding context that is used for our output
                            ThreadEncodingContext.contentType.set(parentContentType);
                            ThreadEncodingContext.validMediaInput.set(parentValidMediaInput);
                        }
                    } finally {
                        mediaWriter.writeSuffix();
                    }
                } else {
                    // If parentValidMediaInput exists, the parent should already be validating our output type.
                    if(parentValidMediaInput!=null) {
                        // Make sure the output is compatibly validated.  It is a bug in the parent to not validate its input consistent with its content type
                        if(!parentValidMediaInput.isValidatingMediaInputType(myOutputType)) { // was containerContentType 2013-09-29
                            throw new LocalizedJspException(
                                ApplicationResources.accessor,
                                "AutoEncodingFilterTag.parentIncompatibleValidation",
                                parentValidMediaInput.getClass().getName(),
                                myOutputType.getMediaType()
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

				// Write any suffix
				writeSuffix(containerContentType, containerValidator);
            }
        } catch(MediaException err) {
            throw new JspException(err);
        }
    }

	/**
	 * <p>
	 * Writes any prefix in the container's media type.
	 * The output must be valid for the provided type.
	 * This will not be called when the output type is <code>null</code>.
	 * </p>
	 * <p>
	 * This default implementation prints nothing.
	 * </p>
	 *
	 * @see  #getOutputType()
	 */
	protected void writePrefix(MediaType containerType, Writer out) throws JspException, IOException {
		// By default, nothing is printed.
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
	 * The body, if present, has already been invoked and any output discarded.
     *
     * This default implementation does nothing.
     */
    abstract protected void doTag(Writer out) throws JspException, IOException;

	/**
	 * <p>
	 * Writes any suffix in the container's media type.
	 * The output must be valid for the provided type.
	 * This will not be called when the output type is <code>null</code>.
	 * </p>
	 * <p>
	 * This default implementation prints nothing.
	 * </p>
	 *
	 * @see  #getOutputType()
	 */
	protected void writeSuffix(MediaType containerType, Writer out) throws JspException, IOException {
		// By default, nothing is printed.
	}
}

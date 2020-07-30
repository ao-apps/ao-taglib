/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2012, 2013, 2016, 2017, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-taglib.
 *
 * ao-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.EncodingContext;
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.MediaValidator;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.encoding.servlet.EncodingContextEE;
import com.aoindustries.io.NullWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
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

	private static final Logger logger = Logger.getLogger(AutoEncodingNullTag.class.getName());

	/**
	 * Gets the output type of this tag.  This is used to determine the correct
	 * encoder.  If the tag never has any output this should return {@code null}.
	 * When {@code null} is returned, any output will result in an error.
	 */
	public abstract MediaType getOutputType();

	@Override
	final public void doTag() throws JspException, IOException {
		// The output type cannot be determined until the body of the tag is invoked, because nested tags may
		// alter the resulting type.  We invoke the body first to accommodate nested tags.
		JspFragment body = getJspBody();
		if(body != null) body.invoke(NullWriter.getInstance());

		MediaType myOutputType = getOutputType();
		if(myOutputType == null) {
			// No output, error if anything written.
			// prefix skipped
			doTag(FailOnWriteWriter.getInstance());
			// suffix skipped
		} else {
			final PageContext pageContext = (PageContext)getJspContext();
			final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			final JspWriter out = pageContext.getOut();

			final RequestEncodingContext parentEncodingContext = RequestEncodingContext.getCurrentContext(request);

			// Determine the container's content type
			final MediaType containerContentType;
			if(parentEncodingContext != null) {
				// Use the output type of the parent
				containerContentType = parentEncodingContext.contentType;
				if(logger.isLoggable(Level.FINER)) {
					logger.finer("containerContentType from parentEncodingContext: " + containerContentType);
				}
				assert parentEncodingContext.validMediaInput.isValidatingMediaInputType(containerContentType)
					: "It is a bug in the parent to not validate its input consistent with its content type";
			} else {
				// Use the content type of the response
				String responseContentType = response.getContentType();
				// Default to XHTML: TODO: Is there a better way since can't set content type early in response then reset again...
				if(responseContentType == null) responseContentType = MediaType.XHTML.getContentType();
				containerContentType = MediaType.getMediaTypeForContentType(responseContentType);
				if(logger.isLoggable(Level.FINER)) {
					logger.finer("containerContentType from responseContentType: " + containerContentType + " from " + responseContentType);
				}
			}

			// Determine the validator for the parent type.  This is to make sure prefix and suffix are valid.
			final Writer containerValidator;
			if(parentEncodingContext != null) {
				// Already validated
				containerValidator = out;
				if(logger.isLoggable(Level.FINER)) {
					logger.finer("containerValidator from parentEncodingContext: " + containerValidator);
				}
			} else {
				// Need to add validator
				containerValidator = MediaValidator.getMediaValidator(containerContentType, out);
				if(logger.isLoggable(Level.FINER)) {
					logger.finer("containerValidator from containerContentType: " + containerValidator + " from " + containerContentType);
				}
			}

			// Write any prefix
			writePrefix(containerContentType, containerValidator);

			// Find the encoder
			EncodingContext encodingContext = new EncodingContextEE(pageContext.getServletContext(), request, response);
			MediaEncoder mediaEncoder = MediaEncoder.getInstance(encodingContext, myOutputType, containerContentType);
			if(mediaEncoder != null) {
				if(logger.isLoggable(Level.FINER)) {
					logger.finer("Using MediaEncoder: " + mediaEncoder);
				}
				logger.finest("Setting encoder options");
				setMediaEncoderOptions(mediaEncoder);
				// Encode our output.  The encoder guarantees valid output for our parent.
				logger.finest("Writing encoder prefix");
				writeEncoderPrefix(mediaEncoder, out);
				try {
					MediaWriter mediaWriter = new MediaWriter(encodingContext, mediaEncoder, out);
					RequestEncodingContext.setCurrentContext(
						request,
						new RequestEncodingContext(myOutputType, mediaWriter)
					);
					try {
						doTag(mediaWriter);
					} finally {
						// Restore previous encoding context that is used for our output
						RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				} finally {
					logger.finest("Writing encoder suffix");
					writeEncoderSuffix(mediaEncoder, out);
				}
			} else {
				// If parentValidMediaInput exists and is validating our output type, no additional validation is required
				if(
					parentEncodingContext != null
					&& parentEncodingContext.validMediaInput.isValidatingMediaInputType(myOutputType)
				) {
					if(logger.isLoggable(Level.FINER)) {
						logger.finer("Passing-through with validating parent: " + parentEncodingContext.validMediaInput);
					}
					RequestEncodingContext.setCurrentContext(
						request,
						new RequestEncodingContext(myOutputType, parentEncodingContext.validMediaInput)
					);
					try {
						doTag(out);
					} finally {
						RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				} else {
					// Not using an encoder and parent doesn't validate our output, validate our own output.
					MediaValidator validator = MediaValidator.getMediaValidator(myOutputType, out);
					if(logger.isLoggable(Level.FINER)) {
						logger.finer("Using MediaValidator: " + validator);
					}
					RequestEncodingContext.setCurrentContext(
						request,
						new RequestEncodingContext(myOutputType, validator)
					);
					try {
						doTag(validator);
					} finally {
						RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				}
			}

			// Write any suffix
			writeSuffix(containerContentType, containerValidator);
		}
	}

	/**
	 * <p>
	 * Writes any prefix in the container's media type.
	 * The output must be valid for the provided type.
	 * This will not be called when the output type is {@code null}.
	 * </p>
	 * <p>
	 * This default implementation prints nothing.
	 * </p>
	 *
	 * @see  #getOutputType()
	 */
	protected void writePrefix(MediaType containerType, Writer out) throws JspTagException, IOException {
		// By default, nothing is printed.
	}

	/**
	 * Sets the media encoder options.  This is how subclass tag attributes
	 * can effect the encoding.
	 */
	protected void setMediaEncoderOptions(MediaEncoder mediaEncoder) {
	}

	protected void writeEncoderPrefix(MediaEncoder mediaEncoder, JspWriter out) throws JspException, IOException {
		mediaEncoder.writePrefixTo(out);
	}

	/**
	 * Once the out JspWriter has been replaced to output the proper content
	 * type, this version of invoke is called.
	 * 
	 * The body, if present, has already been invoked and any output discarded.
	 *
	 * This default implementation does nothing.
	 */
	protected void doTag(Writer out) throws JspTagException, IOException {
		// Do nothing by default
	}

	protected void writeEncoderSuffix(MediaEncoder mediaEncoder, JspWriter out) throws JspException, IOException {
		mediaEncoder.writeSuffixTo(out);
	}

	/**
	 * <p>
	 * Writes any suffix in the container's media type.
	 * The output must be valid for the provided type.
	 * This will not be called when the output type is {@code null}.
	 * </p>
	 * <p>
	 * This default implementation prints nothing.
	 * </p>
	 *
	 * @see  #getOutputType()
	 */
	protected void writeSuffix(MediaType containerType, Writer out) throws JspTagException, IOException {
		// By default, nothing is printed.
	}
}

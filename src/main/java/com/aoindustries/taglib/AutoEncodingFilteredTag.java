/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2019  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.MediaValidator;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.encoding.servlet.HttpServletResponseEncodingContext;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
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
public abstract class AutoEncodingFilteredTag extends SimpleTagSupport {

	/**
	 * Gets the type of data that is contained by this tag.
	 * This is also the output type.
	 */
	public abstract MediaType getContentType();

	/**
	 * The validator is stored to allow nested tags to check if their output
	 * is already being filtered on this tags input.  When this occurs they
	 * skip the validation of their own output.
	 */
	/*
	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return inputValidator!=null && inputValidator.isValidatingMediaInputType(inputType);
	}*/

	@Override
	public void doTag() throws JspException, IOException {
		try {
			final PageContext pageContext = (PageContext)getJspContext();
			final ServletRequest request = pageContext.getRequest();
			final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			final JspWriter out = pageContext.getOut();

			final ThreadEncodingContext parentEncodingContext = ThreadEncodingContext.getCurrentContext(request);

			// Determine the container's content type
			MediaType containerContentType;
			if(parentEncodingContext != null) {
				// Use the output type of the parent
				containerContentType = parentEncodingContext.contentType;
			} else {
				// Use the content type of the response
				String responseContentType = response.getContentType();
				// Default to XHTML: TODO: Is there a better way since can't set content type early in response then reset again...
				if(responseContentType == null) responseContentType = MediaType.XHTML.getContentType();
				containerContentType = MediaType.getMediaTypeForContentType(responseContentType);
			}
			// Find the encoder
			final MediaType myContentType = getContentType();
			MediaEncoder mediaEncoder = MediaEncoder.getInstance(new HttpServletResponseEncodingContext(response), myContentType, containerContentType);
			if(mediaEncoder != null) {
				setMediaEncoderOptions(mediaEncoder);
				// Encode both our output and the content.  The encoder validates our input and guarantees valid output for our parent.
				writeEncoderPrefix(mediaEncoder, out);
				try {
					MediaWriter mediaWriter = new MediaWriter(mediaEncoder, out);
					ThreadEncodingContext.setCurrentContext(
						request,
						new ThreadEncodingContext(myContentType, mediaWriter)
					);
					try {
						doTag(mediaWriter);
					} finally {
						// Restore previous encoding context that is used for our output
						ThreadEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				} finally {
					writeEncoderSuffix(mediaEncoder, out);
				}
			} else {
				// If parentValidMediaInput exists and is validating our output type, no additional validation is required
				if(
					parentEncodingContext != null
					&& parentEncodingContext.validMediaInput.isValidatingMediaInputType(myContentType)
				) {
					ThreadEncodingContext.setCurrentContext(
						request,
						new ThreadEncodingContext(myContentType, parentEncodingContext.validMediaInput)
					);
					try {
						doTag(out);
					} finally {
						ThreadEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				} else {
					// Not using an encoder and parent doesn't validate our output, validate our own output.
					MediaValidator validator = MediaValidator.getMediaValidator(myContentType, out);
					ThreadEncodingContext.setCurrentContext(
						request,
						new ThreadEncodingContext(myContentType, validator)
					);
					try {
						doTag(validator);
					} finally {
						ThreadEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				}
			}
		} catch(MediaException err) {
			throw new JspTagException(err);
		}
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
	 * <p>
	 * This default implementation invokes the jsp body, if present.
	 * </p>
	 *
	 * @param  out  the output.  If passed-through, this will be a <code>JspWriter</code>
	 *
	 * @throws javax.servlet.jsp.JspTagException
	 */
	protected void doTag(Writer out) throws JspException, IOException {
		JspFragment body = getJspBody();
		if(body!=null) {
			// Check for JspWriter to avoid a JspWriter wrapping a JspWriter
			body.invoke(
				(out instanceof JspWriter)
				? null
				: out
			);
		}
	}

	protected void writeEncoderSuffix(MediaEncoder mediaEncoder, JspWriter out) throws JspException, IOException {
		mediaEncoder.writeSuffixTo(out);
	}
}

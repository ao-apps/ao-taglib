/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.io.buffer.AutoTempFileWriter;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.buffer.BufferWriter;
import com.aoindustries.io.buffer.CharArrayBufferWriter;
import com.aoindustries.tempfiles.TempFileContext;
import com.aoindustries.tempfiles.servlet.TempFileContextEE;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <p>
 * The exhibits all of the behavior of {@link AutoEncodingFilteredTag} with
 * the only exception being that it buffers its content instead of using filters.
 * This allows the tag to capture its body.  Character validation is performed
 * as the data goes into the buffer to ensure the captured data is correct for
 * its content type.
 * </p>
 * <p>
 * The tag also has the addition of a separate output type.  Thus, we have three
 * types involved:
 * </p>
 * <ol>
 * <li>contentType - The characters are validated to this type as they go into the buffer.</li>
 * <li>outputType - Our output characters are validated to this type as they are written.</li>
 * <li>containerContentType - Our output characters are encoded to this type as they are written.</li>
 * </ol>
 *
 * @author  AO Industries, Inc.
 */
public abstract class AutoEncodingBufferedTag extends SimpleTagSupport {

	/**
	 * Creates an instance of the currently preferred {@link BufferWriter}.
	 * Buffering strategies may change over time as technology develops and
	 * options become available.
	 *
	 * @see  TempFileContext
	 * @see  AutoTempFileWriter
	 */
	public static BufferWriter newBufferWriter(TempFileContext tempFileContext, long tempFileThreshold) {
		//return new SegmentedWriter();
		BufferWriter bufferWriter = new CharArrayBufferWriter();
		if(tempFileThreshold != Long.MAX_VALUE) {
			bufferWriter = new AutoTempFileWriter(
				bufferWriter,
				tempFileContext,
				tempFileThreshold
			);
		}
		return bufferWriter;
	}

	/**
	 * @see  #newBufferWriter(com.aoindustries.tempfiles.TempFileContext, long)
	 * @see  AutoTempFileWriter#DEFAULT_TEMP_FILE_THRESHOLD
	 */
	public static BufferWriter newBufferWriter(TempFileContext tempFileContext) {
		return newBufferWriter(tempFileContext, AutoTempFileWriter.DEFAULT_TEMP_FILE_THRESHOLD);
	}

	/**
	 * @see  #newBufferWriter(com.aoindustries.tempfiles.TempFileContext, long)
	 * @see  TempFileContextEE#get(javax.servlet.ServletRequest)
	 */
	public static BufferWriter newBufferWriter(ServletRequest request, long tempFileThreshold) {
		return newBufferWriter(TempFileContextEE.get(request), tempFileThreshold);
	}

	/**
	 * @see  #newBufferWriter(javax.servlet.ServletRequest, long)
	 * @see  AutoTempFileWriter#DEFAULT_TEMP_FILE_THRESHOLD
	 */
	public static BufferWriter newBufferWriter(ServletRequest request) {
		return newBufferWriter(request, AutoTempFileWriter.DEFAULT_TEMP_FILE_THRESHOLD);
	}

	/**
	 * Gets the type of data that is contained by this tag.
	 */
	public abstract MediaType getContentType();

	/**
	 * Gets the output type of this tag.  This is used to determine the correct
	 * encoder.  If the tag never has any output this should return {@code null}.
	 * When {@code null} is returned, any output will result in an error.
	 */
	public abstract MediaType getOutputType();

	/**
	 * Gets the number of characters that may be buffered before switching to the
	 * use of a temp file.
	 *
	 * @return the threshold or {@link Long#MAX_VALUE} to never use temp files.
	 *
	 * @see  AutoTempFileWriter#DEFAULT_TEMP_FILE_THRESHOLD
	 */
	public long getTempFileThreshold() {
		return AutoTempFileWriter.DEFAULT_TEMP_FILE_THRESHOLD;
	}

	@Override
	public void doTag() throws JspException, IOException {
		final PageContext pageContext = (PageContext)getJspContext();
		final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		final RequestEncodingContext parentEncodingContext = RequestEncodingContext.getCurrentContext(request);

		// Capture the body output while validating
		// BufferWriter bufferWriter = new CharArrayBufferWriter(128, getTempFileThreshold());
		BufferWriter bufferWriter = newBufferWriter(request, getTempFileThreshold());
		try {
			JspFragment body = getJspBody();
			if(body!=null) {
				final MediaType myContentType = getContentType();
				MediaValidator captureValidator = MediaValidator.getMediaValidator(myContentType, bufferWriter);
				RequestEncodingContext.setCurrentContext(
					request,
					new RequestEncodingContext(myContentType, captureValidator)
				);
				try {
					invoke(body, captureValidator);
					captureValidator.flush();
				} finally {
					// Restore previous encoding context that is used for our output
					RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
				}
			}
		} finally {
			bufferWriter.close();
		}
		final BufferResult capturedBody = bufferWriter.getResult();
		bufferWriter = null; // Done with object, don't need to hold long-term reference

		MediaType myOutputType = getOutputType();
		if(myOutputType==null) {
			// No output, error if anything written.
			doTag(capturedBody, FailOnWriteWriter.getInstance());
		} else {
			final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			final JspWriter out = pageContext.getOut();

			// Determine the container's content type
			final MediaType containerContentType;
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
			EncodingContext encodingContext = new EncodingContextEE(pageContext.getServletContext(), request, response);
			MediaEncoder mediaEncoder = MediaEncoder.getInstance(encodingContext, myOutputType, containerContentType);
			if(mediaEncoder!=null) {
				setMediaEncoderOptions(mediaEncoder);
				// Encode our output.  The encoder guarantees valid output for our parent.
				writeEncoderPrefix(mediaEncoder, out);
				try {
					MediaWriter mediaWriter = new MediaWriter(encodingContext, mediaEncoder, out);
					RequestEncodingContext.setCurrentContext(
						request,
						new RequestEncodingContext(myOutputType, mediaWriter)
					);
					try {
						doTag(capturedBody, mediaWriter);
					} finally {
						// Restore previous encoding context that is used for our output
						RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				} finally {
					writeEncoderSuffix(mediaEncoder, out);
				}
			} else {
				// If parentValidMediaInput exists and is validating our output type, no additional validation is required
				if(
					parentEncodingContext != null
					&& parentEncodingContext.validMediaInput.isValidatingMediaInputType(myOutputType)
				) {
					RequestEncodingContext.setCurrentContext(
						request,
						new RequestEncodingContext(myOutputType, parentEncodingContext.validMediaInput)
					);
					try {
						doTag(capturedBody, out);
					} finally {
						RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				} else {
					// Not using an encoder and parent doesn't validate our output, validate our own output.
					MediaValidator validator = MediaValidator.getMediaValidator(myOutputType, out);
					RequestEncodingContext.setCurrentContext(
						request,
						new RequestEncodingContext(myOutputType, validator)
					);
					try {
						doTag(capturedBody, validator);
					} finally {
						RequestEncodingContext.setCurrentContext(request, parentEncodingContext);
					}
				}
			}
		}
	}

	/**
	 * Invokes the body.  This is only called when a body exists.  Subclasses may override this to perform
	 * actions before and/or after invoking the body.  Any overriding implementation should call
	 * super.invoke(JspFragment,MediaValidator) to invoke the body.
	 */
	protected void invoke(JspFragment body, MediaValidator captureValidator) throws JspException, IOException {
		body.invoke(captureValidator);
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
	 * Once the data is captured, this is called.
	 */
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		// Do nothing by default
	}

	protected void writeEncoderSuffix(MediaEncoder mediaEncoder, JspWriter out) throws JspException, IOException {
		mediaEncoder.writeSuffixTo(out);
	}
}

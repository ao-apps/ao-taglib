/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.EncodingContext;
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.MediaValidator;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.encoding.servlet.EncodingContextEE;
import com.aoindustries.io.buffer.AutoTempFileWriter;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.buffer.BufferWriter;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * <p>
 * The exhibits all of the behavior of {@link AutoEncodingFilteredBodyTag} with
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
public abstract class AutoEncodingBufferedBodyTag extends BodyTagSupport implements TryCatchFinally {

	private static final Logger logger = Logger.getLogger(AutoEncodingBufferedBodyTag.class.getName());

	public AutoEncodingBufferedBodyTag() {
		init();
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

	private static final long serialVersionUID = 1L;

	private transient RequestEncodingContext parentEncodingContext;
	private transient MediaEncoder mediaEncoder;
	private transient RequestEncodingContext validatingOutEncodingContext;
	private transient Writer validatingOut;
	private transient BufferWriter captureBuffer;
	private transient MediaValidator captureValidator;
	private transient boolean bodyUnbuffered;

	private void init() {
		parentEncodingContext = null;
		mediaEncoder = null;
		validatingOutEncodingContext = null;
		validatingOut = null;
		captureBuffer = null;
		captureValidator = null;
		bodyUnbuffered = false;
	}

	/**
	 * @deprecated  You should probably be implementing in {@link #doStartTag(java.io.Writer)}
	 *
	 * @see  #doStartTag(java.io.Writer)
	 */
	@Override
	@Deprecated
	public int doStartTag() throws JspException {
		try {
			final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

			parentEncodingContext = RequestEncodingContext.getCurrentContext(request);

			MediaType myOutputType = getOutputType();
			if(myOutputType == null) {
				// No output, error if anything written.
				// prefix skipped
				validatingOutEncodingContext = parentEncodingContext;
				validatingOut = FailOnWriteWriter.getInstance();
				// suffix skipped
			} else {
				final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
				final JspWriter out = pageContext.getOut();

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
				// Find the encoder
				EncodingContext encodingContext = new EncodingContextEE(pageContext.getServletContext(), request, response);
				mediaEncoder = MediaEncoder.getInstance(encodingContext, myOutputType, containerContentType);
				if(mediaEncoder != null) {
					if(logger.isLoggable(Level.FINER)) {
						logger.finer("Using MediaEncoder: " + mediaEncoder);
					}
					logger.finest("Setting encoder options");
					setMediaEncoderOptions(mediaEncoder);
					// Encode our output.  The encoder guarantees valid output for our parent.
					logger.finest("Writing encoder prefix");
					writeEncoderPrefix(mediaEncoder, out); // TODO: Skip prefix and suffix when empty?
					MediaWriter mediaWriter = new MediaWriter(encodingContext, mediaEncoder, out);
					validatingOutEncodingContext = new RequestEncodingContext(myOutputType, mediaWriter);
					validatingOut = mediaWriter;
				} else {
					// If parentValidMediaInput exists and is validating our output type, no additional validation is required
					if(
						parentEncodingContext != null
						&& parentEncodingContext.validMediaInput.isValidatingMediaInputType(myOutputType)
					) {
						if(logger.isLoggable(Level.FINER)) {
							logger.finer("Passing-through with validating parent: " + parentEncodingContext.validMediaInput);
						}
						validatingOutEncodingContext = new RequestEncodingContext(myOutputType, parentEncodingContext.validMediaInput);
						validatingOut = out;
					} else {
						// Not using an encoder and parent doesn't validate our output, validate our own output.
						MediaValidator validator = MediaValidator.getMediaValidator(myOutputType, out);
						if(logger.isLoggable(Level.FINER)) {
							logger.finer("Using MediaValidator: " + validator);
						}
						validatingOutEncodingContext = new RequestEncodingContext(myOutputType, validator);
						validatingOut = validator;
					}
				}
				RequestEncodingContext.setCurrentContext(request, validatingOutEncodingContext);
			}
			return checkStartTagReturn(doStartTag(validatingOut));
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	private static int checkStartTagReturn(int startTagReturn) throws JspTagException {
		if(
			startTagReturn != EVAL_BODY_BUFFERED
			&& startTagReturn != SKIP_BODY
		) throw new LocalizedJspTagException(
			ApplicationResources.accessor,
			"AutoEncodingBufferedBodyTag.checkStartTagReturn.invalid",
			startTagReturn
		);
		return startTagReturn;
	}

	/**
	 * Once the out {@link JspWriter} has been replaced to output the proper content
	 * type, this version of {@link #doStartTag()} is called.
	 *
	 * @param  out  the output.  If passed-through, this will be a {@link JspWriter}
	 *
	 * @return  Must return either {@link #EVAL_BODY_BUFFERED} (the default) or {@link #SKIP_BODY}
	 */
	protected int doStartTag(Writer out) throws JspException, IOException {
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Replaces the captureBuffer, preparing for the next invocation of doBody
	 */
	private void initCapture() throws JspTagException, UnsupportedEncodingException {
		assert captureBuffer == null;
		assert captureValidator == null;
		ServletRequest request = pageContext.getRequest();
		captureBuffer = AutoEncodingBufferedTag.newBufferWriter(request, getTempFileThreshold());
		final MediaType myContentType = getContentType();
		captureValidator = MediaValidator.getMediaValidator(myContentType, captureBuffer);
		RequestEncodingContext.setCurrentContext(
			request,
			new RequestEncodingContext(myContentType, captureValidator)
		);
		bodyUnbuffered = BodyTagUtils.unbuffer(bodyContent, captureValidator);
	}

	/**
	 * <p>
	 * The only way to replace the "out" variable in the generated JSP is to use
	 * {@link #EVAL_BODY_BUFFERED}.  Without this, any writer given to {@link PageContext#pushBody(java.io.Writer)}
	 * is not used.  We want to both use {@linkplain BufferWriter our own buffer implementation} as well as validate the
	 * data on-the-fly.
	 * </p>
	 * <p>
	 * To workaround this issue, this very hackily replaces the writer field directly on the
	 * <code>BodyContentImpl</code>.  When unable to replace the field, falls back to using
	 * the standard buffering (much less desirable).
	 * </p>
	 * <p>
	 * This is similar to the direct field access performed by {@link Coercion}.
	 * </p>
	 */
	@Override
	final public void doInitBody() throws JspTagException {
		try {
			initCapture();
		} catch(UnsupportedEncodingException e) {
			throw new JspTagException(e);
		}
	}

	/**
	 * @deprecated  You should probably be implementing in {@link #doAfterBody(com.aoindustries.io.buffer.BufferResult, java.io.Writer)}
	 *
	 * @see  #doAfterBody(com.aoindustries.io.buffer.BufferResult, java.io.Writer)
	 */
	@Override
	@Deprecated
	public int doAfterBody() throws JspException {
		try {
			if(!bodyUnbuffered) {
				bodyContent.writeOut(captureValidator);
				if(logger.isLoggable(Level.FINER)) {
					int charCount = bodyContent.getBufferSize() - bodyContent.getRemaining();
					logger.finer("Validated " + charCount + " buffered " + (charCount == 1 ? "character" : "characters"));
				}
				bodyContent.clear();
			}
			captureValidator.flush();
			captureBuffer.close();
			final BufferResult capturedBody = captureBuffer.getResult();
			captureBuffer = null;
			captureValidator = null;
			RequestEncodingContext.setCurrentContext(pageContext.getRequest(), validatingOutEncodingContext);
			int afterBodyReturn = BodyTagUtils.checkAfterBodyReturn(doAfterBody(capturedBody, validatingOut));
			if(afterBodyReturn == EVAL_BODY_AGAIN) {
				initCapture();
			}
			return afterBodyReturn;
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	/**
	 * While the out {@link JspWriter} is still replaced to output the proper content
	 * type, this version of {@link #doAfterBody()} is called.
	 *
	 * @param  out  the output.  If passed-through, this will be a {@link JspWriter}
	 *
	 * @return  Must return either {@link #SKIP_BODY} (the default) or {@link #EVAL_BODY_AGAIN}
	 */
	protected int doAfterBody(BufferResult capturedBody, Writer out) throws JspException, IOException {
		return SKIP_BODY;
	}

	/**
	 * @deprecated  You should probably be implementing in {@link #doEndTag(java.io.Writer)}
	 *
	 * @see  #doEndTag(java.io.Writer)
	 */
	@Override
	@Deprecated
	public int doEndTag() throws JspException {
		try {
			RequestEncodingContext.setCurrentContext(pageContext.getRequest(), validatingOutEncodingContext);
			int endTagReturn = BodyTagUtils.checkEndTagReturn(doEndTag(validatingOut));
			if(mediaEncoder != null) {
				logger.finest("Writing encoder suffix");
				writeEncoderSuffix(mediaEncoder, pageContext.getOut());
			}
			return endTagReturn;
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	/**
	 * While the out {@link JspWriter} is still replaced to output the proper content
	 * type, this version of {@link #doEndTag()} is called.
	 *
	 * @param  out  the output.  If passed-through, this will be a {@link JspWriter}
	 *
	 * @return  Must return either {@link #EVAL_PAGE} (the default) or {@link #SKIP_PAGE}
	 */
	protected int doEndTag(Writer out) throws JspException, IOException {
		return EVAL_PAGE;
	}

	@Override
	public void doCatch(Throwable t) throws Throwable {
		throw t;
	}

	@Override
	public void doFinally() {
		try {
			// Restore previous encoding context that is used for our output
			RequestEncodingContext.setCurrentContext(pageContext.getRequest(), parentEncodingContext);
		} finally {
			init();
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

	protected void writeEncoderSuffix(MediaEncoder mediaEncoder, JspWriter out) throws JspException, IOException {
		mediaEncoder.writeSuffixTo(out);
	}
}

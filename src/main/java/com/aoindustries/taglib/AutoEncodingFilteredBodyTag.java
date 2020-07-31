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
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
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
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * <p>
 * An implementation of {@link BodyTagSupport} that automatically validates its
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
 * context.  To determine its context, it uses the content type of the currently
 * registered {@link RequestEncodingContext} to perform proper encoding.
 * If it fails to find any such context, it uses the content type of the
 * {@link HttpServletResponse}.
 * </p>
 * <p>
 * Finally, if no existing {@link RequestEncodingContext} is found, this will
 * validate its own output against the content type of the
 * {@link HttpServletResponse} to make sure it is well-behaved.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
// TODO: Rename other tags to "...SimpleTag" (leave deprecated subclasses at old name)?
public abstract class AutoEncodingFilteredBodyTag extends BodyTagSupport implements TryCatchFinally {

	private static final Logger logger = Logger.getLogger(AutoEncodingFilteredBodyTag.class.getName());

	/**
	 * Return value for {@link #doStartTag(java.io.Writer)}.  It will be converted
	 * to either {@link #EVAL_BODY_INCLUDE} or {@link #EVAL_BODY_BUFFERED}, as
	 * appropriate to the given filtering and validation.
	 */
	public static final int EVAL_BODY_FILTERED = 7;

	static {
		assert EVAL_BODY_FILTERED != SKIP_BODY;
		assert EVAL_BODY_FILTERED != EVAL_BODY_INCLUDE;
		assert EVAL_BODY_FILTERED != EVAL_BODY_BUFFERED;
	}

	/**
	 * Gets the type of data that is contained by this tag.
	 * This is also the output type.
	 */
	public abstract MediaType getContentType();

	private enum Mode {
		PASSTHROUGH(false),
		ENCODING(true),
		VALIDATING(true);

		private final boolean buffered;

		private Mode(boolean buffered) {
			this.buffered = buffered;
		}
	}
	
	private static final long serialVersionUID = 1L;

	private transient RequestEncodingContext parentEncodingContext;
	private transient MediaEncoder mediaEncoder;
	private transient RequestEncodingContext validatingOutEncodingContext;
	private transient Writer validatingOut;
	private transient Mode mode;
	private transient boolean bodyUnbuffered;

	@Override
	public void release() {
		parentEncodingContext = null;
		mediaEncoder = null;
		validatingOutEncodingContext = null;
		validatingOut = null;
		mode = null;
		bodyUnbuffered = false;
		super.release();
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
			final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			final JspWriter out = pageContext.getOut();

			parentEncodingContext = RequestEncodingContext.getCurrentContext(request);

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
			final MediaType myContentType = getContentType();
			EncodingContext encodingContext = new EncodingContextEE(pageContext.getServletContext(), request, response);
			mediaEncoder = MediaEncoder.getInstance(encodingContext, myContentType, containerContentType);
			if(mediaEncoder != null) {
				if(logger.isLoggable(Level.FINER)) {
					logger.finer("Using MediaEncoder: " + mediaEncoder);
				}
				logger.finest("Setting encoder options");
				setMediaEncoderOptions(mediaEncoder);
				// Encode both our output and the content.  The encoder validates our input and guarantees valid output for our parent.
				logger.finest("Writing encoder prefix");
				writeEncoderPrefix(mediaEncoder, out);
				MediaWriter mediaWriter = new MediaWriter(encodingContext, mediaEncoder, out);
				validatingOutEncodingContext = new RequestEncodingContext(myContentType, mediaWriter);
				validatingOut = mediaWriter;
				mode = Mode.ENCODING;
			} else {
				// If parentValidMediaInput exists and is validating our output type, no additional validation is required
				if(
					parentEncodingContext != null
					&& parentEncodingContext.validMediaInput.isValidatingMediaInputType(myContentType)
				) {
					if(logger.isLoggable(Level.FINER)) {
						logger.finer("Passing-through with validating parent: " + parentEncodingContext.validMediaInput);
					}
					validatingOutEncodingContext = new RequestEncodingContext(myContentType, parentEncodingContext.validMediaInput);
					validatingOut = out;
					mode = Mode.PASSTHROUGH;
				} else {
					// Not using an encoder and parent doesn't validate our output, validate our own output.
					MediaValidator validator = MediaValidator.getMediaValidator(myContentType, out);
					if(logger.isLoggable(Level.FINER)) {
						logger.finer("Using MediaValidator: " + validator);
					}
					validatingOutEncodingContext = new RequestEncodingContext(myContentType, validator);
					validatingOut = validator;
					mode = Mode.VALIDATING;
				}
			}
			bodyUnbuffered = !mode.buffered;
			RequestEncodingContext.setCurrentContext(request, validatingOutEncodingContext);
			return checkStartTagReturn(doStartTag(validatingOut), mode);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	private static int checkStartTagReturn(int startTagReturn, Mode mode) throws JspTagException {
		if(startTagReturn == EVAL_BODY_FILTERED) {
			return mode.buffered ? EVAL_BODY_BUFFERED : EVAL_BODY_INCLUDE;
		}
		if(startTagReturn != SKIP_BODY) throw new LocalizedJspTagException(
			ApplicationResources.accessor,
			"AutoEncodingFilteredBodyTag.checkStartTagReturn.invalid",
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
	 * @return  Must return either {@link #EVAL_BODY_FILTERED} (the default) or {@link #SKIP_BODY}
	 */
	protected int doStartTag(Writer out) throws JspException, IOException {
		return EVAL_BODY_FILTERED;
	}

	/**
	 * <p>
	 * The only way to replace the "out" variable in the generated JSP is to use
	 * {@link #EVAL_BODY_BUFFERED}.  Without this, any writer given to {@link PageContext#pushBody(java.io.Writer)}
	 * is not used.  We don't actually want to buffer the content, but only want to filter and validate the data
	 * on-the-fly.
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
		assert mode.buffered;
		bodyUnbuffered = BodyTagUtils.unbuffer(bodyContent, validatingOut);
	}

	/**
	 * @deprecated  You should probably be implementing in {@link #doAfterBody(java.io.Writer)}
	 *
	 * @see  #doAfterBody(java.io.Writer)
	 */
	@Override
	@Deprecated
	public int doAfterBody() throws JspException {
		try {
			if(!bodyUnbuffered) {
				assert mode.buffered;
				bodyContent.writeOut(validatingOut);
				if(logger.isLoggable(Level.FINER)) {
					int charCount = bodyContent.getBufferSize() - bodyContent.getRemaining();
					logger.finer((mode == Mode.ENCODING ? "Encoded" : "Validated ") + charCount + " buffered " + (charCount == 1 ? "character" : "characters"));
				}
				bodyContent.clear();
			}
			RequestEncodingContext.setCurrentContext(pageContext.getRequest(), validatingOutEncodingContext);
			return BodyTagUtils.checkAfterBodyReturn(doAfterBody(validatingOut));
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
	protected int doAfterBody(Writer out) throws JspException, IOException {
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
		// Restore previous encoding context that is used for our output
		RequestEncodingContext.setCurrentContext(pageContext.getRequest(), parentEncodingContext);
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

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013  AO Industries, Inc.
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
import com.aoindustries.encoding.ValidMediaInput;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.MediaValidator;
import com.aoindustries.io.TempFileList;
import com.aoindustries.io.buffer.AutoTempFileWriter;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.buffer.BufferWriter;
import com.aoindustries.io.buffer.LoggingWriter;
import com.aoindustries.io.buffer.SegmentedWriter;
import com.aoindustries.servlet.filter.TempFileContext;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import com.aoindustries.util.WrappedException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <p>
 * The exhibits all of the behavior of <code>AutoEncodingFilteredTag</code> with
 * the only exception being that it buffers its content instead of using filters.
 * This allows the tag to capture its body.  Character validation is performed
 * as the data goes into the buffer to ensure the captured data is correct for
 * its content type.
 * </p>
 * <p>
 * The tag also has the addition of a separate output type.  Thus, we have three
 * types involved:
 * <ol>
 * <li>contentType - The characters are validated to this type as they go into the buffer.</li>
 * <li>outputType - Our output characters are validated to this type as they are written.</li>
 * <li>containerContentType - Our output characters are encoded to this type as they are written.</li>
 * </ol>
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class AutoEncodingBufferedTag extends SimpleTagSupport {

	private static final Logger logger = Logger.getLogger(AutoEncodingBufferedTag.class.getName());

	/**
	 * Enables logging of all buffer calls.
	 */
	private static final boolean ENABLE_BUFFER_LOGGING = false; // Must be false for production releases

	/**
	 * Shared log writer.
	 */
	private static final Writer log;
	static {
		if(ENABLE_BUFFER_LOGGING) {
			try {
				File tempFile = File.createTempFile("AutoEncodingBufferedTag.log", null);
				tempFile.deleteOnExit();
				log = new BufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(
							tempFile
						)
					)
				);
			} catch(IOException e) {
				throw new WrappedException(e);
			}
		} else {
			log = null;
		}
	}

    /**
     * Gets the type of data that is contained by this tag.
     */
    public abstract MediaType getContentType();

    /**
     * Gets the output type of this tag.  This is used to determine the correct
     * encoder.  If the tag never has any output this should return <code>null</code>.
     * When <code>null</code> is returned, any output will result in an error.
     */
    public abstract MediaType getOutputType();

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

    /**
     * Gets the initial buffer size.  Defaults to 32 characters (the same
     * default as <code>CharArrayBuffer</code>.
     */
    public int getInitialBufferSize() {
        return 32;
    }

    /**
     * Gets the number of characters that may be buffered before switching to the
     * use of a temp file.  The default is 4 MB.
	 * 
	 * @return the threshold or <code>Long.MAX_VALUE</code> to never use temp files.
     */
    public long getTempFileThreshold() {
        return 4L * 1024L * 1024L;
    }

	private static final Object tempFileWarningLock = new Object();
	private static boolean tempFileWarned = false;

    @Override
    final public void doTag() throws JspException, IOException {
        try {
			final PageContext pageContext = (PageContext)getJspContext();
            final MediaType parentContentType = ThreadEncodingContext.contentType.get();
            final ValidMediaInput parentValidMediaInput = ThreadEncodingContext.validMediaInput.get();

            // Capture the body output while validating
            // BufferWriter bufferWriter = new CharArrayBufferWriter(128, getTempFileThreshold());
            BufferWriter bufferWriter = new SegmentedWriter();
			try {
				// Enable temp files if context active and threshold not Long.MAX_VALUE
				long tempFileThreshold = getTempFileThreshold();
				if(tempFileThreshold!=Long.MAX_VALUE) {
					TempFileList tempFileList = TempFileContext.getTempFileList(pageContext.getRequest());
					if(tempFileList!=null) {
						bufferWriter = new AutoTempFileWriter(
							bufferWriter,
							tempFileThreshold,
							tempFileList
						);
					} else {
						// Warn once
						synchronized(tempFileWarningLock) {
							if(!tempFileWarned) {
								if(logger.isLoggable(Level.WARNING)) {
									logger.log(
										Level.WARNING,
										"TempFileContext not initialized: refusing to automatically create temp files for large buffers.  "
										+ "Additional heap space may be used for large requests.  "
										+ "Please add the " + TempFileContext.class.getName() + " filter to your web.xml file.",
										new Throwable("Stack Trace")
									);
								}
								tempFileWarned = true;
							}
						}
					}
				}
				if(ENABLE_BUFFER_LOGGING) bufferWriter = new LoggingWriter(bufferWriter, log);
				JspFragment body = getJspBody();
				if(body!=null) {
					final MediaType myContentType = getContentType();
					MediaValidator captureValidator = MediaValidator.getMediaValidator(myContentType, bufferWriter);
					ThreadEncodingContext.contentType.set(myContentType);
					ThreadEncodingContext.validMediaInput.set(captureValidator);
					try {
						body.invoke(captureValidator);
						captureValidator.flush();
					} finally {
						// Restore previous encoding context that is used for our output
						ThreadEncodingContext.contentType.set(parentContentType);
						ThreadEncodingContext.validMediaInput.set(parentValidMediaInput);
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
				MediaType containerContentType;
				if(parentContentType!=null) {
					// Use the output type of the parent
					containerContentType = parentContentType;
				} else {
					// Use the content type of the response
					containerContentType = MediaType.getMediaType(response.getContentType());
				}
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
							doTag(capturedBody, mediaWriter);
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
							doTag(capturedBody, out);
						} finally {
							ThreadEncodingContext.contentType.set(parentContentType);
						}
					} else {
						// Not using an encoder and no parent, validate our own output.
						MediaValidator validator = MediaValidator.getMediaValidator(myOutputType, out);
						ThreadEncodingContext.contentType.set(myOutputType);
						ThreadEncodingContext.validMediaInput.set(validator);
						try {
							doTag(capturedBody, validator);
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
     * Once the data is captured, this is called.
     * type, this version of invoke is called.
     */
    abstract protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException;
}

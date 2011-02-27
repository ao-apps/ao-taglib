/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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
import com.aoindustries.io.AutoTempFileWriter;
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
public abstract class AutoEncodingBufferedTag extends SimpleTagSupport implements ContentTypeJspTag {

    /**
     * @see ContentTypeJspTag#getContentType()
     */
    @Override
    public abstract MediaType getContentType();

    /**
     * Gets the output type of this tag.  This is used to determine the correct
     * encoder.  If the tag never has any output this should return <code>null</code>.
     * When <code>null</code> is returned, any output will result in an error.
     */
    public abstract MediaType getOutputType();

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
     */
    public int getTempFileThreshold() {
        return 4 * 1024 * 1024;
    }

    private static final Writer failOnWriteWriter = new Writer() {
        @Override
        public void write(int c) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public void write(char cbuf[]) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public void write(String str) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public Writer append(CharSequence csq) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public Writer append(char c) throws IOException {
            throw new IOException(ApplicationResources.accessor.getMessage("AutoEncodingBufferedTag.noOutputAllowed"));
        }

        @Override
        public void flush() {
            // Do nothing
        }

        @Override
        public void close() {
            // Do nothing
        }
    };

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

            // Capture the body output while validating
            AutoTempFileWriter capturedBody = new AutoTempFileWriter(getInitialBufferSize(), getTempFileThreshold());
            try {
                MediaValidator captureValidator = MediaValidator.getMediaValidator(myContentType, capturedBody);
                inputValidator = captureValidator;
                JspFragment body = getJspBody();
                if(body!=null) {
                    body.invoke(captureValidator);
                    captureValidator.flush();
                }

                MediaType myOutputType = getOutputType();
                if(myOutputType==null) {
                    // No output, error if anything written.
                    doTag(capturedBody, failOnWriteWriter);
                } else {
                    // Find the encoder
                    MediaEncoder mediaEncoder = MediaEncoder.getMediaEncoder(response, myOutputType, containerContentType, out);
                    if(mediaEncoder!=null) {
                        // Encode the content.  The encoder is also a redundant validator for our input and guarantees valid output for our parent.
                        mediaEncoder.writePrefix();
                        try {
                            doTag(capturedBody, mediaEncoder);
                        } finally {
                            mediaEncoder.writeSuffix();
                        }
                    } else {
                        // Not using an encoder, validate our own output.
                        MediaValidator validator = MediaValidator.getMediaValidator(myOutputType, out);
                        doTag(capturedBody, validator);
                    }
                }
            } finally {
                capturedBody.delete();
            }
        } catch(MediaException err) {
            throw new JspException(err);
        }
    }

    /**
     * Once the data is captured, this is called.
     * type, this version of invoke is called.
     */
    abstract protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException;
}

/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.ValidMediaInput;
import javax.servlet.ServletRequest;

/**
 * Since the parent tag is not available from included JSP pages, the current
 * content type and validator is maintained as a request attribute.
 * These are updated for each of the nested tag levels.
 *
 * @author  AO Industries, Inc.
 */
class ThreadEncodingContext {

	private static final String CURRENT_CONTEXT_REQUEST_ATTRIBUTE = ThreadEncodingContext.class.getName() + ".currentContext";

	static ThreadEncodingContext getCurrentContext(ServletRequest request) {
		return (ThreadEncodingContext)request.getAttribute(CURRENT_CONTEXT_REQUEST_ATTRIBUTE);
	}

	static void setCurrentContext(ServletRequest request, ThreadEncodingContext context) {
		request.setAttribute(CURRENT_CONTEXT_REQUEST_ATTRIBUTE, context);
	}

	/**
	 * The content type that is currently be written or null if not set.
	 */
	final MediaType contentType;

	/**
	 * The validator that is ensuring the data being written is valid for the current
	 * outputType.
	 */
	final ValidMediaInput validMediaInput;

	ThreadEncodingContext(MediaType contentType, ValidMediaInput validMediaInput) {
		this.contentType = contentType;
		this.validMediaInput = validMediaInput;
	}
}

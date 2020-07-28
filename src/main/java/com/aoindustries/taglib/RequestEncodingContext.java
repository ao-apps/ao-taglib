/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2012, 2016, 2017, 2020  AO Industries, Inc.
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
// TODO: Reset this on ao-servlet-subrequest sub-requests?
//       Or done as a registrable subrequest event?  (Remove self from subrequest attributes OnSubrequest)
//       Or should this just be reset of SemantiCCMS page captures only?
//       Basically, how do we know when in a new page, and the old tag context is not actually what we want?
class RequestEncodingContext {

	private static final String CURRENT_CONTEXT_REQUEST_ATTRIBUTE = RequestEncodingContext.class.getName() + ".currentContext";

	static RequestEncodingContext getCurrentContext(ServletRequest request) {
		return (RequestEncodingContext)request.getAttribute(CURRENT_CONTEXT_REQUEST_ATTRIBUTE);
	}

	static void setCurrentContext(ServletRequest request, RequestEncodingContext context) {
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

	RequestEncodingContext(MediaType contentType, ValidMediaInput validMediaInput) {
		this.contentType = contentType;
		this.validMediaInput = validMediaInput;
	}
}

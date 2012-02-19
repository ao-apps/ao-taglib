/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.ValidMediaInput;

/**
 * Since the parent tag is not available from included JSP pages, the current
 * content type and validator is maintained as a ThreadLocal.  These are updated
 * for each of the nested tag levels.
 *
 * @author  AO Industries, Inc.
 */
class ThreadEncodingContext {

    /**
     * The content type that is currently be written or null if not set.
     */
    static ThreadLocal<MediaType> contentType = new ThreadLocal<MediaType>();

    /**
     * The validator that is ensuring the data being written is valid for the current
     * outputType.
     */
    static ThreadLocal<ValidMediaInput> validMediaInput = new ThreadLocal<ValidMediaInput>();

    // Make no instances
    private ThreadEncodingContext() {
    }
}

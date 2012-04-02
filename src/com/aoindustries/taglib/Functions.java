/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2012  AO Industries, Inc.
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

import com.aoindustries.servlet.filter.FunctionContext;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.util.StringUtility;

final public class Functions {

    private Functions() {
    }

    public static String join(Iterable<?> iter, String separator) {
        if(iter==null) return null;
        return StringUtility.join(iter, separator);
    }

    public static String getAbsoluteURL(String relPath) {
        return ServletUtil.getAbsoluteURL(FunctionContext.getRequest(), relPath);
    }

    public static String encodeURL(String url) {
        return FunctionContext.getResponse().encodeURL(url);
    }
}

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
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

import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;

/**
 * @author  AO Industries, Inc.
 */
public class NeedAttributeParentException extends LocalizedJspTagException {

    private static final long serialVersionUID = 1L;

    private final String fromTagName;
    private final String attribute;

    public NeedAttributeParentException(String fromTagName, String attribute) {
        super(accessor, "NeedAttributeParent.message", fromTagName, attribute);
        this.fromTagName = fromTagName;
        this.attribute = attribute;
    }

    public String getFromTagName() {
        return fromTagName;
    }

    public String getAttribute() {
        return attribute;
    }
}

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag extends AutoEncodingFilteredTag {

    private MediaType type = MediaType.JAVASCRIPT;

    @Override
    public MediaType getContentType() {
        return type;
    }

    public String getType() {
        return type.getMediaType();
    }

    public void setType(String type) throws MediaException {
        MediaType newType = MediaType.getMediaType(type);
        if(newType!=MediaType.JAVASCRIPT) throw new MediaException(ApplicationResources.accessor.getMessage("ScriptTag.unsupportedMediaType", type));
        this.type = newType;
    }
}

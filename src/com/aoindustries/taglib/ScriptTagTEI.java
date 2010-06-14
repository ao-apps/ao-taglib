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

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTagTEI extends TagExtraInfo {

    @Override
    public ValidationMessage[] validate(TagData data) {
        Object o = data.getAttribute("type");
        if(
            o != null
            && o != TagData.REQUEST_TIME_VALUE
        ) {
            String type = (String)o;
            try {
                MediaType mediaType = MediaType.getMediaType(type);
                if(mediaType!=MediaType.JAVASCRIPT) {
                    return new ValidationMessage[] {
                        new ValidationMessage(data.getId(), ApplicationResources.accessor.getMessage("ScriptTag.unsupportedMediaType", type))
                    };
                }
            } catch(MediaException err) {
                return new ValidationMessage[] {
                    new ValidationMessage(data.getId(), err.getMessage())
                };
            }
        }
        return null;
    }
}

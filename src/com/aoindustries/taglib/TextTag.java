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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.TextInXhtmlEncoder;

/**
 * @author  AO Industries, Inc.
 */
public class TextTag extends AutoEncodingFilteredTag {

    private boolean makeBr = false;
    private boolean makeNbsp = false;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    /**
     * @return the makeBr
     */
    public boolean isMakeBr() {
        return makeBr;
    }

    /**
     * @param makeBr the makeBr to set
     */
    public void setMakeBr(boolean makeBr) {
        this.makeBr = makeBr;
    }

    /**
     * @return the makeNbsp
     */
    public boolean isMakeNbsp() {
        return makeNbsp;
    }

    /**
     * @param makeNbsp the makeNbsp to set
     */
    public void setMakeNbsp(boolean makeNbsp) {
        this.makeNbsp = makeNbsp;
    }

    @Override
    protected void setMediaEncoderOptions(MediaEncoder mediaEncoder) {
        if(mediaEncoder instanceof TextInXhtmlEncoder) {
            TextInXhtmlEncoder tixEncoder = (TextInXhtmlEncoder)mediaEncoder;
            tixEncoder.setMakeNbsp(makeNbsp);
            tixEncoder.setMakeBr(makeBr);
        }
    }
}

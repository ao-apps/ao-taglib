package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.TextInXhtmlEncoder;

/**
 * @author  AO Industries, Inc.
 */
public class TextTag extends AutoEncodingFilteredTag {

    private static final long serialVersionUID = 1L;

    private boolean makeBr = false;
    private boolean makeNbsp = false;

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

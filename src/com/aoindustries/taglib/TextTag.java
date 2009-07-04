package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaType;

/**
 * @author  AO Industries, Inc.
 */
public class TextTag extends AutoEncodingSimpleTag {

    private static final long serialVersionUID = 1L;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }
}

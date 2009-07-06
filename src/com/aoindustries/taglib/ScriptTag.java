package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import java.util.Locale;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag extends AutoEncodingFilteredTag {

    private static final long serialVersionUID = 1L;

    private MediaType type = MediaType.JAVASCRIPT;

    public MediaType getContentType() {
        return type;
    }

    public String getType() {
        return type.getMediaType();
    }

    public void setType(String type) throws MediaException {
        Locale locale = Locale.getDefault();
        MediaType newType = MediaType.getMediaType(locale, type);
        if(newType!=MediaType.JAVASCRIPT) throw new MediaException(ApplicationResourcesAccessor.getMessage(locale, "ScriptTag.unsupportedMediaType", type));
        this.type = newType;
    }
}

package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaException;
import com.aoindustries.media.MediaType;
import java.util.Locale;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class WriteTagTEI extends TagExtraInfo {

    @Override
    public ValidationMessage[] validate(TagData data) {
        Object o = data.getAttribute("type");
        if(
            o != null
            && o != TagData.REQUEST_TIME_VALUE
        ) {
            String type = (String)o;
            try {
                MediaType mediaType = MediaType.getMediaType(Locale.getDefault(), type);
                // Value is OK
                return null;
            } catch(MediaException err) {
                return new ValidationMessage[] {
                    new ValidationMessage(
                        data.getId(),
                        err.getMessage()
                    )
                };
            }
        } else {
            return null;
        }
    }
}

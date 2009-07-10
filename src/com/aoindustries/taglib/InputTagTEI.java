package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class InputTagTEI extends TagExtraInfo {

    @Override
    public ValidationMessage[] validate(TagData data) {
        Object o = data.getAttribute("type");
        if(
            o != null
            && o != TagData.REQUEST_TIME_VALUE
        ) {
            String type = (String)o;
            if(!InputTag.isValidType(type)) {
                return new ValidationMessage[] {
                    new ValidationMessage(data.getId(), ApplicationResourcesAccessor.getMessage(Locale.getDefault(), "InputTag.type.invalid", type))
                };
            }
        }
        return null;
    }
}

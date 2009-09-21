/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.StringBuilderWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class ClassTag extends AutoEncodingBufferedTag {

    public MediaType getContentType() {
        return MediaType.URL;
    }

    public MediaType getOutputType() {
        return null;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        JspTag parent = getParent();
        if(parent==null || !(parent instanceof ClassAttribute)) {
            PageContext pageContext = (PageContext)getJspContext();
            Locale userLocale = pageContext.getResponse().getLocale();
            throw new JspException(ApplicationResourcesAccessor.getMessage(userLocale, "ClassTag.needClassAttributeParent"));
        }
        ClassAttribute classAttribute = (ClassAttribute)parent;
        classAttribute.setClazz(capturedBody.toString());
    }
}

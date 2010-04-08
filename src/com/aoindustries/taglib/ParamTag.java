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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class ParamTag extends AutoEncodingBufferedTag implements NameAttribute, ValueAttribute {

    private String name;
    private String value;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    public MediaType getOutputType() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        JspTag parent = findAncestorWithClass(this, ParamsAttribute.class);
        if(parent==null) throw new JspException(ApplicationResourcesAccessor.getMessage(((PageContext)getJspContext()).getResponse().getLocale(), "ParamTag.needParamsAttributeParent"));
        if(name==null) throw new JspException(ApplicationResourcesAccessor.getMessage(((PageContext)getJspContext()).getResponse().getLocale(), "ParamTag.name.required"));
        if(value==null) value = capturedBody.toString().trim();
        ((ParamsAttribute)parent).addParam(name, value);
    }
}
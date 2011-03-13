/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011  AO Industries, Inc.
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
import com.aoindustries.io.AutoTempFileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class ParamsTag extends AutoEncodingBufferedTag implements NameAttribute {

    private String name;
    private Object values;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setValues(Object values) {
        this.values = values;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
        JspTag parent = findAncestorWithClass(this, ParamsAttribute.class);
        if(parent==null) throw new JspException(ApplicationResources.accessor.getMessage("ParamsTag.needParamsAttributeParent"));
        if(name==null) throw new JspException(ApplicationResources.accessor.getMessage("ParamsTag.name.required"));
        if(values!=null) {
            ParamsAttribute paramsParent = (ParamsAttribute)parent;
            if(values instanceof Iterable) {
                for(Object value : (Iterable)values) paramsParent.addParam(name, value==null ? "" : value.toString());
            } else if(values instanceof Iterator) {
                Iterator<?> iter = (Iterator)values;
                while(iter.hasNext()) {
                    Object value = iter.next();
                    paramsParent.addParam(name, value==null ? "" : value.toString());
                }
            } else if(values.getClass().isArray()) {
                int len = Array.getLength(values);
                for(int c=0; c<len; c++) {
                    Object value = Array.get(values, c);
                    paramsParent.addParam(name, value==null ? "" : value.toString());
                }
            } else {
                throw new JspException(ApplicationResources.accessor.getMessage("ParamsTag.values.unexpectedType", values.getClass().getName()));
            }
        }
    }
}

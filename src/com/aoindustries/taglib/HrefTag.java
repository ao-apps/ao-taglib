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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class HrefTag extends AutoEncodingBufferedTag implements ParamsAttribute {

    private HttpParametersMap params;

    @Override
    public MediaType getContentType() {
        return MediaType.URL;
    }

    @Override
    public MediaType getOutputType() {
        return null;
    }

    @Override
    public HttpParameters getParams() {
        return params==null ? EmptyParameters.getInstance() : params;
    }

    @Override
    public void addParam(String name, String value) {
        if(params==null) params = new HttpParametersMap();
        params.addParameter(name, value);
    }

    static String addParams(String href, HttpParameters params) throws UnsupportedEncodingException {
        if(params!=null) {
            boolean hasQuestion = href.indexOf('?')!=-1;
            StringBuilder sb = new StringBuilder(href);
            for(Map.Entry<String,List<String>> entry : params.getParameterMap().entrySet()) {
                String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
                for(String value : entry.getValue()) {
                    if(hasQuestion) sb.append('&');
                    else {
                        sb.append('?');
                        hasQuestion = true;
                    }
                    sb.append(encodedName);
                    if(value!=null) sb.append('=').append(URLEncoder.encode(value, "UTF-8"));
                }
            }
            href = sb.toString();
        }
        return href;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
        JspTag parent = findAncestorWithClass(this, HrefAttribute.class);
        if(parent==null) throw new JspException(ApplicationResources.accessor.getMessage("HrefTag.needHrefAttributeParent"));
        HrefAttribute hrefAttribute = (HrefAttribute)parent;
        hrefAttribute.setHref(addParams(capturedBody.toString().trim(), params));
    }
}

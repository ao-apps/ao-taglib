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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class HrefTag extends AutoEncodingBufferedTag implements ParamsAttribute {

    private SortedMap<String,List<String>> params;

    @Override
    public MediaType getContentType() {
        return MediaType.URL;
    }

    @Override
    public MediaType getOutputType() {
        return null;
    }

    @Override
    public Map<String,List<String>> getParams() {
        if(params==null) return Collections.emptyMap();
        return params;
    }

    @Override
    public void addParam(String name, String value) {
        if(params==null) params = new TreeMap<String,List<String>>();
        List<String> values = params.get(name);
        if(values==null) params.put(name, values = new ArrayList<String>());
        values.add(value);
    }

    static String addParams(String href, Map<String,List<String>> params) throws UnsupportedEncodingException {
        if(params!=null) {
            boolean hasQuestion = href.indexOf('?')!=-1;
            StringBuilder sb = new StringBuilder(href);
            for(Map.Entry<String,List<String>> entry : params.entrySet()) {
                String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
                for(String value : entry.getValue()) {
                    if(hasQuestion) sb.append('&');
                    else {
                        sb.append('?');
                        hasQuestion = true;
                    }
                    sb.append(encodedName).append('=').append(URLEncoder.encode(value, "UTF-8"));
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
        String href = addParams(capturedBody.toString().trim(), params);
        HrefAttribute hrefAttribute = (HrefAttribute)parent;
        hrefAttribute.setHref(href);
    }
}

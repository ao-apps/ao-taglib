/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.Coercion;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.util.ref.ReferenceUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTag
	extends AutoEncodingNullTag
	implements
		HrefAttribute,
		ParamsAttribute,
		HreflangAttribute,
		RelAttribute,
		TypeAttribute
{

    private String href;
    private MutableHttpParameters params;
	private Object hreflang;
	private String rel;
	private String type;

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
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

	@Override
    public Object getHreflang() {
        return hreflang;
    }

	@Override
	public void setHreflang(Object hreflang) {
		this.hreflang = ReferenceUtils.replace(this.hreflang, hreflang);
	}

	@Override
    public String getRel() {
        return rel;
    }

    @Override
    public void setRel(String rel) {
        this.rel = rel;
    }

	@Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void doTag(Writer out) throws JspException, IOException {
		try {
			// Call super so attributes may be set by nested tags
			super.doTag(out);
			JspTag parent = findAncestorWithClass(this, LinksAttribute.class);
			if(parent!=null) {
				((LinksAttribute)parent).addLink(
					new Link(
						href,
						params,
						Coercion.toString(hreflang),
						rel,
						type
					)
				);
			} else {
				PageContext pageContext = (PageContext)getJspContext();
				out.write("<link");
				ATag.writeHref(out, pageContext, href, params);
				if(hreflang!=null) {
					out.write(" hreflang=\"");
					Coercion.write(hreflang, textInXhtmlAttributeEncoder, out);
					out.write('"');
				}
				if(rel!=null) {
					out.write(" rel=\"");
					encodeTextInXhtmlAttribute(rel, out);
					out.write('"');
				}
				if(type!=null) {
					out.write(" type=\"");
					encodeTextInXhtmlAttribute(type, out);
					out.write('"');
				}
				out.write(" />");
			}
		} finally {
			hreflang = ReferenceUtils.release(hreflang);
		}
    }
}

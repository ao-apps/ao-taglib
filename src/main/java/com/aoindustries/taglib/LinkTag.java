/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2013, 2015, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-taglib.
 *
 * ao-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTag
	extends AutoEncodingNullTag
	implements
		DynamicAttributes,
		HrefAttribute,
		ParamsAttribute,
		HreflangAttribute,
		RelAttribute,
		TypeAttribute,
		TitleAttribute
{

	private String href;
	private MutableHttpParameters params;
	private boolean hrefAbsolute;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;
	private Object hreflang;
	private Object rel;
	private Object type;
	private String media;
	private Object title;

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

	public boolean getHrefAbsolute() {
		return hrefAbsolute;
	}

	public void setHrefAbsolute(boolean hrefAbsolute) {
		this.hrefAbsolute = hrefAbsolute;
	}

	public String getAddLastModified() {
		return addLastModified.getLowerName();
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = LastModifiedServlet.AddLastModifiedWhen.valueOfLowerName(addLastModified);
	}

	@Override
	public Object getHreflang() {
		return hreflang;
	}

	@Override
	public void setHreflang(Object hreflang) {
		this.hreflang = hreflang;
	}

	@Override
	public Object getRel() {
		return rel;
	}

	@Override
	public void setRel(Object rel) {
		this.rel = rel;
	}

	@Override
	public Object getType() {
		return type;
	}

	@Override
	public void setType(Object type) {
		this.type = type;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	@Override
	public Object getTitle() {
		return title;
	}

	@Override
	public void setTitle(Object title) {
		this.title = title;
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(
			uri==null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			ParamUtils.setDynamicAttribute(this, uri, localName, value);
		} else {
			throw new LocalizedJspTagException(
				accessor,
				"error.unexpectedDynamicAttribute",
				localName,
				ParamUtils.PARAM_ATTRIBUTE_PREFIX+"*"
			);
		}
	}

	@Override
	protected void doTag(Writer out) throws JspTagException, IOException {
		JspTag parent = findAncestorWithClass(this, LinksAttribute.class);
		if(parent!=null) {
			((LinksAttribute)parent).addLink(
				new Link(
					href,
					hrefAbsolute,
					params,
					addLastModified,
					Coercion.toString(hreflang),
					Coercion.toString(rel),
					Coercion.toString(type),
					media,
					Coercion.toString(title)
				)
			);
		} else {
			out.write("<link");
			UrlUtils.writeHref(getJspContext(), out, href, params, hrefAbsolute, addLastModified);
			if(hreflang!=null) {
				out.write(" hreflang=\"");
				Coercion.write(hreflang, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(rel!=null) {
				out.write(" rel=\"");
				Coercion.write(rel, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(type!=null) {
				out.write(" type=\"");
				Coercion.write(type, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(media!=null) {
				out.write(" media=\"");
				encodeTextInXhtmlAttribute(media, out);
				out.write('"');
			}
			if(title!=null) {
				out.write(" title=\"");
				Coercion.write(title, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			out.write(" />");
		}
	}
}

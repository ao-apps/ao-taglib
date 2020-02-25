/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.html.Html;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
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
	private MutableURIParameters params;
	private boolean absolute;
	private boolean canonical;
	private AddLastModified addLastModified = AddLastModified.AUTO;
	private Object hreflang;
	private Object rel;
	private Object type;
	private String media; // TODO: media to Object
	private Object title;

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	/**
	 * Copies all values from the provided link.
	 */
	public void setLink(Link link) throws JspTagException {
		setHref(link.getHref());
		setAbsolute(link.getAbsolute());
		URIParameters linkParams = link.getParams();
		if(linkParams != null) {
			for(Map.Entry<String,List<String>> entry : linkParams.getParameterMap().entrySet()) {
				String paramName = entry.getKey();
				for(String paramValue : entry.getValue()) {
					addParam(paramName, paramValue);
				}
			}
		}
		this.addLastModified = link.getAddLastModified();
		setHreflang(link.getHreflang());
		setRel(link.getRel());
		setType(link.getType());
		setMedia(link.getMedia());
		setTitle(link.getTitle());
	}

	@Override
	public void setHref(String href) {
		this.href = AttributeUtils.nullIfEmpty(href);
	}

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new URIParametersMap();
		params.addParameter(name, value);
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	public void setCanonical(boolean canonical) {
		this.canonical = canonical;
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = AddLastModified.valueOfLowerName(addLastModified.trim().toLowerCase(Locale.ROOT));
	}

	@Override
	public void setHreflang(Object hreflang) throws JspTagException {
		this.hreflang = hreflang;
	}

	@Override
	public void setRel(Object rel) throws JspTagException {
		this.rel = AttributeUtils.trimNullIfEmpty(rel);
	}

	@Override
	public void setType(Object type) throws JspTagException {
		this.type = AttributeUtils.trimNullIfEmpty(type);
	}

	public void setMedia(String media) {
		this.media = AttributeUtils.trimNullIfEmpty(media);
	}

	@Override
	public void setTitle(Object title) throws JspTagException {
		this.title = AttributeUtils.trimNullIfEmpty(title);
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
			String hreflangStr;
			if(hreflang instanceof Locale) {
				hreflangStr = ((Locale)hreflang).toLanguageTag();
			} else {
				hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
				hreflangStr = Coercion.toString(hreflang);
			}
			((LinksAttribute)parent).addLink(
				new Link(
					href,
					absolute,
					canonical,
					params,
					addLastModified,
					hreflangStr,
					Coercion.toString(rel),
					Coercion.toString(type),
					media,
					Coercion.toString(title)
				)
			);
		} else {
			PageContext pageContext = (PageContext)getJspContext();
			Html html = HtmlEE.get(
				pageContext.getServletContext(),
				(HttpServletRequest)pageContext.getRequest(),
				out
			);
			com.aoindustries.html.Link link = html.link()
				.href(UrlUtils.getHref(pageContext, href, params, addLastModified, absolute, canonical));
			if(hreflang instanceof Locale) {
				link.hreflang((Locale)hreflang);
			} else {
				hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
				link.hreflang(Coercion.toString(hreflang));
			}
			link
				// TODO: These are coerced in both uses, change attribute to String
				.rel(Coercion.toString(rel))
				.type(Coercion.toString(type))
				.media(media)
				.title(title)
				.__();
		}
	}
}

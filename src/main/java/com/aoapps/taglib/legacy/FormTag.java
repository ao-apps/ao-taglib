/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib.legacy;

import com.aoapps.encoding.Doctype;
import static com.aoapps.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoapps.encoding.MediaType;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.net.AnyURI;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIEncoder;
import com.aoapps.net.URIParametersMap;
import com.aoapps.net.URIParametersUtils;
import com.aoapps.net.URIResolver;
import com.aoapps.servlet.http.Dispatcher;
import com.aoapps.taglib.ActionAttribute;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.EnctypeAttribute;
import static com.aoapps.taglib.FormTag.RESOURCES;
import com.aoapps.taglib.FormTagTEI;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.MethodAttribute;
import com.aoapps.taglib.OnsubmitAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.TargetAttribute;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class FormTag extends ElementBufferedBodyTag
	implements
		// Attributes
		ActionAttribute,
		EnctypeAttribute,
		MethodAttribute,
		ParamsAttribute,
		TargetAttribute,
		// Events
		OnsubmitAttribute
{

/* SimpleTag only:
	public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, FormTag.class);
/**/

	public FormTag() {
		init();
	}

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	private String action;
	@Override
	public void setAction(String action) {
		this.action = Strings.nullIfEmpty(action);
	}

	private String enctype;
	@Override
	public void setEnctype(String enctype) {
		this.enctype = Strings.trimNullIfEmpty(enctype);
	}

	private String method;
	@Override
	public void setMethod(String method) {
		method = Strings.trimNullIfEmpty(method);
		if(method != null && !FormTagTEI.isValidMethod(method)) throw new LocalizedIllegalArgumentException(RESOURCES, "method.invalid", method);
		this.method = method;
	}

	private MutableURIParameters params;
	@Override
	public void addParam(String name, Object value) {
		if(params == null) params = new URIParametersMap();
		params.add(name, value);
	}

	private String target;
	@Override
	public void setTarget(String target) {
		this.target = Strings.trimNullIfEmpty(target);
	}

	private Object onsubmit;
	@Override
	public void setOnsubmit(Object onsubmit) {
		this.onsubmit = AttributeUtils.trimNullIfEmpty(onsubmit);
	}

	/**
	 * @see  ParamUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoapps.taglib.ParamsAttribute)
	 */
	@Override
	protected boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns) throws JspTagException {
		return
			super.addDynamicAttribute(uri, localName, value, expectedPatterns)
			|| ParamUtils.addDynamicAttribute(uri, localName, value, expectedPatterns, this);
	}

	private void init() {
		action = null;
		enctype = null;
		method = null;
		params = null;
		target = null;
		onsubmit = null;
	}

	@Override
/* BodyTag only: */
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		DocumentEE document = new DocumentEE(
			pageContext.getServletContext(),
			request,
			response,
			out,
			false, // Do not add extra newlines to JSP
			false  // Do not add extra indentation to JSP
		);
		Doctype doctype = document.encodingContext.getDoctype();
		out.write("<form");
		GlobalAttributesUtils.writeGlobalAttributes(global, out);
		Map<String, List<String>> actionParams;
		if(action != null) {
			out.write(" action=\"");
			String encodedAction = URIResolver.getAbsolutePath(Dispatcher.getCurrentPagePath(request), action);
			if(encodedAction.startsWith("/")) {
				String contextPath = request.getContextPath();
				if(!contextPath.isEmpty()) encodedAction = contextPath + encodedAction;
			}
			encodedAction = response.encodeURL(URIEncoder.encodeURI(encodedAction));
			// The action attribute is everything up to the first question mark
			AnyURI actionURI = new AnyURI(encodedAction);
			actionParams = URIParametersUtils.of(actionURI.getQueryString()).getParameterMap();
			textInXhtmlAttributeEncoder.write(actionURI.setQueryString(null).toString(), out);
			out.append('"');
		} else {
			if(doctype != Doctype.HTML5) {
				// Action required before HTML 5
				out.write(" action=\"\"");
			}
			actionParams = null;
		}
		if(enctype != null) {
			out.write(" enctype=\"");
			encodeTextInXhtmlAttribute(enctype, out);
			out.append('"');
		}
		if(method != null) {
			out.write(" method=\"");
			out.write(method);
			out.append('"');
		}
		if(target != null) {
			out.write(" target=\"");
			encodeTextInXhtmlAttribute(target, out);
			out.append('"');
		}
		if(onsubmit != null) {
			out.write(" onsubmit=\"");
			MarkupCoercion.write(onsubmit, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
		out.append('>');
		// Automatically add URL request parameters as hidden fields to support custom URL rewritten parameters in GET requests.
		boolean didDiv = false;
		if(actionParams != null && !actionParams.isEmpty()) {
			for(Map.Entry<String, List<String>> entry : actionParams.entrySet()) {
				if(!didDiv && doctype != Doctype.HTML5) {
					out.write("<div>\n");
					didDiv = true;
				}
				String name = entry.getKey();
				for(String value : entry.getValue()) {
					document.input().hidden().name(name).value(value).__().autoNl();
				}
			}
		}
		// Write any parameters as hidden fields
		if(params != null) {
			for(Map.Entry<String, List<String>> entry : params.getParameterMap().entrySet()) {
				if(!didDiv && doctype != Doctype.HTML5) {
					out.write("<div>\n");
					didDiv = true;
				}
				String name = entry.getKey();
				List<String> paramValues = entry.getValue();
				assert !paramValues.isEmpty();
				for(String paramValue : paramValues) {
					document.input().hidden().name(name).value(paramValue).__().autoNl();
				}
			}
		}
		if(didDiv) out.write("</div>");
		MarkupCoercion.write(capturedBody, MarkupType.XHTML, out, true);
		out.write("</form>");
/* BodyTag only: */
		return EVAL_PAGE;
/**/
	}

/* BodyTag only: */
	@Override
	public void doFinally() {
		try {
			init();
		} finally {
			super.doFinally();
		}
	}
/**/
}

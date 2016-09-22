/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2010, 2011, 2013, 2015, 2016  AO Industries, Inc.
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
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class FormTag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
		MethodAttribute,
		IdAttribute,
		ActionAttribute,
		ParamsAttribute,
		TargetAttribute,
		EnctypeAttribute,
		StyleAttribute,
		OnsubmitAttribute
{

	public static boolean isValidMethod(String method) {
		return
			"get".equals(method)
			|| "post".equals(method)
		;
	}

	private String method = "get";
	private Object id;
	private String action;
	private MutableHttpParameters params;
	private Object target;
	private Object enctype;
	private Object style;
	private Object onsubmit;

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setMethod(String method) throws JspTagException {
		if(!isValidMethod(method)) throw new LocalizedJspTagException(ApplicationResources.accessor, "FormTag.method.invalid", method);
		this.method = method;
	}

	@Override
	public void setId(Object id) {
		this.id = id;
	}

	@Override
	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new HttpParametersMap();
		params.addParameter(name, value);
	}

	@Override
	public void setTarget(Object target) {
		this.target = target;
	}

	@Override
	public void setEnctype(Object enctype) {
		this.enctype = enctype;
	}

	@Override
	public void setStyle(Object style) {
		this.style = style;
	}

	@Override
	public void setOnsubmit(Object onsubmit) {
		this.onsubmit = onsubmit;
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
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		String responseEncoding = response.getCharacterEncoding();
		out.write("<form method=\"");
		out.write(method);
		out.write('"');
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		// TODO: Allow params on the form itself?  These would all become hiddens.  It would give a nice way
		//       to pass-through values in the same was a redirect or link.
		final int questionPos;
		if(action!=null) {
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			out.write(" action=\"");
			action = ServletUtil.getAbsolutePath(Dispatcher.getCurrentPagePath(request), action);
			if(action.startsWith("/")) {
				String contextPath = request.getContextPath();
				if(contextPath.length()>0) action = contextPath + action;
			}
			action = com.aoindustries.net.UrlUtils.encodeUrlPath(action, responseEncoding);
			action = response.encodeURL(action);
			questionPos = action.indexOf('?');
			// The action attribute is everything up to the first question mark
			encodeTextInXhtmlAttribute(
				action,
				0,
				questionPos==-1 ? action.length() : questionPos,
				out
			);
			out.write('"');
		} else {
			questionPos = -1;
		}
		if(target!=null) {
			out.write(" target=\"");
			Coercion.write(target, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(enctype!=null) {
			out.write(" enctype=\"");
			Coercion.write(enctype, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onsubmit!=null) {
			out.write(" onsubmit=\"");
			Coercion.write(onsubmit, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write('>');
		// Automatically add URL request parameters as hidden fields to support custom URL rewritten parameters in GET requests.
		boolean didDiv = false;
		if(questionPos!=-1) {
			assert action!=null;
			List<String> nameVals = StringUtility.splitString(action, questionPos+1, action.length(), '&');
			if(!nameVals.isEmpty()) {
				if(!didDiv) {
					out.write("<div>\n");
					didDiv = true;
				}
				for(String nameVal : nameVals) {
					int equalPos = nameVal.indexOf('=');
					String name, value;
					if(equalPos==-1) {
						name = URLDecoder.decode(nameVal, responseEncoding);
						value = "";
					} else {
						name = URLDecoder.decode(nameVal.substring(0, equalPos), responseEncoding);
						value = URLDecoder.decode(nameVal.substring(equalPos+1), responseEncoding);
					}
					out.write("<input type=\"hidden\" name=\"");
					encodeTextInXhtmlAttribute(name, out);
					out.write("\" value=\"");
					encodeTextInXhtmlAttribute(value, out);
					out.write("\" />\n");
				}
			}
		}
		// Write any parameters as hidden fields
		if(params!=null) {
			Iterator<String> paramNames = params.getParameterNames();
			while(paramNames.hasNext()) {
				String paramName = paramNames.next();
				List<String> paramValues = params.getParameterValues(paramName);
				if(paramValues!=null && !paramValues.isEmpty()) {
					if(!didDiv) {
						out.write("<div>\n");
						didDiv = true;
					}
					for(String paramValue : paramValues) {
						out.write("<input type=\"hidden\" name=\"");
						encodeTextInXhtmlAttribute(paramName, out);
						out.write("\" value=\"");
						encodeTextInXhtmlAttribute(paramValue, out);
						out.write("\" />\n");
					}
				}
			}
		}
		if(didDiv) out.write("</div>");
		MarkupUtils.writeWithMarkup(capturedBody, MarkupType.XHTML, out);
		out.write("</form>");
	}
}

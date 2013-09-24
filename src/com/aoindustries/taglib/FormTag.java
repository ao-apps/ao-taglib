/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013  AO Industries, Inc.
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

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.NewEncodingUtils;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class FormTag
	extends AutoEncodingBufferedTag
	implements
		MethodAttribute,
		IdAttribute,
		ActionAttribute,
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
	private String target;
    private Object enctype;
    private String style;
    private String onsubmit;

    @Override
    public MediaType getContentType() {
        return MediaType.XHTML;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) throws JspException {
        if(!isValidMethod(method)) throw new LocalizedJspException(ApplicationResources.accessor, "FormTag.method.invalid", method);
        this.method = method;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
		this.id = id;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

	@Override
    public Object getEnctype() {
        return enctype;
    }

    @Override
    public void setEnctype(Object enctype) {
		this.enctype = enctype;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getOnsubmit() {
        return onsubmit;
    }

    @Override
    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		out.write("<form method=\"");
		out.write(method);
		out.write('"');
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		String actionUrl;
		if(action!=null) {
			HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			out.write(" action=\"");
			if(action.startsWith("/")) {
				String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
				if(contextPath.length()>0) action = contextPath+action;
			}
			actionUrl = response.encodeURL(NewEncodingUtils.encodeUrlPath(action));
			encodeTextInXhtmlAttribute(actionUrl, out);
			out.write('"');
		} else {
			actionUrl = null;
		}
		if(target!=null) {
			out.write(" target=\"");
			encodeTextInXhtmlAttribute(target, out);
			out.write('"');
		}
		if(enctype!=null) {
			out.write(" enctype=\"");
			Coercion.write(enctype, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			encodeTextInXhtmlAttribute(style, out);
			out.write('"');
		}
		if(onsubmit!=null) {
			out.write(" onsubmit=\"");
			encodeJavaScriptInXhtmlAttribute(onsubmit, out);
			out.write('"');
		}
		out.write('>');
		// Automatically add URL request parameters as hidden fields to support custom URL rewritten parameters in GET requests.
		if(actionUrl!=null) {
			int questionPos = actionUrl.indexOf('?');
			if(questionPos!=-1) {
				String[] nameVals = StringUtility.splitString(actionUrl.substring(questionPos+1), '&');
				if(nameVals.length>0) {
					out.write("<div>");
					for(String nameVal : nameVals) {
						int equalPos = nameVal.indexOf('=');
						String name, value;
						if(equalPos==-1) {
							name = URLDecoder.decode(nameVal, "UTF-8");
							value = "";
						} else {
							name = URLDecoder.decode(nameVal.substring(0, equalPos), "UTF-8");
							value = URLDecoder.decode(nameVal.substring(equalPos+1), "UTF-8");
						}
						out.write("<input type=\"hidden\" name=\"");
						encodeTextInXhtmlAttribute(name, out);
						out.write("\" value=\"");
						encodeTextInXhtmlAttribute(value, out);
						out.write("\" />");
					}
					out.write("</div>");
				}
			}
		}
		capturedBody.writeTo(out);
		out.write("</form>");
    }
}

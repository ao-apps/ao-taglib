/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2013  AO Industries, Inc.
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

import com.aoindustries.io.NullWriter;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * TODO: Redirect does not work when response has been committed, and no error is logged.
 *       Or some other issue, because jsp:forward worked while redirect didn't when HTML output before redirect.
 *
 * @author  AO Industries, Inc.
 */
public class RedirectTag
	extends SimpleTagSupport
	implements
		DynamicAttributes,
		HrefAttribute,
		ParamsAttribute
{

	public static boolean isValidStatusCode(String statusCode) {
        return
            "moved_permanently".equals(statusCode)
            || "permanent".equals(statusCode)
            || "301".equals(statusCode)
            || "moved_temporarily".equals(statusCode)
            || "found".equals(statusCode)
            || "temporary".equals(statusCode)
            || "302".equals(statusCode)
            || "see_other".equals(statusCode)
            || "303".equals(statusCode)
        ;
    }

	private String statusCode;
    private String href;
    private HttpParametersMap params;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) throws JspException {
        if(!isValidStatusCode(statusCode)) throw new LocalizedJspException(ApplicationResources.accessor, "RedirectTag.statusCode.invalid", statusCode);
        this.statusCode = statusCode;
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
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		ParamUtils.setDynamicAttribute(this, uri, localName, value);
	}

	@Override
    public void doTag() throws IOException, SkipPageException, JspException {
        JspFragment body = getJspBody();
        if(body!=null) {
            // Discard all nested output, since this will not use the output and this
            // output could possibly fill the response buffer and prevent the redirect
            // from functioning.
            body.invoke(NullWriter.getInstance());
        }
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

        int status;
        if(statusCode==null) throw new AttributeRequiredException("statusCode");
        if(
			"301".equals(statusCode)
			|| "moved_permanently".equals(statusCode)
			|| "permanent".equals(statusCode)
		) {
            status = HttpServletResponse.SC_MOVED_PERMANENTLY;
        } else if(
			"302".equals(statusCode)
			|| "moved_temporarily".equals(statusCode)
			|| "found".equals(statusCode)
			|| "temporary".equals(statusCode)
		) {
            status = HttpServletResponse.SC_MOVED_TEMPORARILY;
            //response.sendRedirect(encodedHref);
        } else if(
			"303".equals(statusCode)
			|| "see_other".equals(statusCode)
		) {
            status = HttpServletResponse.SC_SEE_OTHER;
        } else {
            throw new AssertionError("Unexpected value for statusCode: "+statusCode);
        }

        // Add any parameters to the URL
        if(href==null) throw new AttributeRequiredException("href");
        href = HttpParametersUtils.addParams(href, params);

        ServletUtil.sendRedirect(request, response, href, status);
        throw new SkipPageException();
    }
}

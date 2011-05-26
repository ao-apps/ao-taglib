/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

public class HtmlTag extends AutoEncodingFilteredTag {

    public static final String CONTENT_TYPE_XHTML = "application/xhtml+xml";
    public static final String CONTENT_TYPE_HTML = "text/html";

    /**
     * Determine the content type by the rules defined in http://www.w3.org/TR/xhtml-media-types/
     * Default to application/xhtml+xml as discussed at http://www.smackthemouse.com/xhtmlxml
     */
    public static String getXhtmlContentType(HttpServletRequest request) {
        // Some test accept headers:
        //   Firefox: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
        //   IE 6: */*
        //   IE 8: */*
        //   IE 8 Compat: */*
        Enumeration acceptValues = request.getHeaders("Accept");

        boolean hasAcceptHeader = false;
        boolean hasAcceptApplicationXhtmlXml = false;
        boolean hasAcceptHtmlHtml = false;
        boolean hasAcceptStarStar = false;
        if(acceptValues!=null) {
            while(acceptValues.hasMoreElements()) {
                hasAcceptHeader = true;
                for(String value : StringUtility.splitString((String)acceptValues.nextElement(), ',')) {
                    value = value.trim();
                    String[] params = StringUtility.splitString(value, ';');
                    if(params.length>0) {
                        String acceptType = params[0].trim();
                        if(acceptType.equals("*/*")) {
                            // No q parameter parsing for */*
                            hasAcceptStarStar = true;
                        } else if(
                            // Parse and check the q for these two types
                            acceptType.equalsIgnoreCase("application/xhtml+xml")
                            || acceptType.equalsIgnoreCase("text/html")
                        ) {
                            // Find any q value
                            boolean hasNegativeQ = false;
                            for(int paramNum = 1; paramNum<params.length; paramNum++) {
                                String paramSet = params[paramNum].trim();
                                if(paramSet.startsWith("q=") || paramSet.startsWith("Q=")) {
                                    try {
                                        float q = Float.parseFloat(paramSet.substring(2).trim());
                                        if(q<0) {
                                            hasNegativeQ = true;
                                            break;
                                        }
                                    } catch(NumberFormatException err) {
                                        // Intentionally ignored
                                    }
                                }
                            }
                            if(!hasNegativeQ) {
                                if(acceptType.equalsIgnoreCase("application/xhtml+xml")) hasAcceptApplicationXhtmlXml = true;
                                else if(acceptType.equalsIgnoreCase("text/html")) hasAcceptHtmlHtml = true;
                                else throw new AssertionError("Unexpected value for acceptType: "+acceptType);
                            }
                        }
                    }
                }
            }
        }
        // If the Accept header explicitly contains application/xhtml+xml  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
        if(hasAcceptApplicationXhtmlXml) return CONTENT_TYPE_XHTML;
        // If the Accept header explicitly contains text/html  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
        if(hasAcceptHtmlHtml) return CONTENT_TYPE_HTML;
        // If the accept header contains "*/*" (a convention some user agents use to indicate that they will accept anything), deliver the document using text/html.
        if(hasAcceptStarStar) return CONTENT_TYPE_HTML;
        // If has no accept headers
        if(!hasAcceptHeader) return CONTENT_TYPE_XHTML;
        // This choice is not clear from either of the cited documents.  If there is an accept line,
        // and it doesn't have */* or application/xhtml+xml or text/html, we'll serve as text/html
        // since it is a fairly broken client anyway and would be even less likely to know xhtml.
        return CONTENT_TYPE_HTML;

    }

    private String doctype = "strict";
    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    private boolean forceHtml = false;
    public void setForceHtml(boolean forceHtml) {
        this.forceHtml = forceHtml;
    }

    @Override
    public MediaType getContentType() {
        return MediaType.XHTML;
    }

    @Override
    public void invokeAutoEncoding(Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        ServletResponse response = pageContext.getResponse();

        // Clear the output buffer
        response.resetBuffer();

        // Set the content type
        response.setContentType(
            forceHtml
            ? CONTENT_TYPE_HTML
            : getXhtmlContentType((HttpServletRequest)pageContext.getRequest())
        );
        // response.setCharacterEncoding("UTF-8");

        // Write the DOCTYPE
        if("strict".equals(doctype))            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        else if("transitional".equals(doctype)) out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
        else if("frameset".equals(doctype))     out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">\n");
        else if(!"none".equals(doctype))        throw new JspException(ApplicationResources.accessor.getMessage("HtmlTag.doctype.invalid", doctype));

        // Begin the HTML tag
        out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
        Locale locale = response.getLocale();
        if(locale!=null) {
            String language = locale.getLanguage();
            if(language.length()>0) {
                String country = locale.getCountry();
                out.write(" lang=\"");
                out.write(language);
                if(country.length()>0) {
                    out.write('-');
                    out.write(country);
                }
                out.write("\" xml:lang=\"");
                out.write(language);
                if(country.length()>0) {
                    out.write('-');
                    out.write(country);
                }
                out.write('"');
            }
        }
        out.write('>');

        // Include the body
        JspFragment body = getJspBody();
        if(body!=null) body.invoke(out);

        // End the HTML tag
        out.write("</html>");
    }
}

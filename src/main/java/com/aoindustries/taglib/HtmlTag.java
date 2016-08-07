/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class HtmlTag extends AutoEncodingFilteredTag {

    public static final String CONTENT_TYPE_XHTML = "application/xhtml+xml";
    public static final String CONTENT_TYPE_HTML = "text/html";

	/**
     * Determine if the content may be served as application/xhtml+xml by the
	 * rules defined in http://www.w3.org/TR/xhtml-media-types/
     * Default to application/xhtml+xml as discussed at http://www.smackthemouse.com/xhtmlxml
     */
    public static boolean useXhtmlContentType(HttpServletRequest request) {
        // Some test accept headers:
        //   Firefox: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
        //   IE 6: */*
        //   IE 8: */*
        //   IE 8 Compat: */*
        @SuppressWarnings("unchecked")
        Enumeration<String> acceptValues = request.getHeaders("Accept");

        boolean hasAcceptHeader = false;
        boolean hasAcceptApplicationXhtmlXml = false;
        boolean hasAcceptHtmlHtml = false;
        boolean hasAcceptStarStar = false;
        if(acceptValues!=null) {
            while(acceptValues.hasMoreElements()) {
                hasAcceptHeader = true;
                for(String value : StringUtility.splitString(acceptValues.nextElement(), ',')) {
                    value = value.trim();
                    final List<String> params = StringUtility.splitString(value, ';');
					final int paramsSize = params.size();
                    if(paramsSize>0) {
                        String acceptType = params.get(0).trim();
                        if(acceptType.equals("*/*")) {
                            // No q parameter parsing for */*
                            hasAcceptStarStar = true;
                        } else if(
                            // Parse and check the q for these two types
                            acceptType.equalsIgnoreCase(CONTENT_TYPE_XHTML)
                            || acceptType.equalsIgnoreCase(CONTENT_TYPE_HTML)
                        ) {
                            // Find any q value
                            boolean hasNegativeQ = false;
                            for(int paramNum = 1; paramNum<paramsSize; paramNum++) {
                                String paramSet = params.get(paramNum).trim();
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
                                if(acceptType.equalsIgnoreCase(CONTENT_TYPE_XHTML)) hasAcceptApplicationXhtmlXml = true;
                                else if(acceptType.equalsIgnoreCase(CONTENT_TYPE_HTML)) hasAcceptHtmlHtml = true;
                                else throw new AssertionError("Unexpected value for acceptType: "+acceptType);
                            }
                        }
                    }
                }
            }
        }
        // If the Accept header explicitly contains application/xhtml+xml  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
        if(hasAcceptApplicationXhtmlXml) return true;
        // If the Accept header explicitly contains text/html  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
        if(hasAcceptHtmlHtml) return false;
        // If the accept header contains "*/*" (a convention some user agents use to indicate that they will accept anything), deliver the document using text/html.
        if(hasAcceptStarStar) return false;
        // If has no accept headers
        if(!hasAcceptHeader) return true;
        // This choice is not clear from either of the cited documents.  If there is an accept line,
        // and it doesn't have */* or application/xhtml+xml or text/html, we'll serve as text/html
        // since it is a fairly broken client anyway and would be even less likely to know xhtml.
        return false;

    }

    public enum DocType {
		// See http://www.ibm.com/developerworks/library/x-think45/
		html5 {
            @Override
            public String getDocTypeLine() {
                return "<!DOCTYPE html>\n";
            }
		},
        strict {
            @Override
            public String getDocTypeLine() {
                return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
            }
        },
        transitional {
            @Override
            public String getDocTypeLine() {
                return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
            }
        },
        frameset {
            @Override
            public String getDocTypeLine() {
                return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">\n";
            }
        },
        none {
            @Override
            public String getDocTypeLine() {
                return "";
            }
        };

        public abstract String getDocTypeLine();
    }

	@Override
    public MediaType getContentType() {
        return MediaType.XHTML;
    }

	private DocType doctype = DocType.strict;
    public void setDoctype(String doctype) {
        this.doctype = doctype==null ? null : DocType.valueOf(doctype);
    }

    private boolean forceHtml = false;
    public void setForceHtml(boolean forceHtml) {
        this.forceHtml = forceHtml;
    }

	private String clazz;
    public String getClazz() {
		return clazz;
    }
    public void setClazz(String clazz) {
		this.clazz = clazz;
    }

	private String oldIeClass;
	public void setOldIeClass(String oldIeClass) {
		this.oldIeClass = oldIeClass;
	}

    public static void writeDocTypeLine(DocType docType, Writer out) throws IOException {
        out.write(docType.getDocTypeLine());
    }

    public static void beginHtmlTag(ServletResponse response, Writer out, boolean isXml, String clazz) throws IOException {
        out.write("<html");
		if(isXml) {
			out.write(" xmlns=\"http://www.w3.org/1999/xhtml\"");
		}
		if(clazz!=null) {
			out.write(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.write('"');
		}
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
				out.write('"');
				if(isXml) {
					out.write(" xml:lang=\"");
					out.write(language);
					if(country.length()>0) {
						out.write('-');
						out.write(country);
					}
					out.write('"');
				}
            }
        }
        out.write('>');
    }

    public static void endHtmlTag(Writer out) throws IOException {
        out.write("</html>");
    }

    @Override
    protected void doTag(Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        ServletResponse response = pageContext.getResponse();

        // Clear the output buffer
        response.resetBuffer();

        // Set the content type
		boolean isXml = !forceHtml && useXhtmlContentType((HttpServletRequest)pageContext.getRequest());
        response.setContentType(
            isXml
            ? CONTENT_TYPE_XHTML
            : CONTENT_TYPE_HTML
        );
        // response.setCharacterEncoding("UTF-8");

        writeDocTypeLine(doctype, out);
		if(oldIeClass!=null) {
			out.write("<!--[if lte IE 8]>");
			beginHtmlTag(response, out, isXml, clazz==null ? oldIeClass : (clazz + " " + oldIeClass));
			out.write("<![endif]-->\n"
					+ "<!--[if gt IE 8]><!-->");
	        beginHtmlTag(response, out, isXml, clazz);
			out.write("<!--<![endif]-->");
		} else {
	        beginHtmlTag(response, out, isXml, clazz);
		}
        super.doTag(out);
        endHtmlTag(out);
    }
}

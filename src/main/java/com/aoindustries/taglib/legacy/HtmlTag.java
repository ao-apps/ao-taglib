/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.taglib.legacy;

import com.aoindustries.encoding.Doctype;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.Serialization;
import com.aoindustries.encoding.servlet.DoctypeEE;
import com.aoindustries.encoding.servlet.SerializationEE;
import com.aoindustries.html.any.AnyDocument;
import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.lang.LocalizedIllegalArgumentException;
import com.aoindustries.servlet.ServletUtil;
import static com.aoindustries.taglib.HtmlTag.RESOURCES;
import static com.aoindustries.taglib.HtmlTag.STRUTS_XHTML_KEY;
import static com.aoindustries.taglib.HtmlTag.beginHtmlTag;
import static com.aoindustries.taglib.HtmlTag.endHtmlTag;
import com.aoindustries.web.resources.registry.Registry;
import com.aoindustries.web.resources.servlet.RegistryEE;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * <p>
 * TODO: Support both filtered and buffered modes, defaulting to filtered
 * This would allow nested tags while in buffered mode.  Would be a
 * boolean attribute "buffered", defaulting to false.  A TLD validator
 * would confirm that attribute-providing tags are not within an
 * unbuffered parent.  This would also likely converge FilteredBodyTag
 * and BufferedBodyTag into a single implementation.  Also, all
 * *Attribute interfaces sould have a "boolean isBuffered()".
 * </p>
 * <p>
 * TODO: Have dir attribute accept a new value "response", which would be
 * the default.  This would set the dir value based on the current
 * response locale.  This would be consistent with the current lang
 * implementation.  "auto" could still be used to override this.
 * Possibly allow set as empty string to override, too.
 * </p>
 * <p>
 * TODO: Support an open-only mode, which would be the default when there
 * is no body.  Values "true", "false", "auto" (the default).  When
 * open-only, the closing &lt;/ao:html&gt; would not be written, and the
 * request attributes would not be restored.  This would allow the
 * &lt;ao:html&gt; tag to be used where the header and footer are split
 * into separate files.  Maybe negate it and call the attribute "close".
 * </p>
 */
public class HtmlTag extends ElementFilteredBodyTag {

/* SimpleTag only:
	public static final Resources RESOURCES = Resources.getResources(HtmlTag.class);
/**/

/* BodyTag only: */
	public HtmlTag() {
		init();
	}
/**/

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	// TODO: charset here, along with:
	//       Page (model), Page (Servlet), PageTag, Theme, View
	//       aoweb-framework: WebPage, WebPageLayout
	//       aoweb-struts: PageAttributes, Skin

	private Serialization serialization;
	public void setSerialization(String serialization) {
		if(serialization == null) {
			this.serialization = null;
		} else {
			serialization = serialization.trim();
			this.serialization = (serialization.isEmpty() || "auto".equalsIgnoreCase(serialization)) ? null : Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
		}
	}

	private Doctype doctype;
	public void setDoctype(String doctype) {
		if(doctype == null) {
			this.doctype = null;
		} else {
			doctype = doctype.trim();
			this.doctype = (doctype.isEmpty() || "default".equalsIgnoreCase(doctype)) ? null : Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
		}
	}

	private Boolean autonli;
	public void setAutonli(String autonli) {
		if(autonli == null) {
			this.autonli = null;
		} else {
			autonli = autonli.trim();
			if(autonli.isEmpty() || "auto".equalsIgnoreCase(autonli)) {
				this.autonli = null;
			} else if("true".equalsIgnoreCase(autonli)) {
				this.autonli = true;
			} else if("false".equalsIgnoreCase(autonli)) {
				this.autonli = false;
			} else {
				throw new LocalizedIllegalArgumentException(RESOURCES, "autonli.invalid", autonli);
			}
		}
	}

	private Boolean indent;
	public void setIndent(String indent) {
		if(indent == null) {
			this.indent = null;
		} else {
			indent = indent.trim();
			if(indent.isEmpty() || "auto".equalsIgnoreCase(indent)) {
				this.indent = null;
			} else if("true".equalsIgnoreCase(indent)) {
				this.indent = true;
			} else if("false".equalsIgnoreCase(indent)) {
				this.indent = false;
			} else {
				throw new LocalizedIllegalArgumentException(RESOURCES, "indent.invalid", indent);
			}
		}
	}

/* BodyTag only: */
	// Values that are used in doFinally
	private transient Serialization oldSerialization;
	private transient Object oldStrutsXhtml;
	private transient boolean setSerialization;
	private transient Doctype oldDoctype;
	private transient boolean setDoctype;
	private transient Boolean oldAutonli;
	private transient boolean setAutonli;
	private transient Boolean oldIndent;
	private transient boolean setIndent;
	private transient Registry oldPageRegistry;

	private void init() {
		serialization = null;
		doctype = null;
		autonli = null;
		indent = null;
		oldSerialization = null;
		oldStrutsXhtml = null;
		setSerialization = false;
		oldDoctype = null;
		setDoctype = false;
		oldAutonli = null;
		setAutonli = false;
		oldIndent = null;
		setIndent = false;
		oldPageRegistry = null;
	}
/**/

	@Override
/* BodyTag only: */
	protected int doStartTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		Serialization oldSerialization;
		Object oldStrutsXhtml;
		boolean setSerialization;
		Doctype oldDoctype;
		boolean setDoctype;
		Boolean oldAutonli;
		boolean setAutonli;
		Boolean oldIndent;
		boolean setIndent;
		Registry oldPageRegistry;
/**/
		ServletContext servletContext = pageContext.getServletContext();
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

		Serialization currentSerialization = serialization;
		if(currentSerialization == null) {
			currentSerialization = SerializationEE.get(servletContext, request);
			oldSerialization = null;
			oldStrutsXhtml = null;
			setSerialization = false;
		} else {
			oldSerialization = SerializationEE.replace(request, currentSerialization);
			oldStrutsXhtml = pageContext.getAttribute(STRUTS_XHTML_KEY, PageContext.PAGE_SCOPE);
			pageContext.setAttribute(STRUTS_XHTML_KEY, Boolean.toString(currentSerialization == Serialization.XML), PageContext.PAGE_SCOPE);
			setSerialization = true;
		}
/* SimpleTag only:
		try {
/**/
			Doctype currentDoctype = doctype;
			if(currentDoctype == null) {
				currentDoctype = DoctypeEE.get(servletContext, request);
				oldDoctype = null;
				setDoctype = false;
			} else {
				oldDoctype = DoctypeEE.replace(request, currentDoctype);
				setDoctype = true;
			}
/* SimpleTag only:
			try {
/**/
				if(autonli == null) {
					DocumentEE.getAutonli(servletContext, request); // Gets or sets the request attribute for "auto"
					oldAutonli = null;
					setAutonli = false;
				} else {
					oldAutonli = DocumentEE.replaceAutonli(request, autonli);
					setAutonli = true;
				}
/* SimpleTag only:
				try {
/**/
					if(indent == null) {
						DocumentEE.getIndent(servletContext, request); // Gets or sets the request attribute for "auto"
						oldIndent = null;
						setIndent = false;
					} else {
						oldIndent = DocumentEE.replaceIndent(request, indent);
						setIndent = true;
					}
/* SimpleTag only:
					try {
/**/
						oldPageRegistry = RegistryEE.Page.get(request);
						if(oldPageRegistry == null) {
							// Create a new page-scope registry
							RegistryEE.Page.set(request, new Registry());
						}
/* SimpleTag only:
						try {
/**/
							ServletResponse response = pageContext.getResponse();
							// Clear the output buffer
							response.resetBuffer();
							// Set the content type
							final String documentEncoding = AnyDocument.ENCODING.name();
							try {
								ServletUtil.setContentType(response, currentSerialization.getContentType(), documentEncoding);
							} catch(ServletException e) {
								throw new JspTagException(e);
							}
							// Write doctype
							currentDoctype.xmlDeclaration(currentSerialization, documentEncoding, out);
							currentDoctype.doctype(currentSerialization, out);
							// Write <html>
							beginHtmlTag(response, out, currentSerialization, this);
/* BodyTag only: */
		return EVAL_BODY_FILTERED;
	}

	@Override
	protected int doEndTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
							super.doTag(out);
/**/
							// Write </html>
							endHtmlTag(out);
/* BodyTag only: */
		return EVAL_PAGE;
	}

	@Override
	public void doFinally() {
		try {
			try {
				javax.servlet.ServletRequest request = pageContext.getRequest();
/**/
/* SimpleTag only:
						} finally {
/**/
							if(oldPageRegistry == null) {
								RegistryEE.Page.set(request, null);
							}
/* SimpleTag only:
						}
					} finally {
/**/
						if(setIndent) DocumentEE.setIndent(request, oldIndent);
/* SimpleTag only:
					}
				} finally {
/**/
					if(setAutonli) DocumentEE.setAutonli(request, oldAutonli);
/* SimpleTag only:
				}
			} finally {
/**/
				if(setDoctype) DoctypeEE.set(request, oldDoctype);
/* SimpleTag only:
			}
		} finally {
/**/
			if(setSerialization) {
				SerializationEE.set(request, oldSerialization);
				pageContext.setAttribute(STRUTS_XHTML_KEY, oldStrutsXhtml, PageContext.PAGE_SCOPE);
			}
/* BodyTag only: */
			} finally {
				init();
			}
		} finally {
			super.doFinally();
/**/
		}
	}
}

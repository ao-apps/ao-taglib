/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2024  AO Industries, Inc.
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

package com.aoapps.taglib;

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;

import com.aoapps.encoding.Doctype;
import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.Serialization;
import com.aoapps.encoding.servlet.DoctypeEE;
import com.aoapps.encoding.servlet.SerializationEE;
import com.aoapps.html.any.AnyDocument;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.lang.attribute.Attribute;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.ServletUtil;
import com.aoapps.servlet.attribute.ScopeEE;
import com.aoapps.web.resources.registry.Registry;
import com.aoapps.web.resources.servlet.RegistryEE;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * TODO: Support both filtered and buffered modes, defaulting to filtered
 * This would allow nested tags while in buffered mode.  Would be a
 * boolean attribute "buffered", defaulting to false.  A TLD validator
 * would confirm that attribute-providing tags are not within an
 * unbuffered parent.  This would also likely converge FilteredBodyTag
 * and BufferedBodyTag into a single implementation.  Also, all
 * *Attribute interfaces sould have a "boolean isBuffered()".
 *
 * <p>TODO: Have dir attribute accept a new value "response", which would be
 * the default.  This would set the dir value based on the current
 * response locale.  This would be consistent with the current lang
 * implementation.  "auto" could still be used to override this.
 * Possibly allow set as empty string to override, too.</p>
 *
 * <p>TODO: Support an open-only mode, which would be the default when there
 * is no body.  Values "true", "false", "auto" (the default).  When
 * open-only, the closing &lt;/ao:html&gt; would not be written, and the
 * request attributes would not be restored.  This would allow the
 * &lt;ao:html&gt; tag to be used where the header and footer are split
 * into separate files.  Maybe negate it and call the attribute "close".</p>
 *
 * <p>TODO: Implement GlobalAttributes, but beware this would make ScriptTag always thing its inside a StyleAttribute.
 *       Could workaround this issue by making a StyleUnexpectedAttribute, which would override StyleAttribute with a
 *       set of deprecated methods, then StyleTag would ignore its StyleAttribute parent tag if it is actually a
 *       StyleUnexpectedAttribute.</p>
 */
public class HtmlTag extends ElementFilteredTag {

  /* SimpleTag only: */
  public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, HtmlTag.class);

  /**/

  /* BodyTag only:
    public HtmlTag() {
      init();
    }
  /**/

  @Override
  public MediaType getContentType() {
    return MediaType.XHTML;
  }

  /* BodyTag only:
    private static final long serialVersionUID = 1L;
  /**/

  // TODO: charset here, along with:
  //       Page (model), Page (Servlet), PageTag, Theme, View
  //       ao-web-framework: WebPage, WebPageLayout
  //       aoweb-struts: PageAttributes, Skin

  private Serialization serialization;

  public void setSerialization(String serialization) {
    if (serialization == null) {
      this.serialization = null;
    } else {
      serialization = Strings.trim(serialization);
      this.serialization = (serialization.isEmpty() || "auto".equalsIgnoreCase(serialization)) ? null : Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
    }
  }

  private Doctype doctype;

  public void setDoctype(String doctype) {
    if (doctype == null) {
      this.doctype = null;
    } else {
      doctype = Strings.trim(doctype);
      this.doctype = (doctype.isEmpty() || "default".equalsIgnoreCase(doctype)) ? null : Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
    }
  }

  private Boolean autonli;

  public void setAutonli(String autonli) {
    if (autonli == null) {
      this.autonli = null;
    } else {
      autonli = Strings.trim(autonli);
      if (autonli.isEmpty() || "auto".equalsIgnoreCase(autonli)) {
        this.autonli = null;
      } else if ("true".equalsIgnoreCase(autonli)) {
        this.autonli = true;
      } else if ("false".equalsIgnoreCase(autonli)) {
        this.autonli = false;
      } else {
        throw new LocalizedIllegalArgumentException(RESOURCES, "autonli.invalid", autonli);
      }
    }
  }

  private Boolean indent;

  public void setIndent(String indent) {
    if (indent == null) {
      this.indent = null;
    } else {
      indent = Strings.trim(indent);
      if (indent.isEmpty() || "auto".equalsIgnoreCase(indent)) {
        this.indent = null;
      } else if ("true".equalsIgnoreCase(indent)) {
        this.indent = true;
      } else if ("false".equalsIgnoreCase(indent)) {
        this.indent = false;
      } else {
        throw new LocalizedIllegalArgumentException(RESOURCES, "indent.invalid", indent);
      }
    }
  }

  /* BodyTag only:
    // Values that are used in doFinally
    private transient Serialization oldSerialization;
    private transient boolean setSerialization;
    private transient Attribute.OldValue oldStrutsXhtml;
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
      setSerialization = false;
      oldStrutsXhtml = null;
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
  /* BodyTag only:
    protected int doStartTag(Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext) getJspContext();
    Serialization oldSerialization;
    boolean setSerialization;
    Attribute.OldValue oldStrutsXhtml;
    Doctype oldDoctype;
    boolean setDoctype;
    Boolean oldAutonli;
    boolean setAutonli;
    Boolean oldIndent;
    boolean setIndent;
    Registry oldPageRegistry;
    /**/
    ServletContext servletContext = pageContext.getServletContext();
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

    Serialization currentSerialization = serialization;
    if (currentSerialization == null) {
      currentSerialization = SerializationEE.get(servletContext, request);
      oldSerialization = null;
      setSerialization = false;
      oldStrutsXhtml = null;
    } else {
      oldSerialization = SerializationEE.replace(request, currentSerialization);
      setSerialization = true;
      oldStrutsXhtml = STRUTS_XHTML_KEY.context(pageContext).init(
          Boolean.toString(currentSerialization == Serialization.XML)
      );
    }
    /* SimpleTag only: */
    try {
      /**/
      Doctype currentDoctype = doctype;
      if (currentDoctype == null) {
        currentDoctype = DoctypeEE.get(servletContext, request);
        oldDoctype = null;
        setDoctype = false;
      } else {
        oldDoctype = DoctypeEE.replace(request, currentDoctype);
        setDoctype = true;
      }
      /* SimpleTag only: */
      try {
        /**/
        if (autonli == null) {
          DocumentEE.getAutonli(servletContext, request); // Gets or sets the request attribute for "auto"
          oldAutonli = null;
          setAutonli = false;
        } else {
          oldAutonli = DocumentEE.replaceAutonli(request, autonli);
          setAutonli = true;
        }
        /* SimpleTag only: */
        try {
          /**/
          if (indent == null) {
            DocumentEE.getIndent(servletContext, request); // Gets or sets the request attribute for "auto"
            oldIndent = null;
            setIndent = false;
          } else {
            oldIndent = DocumentEE.replaceIndent(request, indent);
            setIndent = true;
          }
          /* SimpleTag only: */
          try {
            /**/
            oldPageRegistry = RegistryEE.Page.get(request);
            if (oldPageRegistry == null) {
              // Create a new page-scope registry
              RegistryEE.Page.set(request, new Registry());
            }
            /* SimpleTag only: */
            try {
              /**/
              ServletResponse response = pageContext.getResponse();
              // Clear the output buffer
              response.resetBuffer();
              // Set the content type
              final String documentEncoding = AnyDocument.ENCODING.name();
              try {
                ServletUtil.setContentType(response, currentSerialization.getContentType(), documentEncoding);
              } catch (ServletException e) {
                throw new JspTagException(e);
              }
              // Write doctype
              currentDoctype.xmlDeclaration(currentSerialization, documentEncoding, out);
              currentDoctype.doctype(currentSerialization, out);
              // Write <html>
              beginHtmlTag(response, out, currentSerialization, this);
              /* BodyTag only:
                  return EVAL_BODY_FILTERED;
                }

                @Override
                protected int doEndTag(Writer out) throws JspException, IOException {
              /**/
              /* SimpleTag only: */
              super.doTag(out);
              /**/
              // Write </html>
              endHtmlTag(out);
              /* BodyTag only:
                  return EVAL_PAGE;
                }

                @Override
                public void doFinally() {
                  try {
                    try {
                      javax.servlet.ServletRequest request = pageContext.getRequest();
              /**/
              /* SimpleTag only: */
            } finally {
              /**/
              if (oldPageRegistry == null) {
                RegistryEE.Page.set(request, null);
              }
              /* SimpleTag only: */
            }
          } finally {
            /**/
            if (setIndent) {
              DocumentEE.setIndent(request, oldIndent);
            }
            /* SimpleTag only: */
          }
        } finally {
          /**/
          if (setAutonli) {
            DocumentEE.setAutonli(request, oldAutonli);
          }
          /* SimpleTag only: */
        }
      } finally {
        /**/
        if (setDoctype) {
          DoctypeEE.set(request, oldDoctype);
        }
        /* SimpleTag only: */
      }
    } finally {
      /**/
      if (setSerialization) {
        SerializationEE.set(request, oldSerialization);
      }
      if (oldStrutsXhtml != null) {
        oldStrutsXhtml.close();
      }
      /* BodyTag only:
          } finally {
            init();
          }
        } finally {
          super.doFinally();
    /**/
    }
  }

  // <editor-fold desc="Static Utilities">
  /**
   * The old Struts 1 XHTML mode page attribute.  To avoiding picking-up a big
   * legacy dependency, we've copied the value here instead of depending on
   * Globals.  Once we no longer have any code running on old Struts, this
   * value may be removed.
   */
  // Java 9: module-private
  // Matches nmw-email-taglib:ContentTag.java
  public static final ScopeEE.Page.Attribute<String> STRUTS_XHTML_KEY =
      ScopeEE.PAGE.attribute("org.apache.struts.globals.XHTML");

  public static void beginHtmlTag(Locale locale, Appendable out, Serialization serialization, GlobalAttributes global) throws IOException {
    out.append("<html");
    if (serialization == Serialization.XML) {
      out.append(" xmlns=\"http://www.w3.org/1999/xhtml\"");
    }
    if (global != null) {
      GlobalAttributesUtils.appendGlobalAttributes(global, out);
    }
    if (locale != null) {
      String lang = locale.toLanguageTag();
      if (!lang.isEmpty()) {
        out.append(" lang=\"");
        encodeTextInXhtmlAttribute(lang, out);
        out.append('"');
        if (serialization == Serialization.XML) {
          out.append(" xml:lang=\"");
          encodeTextInXhtmlAttribute(lang, out);
          out.append('"');
        }
      }
    }
    out.append('>');
  }

  /**
   * @deprecated  Please use {@link #beginHtmlTag(java.util.Locale, java.lang.Appendable, com.aoapps.encoding.Serialization, com.aoapps.taglib.GlobalAttributes)}
   */
  @Deprecated
  public static void beginHtmlTag(Locale locale, Appendable out, Serialization serialization, String clazz) throws IOException {
    beginHtmlTag(
        locale,
        out,
        serialization,
        ImmutableGlobalAttributes.of(
            null, // id
            clazz,
            null, // data
            null, // dir
            null  // style
        )
    );
  }

  // TODO: Move to Document or DocumentEE class (or HTML_factory)?
  public static void beginHtmlTag(ServletResponse response, Appendable out, Serialization serialization, GlobalAttributes global) throws IOException {
    beginHtmlTag(response.getLocale(), out, serialization, global);
  }

  /**
   * @deprecated  Please use {@link #beginHtmlTag(javax.servlet.ServletResponse, java.lang.Appendable, com.aoapps.encoding.Serialization, com.aoapps.taglib.GlobalAttributes)}
   */
  @Deprecated
  public static void beginHtmlTag(ServletResponse response, Appendable out, Serialization serialization, String clazz) throws IOException {
    beginHtmlTag(response.getLocale(), out, serialization, clazz);
  }

  public static void endHtmlTag(Appendable out) throws IOException {
    out.append("</html>");
  }
  // </editor-fold>
}

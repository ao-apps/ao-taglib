package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class InputTag extends AutoEncodingBufferedTag implements ValueAttribute, NameAttribute, OnclickAttribute {

    public static boolean isValidType(String type) {
        return
            "button".equals(type)
            || "checkbox".equals(type)
            || "file".equals(type)
            || "hidden".equals(type)
            || "image".equals(type)
            || "password".equals(type)
            || "radio".equals(type)
            || "reset".equals(type)
            || "submit".equals(type)
            || "text".equals(type)
        ;
    }

    private String type;
    private String name;
    private String value;
    private String onclick;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws JspException {
        if(!isValidType(type)) throw new JspException(ApplicationResourcesAccessor.getMessage(Locale.getDefault(), "InputTag.type.invalid", type));
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        Locale userLocale = response.getLocale();
        if(type==null) throw new JspException(ApplicationResourcesAccessor.getMessage(userLocale, "InputTag.type.required"));
        if(value==null) value = capturedBody.toString().trim();
        out.write("<input type=\"");
        out.write(type);
        out.write('"');
        if(name!=null) {
            out.write(" name=\"");
            EncodingUtils.encodeXmlAttribute(name, out);
            out.write('"');
        }
        out.write(" value=\"");
        EncodingUtils.encodeXmlAttribute(value, out);
        out.write('"');
        if(onclick!=null) {
            out.write(" onclick=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onclick, out);
            out.write('"');
        }
        out.write(" />");
    }
}

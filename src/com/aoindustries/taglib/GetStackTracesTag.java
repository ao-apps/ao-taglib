package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaType;
import com.aoindustries.util.ErrorPrinter;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class GetStackTracesTag extends AutoEncodingTag {

    private static final long serialVersionUID = 1L;

    private String scope;
    private String name;
    private String property;

    @Override
    protected void init() {
        scope = null;
        name = null;
        property = null;
    }

    public MediaType getContentType() {
        return MediaType.PLAINTEXT;
    }

    @Override
    public int doAutoEncodingStartTag() throws JspException {
        // Find the Throwable to display
        Object value = PropertyUtils.findObject(pageContext, scope, name, property, true, true);
        if(!(value instanceof Throwable)) throw new JspException(ApplicationResourcesAccessor.getMessage(pageContext.getResponse().getLocale(), "GetStackTracesTag.notThrowable", value.getClass().getName()));
        Throwable throwable = (Throwable)value;

        // Print the stack traces
        ErrorPrinter.printStackTraces(throwable, pageContext.getOut());

        // Skip body
        return SKIP_BODY;
    }

    @Override
    public int doAutoEncodingEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }
}

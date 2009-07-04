package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaException;
import com.aoindustries.media.MediaType;
import com.aoindustries.util.LocalizedToString;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * @author  AO Industries, Inc.
 */
public class WriteTag extends AutoEncodingTag {

    private static final long serialVersionUID = 1L;

    private String scope;
    private String name;
    private String property;
    private MediaType type;

    @Override
    protected void init() {
        scope = null;
        name = null;
        property = null;
        type = MediaType.PLAINTEXT;
    }

    public MediaType getContentType() {
        return type;
    }

    private static final Class[] toStringParamTypes = new Class[] {Locale.class};

    @Override
    public int doAutoEncodingStartTag() throws JspException {
        try {
            // Find the bean to write
            Object value = PropertyUtils.findObject(pageContext, scope, name, property, true, false);

            // Print the value
            if(value!=null) {
                JspWriter out = pageContext.getOut();
                // Avoid reflection when possible by using the aocode-public interface
                if(value instanceof LocalizedToString) {
                    out.write(((LocalizedToString)value).toString(pageContext.getResponse().getLocale()));
                } else {
                    try {
                        Method method = value.getClass().getMethod("toString", toStringParamTypes);
                        if(method.getReturnType()==String.class) {
                            out.write((String)method.invoke(value, pageContext.getResponse().getLocale()));
                        } else {
                            out.write(value.toString());
                        }
                    } catch(NoSuchMethodException err) {
                        out.write(value.toString());
                    } catch(IllegalAccessException err) {
                        out.write(value.toString());
                    }
                }
            }

            // Skip body
            return SKIP_BODY;
        } catch(IOException err) {
            throw new JspException(err);
        } catch(InvocationTargetException err) {
            throw new JspException(err);
        }
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

    public String getType() {
        return type.getMediaType();
    }

    public void setType(String type) throws MediaException {
        this.type = MediaType.getMediaType(Locale.getDefault(), type);
    }
}

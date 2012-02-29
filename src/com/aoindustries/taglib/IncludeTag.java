/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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

import com.aoindustries.lang.NotImplementedException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * @author  AO Industries, Inc.
 */
public class IncludeTag extends DispatchTag {

    @Override
    void dispatch(RequestDispatcher dispatcher, final JspWriter out, HttpServletRequest request, HttpServletResponse response) throws IOException, JspException {
        try {
            // Write to the current JSP out instead of creating a new writer.
            dispatcher.include(
                request,
                new HttpServletResponseWrapper(response) {
                    @Override
                    public ServletOutputStream getOutputStream() throws IOException {
                        throw new NotImplementedException("Implement when first needed, and how would we accomplish this?  What would it mean in a JSP context?  Pass-through to original response out and clear previous content?");
                    }

                    /**
                     * Uses the writer from the JSP context instead of creating a new one.  This
                     * is required because the JSP out cannot be flushed from a custom tag:
                     * <pre>java.io.IOException: Illegal to flush within a custom tag</pre>
                     */
                    @Override
                    public PrintWriter getWriter() throws IOException {
                        return new PrintWriter(out);
                    }
                }
            );
        } catch(ServletException e) {
            throw new JspException(e);
        }
    }
}

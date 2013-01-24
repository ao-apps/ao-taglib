/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2012, 2013  AO Industries, Inc.
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
import java.io.IOException;
import java.io.Writer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.SkipPageException;

/**
 * @author  AO Industries, Inc.
 */
public class ForwardTag extends DispatchTag {

    /**
     * Discard all nested output, since this will not use the output and this
     * output could possibly fill the response buffer and prevent the forward
     * from functioning.
     */
    @Override
    Writer getJspFragmentWriter(JspWriter out) {
        return NullWriter.getInstance();
    }

    @Override
    void dispatch(RequestDispatcher dispatcher, JspWriter out, HttpServletRequest request, HttpServletResponse response) throws IOException, JspException {
        try {
            // Clear the previous JSP out buffer
            out.clear();
            dispatcher.forward(request, response);
        } catch(ServletException e) {
            throw new JspException(e);
        }
        throw new SkipPageException();
    }
}

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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.util.i18n.EditableResourceBundle;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * Allows editing of the website resource bundles through the website itself.
 * This should be placed immediately before the body tag is closed.
 * If no lookups have been recorded, such as when the resource editor is disable,
 * this will not create the editor.
 *
 * @author  AO Industries, Inc.
 */
public class ResourceEditorTag extends AutoEncodingNullTag {

    public ResourceEditorTag() {
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    protected void doTag(Writer out) throws JspTagException, IOException {
        out.write("<div style=\"font-size:smaller\">");
        EditableResourceBundle.printEditableResourceBundleLookups(out, 3, false);
        out.write("</div>");
    }
}

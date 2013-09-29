/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.Coercion;
import com.aoindustries.util.i18n.BundleLookup;
import com.aoindustries.util.i18n.BundleLookupResult;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public final class AttributeUtils  {

    /**
     * Finds the attribute parent tag of the provided class (or subclass).
     *
     * @return  the parent tag
     * @exception  NeedAttributeParentException  if parent not found
     */
    public static <T> T findAttributeParent(String tag, JspTag from, String attribute, Class<? extends T> clazz) throws NeedAttributeParentException {
        T parent = clazz.cast(SimpleTagSupport.findAncestorWithClass(from, clazz));
        if(parent==null) throw new NeedAttributeParentException(tag, attribute);
        return parent;
    }

	/**
	 * Writes an attribute with text markup enabled.  The attribute is encoded.
	 * 
	 * @see  BundleLookup.MarkupType
	 */
	public static void writeAttributeTextMarkup(Object value, Writer out) throws IOException {
		if(value instanceof BundleLookup) {
			BundleLookupResult result = ((BundleLookup)value).toString(BundleLookup.MarkupType.TEXT);
			result.appendPrefixTo(textInXhtmlAttributeEncoder, out);
			encodeTextInXhtmlAttribute(result.getResult(), out);
			result.appendSuffixTo(textInXhtmlAttributeEncoder, out);
		} else {
			Coercion.write(value, textInXhtmlAttributeEncoder, out);
		}
	}

	/**
     * Make no instances.
     */
    private AttributeUtils() {
    }
}

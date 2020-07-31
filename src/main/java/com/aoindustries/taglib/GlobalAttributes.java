/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.html.Attributes.Global;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;

/**
 * {@linkplain Global Global attributes} when used in a filtered context.
 * These attributes may not be set from nested tags due to the lack of buffering.
 *
 * @author  AO Industries, Inc.
 */
public interface GlobalAttributes {

	String getId();

	/**
	 * Getter required because without it, we get the exception:
	 * <pre>Unable to find setter method for attribute: class</pre>
	 */
	String getClazz();

	String getDir();

	Object getStyle();

	default <G extends Global<?>> G doGlobalAttributes(G global) throws IOException {
		global
			.id(getId())
			.clazz(getClazz())
			.dir(getDir())
			.style(getStyle());
		return global;
	}

	default void writeGlobalAttributes(Writer out) throws IOException {
		String id = getId();
		if(id != null) {
			out.write(" id=\"");
			encodeTextInXhtmlAttribute(id, out);
			out.write('"');
		}
		String clazz = getClazz();
		if(clazz != null) {
			out.write(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.write('"');
		}
		String dir = getDir();
		if(dir != null) {
			out.write(" dir=\"");
			encodeTextInXhtmlAttribute(dir, out);
			out.write('"');
		}
		Object style = getStyle();
		if(style != null) {
			out.write(" style=\"");
			// TODO: Review other MarkupType.JAVASCRIPT that should be MarkupType.CSS
			Coercion.write(style, MarkupType.CSS, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
	}

	default void appendGlobalAttributes(Appendable out) throws IOException {
		String id = getId();
		if(id != null) {
			out.append(" id=\"");
			encodeTextInXhtmlAttribute(id, out);
			out.append('"');
		}
		String clazz = getClazz();
		if(clazz != null) {
			out.append(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.append('"');
		}
		String dir = getDir();
		if(dir != null) {
			out.append(" dir=\"");
			encodeTextInXhtmlAttribute(dir, out);
			out.append('"');
		}
		Object style = getStyle();
		if(style != null) {
			out.append(" style=\"");
			Coercion.append(style, MarkupType.CSS, textInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
	}
}

/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020, 2021  AO Industries, Inc.
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
package com.aoapps.taglib;

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.html.any.attributes.Text.Data;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Throwables;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * Utilities for working with {@linkplain com.aoapps.html.any.GlobalAttributes global attributes}.
 *
 * @author  AO Industries, Inc.
 */
public class GlobalAttributesUtils {

	/**
	 * The prefix for <code>dataset.*</code> dynamic attributes.
	 */
	public static final String DATASET_ATTRIBUTE_PREFIX = "dataset.";

	/**
	 * Adds the <code>data-*</code> and <code>dataset.*</code> {@linkplain DynamicAttributes dynamic attributes}.
	 *
	 * @return  {@code true} when added, or {@code false} when attribute not expected and has not been added.
	 *
	 * @see  DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	public static boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns, MutableGlobalAttributes global) throws JspTagException {
		try {
			if(localName.startsWith(Data.data.ATTRIBUTE_PREFIX)) {
				global.addData(localName, value);
				return true;
			} else if(localName.startsWith(DATASET_ATTRIBUTE_PREFIX)) {
				global.addData(
					Data.dataset.toAttrName(
						localName.substring(DATASET_ATTRIBUTE_PREFIX.length())
					),
					value
				);
				return true;
			} else {
				expectedPatterns.add(Data.data.ATTRIBUTE_PREFIX + "*");
				expectedPatterns.add(GlobalAttributesUtils.DATASET_ATTRIBUTE_PREFIX + "*");
				return false;
			}
		} catch(Throwable t) {
			throw Throwables.wrap(t, JspTagException.class, JspTagException::new);
		}
	}

	/**
	 * Copies all global attributes.
	 */
	public static void copy(GlobalAttributes from, GlobalBufferedAttributes to) {
		to.setId(from.getId());
		to.setClazz(from.getClazz());
		to.setData(from.getData());
		to.setDir(from.getDir());
		to.setStyle(from.getStyle());
	}

	public static <G extends com.aoapps.html.any.GlobalAttributes<?>> G doGlobalAttributes(GlobalAttributes from, G to) throws IOException {
		// TODO: normalize, then only throw when non-empty/null.  Here and other attributes.
		// TODO: Once that is done, just do like before:
		// to
		//	.id(from.getId())
		//	.clazz(from.getClazz());
		// id is not valid in all doctypes
		String id = from.getId();
		if(id != null) to.id(id);
		// class is not valid in all doctypes
		String clazz = from.getClazz();
		if(clazz != null) to.clazz(clazz);
		for(Map.Entry<String, Object> entry : from.getData().entrySet()) {
			to.data(entry.getKey(), entry.getValue());
		}
		// TODO: Once that is done, just do like before:
		// to
		//	.dir(from.getDir())
		//	.style(from.getStyle());
		to.dir(from.getDir());
		// style is not valid in all doctypes
		Object style = Coercion.trimNullIfEmpty(from.getStyle());
		if(style != null) to.style(style);
		return to;
	}

	// TODO: Doctype constraints in id, class, and style like on by ao-fluent-html via doGlobalAttributes?
	public static void writeGlobalAttributes(GlobalAttributes global, Writer out) throws IOException {
		String id = global.getId();
		if(id != null) {
			out.write(" id=\"");
			encodeTextInXhtmlAttribute(id, out);
			out.append('"');
		}
		String clazz = global.getClazz();
		if(clazz != null) {
			out.write(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.append('"');
		}
		for(Map.Entry<String, Object> entry : global.getData().entrySet()) {
			String attrName = entry.getKey();
			assert Data.data.validate(attrName).isValid();
			out.append(' ').write(attrName);
			out.write("=\"");
			encodeTextInXhtmlAttribute(entry.getValue(), out);
			out.append('"');
		}
		String dir = global.getDir();
		if(dir != null) {
			out.write(" dir=\"");
			encodeTextInXhtmlAttribute(dir, out);
			out.append('"');
		}
		Object style = Coercion.trimNullIfEmpty(global.getStyle());
		if(style != null) {
			out.write(" style=\"");
			// TODO: Review other MarkupType.JAVASCRIPT that should be MarkupType.CSS
			MarkupCoercion.write(style, MarkupType.CSS, true, textInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
	}

	public static void appendGlobalAttributes(GlobalAttributes global, Appendable out) throws IOException {
		String id = global.getId();
		if(id != null) {
			out.append(" id=\"");
			encodeTextInXhtmlAttribute(id, out);
			out.append('"');
		}
		String clazz = global.getClazz();
		if(clazz != null) {
			out.append(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.append('"');
		}
		for(Map.Entry<String, Object> entry : global.getData().entrySet()) {
			out.append(' ');
			String attrName = entry.getKey();
			assert Data.data.validate(attrName).isValid();
			out.append(attrName);
			out.append("=\"");
			encodeTextInXhtmlAttribute(entry.getValue(), out);
			out.append('"');
		}
		String dir = global.getDir();
		if(dir != null) {
			out.append(" dir=\"");
			encodeTextInXhtmlAttribute(dir, out);
			out.append('"');
		}
		Object style = Coercion.trimNullIfEmpty(global.getStyle());
		if(style != null) {
			out.append(" style=\"");
			MarkupCoercion.append(style, MarkupType.CSS, true, textInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
	}

	private GlobalAttributesUtils() {}
}

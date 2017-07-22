/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.io.Writable;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;

/**
 * Utilities for handling writable values along with in-context translation
 * markup.
 *
 * @author  AO Industries, Inc.
 */
public final class MarkupUtils  {

	/**
	 * Writes a value with markup enabled.
	 * 
	 * @see  MarkupType
	 */
	public static void writeWithMarkup(Object value, MarkupType markupType, Writer out) throws IOException {
		if(value!=null) {
			if(
				value instanceof Writable
				&& !((Writable)value).isFastToString()
			) {
				// Avoid intermediate String from Writable
				Coercion.write(value, out);
			} else {
				String str = Coercion.toString(value);
				BundleLookupMarkup lookupMarkup;
				BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
				if(threadContext!=null) {
					lookupMarkup = threadContext.getLookupMarkup(str);
				} else {
					lookupMarkup = null;
				}
				if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(markupType, out);
				out.write(str);
				if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(markupType, out);
			}
		}
	}

	/**
	 * Writes a value with markup enabled using the provided encoder.
	 * 
	 * @param  encoder  no encoding performed when null
	 *
	 * @see  MarkupType
	 */
	public static void writeWithMarkup(Object value, MarkupType markupType, MediaEncoder encoder, Writer out) throws IOException {
		if(encoder==null) {
			writeWithMarkup(value, markupType, out);
		} else {
			if(value!=null) {
				if(
					value instanceof Writable
					&& !((Writable)value).isFastToString()
				) {
					// Avoid intermediate String from Writable
					Coercion.write(value, encoder, out);
				} else {
					String str = Coercion.toString(value);
					BundleLookupMarkup lookupMarkup;
					BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
					if(threadContext!=null) {
						lookupMarkup = threadContext.getLookupMarkup(str);
					} else {
						lookupMarkup = null;
					}
					if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(markupType, encoder, out);
					encoder.write(str, out);
					if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(markupType, encoder, out);
				}
			}
		}
	}

	/**
	 * Make no instances.
	 */
	private MarkupUtils() {
	}
}

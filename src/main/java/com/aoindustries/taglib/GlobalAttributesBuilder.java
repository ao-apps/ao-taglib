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
import com.aoindustries.lang.Strings;
import java.io.IOException;

/**
 * Builder for {@link GlobalAttributes} instances.
 */
public class GlobalAttributesBuilder {

	/**
	 * Used for thread-safe defensive copies
	 */
	private static class Instance implements GlobalAttributes {

		private final String id;
		private final String clazz;
		private final Object style;

		private Instance(
			String id,
			String clazz,
			Object style
		) {
			this.id = id;
			this.clazz = clazz;
			this.style = style;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getClazz() {
			return clazz;
		}

		@Override
		public Object getStyle() {
			return style;
		}
	}

	private String id;
	private String clazz;
	private Object style;

	private GlobalAttributesBuilder() {}

	public GlobalAttributesBuilder setId(String id) {
		this.id = Strings.trimNullIfEmpty(id);
		return this;
	}

	public GlobalAttributesBuilder setClazz(String clazz) {
		this.clazz = Strings.trimNullIfEmpty(clazz);
		return this;
	}

	public GlobalAttributesBuilder setStyle(Object style) throws IOException {
		this.style = Coercion.trimNullIfEmpty(style);
		return this;
	}

	/**
	 * Creates a defensive copy of the given set of global attributes.
	 */
	public GlobalAttributesBuilder copy(GlobalAttributes global) throws IOException {
		setId(global.getId());
		setClazz(global.getClazz());
		setStyle(global.getStyle());
		return this;
	}

	/**
	 * Builds the instance, returning {@code null} when has no attributes.
	 *
	 * @return  The instance or {@code null} when empty.
	 */
	public GlobalAttributes build() {
		if(
			id == null
			&& clazz == null
			&& style == null
		) {
			return null;
		} else {
			return new Instance(
				id,
				clazz,
				style
			);
		}
	}

	public static GlobalAttributesBuilder builder() {
		return new GlobalAttributesBuilder();
	}
}

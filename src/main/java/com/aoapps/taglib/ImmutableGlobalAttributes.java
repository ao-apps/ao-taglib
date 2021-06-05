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

import com.aoapps.collections.AoCollections;
import java.util.Collections;
import java.util.Map;

/**
 * Thread-safe, unmodifiable {@link GlobalAttributes} instances.
 */
public class ImmutableGlobalAttributes implements GlobalAttributes {

	public static final ImmutableGlobalAttributes EMPTY = new ImmutableGlobalAttributes(null, null, null, null, null);

	/**
	 * Gets an immutable, thread-safe instance, returning {@link ImmutableGlobalAttributes#EMPTY} when has no attributes.
	 * <p>
	 * To future-proof code, prefer using {@link MutableGlobalAttributes} in a builder pattern.  However, if maximum
	 * code efficiency is desired, this method is available.
	 * </p>
	 *
	 * @return  The instance or {@link ImmutableGlobalAttributes#EMPTY} when empty.
	 */
	public static ImmutableGlobalAttributes of(
		String id,
		String clazz,
		Map<String, Object> data,
		String dir,
		Object style
	) {
		if(
			id == null
			&& clazz == null
			&& (data == null || data.isEmpty())
			&& dir == null
			&& style == null
		) {
			return EMPTY;
		} else {
			return new ImmutableGlobalAttributes(
				id,
				clazz,
				data,
				dir,
				style
			);
		}
	}

	/**
	 * Gets an immutable, thread-safe instance, returning {@link ImmutableGlobalAttributes#EMPTY} when has no attributes.
	 *
	 * @return  The instance or {@link ImmutableGlobalAttributes#EMPTY} when empty.
	 */
	public static ImmutableGlobalAttributes of(GlobalAttributes global) {
		return (global == null) ? EMPTY : of(
			global.getId(),
			global.getClazz(),
			global.getData(),
			global.getDir(),
			global.getStyle()
		);
	}

	private final String id;
	private final String clazz;
	private final Map<String, Object> data;
	private final String dir;
	private final Object style;

	private ImmutableGlobalAttributes(
		String id,
		String clazz,
		Map<String, Object> data,
		String dir,
		Object style
	) {
		this.id = id;
		this.clazz = clazz;
		this.data = (data == null) ? Collections.emptyMap() : AoCollections.unmodifiableCopyMap(data);
		this.dir = dir;
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
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public Map<String, Object> getData() {
		return data;
	}

	@Override
	public String getDir() {
		return dir;
	}

	@Override
	public Object getStyle() {
		return style;
	}
}

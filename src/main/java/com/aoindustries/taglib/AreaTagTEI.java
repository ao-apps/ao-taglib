/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.lang.Strings;
import static com.aoindustries.taglib.Resources.PACKAGE_RESOURCES;
import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class AreaTagTEI extends ElementTagTEI {

	public static boolean isValidShape(String shape) {
		return
			"default".equals(shape)
			|| "rect".equals(shape)
			|| "circle".equals(shape)
			|| "poly".equals(shape);
	}

	@Override
	protected void validate(TagData data, List<ValidationMessage> messages) {
		super.validate(data, messages);
		Object shapeAttr = data.getAttribute("shape");
		if(shapeAttr == null) {
			messages.add(new ValidationMessage(
					data.getId(),
					PACKAGE_RESOURCES.getMessage("AttributeRequiredException.message", "shape")
				)
			);
		} else if(shapeAttr != TagData.REQUEST_TIME_VALUE) {
			String shape = ((String)shapeAttr).trim(); // TODO: normalizeShape
			if(shape.isEmpty()) {
				messages.add(new ValidationMessage(
						data.getId(),
						PACKAGE_RESOURCES.getMessage("AttributeRequiredException.message", "shape")
					)
				);
			} else {
				if(!isValidShape(shape)) {
					messages.add(new ValidationMessage(
							data.getId(),
							PACKAGE_RESOURCES.getMessage("AreaTag.shape.invalid", shape)
						)
					);
				} else if(!"default".equals(shape)) {
					Object coordsAttr = data.getAttribute("coords");
					if(coordsAttr == null) {
						messages.add(new ValidationMessage(
								data.getId(),
								PACKAGE_RESOURCES.getMessage("AttributeRequiredException.message", "coords")
							)
						);
					} else if(coordsAttr != TagData.REQUEST_TIME_VALUE) {
						String coords = Strings.trimNullIfEmpty((String)coordsAttr); // TODO: normalizeCoords
						if(coords == null) {
							messages.add(new ValidationMessage(
									data.getId(),
									PACKAGE_RESOURCES.getMessage("AttributeRequiredException.message", "coords")
								)
							);
						}
					}
				}
			}
		}
	}
}

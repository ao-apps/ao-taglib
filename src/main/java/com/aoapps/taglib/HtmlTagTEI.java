/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib;

import com.aoapps.encoding.Doctype;
import com.aoapps.encoding.Serialization;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class HtmlTagTEI extends ElementTagTEI {

	@Override
	protected void validate(TagData data, List<ValidationMessage> messages) {
		super.validate(data, messages);
		Object serializationAttr = data.getAttribute("serialization");
		if(
			serializationAttr != null
			&& serializationAttr != TagData.REQUEST_TIME_VALUE
		) {
			String serialization = ((String)serializationAttr).trim(); // TODO: normalizeSerialization
			if(!serialization.isEmpty() && !"auto".equalsIgnoreCase(serialization)) {
				try {
					Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
				} catch(IllegalArgumentException e) {
					messages.add(
						new ValidationMessage(
							data.getId(),
							HtmlTag.RESOURCES.getMessage("serialization.invalid", serialization)
						)
					);
				}
			}
		}
		Object doctypeAttr = data.getAttribute("doctype");
		if(
			doctypeAttr != null
			&& doctypeAttr != TagData.REQUEST_TIME_VALUE
		) {
			String doctype = ((String)doctypeAttr).trim(); // TODO: normalizeDoctype
			if(!doctype.isEmpty() && !"default".equalsIgnoreCase(doctype)) {
				try {
					Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
				} catch(IllegalArgumentException e) {
					messages.add(
						new ValidationMessage(
							data.getId(),
							HtmlTag.RESOURCES.getMessage("doctype.invalid", doctype)
						)
					);
				}
			}
		}
		Object autonliAttr = data.getAttribute("autonli");
		if(
			autonliAttr != null
			&& autonliAttr != TagData.REQUEST_TIME_VALUE
		) {
			String autonli = ((String)autonliAttr).trim(); // TODO: normalizeAutonli
			if(
				!autonli.isEmpty()
				&& !"auto".equalsIgnoreCase(autonli)
				&& !"true".equalsIgnoreCase(autonli)
				&& !"false".equalsIgnoreCase(autonli)
			) {
				messages.add(
					new ValidationMessage(
						data.getId(),
						HtmlTag.RESOURCES.getMessage("autonli.invalid", autonli)
					)
				);
			}
		}
		Object indentAttr = data.getAttribute("indent");
		if(
			indentAttr != null
			&& indentAttr != TagData.REQUEST_TIME_VALUE
		) {
			String indent = ((String)indentAttr).trim(); // TODO: normalizeIndent
			if(
				!indent.isEmpty()
				&& !"auto".equalsIgnoreCase(indent)
				&& !"true".equalsIgnoreCase(indent)
				&& !"false".equalsIgnoreCase(indent)
			) {
				messages.add(
					new ValidationMessage(
						data.getId(),
						HtmlTag.RESOURCES.getMessage("indent.invalid", indent)
					)
				);
			}
		}
	}
}

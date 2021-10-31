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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib;

import com.aoapps.collections.MinimalList;
import com.aoapps.hodgepodge.i18n.EditableResourceBundle;
import com.aoapps.lang.Strings;
import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class DisableResourceEditorTagTEI extends TagExtraInfo {

	@Override
	public ValidationMessage[] validate(TagData data) {
		List<ValidationMessage> messages = MinimalList.emptyList();

		Object scopeAttr = data.getAttribute("scope");
		if(scopeAttr != null && scopeAttr != TagData.REQUEST_TIME_VALUE) {
			String scope = (String)scopeAttr;
			if(!DisableResourceEditorTag.isValidScope(scope)) {
				messages = MinimalList.add(
					messages,
					new ValidationMessage(
						data.getId(),
						DisableResourceEditorTag.RESOURCES.getMessage("scope.invalid", scope)
					)
				);
			}
		}

		Object modeAttr = data.getAttribute("mode");
		if(modeAttr != null && modeAttr != TagData.REQUEST_TIME_VALUE) {
			String mode = Strings.trimNullIfEmpty((String)modeAttr);
			if(mode != null) {
				try {
					EditableResourceBundle.ThreadSettings.Mode.valueOf(mode);
				} catch(IllegalArgumentException e) {
					messages = MinimalList.add(
						messages,
						new ValidationMessage(data.getId(), e.getLocalizedMessage())
					);
				}
			}
		}

		return messages.isEmpty() ? null : messages.toArray(new ValidationMessage[messages.size()]);
	}
}

/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.i18n.EditableResourceBundle;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.attribute.ScopeEE;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * Disables the resource editor.
 */
public class DisableResourceEditorTag extends TagSupport implements TryCatchFinally {

	static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, DisableResourceEditorTag.class);

	/**
	 * Scope used for automatic determination.
	 */
	private static final String AUTO = "auto";

	/**
	 * Scope used for body content.
	 */
	private static final String BODY = "body";

	public static boolean isValidScope(String scope) {
		scope = Strings.trimNullIfEmpty(scope);
		return
			scope == null
			|| scope.equalsIgnoreCase(AUTO)
			|| scope.equalsIgnoreCase(BODY)
			|| scope.equalsIgnoreCase(ScopeEE.Page.SCOPE_REQUEST);
	}

	public DisableResourceEditorTag() {
		init();
	}

	private static final long serialVersionUID = 1L;

	private String scope;
	public void setScope(String scope) {
		scope = Strings.trimNullIfEmpty(scope);
		if(scope == null || scope.equalsIgnoreCase(AUTO)) {
			this.scope = null;
		} else if(scope.equalsIgnoreCase(BODY)) {
			this.scope = BODY;
		} else if(scope.equalsIgnoreCase(ScopeEE.Page.SCOPE_REQUEST)) {
			this.scope = ScopeEE.Page.SCOPE_REQUEST;
		} else {
			throw new LocalizedIllegalArgumentException(RESOURCES, "scope.invalid", scope);
		}
	}

	private EditableResourceBundle.ThreadSettings.Mode mode;
	public void setMode(String mode) {
		mode = Strings.trimNullIfEmpty(mode);
		this.mode = (mode == null)
			? EditableResourceBundle.ThreadSettings.Mode.DISABLED
			: EditableResourceBundle.ThreadSettings.Mode.valueOf(mode.toUpperCase(Locale.ROOT));
	}

	private transient EditableResourceBundle.ThreadSettings oldThreadSettings;
	private transient boolean hasBody;

	private void init() {
		scope = null;
		mode = EditableResourceBundle.ThreadSettings.Mode.DISABLED;
		oldThreadSettings = null;
		hasBody = false;
	}

	@Override
	public int doStartTag() throws JspException {
		oldThreadSettings = EditableResourceBundle.getThreadSettings();
		EditableResourceBundle.ThreadSettings newThreadSettings = oldThreadSettings.setMode(mode);
		if(newThreadSettings != oldThreadSettings) {
			EditableResourceBundle.setThreadSettings(newThreadSettings);
		} else {
			// Unchanged
			oldThreadSettings = null;
		}
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody() throws JspException {
		hasBody = true;
		return SKIP_BODY;
	}

	@Override
	public void doCatch(Throwable t) throws Throwable {
		throw t;
	}

	@Override
	@SuppressWarnings("StringEquality") // Exact string instances are used in setScope
	public void doFinally() {
		try {
			if(
				oldThreadSettings != null
				&& (
					(scope == null && hasBody) // Auto mode, with body
					|| scope == BODY // Body mode
				)
			) {
				// Restore old settings
				EditableResourceBundle.setThreadSettings(oldThreadSettings);
			}
		} finally {
			init();
		}
	}
}

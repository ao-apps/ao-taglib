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

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.Coercion;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.BundleLookup;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class MessageTag
	extends AutoEncodingNullTag
	implements
		TypeAttribute,
		MessageArgsAttribute
{

	private String key;
	private Object type = MediaType.TEXT;
    private MediaType mediaType = MediaType.TEXT;
	private List<Object> messageArgs;

	@Override
    public MediaType getOutputType() {
        return mediaType;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
    public Object getType() {
        return type;
    }

	@Override
    public void setType(Object type) throws JspException {
		try {
			MediaType newMediaType =
				(type instanceof MediaType)
				? (MediaType)type
				: MediaType.getMediaType(Coercion.toString(type))
			;
			this.type = type;
			this.mediaType = newMediaType;
		} catch(MediaException e) {
			throw new JspException(e);
		}
    }

	@Override
	public List<Object> getMessageArgs() {
		if(messageArgs==null) {
			return Collections.emptyList();
		} else {
			return messageArgs;
		}
	}

	@Override
	public void addMessageArg(Object value) {
		if(messageArgs==null) messageArgs = new ArrayList<Object>();
		messageArgs.add(value);
	}

	@Override
    protected void doTag(Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		BundleTag bundleTag = BundleTag.getBundleTag(pageContext.getRequest());
		if(bundleTag==null) throw new LocalizedJspException(accessor, "error.requiredParentTagNotFound", "bundle");
		Locale locale = pageContext.getResponse().getLocale();
		String prefix = bundleTag.getPrefix();
		String combinedKey = prefix==null || prefix.isEmpty() ? key : prefix.concat(key);
		BundleLookup bundleLookup;
		if(messageArgs==null) {
			bundleLookup = new BundleLookup(
				bundleTag.getBasename(),
				locale,
				combinedKey
			);
		} else {
			bundleLookup = new BundleLookup(
				bundleTag.getBasename(),
				locale,
				combinedKey,
				messageArgs.toArray()
			);
		}
		Coercion.write(bundleLookup, out);
    }
}

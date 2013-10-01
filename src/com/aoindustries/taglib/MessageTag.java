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
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
		MediaType newMediaType;
		if(type instanceof MediaType) {
			newMediaType = (MediaType)type;
		} else {
			String typeStr = Coercion.toString(type);
			newMediaType = MediaType.getMediaTypeByName(typeStr);
			if(newMediaType==null) {
				try {
					newMediaType = MediaType.getMediaTypeForContentType(typeStr);
				} catch(MediaException e) {
					throw new JspException(e);
				}
			}
		}
		this.type = type;
		this.mediaType = newMediaType;
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

	private String lookupResult;
	private BundleLookupMarkup lookupMarkup;

	@Override
	protected void writePrefix(MediaType containerType, Writer out) throws JspException, IOException {
		// Find parent bundle
		PageContext pageContext = (PageContext)getJspContext();
		BundleTag bundleTag = BundleTag.getBundleTag(pageContext.getRequest());
		if(bundleTag==null) throw new LocalizedJspException(accessor, "error.requiredParentTagNotFound", "bundle");
		ApplicationResourcesAccessor accessor = bundleTag.getAccessor();
		// Lookup the message value
		String prefix = bundleTag.getPrefix();
		String combinedKey = prefix==null || prefix.isEmpty() ? key : prefix.concat(key);
		if(messageArgs==null) {
			lookupResult = accessor.getMessage(combinedKey);
		} else {
			lookupResult = accessor.getMessage(combinedKey, messageArgs.toArray());
		}
		// Look for any message markup
		BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
		if(threadContext!=null) {
			lookupMarkup = threadContext.getLookupMarkup(lookupResult);
		}
		if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(containerType.getMarkupType(), out);
	}

	@Override
    protected void doTag(Writer out) throws JspException, IOException {
		out.write(lookupResult);
    }

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws IOException {
		if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(containerType.getMarkupType(), out);
	}
}

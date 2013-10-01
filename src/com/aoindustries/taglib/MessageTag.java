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
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class MessageTag
	extends AutoEncodingNullTag
	implements
		DynamicAttributes,
		TypeAttribute,
		MessageArgsAttribute
{

	private String key;
	private Object type = MediaType.TEXT;
    private MediaType mediaType = MediaType.TEXT;
	private BitSet messageArgsSet;
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
		// Create lists on first use
		if(messageArgs==null) {
			messageArgsSet = new BitSet();
			messageArgs = new ArrayList<Object>();
		}
		// Find the first index that is not used
		int index = messageArgsSet.nextClearBit(0);
		messageArgsSet.set(index);
		if(index>=messageArgs.size()) {
			assert index==messageArgs.size();
			messageArgs.add(value);
		} else {
			messageArgs.set(index, value);
		}
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		System.err.println("DEBUG: uri="+uri);
		if(localName.startsWith("arg")) {
			try {
				String numSubstring = localName.substring(3);
				int index = Integer.parseInt(numSubstring);
				// Do not allow "arg00" in place of "arg0"
				if(!numSubstring.equals(Integer.toString(index))) throw new LocalizedJspException(accessor, "MessageTag.unexpectedDynamicAttribute", localName);
				// Create lists on first use
				if(messageArgs==null) {
					messageArgsSet = new BitSet();
					messageArgs = new ArrayList<Object>();
				}
				// Must not already be set
				if(messageArgsSet.get(index)) throw new LocalizedJspException(accessor, "MessageTag.duplicateAttribute", localName);
				messageArgsSet.set(index);
				if(index>=messageArgs.size()) {
					while(messageArgs.size() < index) {
						messageArgs.add(null);
					}
					assert index==messageArgs.size();
					messageArgs.add(value);
				} else {
					if(messageArgs.set(index, value)!=null) throw new AssertionError();
				}
			} catch(NumberFormatException err) {
				throw new LocalizedJspException(err, accessor, "MessageTag.unexpectedDynamicAttribute", localName);
			}
		} else {
			throw new LocalizedJspException(accessor, "MessageTag.unexpectedDynamicAttribute", localName);
		}
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
			// Error if gap in message args (any not set in range)
			int firstClear = messageArgsSet.nextClearBit(0);
			if(firstClear < messageArgs.size()) throw new LocalizedJspException(accessor, "MessageTag.argumentMissing", firstClear);
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

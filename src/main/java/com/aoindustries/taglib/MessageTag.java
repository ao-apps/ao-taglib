/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.taglib.EncodingNullSimpleTag;
import com.aoindustries.lang.Strings;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class MessageTag
	extends EncodingNullSimpleTag
	implements
		DynamicAttributes,
		TypeAttribute,
		MessageArgsAttribute
{

	private String bundle;
	private String key;
	private MediaType mediaType = MediaType.TEXT;
	private BitSet messageArgsSet;
	private List<Object> messageArgs;

	@Override
	public MediaType getOutputType() {
		return mediaType;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void setType(String type) throws JspTagException {
		String typeStr = Strings.trim(type);
		MediaType newMediaType = MediaType.getMediaTypeByName(typeStr);
		if(newMediaType==null) {
			try {
				newMediaType = MediaType.getMediaTypeForContentType(typeStr);
			} catch(UnsupportedEncodingException e) {
				throw new JspTagException(e);
			}
		}
		this.mediaType = newMediaType;
	}

	@Override
	public void addMessageArg(Object value) {
		// Create lists on first use
		if(messageArgs==null) {
			messageArgsSet = new BitSet();
			messageArgs = new ArrayList<>();
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

	public void setArg0(Object value) throws JspTagException {
		insertMessageArg("arg0", 0, value);
	}

	public void setArg1(Object value) throws JspTagException {
		insertMessageArg("arg1", 1, value);
	}

	public void setArg2(Object value) throws JspTagException {
		insertMessageArg("arg2", 2, value);
	}

	public void setArg3(Object value) throws JspTagException {
		insertMessageArg("arg3", 3, value);
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(uri==null && localName.startsWith("arg")) {
			try {
				String numSubstring = localName.substring(3);
				int index = Integer.parseInt(numSubstring);
				// Do not allow "arg00" in place of "arg0"
				if(!numSubstring.equals(Integer.toString(index))) throw new LocalizedJspTagException(accessor, "error.unexpectedDynamicAttribute1", localName, "arg*");
				insertMessageArg(localName, index, value);
			} catch(NumberFormatException err) {
				throw new LocalizedJspTagException(err, accessor, "error.unexpectedDynamicAttribute1", localName, "arg*");
			}
		} else {
			throw new LocalizedJspTagException(accessor, "error.unexpectedDynamicAttribute1", localName, "arg*");
		}
	}

	private void insertMessageArg(String localName, int index, Object value) throws JspTagException {
		// Create lists on first use
		if(messageArgs==null) {
			messageArgsSet = new BitSet();
			messageArgs = new ArrayList<>();
		}
		// Must not already be set
		if(messageArgsSet.get(index)) throw new LocalizedJspTagException(accessor, "MessageTag.duplicateArgument", localName);
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
	}

	private String lookupResult;
	private BundleLookupMarkup lookupMarkup;

	@Override
	protected void writePrefix(MediaType containerType, Writer out) throws JspTagException, IOException {
		ApplicationResourcesAccessor accessor;
		String combinedKey;
		if(bundle != null) {
			accessor = ApplicationResourcesAccessor.getInstance(bundle);
			combinedKey = key;
		} else {
			// Find parent bundle
			PageContext pageContext = (PageContext)getJspContext();
			BundleTag bundleTag = BundleTag.getBundleTag(pageContext.getRequest());
			if(bundleTag==null) throw new LocalizedJspTagException(com.aoindustries.taglib.ApplicationResources.accessor, "error.requiredParentTagNotFound", "bundle");
			accessor = bundleTag.getAccessor();
			String prefix = bundleTag.getPrefix();
			combinedKey = prefix==null || prefix.isEmpty() ? key : prefix.concat(key);
		}
		// Lookup the message value
		if(messageArgs==null) {
			lookupResult = accessor.getMessage(combinedKey);
		} else {
			// Error if gap in message args (any not set in range)
			int firstClear = messageArgsSet.nextClearBit(0);
			if(firstClear < messageArgs.size()) throw new LocalizedJspTagException(com.aoindustries.taglib.ApplicationResources.accessor, "MessageTag.argumentMissing", firstClear);
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
	protected void doTag(Writer out) throws JspTagException, IOException {
		out.write(lookupResult);
	}

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws IOException {
		if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(containerType.getMarkupType(), out);
	}
}

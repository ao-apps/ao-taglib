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

import com.aoindustries.lang.LocalizedIllegalArgumentException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.StringUtility;
import java.util.Collections;
import java.util.List;

/**
 * Matches simple wildcard patterns, supporting prefix, suffix, and exact value.
 * Supports:
 * <ul>
 *   <li>*            Match all</li>
 *   <li>*suffix      Suffix match</li>
 *   <li>prefix*      Prefix match</li>
 *   <li>exact_value  Exact match</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class WildcardPatternMatcher {

	private static final WildcardPatternMatcher emptyMatcher;
	static {
		List<String> emptyList = Collections.emptyList();
		emptyMatcher = new WildcardPatternMatcher(emptyList);
	}

	/**
	 * Gets the empty matcher.
	 */
	public static WildcardPatternMatcher getEmptyMatcher() {
		return emptyMatcher;
	}

	/**
	 * Gets the matcher for the comma and/or space separated patterns.
	 */
	public static WildcardPatternMatcher getInstance(String patterns) {
		if(patterns==null || patterns.isEmpty()) {
			return emptyMatcher;
		} else {
			List<String> list = StringUtility.splitStringCommaSpace(patterns);
			if(list.isEmpty()) return emptyMatcher;
			return new WildcardPatternMatcher(list);
		}
	}

	private final List<String> patterns;

	private WildcardPatternMatcher(List<String> patterns) {
		this.patterns = patterns;
	}

	/**
	 * Checks if this is empty (has no patterns).
	 */
	public boolean isEmpty() {
		return patterns.isEmpty();
	}

	public boolean isMatch(String paramName) {
		for(String pattern : patterns) {
			final int patternLen = pattern.length();
			if(patternLen>0) {
				final int firstStar = pattern.indexOf('*');
				if(firstStar==-1) {
					// Exact match
					if(paramName.equals(pattern)) return true;
				} else if(patternLen==1) {
					// Match all
					return true;
				} else {
					final int lastStar = pattern.lastIndexOf('*');
					// May not have two asterisks
					if(firstStar!=lastStar) {
						throw new LocalizedIllegalArgumentException(accessor, "ParameterMatcher.invalidParameterFilter", pattern);
					}
					if(firstStar==0) {
						// Suffix match
						final int paramNameLen = paramName.length();
						if(
							paramNameLen >= (patternLen-1)
							&& paramName.regionMatches(
								paramNameLen-(patternLen-1),
								pattern,
								1,
								patternLen-1
							)
							//paramName.endsWith(filter.substring(1))
						) return true;
					} else if(lastStar==(patternLen-1)) {
						// Prefix match
						if(
							paramName.length() >= (patternLen-1)
							&& paramName.regionMatches(
								0,
								pattern,
								0,
								patternLen-1
							)
							//paramName.startsWith(filter.substring(0, filterLen-1))
						) return true;
					} else {
						// Asterisk in middle
						throw new LocalizedIllegalArgumentException(accessor, "ParameterMatcher.invalidParameterFilter", pattern);
					}
				}
			}
		}
		return false;
	}
}

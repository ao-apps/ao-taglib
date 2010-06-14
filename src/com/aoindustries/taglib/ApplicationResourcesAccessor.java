/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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

/**
 * Provides a simplified interface for obtaining localized values from the ApplicationResources.properties files.
 *
 * @author  AO Industries, Inc.
 */
public final class ApplicationResourcesAccessor {

    /**
     * Make no instances.
     */
    private ApplicationResourcesAccessor() {
    }

    private static final com.aoindustries.util.i18n.ApplicationResourcesAccessor accessor = com.aoindustries.util.i18n.ApplicationResourcesAccessor.getInstance("com.aoindustries.taglib.ApplicationResources");

    public static String getMessage(String key) {
        return accessor.getMessage(key);
    }
    
    public static String getMessage(String key, Object... args) {
        return accessor.getMessage(key, args);
    }

    public static String getMessage(String missingDefault, String key) {
        return accessor.getMessage(missingDefault, key);
    }

    public static String getMessage(String missingDefault, String key, Object... args) {
        return accessor.getMessage(missingDefault, key, args);
    }
}

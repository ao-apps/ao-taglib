package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Something with a value attribute.
 *
 * @author  AO Industries, Inc.
 */
public interface ValueAttribute {

    String getValue();

    void setValue(String value);
}

/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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

import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;

final class FailOnWriteWriter extends Writer {

    private static FailOnWriteWriter instance = new FailOnWriteWriter();

    static FailOnWriteWriter getInstance() {
        return instance;
    }

    private FailOnWriteWriter() {
    }

    @Override
    public void write(int c) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public void write(char cbuf[]) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public void write(String str) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public FailOnWriteWriter append(CharSequence csq) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public FailOnWriteWriter append(CharSequence csq, int start, int end) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public FailOnWriteWriter append(char c) throws IOException {
        throw new LocalizedIOException(accessor, "FailOnWriteWriter.noOutputAllowed");
    }

    @Override
    public void flush() {
        // Do nothing
    }

    @Override
    public void close() {
        // Do nothing
    }
}

/*
 * * Copyright (C) 2018-2020 Matt Baxter https://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.pastegg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PasteContent {
    @SuppressWarnings("unused")
    private final @NotNull PasteContentFormat format;
    private final @NotNull String value;
    private transient @Nullable String processedValue; // todo

    /**
     * Constructs a paste content.
     *
     * @param format format of the content
     * @param value  content
     */
    public PasteContent(final @NotNull PasteContentFormat format, final @Nullable String value) {
        if (format == PasteContentFormat.XZ) {
            throw new UnsupportedOperationException("XZ not presently supported");
        }
        this.format = format;
        this.value = format.encode(value);
        this.processedValue = value;
    }

    public @NotNull String getValue() {
        if (this.processedValue == null) {
            // TODO magic
        }
        return this.processedValue;
    }
}

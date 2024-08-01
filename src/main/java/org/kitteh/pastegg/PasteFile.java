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

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a paste contains at least one or more files
 */
public record PasteFile(
        @Nullable String name,
        @NotNull PasteContent content,
        @SerializedName("highlight_language") @Nullable HighlightLanguage highlightLanguage) {

    public PasteFile(@Nullable String name, @NotNull PasteContent content) {
        this(name, content, null);
    }

    /**
     * The name can be null, if the file was created by the client, but the server will assign default names.
     * However, you can delete the name again, and it will become null, the id can't be changed, after it was assigned.
     */
    @Override
    public @Nullable String name() {
        return name;
    }

    public @NotNull PasteContent content() {
        return content;
    }

    @Override
    public @Nullable HighlightLanguage highlightLanguage() {
        return highlightLanguage;
    }

    @Override
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder("{");

        if (name != null) {
            builder.append("\"name\":\"").append(name).append("\", ");
        }

        if (highlightLanguage != null) {
            builder.append("\"highlightLanguage\":\"").append(highlightLanguage).append("\", ");
        }

        builder.append("\"content\":{").append(content).append("}");

        builder.append("}");

        return builder.toString();
    }
}

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

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;

public class PasteContent {
    public enum ContentType {
        /**
         * QmFzZTY0IGVuY29kZWQgY29udGVudC4=
         */
        @SerializedName("base64")
        BASE64(string -> Base64.getUrlEncoder().encodeToString(string.getBytes())),
        /**
         * GZIP encoded content.
         */
        @SerializedName("gzip")
        GZIP(string -> {
            byte[] bytes = string.getBytes();

            try {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(bytes.length);
                try (byteOutput) {
                    try (GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput)) {
                        gzipOutput.write(bytes);
                    }
                }
                return Base64.getUrlEncoder().encodeToString(byteOutput.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(); // TODO
            }
        }
        ),
        /**
         * Just give me the text!
         */
        @SerializedName("text")
        TEXT(string -> string),
        /**
         * XZ is presently unsupported.
         */
        @SerializedName("xz")
        XZ(string -> string); // TODO

        private final @NotNull Function<String, String> processor;

        ContentType(@NotNull Function<String, String> processor) {
            this.processor = processor;
        }

        public String process(String strToProcess) {
            return this.processor.apply(strToProcess);
        }
    }

    @SuppressWarnings("unused")
    private final @NotNull ContentType format;
    @SuppressWarnings("unused")
    private final @NotNull String value;
    private transient @Nullable String processedValue;

    /**
     * Constructs a paste content.
     *
     * @param format format of the content
     * @param value content
     */
    public PasteContent(final @NotNull ContentType format, final @Nullable String value) {
        if (format == ContentType.XZ) {
            throw new UnsupportedOperationException("XZ not presently supported");
        }
        this.format = format;
        this.value = format.process(value);
        this.processedValue = value;
    }

    public String getValue() {
        if (this.processedValue == null) {
            // TODO magic
        }
        return this.processedValue;
    }
}

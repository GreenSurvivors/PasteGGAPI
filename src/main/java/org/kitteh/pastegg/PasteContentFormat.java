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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * These are the only data formats paste.gg supports.
 * I would love to provide a register for all kinds of different formats,
 * but our paste bin doesn't allow it.
 */
public enum PasteContentFormat { // TODO support more like XZ archives
    /**
     * Just give me the text!
     */
    @SerializedName("text")
    TEXT {
        @Override
        public @NotNull String encode(@NotNull String strToEncode) {
            return strToEncode;
        }

        @Override
        public @NotNull String decode(@NotNull String strToDecode) throws UnsupportedOperationException {
            return strToDecode;
        }
    },
    @SerializedName("gzip")
    GZIP {
        @Override
        public @NotNull String encode(@NotNull String strToEncode) {
            byte[] bytes = strToEncode.getBytes(StandardCharsets.ISO_8859_1);

            try {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(bytes.length);
                try (byteOutput) {
                    try (GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput)) {
                        gzipOutput.write(bytes);
                    }
                }
                return Base64.getUrlEncoder().encodeToString(byteOutput.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e); // TODO
            }
        }

        @Override
        public @NotNull String decode(@NotNull String strToDecode) throws UnsupportedOperationException {
            byte[] bytes = Base64.getUrlDecoder().decode(strToDecode);

            ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
            try (byteArrayInput; GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInput)) {
                byte[] inflatedBytes = gzipInputStream.readAllBytes();

                return new String(inflatedBytes, StandardCharsets.ISO_8859_1);
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO
            }
        }
    },

    @SerializedName("base64")
    BASE64 {
        @Override
        public @NotNull String encode(@NotNull String strToEncode) {
            return Base64.getUrlEncoder().encodeToString(strToEncode.getBytes(StandardCharsets.ISO_8859_1));
        }

        @Override
        public @NotNull String decode(@NotNull String strToDecode) throws UnsupportedOperationException {
            return new String(Base64.getUrlDecoder().decode(strToDecode), StandardCharsets.ISO_8859_1);
        }
    },
    /**
     * @deprecated DO NOT USE, this is NOT implemented!
     */
    @Deprecated
    @SerializedName("xz")
    XZ { // there is no easy / native way for us to do this. We probably would have to depend on https://commons.apache.org/proper/commons-compress/ or https://github.com/tukaani-project/xz-java
        @Override
        public @NotNull String encode(@NotNull String strToEncode) {
            throw new UnsupportedOperationException("xz is not implemented yet!");
        }

        @Override
        public @NotNull String decode(@NotNull String strToDecode) {
            throw new UnsupportedOperationException("xz is not implemented yet!");
        }
    };

    public abstract @NotNull String encode(@NotNull String strToEncode);

    public abstract @NotNull String decode(@NotNull String strToDecode);
}

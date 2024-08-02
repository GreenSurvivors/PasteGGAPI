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
package org.kitteh.pastegg.client;

import org.jetbrains.annotations.NotNull;
import org.kitteh.pastegg.PasteContent;
import org.kitteh.pastegg.PasteContentFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class FormatCodec<T> {
    public static final FormatCodec<byte @NotNull []> BYTES_TO_BASE_64 =
            new FormatCodec<>(PasteContentFormat.BASE64, "bytes_to_base64") {

                @Override
                public @NotNull String encodeRaw(byte @NotNull [] toEncode) {
                    return Base64.getEncoder().encodeToString(toEncode);
                }

                @Override
                public byte @NotNull [] decodeRaw(@NotNull String strToDecode) throws IllegalArgumentException {
                    return Base64.getDecoder().decode(strToDecode);
                }
            };
    /**
     * when encoding deflates all incoming bytes to gzip and formats the output to a base64 string
     * when decoding inflates the base64 string via a GZIPInputStream.
     */
    public static final FormatCodec<@NotNull InputStream> STREAM_TO_GZIP =
            new FormatCodec<>(PasteContentFormat.GZIP, "stream_to_gzip") {

                @Override
                public @NotNull String encodeRaw(@NotNull InputStream toEncode) {

                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(toEncode.available());) {
                        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) { // close stream to write all bytes!
                            // input stream to output!
                            toEncode.transferTo(gzipOutputStream);
                        }

                        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public @NotNull InputStream decodeRaw(@NotNull String strToDecode) {
                    byte[] bytes = Base64.getDecoder().decode(strToDecode);

                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
                        return new GZIPInputStream(byteArrayInputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
    public static @NotNull FormatCodec<@NotNull String> TEXT_TO_TEXT =
            new FormatCodec<>(PasteContentFormat.TEXT, "text_to_text") {

                @Override
                public @NotNull String encodeRaw(@NotNull String toEncode) {
                    return toEncode;
                }

                @Override
                public @NotNull String decodeRaw(@NotNull String strToDecode) {
                    return strToDecode;
                }
            };
    protected final @NotNull String uniqueID;
    protected final @NotNull PasteContentFormat format;

    public FormatCodec(@NotNull PasteContentFormat format, @NotNull String uniqueID) {
        this.uniqueID = uniqueID;
        this.format = format;
    }

    public @NotNull PasteContent encode(@NotNull T toEncode) {
        return new PasteContent(getFormat(), encodeRaw(toEncode));
    }

    public @NotNull T decode(@NotNull PasteContent toDecode) {
        return decodeRaw(toDecode.value());
    }

    public @NotNull PasteContentFormat getFormat() {
        return format;
    }

    public @NotNull String getUniqueID() {
        return uniqueID;
    }

    public abstract @NotNull String encodeRaw(T toEncode);

    public abstract T decodeRaw(@NotNull String strToDecode);
}

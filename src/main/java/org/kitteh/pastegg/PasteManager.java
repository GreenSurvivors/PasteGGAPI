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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.pastegg.reply.ErrorReply;
import org.kitteh.pastegg.reply.content.PasteResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PasteManager {
    private static final @NotNull Map<@NotNull String /* id*/, PasteResult> sessionPastes = new HashMap<>();
    private static @Nullable String apiKey;

    /**
     * Return the API key.
     *
     * @return the key
     */
    @Contract(pure = true)
    public static @Nullable String getApiKey() {
        return apiKey;
    }

    /**
     * Set the managers API key.
     *
     * @param key api Key
     */
    public static void setApiKey(@Nullable String key) {
        apiKey = key;
    }

    /**
     * Clear all stored pastes.
     */
    public static void clearPastes() {
        sessionPastes.clear();
    }

    /**
     * Add a paste.
     *
     * @param result the result to add
     */
    protected static void trackPaste(@NotNull PasteResult result) {
        sessionPastes.put(result.id(), result);
    }

    /**
     * Attempt to delete a paste, posted in this session
     * using a stored deletion key OR
     * the api key if provided - if neither are present it will return false
     *
     * @param id the paste ID
     * @return true if the paste with the id was successfully deleted,
     * false if paste.gg reported anything else than a success,
     * throws an exception if anything on the way goes wrong.
     * @throws InvalidPasteException if the id was not created in this session, or has no deletion key nor is an api ke known
     * @see #deletePaste(String, String)
     */
    public static boolean deletePaste(@NotNull String id) throws InvalidPasteException, IOException {
        PasteResult result = sessionPastes.get(id);
        if (result == null) {
            return false;
        } else {
            String pasteKey;
            if (result.deletionKey() != null) {
                pasteKey = result.deletionKey();
            } else {
                if (apiKey == null) {
                    return false;
                }

                pasteKey = apiKey;
            }

            return deletePaste(result.id(), pasteKey) != null;
        }
    }

    /**
     * Attempt to delete a paste.
     *
     * @param deletionKey may be the key provided by the service when the paste was posted; OR the matching API-key
     * @param id          the paste ID
     * @return null if successful,
     * PasteResultError if the server answered with one of those,
     * throws an IOException otherwise
     * @throws InvalidPasteException if something unexpected happens
     * @see #deletePaste(String)
     */
    public static @Nullable ErrorReply deletePaste(@NotNull String id, @NotNull String deletionKey) throws IOException {
        try {
            return ConnectionProvider.deletePaste(id, deletionKey);
        } catch (IOException e) {
            throw new IOException("Paste could not be deleted. ID: " + id, e);
        }
    }
}

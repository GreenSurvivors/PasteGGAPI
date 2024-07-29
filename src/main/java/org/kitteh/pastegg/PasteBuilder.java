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

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.kitteh.pastegg.pasteresult.APasteResult;
import org.kitteh.pastegg.pasteresult.PasteResultSuccess;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class PasteBuilder {
    private final static @NotNull Gson GSON = new Gson();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final @NotNull List<@NotNull PasteFile> files = new LinkedList<>();
    private @Nullable String apiKey = null;
    private @NotNull Visibility visibility = Visibility.getDefault();
    private @Nullable String name = null;
    private @Nullable String expires = null;
    private @Nullable String description = null;
    // transient - don't send our debug to paste.gg
    private transient boolean debug = false;

    public @NotNull PasteBuilder name(@Nullable String name) {
        this.name = name;
        return this;
    }

    // ZonedDateTime.now( ZoneOffset.UTC ).plusSeconds(10)
    public @NotNull PasteBuilder expires(@Nullable ZonedDateTime when) {
        this.expires = when == null ? null : when.format(DateTimeFormatter.ISO_INSTANT);
        return this;
    }

    public @NotNull PasteBuilder setApiKey(@Nullable String key) {
        this.apiKey = key;
        return this;
    }

    public @NotNull PasteBuilder visibility(@NotNull Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public @NotNull PasteBuilder description(@NotNull String description) {
        this.description = description;
        return this;
    }

    /**
     * debug the connection.
     */
    @VisibleForTesting
    protected @NotNull PasteBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public @NotNull PasteBuilder addFile(@NotNull PasteFile file) {
        files.add(file);
        return this;
    }

    /**
     * Please note: the result of this Build is NOT a {@link Paste},
     * but the created paste will already be uploaded to paste.gg,
     * and instead the answer of the server will get returned.
     */
    public @NotNull APasteResult build() throws InvalidPasteException, IOException {
        if (visibility == Visibility.PRIVATE && apiKey == null) {
            throw new InvalidPasteException("No API Key Provided for Private Paste...");
        } else {
            String toString = GSON.toJson(this);

            try {
                APasteResult result = ConnectionProvider.processPasteRequest(apiKey, toString, debug);

                if (result instanceof PasteResultSuccess pasteResultSuccess && pasteResultSuccess.getPaste() != null) {
                    PasteManager.trackPaste(pasteResultSuccess.getPaste());
                }

                return result;
            } catch (IOException e) {
                throw new IOException("Paste could not be sent to past.gg", e);
            }
        }
    }
}

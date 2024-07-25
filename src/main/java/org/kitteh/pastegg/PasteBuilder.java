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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class PasteBuilder {
    private final static @NotNull Gson GSON = new Gson();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final @NotNull List<@NotNull PasteFile> files = new LinkedList<>();
    private @Nullable String apiKey = null;
    private @NotNull Visibility visibility = Visibility.getDefault();
    private @Nullable String name = null;
    private @Nullable String expires = null;
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

    /**
     * debug the connection.
     */
    public @NotNull PasteBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public @NotNull PasteBuilder addFile(@NotNull PasteFile file) {
        files.add(file);
        return this;
    }

    public @NotNull PasteResult build() throws InvalidPasteException {
        if (visibility == Visibility.PRIVATE && apiKey == null) {
            throw new InvalidPasteException("No API Key Provided for Private Paste...");
        } else {
            String toString = GSON.toJson(this);

            try {
                String result = ConnectionProvider.processPasteRequest(apiKey, toString, debug);
                PasteResult pasteResult = GSON.fromJson(result, PasteResult.class);
                if (pasteResult.getPaste().isPresent()) {
                    PasteManager.trackPaste(pasteResult.getPaste().get());
                }

                return pasteResult;
            } catch (IOException e) {
                InvalidPasteException invalid = new InvalidPasteException("Paste could not be sent to past.gg: " +
                        e.getMessage());
                invalid.addSuppressed(e);
                throw invalid;
            }
        }
    }

    public static class PasteResult {
        private @NotNull String status;
        private @Nullable Paste result;
        private @Nullable String message;

        public PasteResult() {
        }

        public Optional<Paste> getPaste() {
            return Optional.ofNullable(this.result);
        }

        public Optional<String> getMessage() {
            return Optional.ofNullable(this.message);
        }

        public @NotNull String getStatus() {
            return status;
        }
    }
}

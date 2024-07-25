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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Optional;

public class Paste {
    private final @NotNull String id;
    private final @Nullable String deletion_key;
    private final @NotNull Visibility visibility;
    private @Nullable Date expires;
    @SerializedName("created_at")
    private @Nullable Date createdAt;
    @SerializedName("updated_at")
    private @Nullable Date updatedAt;

    /**
     * Constructs a public paste without a deletion key.
     *
     * @param id id
     */
    public Paste(@NotNull String id) {
        this(id, null);
    }

    /**
     * Constructs a public paste.
     *
     * @param id          id
     * @param deletionKey deletion key, or null
     */
    public Paste(@NotNull String id, @Nullable String deletionKey) {
        this(id, deletionKey, Visibility.PUBLIC);
    }

    public Paste(@NotNull String id, @Nullable String deletion_key, @NotNull Visibility visibility) {
        this.id = id;
        this.deletion_key = deletion_key;
        this.visibility = visibility;
    }

    public @Nullable Date getExpires() {
        return expires;
    }

    public @Nullable Date getCreatedAt() {
        return createdAt;
    }

    public @Nullable Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Gets the paste's id.
     *
     * @return id
     */
    public @NotNull String getId() {
        return this.id;
    }

    /**
     * Gets the paste's deletion key for anonymous pastes.
     *
     * @return deletion key or empty if not anonymous
     */
    @Contract(pure = true)
    public @NotNull Optional<String> getDeletionKey() {
        return Optional.ofNullable(this.deletion_key);
    }

    public @NotNull Visibility getVisibility() {
        return visibility;
    }
}

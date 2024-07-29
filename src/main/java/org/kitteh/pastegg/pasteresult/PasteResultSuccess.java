package org.kitteh.pastegg.pasteresult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.pastegg.Paste;

public class PasteResultSuccess extends APasteResult {
    private final @Nullable Paste result;
    private final @Nullable String message;

    public PasteResultSuccess(@NotNull PasteStatus status, @Nullable Paste result, @Nullable String message) {
        super(status);
        this.result = result;
        this.message = message;
    }

    public @Nullable Paste getPaste() {
        return this.result;
    }

    public @Nullable String getMessage() {
        return this.message;
    }
}

package org.kitteh.pastegg.pasteresult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PasteResultError extends APasteResult {
    private final @NotNull String error;
    private final @Nullable String message;

    public PasteResultError(@NotNull APasteResult.PasteStatus status, @NotNull String error, @Nullable String message) {
        super(status);
        this.error = error;
        this.message = message;
    }

    public @NotNull String getError() {
        return error;
    }

    public @Nullable String getMessage() {
        return message;
    }
}

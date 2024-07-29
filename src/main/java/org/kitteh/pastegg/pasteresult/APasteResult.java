package org.kitteh.pastegg.pasteresult;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public abstract class APasteResult {
    private final @NotNull PasteStatus status;

    protected APasteResult(@NotNull PasteStatus status) {
        this.status = status;
    }

    public @NotNull PasteStatus getStatus() {
        return status;
    }

    public enum PasteStatus {
        @SerializedName("error")
        ERROR,
        @SerializedName("success")
        SUCCESS
    }
}

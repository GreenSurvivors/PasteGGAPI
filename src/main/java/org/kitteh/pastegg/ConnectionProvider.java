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
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.kitteh.pastegg.client.Paste;
import org.kitteh.pastegg.reply.ErrorReply;
import org.kitteh.pastegg.reply.IReply;
import org.kitteh.pastegg.reply.SuccessReply;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ConnectionProvider {
    @VisibleForTesting
    protected final static @NotNull Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(HighlightLanguage.class, new HighlightLanguage.TypeAdapter());

        GSON = builder.create();
    }

    public static @NotNull IReply processPasteRequest(@Nullable String key, @NotNull Paste paste) throws IOException {
        return processPasteRequest(key, paste, false);
    }

    @VisibleForTesting
    protected static @NotNull IReply processPasteRequest(@Nullable String key, @NotNull Paste paste, boolean debug) throws IOException {
        final String jsonOutput = GSON.toJson(paste);

        URL url = URI.create("https://api.paste.gg/v1/pastes").toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        conn.setDoOutput(true);
        if (key != null) {
            conn.setRequestProperty("Authorization", "Key " + key);
        }

        conn.setRequestProperty("Accept", "application/json");

        if (debug) {
            System.out.println("----------Connection--------------");
            System.out.println(conn);
            System.out.println("----------Output--------------");
            System.out.println(jsonOutput);
            System.out.println("------------------------------");
        }

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonOutput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode;
        // get responseCode
        try {
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            try (InputStream in = conn.getErrorStream()) {

                if (in != null && debug) {
                    try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                         BufferedReader errorIn = new BufferedReader(reader)) {

                        String error = errorIn.lines().collect(Collectors.joining());
                        System.out.println("----------Error Response--------------");
                        System.out.println(error);
                        System.out.println("------------------------------");
                    }
                }
            }

            throw new IOException(e); // rethrow and exit this methode
        }

        return switch (responseCode) {
            case HttpsURLConnection.HTTP_CREATED -> GSON.fromJson(getResultAsString(conn, false), SuccessReply.class);
            case 400, 403, 404 -> GSON.fromJson(getResultAsString(conn, true), ErrorReply.class);
            default -> throw new ConnectException("Unexpected response code: " + responseCode);
        };
    }

    private static @NotNull String getResultAsString(final @NotNull HttpsURLConnection conn, boolean error) throws IOException {
        try (InputStream stream = error ? conn.getErrorStream() : conn.getInputStream();
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(reader)) {

            return in.lines().collect(Collectors.joining());
        } // other exit is via exception
    }

    /**
     * @return null if successful, PasteResultError if the server answered with one of those, throws an IOException otherwise
     */
    public static @Nullable ErrorReply deletePaste(@NotNull String pasteId, @NotNull String deletionKey) throws IOException {
        URL url = URI.create("https://api.paste.gg/v1/pastes/" + pasteId).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        String key = "Key " + deletionKey;

        conn.setRequestProperty("Authorization", key);
        conn.connect();

        int responseCode = conn.getResponseCode();
        return switch (responseCode) {
            case HttpsURLConnection.HTTP_NO_CONTENT -> null;
            case 400, 403, 404 -> GSON.fromJson(getResultAsString(conn, true), ErrorReply.class);
            default -> throw new ConnectException("Unexpected response code: " + responseCode);
        };
    }
}

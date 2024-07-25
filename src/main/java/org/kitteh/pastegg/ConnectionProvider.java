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

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Narimm on 28/02/2020.
 */
public class ConnectionProvider {
    private static Integer responseCode = null;

    public static Integer getLastResponseCode() {
        return responseCode;
    }

    static @NotNull String processPasteRequest(@NotNull String key, String output) throws IOException {
        return processPasteRequest(key, output,false);
    }

    static String processPasteRequest(String key, String output, boolean debug) throws IOException {
        URL url = URI.create("https://api.paste.gg/v1/pastes").toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        conn.setDoOutput(true);
        if (key != null) {
            conn.setRequestProperty("Authorization", "Key " + key);
        }
        conn.setRequestProperty("Accept", "application/json");
        if (debug) {
            System.out.println("----------Connection--------------");
            System.out.println(conn.toString());
            System.out.println("----------Output--------------");
            System.out.println(output);
            System.out.println("------------------------------");
        }
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = output.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        StringBuilder content = new StringBuilder();
        try {
            responseCode = conn.getResponseCode();
        }catch (IOException e){
            InputStream in = conn.getErrorStream();
            if (in != null) {
                InputStreamReader reader = new InputStreamReader(in,StandardCharsets.UTF_8);
                BufferedReader errorIn = new BufferedReader(reader);
                String inputLine;
                while ((inputLine = errorIn.readLine()) != null) {
                    content.append(inputLine);
                }
                if ( debug ) {
                    System.out.println("----------Error Response--------------");
                    System.out.println(content.toString());
                    System.out.println("------------------------------");
                }
                throw new IOException(e.getMessage() + " Error Data: " + content.toString());
            }
            throw e;
        }
        try (
              InputStream stream = conn.getInputStream();
              InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
              BufferedReader in = new BufferedReader(reader)) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        return content.toString();
    }

    public static boolean deletePaste(String pasteId, String deletionKey) throws IOException{
        URL url = new URL("https://api.paste.gg/v1/pastes/"+pasteId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        String key = "Key "+deletionKey;
        conn.setRequestProperty("Authorization",key);
        conn.connect();

        int responseCode = conn.getResponseCode();
        return responseCode == 204;
    }
}

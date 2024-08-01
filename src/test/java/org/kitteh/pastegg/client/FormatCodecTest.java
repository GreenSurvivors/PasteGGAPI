package org.kitteh.pastegg.client;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.kitteh.pastegg.PasteContent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class FormatCodecTest { // todo .png test
    public static final String CONTENT_TEXT = "HELLO WORLD";
    Gson gson;

    @Before
    public void setUp() {
        gson = new Gson();
    }

    @Test
    public void getTextValue() {
        PasteContent content = FormatCodec.TEXT_TO_TEXT.encode(CONTENT_TEXT);

        assertEquals(CONTENT_TEXT, content.value());
        assertEquals(CONTENT_TEXT, FormatCodec.TEXT_TO_TEXT.decodeRaw(content.value()));
        assertEquals(CONTENT_TEXT, FormatCodec.TEXT_TO_TEXT.decode(content));
    }

    @Test
    public void textSerialize() {
        PasteContent content = FormatCodec.TEXT_TO_TEXT.encode(CONTENT_TEXT);
        String out = gson.toJson(content);
        assertEquals("{\"format\":\"text\",\"value\":\"HELLO WORLD\"}", out);

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);

        assertEquals(CONTENT_TEXT, contentDeserialized.value());
        assertEquals(CONTENT_TEXT, FormatCodec.TEXT_TO_TEXT.decodeRaw(contentDeserialized.value()));
        assertEquals(CONTENT_TEXT, FormatCodec.TEXT_TO_TEXT.decode(contentDeserialized));
    }

    @Test
    public void getGZIPValue() throws IOException {
        PasteContent content = FormatCodec.STREAM_TO_GZIP.encode(new ByteArrayInputStream(CONTENT_TEXT.getBytes(StandardCharsets.UTF_8)));
        assertNotEquals(CONTENT_TEXT, content.value());
        assertNotNull(content.value());

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream inputStream = FormatCodec.STREAM_TO_GZIP.decodeRaw(content.value())) {

            inputStream.transferTo(outputStream);
            assertEquals(CONTENT_TEXT, outputStream.toString(StandardCharsets.UTF_8));
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream inputStream = FormatCodec.STREAM_TO_GZIP.decode(content)) {

            inputStream.transferTo(outputStream);
            assertEquals(CONTENT_TEXT, outputStream.toString(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void gzipSerialize() throws IOException {
        PasteContent content = FormatCodec.STREAM_TO_GZIP.encode(new ByteArrayInputStream(CONTENT_TEXT.getBytes(StandardCharsets.UTF_8)));
        String out = gson.toJson(content);
        assertEquals("{\"format\":\"gzip\",\"value\":\"H4sIAAAAAAAA//Nw9fHxVwj3D/JxAQBbhuWHCwAAAA\\u003d\\u003d\"}", out); // \u003d is =

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);
        assertNotEquals(CONTENT_TEXT, contentDeserialized.value());
        assertNotNull(contentDeserialized.value());

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream inputStream = FormatCodec.STREAM_TO_GZIP.decodeRaw(contentDeserialized.value())) {

            inputStream.transferTo(outputStream);
            assertEquals(CONTENT_TEXT, outputStream.toString(StandardCharsets.UTF_8));
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream inputStream = FormatCodec.STREAM_TO_GZIP.decode(contentDeserialized)) {

            inputStream.transferTo(outputStream);
            assertEquals(CONTENT_TEXT, outputStream.toString(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void getBase64Value() throws IOException {
        PasteContent content = FormatCodec.BYTES_TO_BASE_64.encode(CONTENT_TEXT.getBytes(StandardCharsets.UTF_8));
        assertNotEquals(CONTENT_TEXT, content.value());
        assertNotNull(content.value());

        assertEquals(CONTENT_TEXT, new String(FormatCodec.BYTES_TO_BASE_64.decodeRaw(content.value()), StandardCharsets.UTF_8));
        assertEquals(CONTENT_TEXT, new String(FormatCodec.BYTES_TO_BASE_64.decode(content), StandardCharsets.UTF_8));
    }

    @Test
    public void base64Serialize() {
        PasteContent content = FormatCodec.BYTES_TO_BASE_64.encode(CONTENT_TEXT.getBytes(StandardCharsets.UTF_8));
        String out = gson.toJson(content);
        assertEquals("{\"format\":\"base64\",\"value\":\"SEVMTE8gV09STEQ\\u003d\"}", out);

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);
        assertNotEquals(CONTENT_TEXT, contentDeserialized.value());
        assertNotNull(contentDeserialized.value());

        assertEquals(CONTENT_TEXT, new String(FormatCodec.BYTES_TO_BASE_64.decodeRaw(contentDeserialized.value()), StandardCharsets.UTF_8));
        assertEquals(CONTENT_TEXT, new String(FormatCodec.BYTES_TO_BASE_64.decode(contentDeserialized), StandardCharsets.UTF_8));
    }
}
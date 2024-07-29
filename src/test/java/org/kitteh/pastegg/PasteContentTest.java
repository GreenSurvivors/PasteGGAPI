package org.kitteh.pastegg;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PasteContentTest {
    private static Gson gson;

    @Before
    public void Setup() {
        gson = new Gson();
    }

    @Test
    public void getTextValue() {
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
    }

    @Test
    public void testText() {
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
        String out = gson.toJson(content);
        assertEquals("{\"format\":\"text\",\"value\":\"HELLO WORLD\"}", out);

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);
        assertEquals(contentDeserialized.getOriginalValue(), "HELLO WORLD");
    }

    @Test
    public void getGZIPValue() {
        PasteContent content = new PasteContent(PasteContentFormat.GZIP, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
    }

    @Test
    public void testGzip() {
        PasteContent content = new PasteContent(PasteContentFormat.GZIP, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
        String out = gson.toJson(content);
        assertEquals("{\"format\":\"gzip\",\"value\":\"H4sIAAAAAAAA__Nw9fHxVwj3D_JxAQBbhuWHCwAAAA\\u003d\\u003d\"}", out);

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);
        assertEquals(contentDeserialized.getOriginalValue(), "HELLO WORLD");
    }

    @Test
    public void getBase64Value() {
        PasteContent content = new PasteContent(PasteContentFormat.BASE64, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
    }

    @Test
    public void testBase64() {
        PasteContent content = new PasteContent(PasteContentFormat.BASE64, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
        String out = gson.toJson(content);
        assertEquals("{\"format\":\"base64\",\"value\":\"SEVMTE8gV09STEQ\\u003d\"}", out);

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);
        assertEquals(contentDeserialized.getOriginalValue(), "HELLO WORLD");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getXZValue() {
        PasteContent content = new PasteContent(PasteContentFormat.XZ, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testXZ() {
        PasteContent content = new PasteContent(PasteContentFormat.XZ, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
        String out = gson.toJson(content);
        //assertEquals("{\"format\":\"xz\",\"value\":\"SEVMTE8gV09STEQ\\u003d\"}", out);

        PasteContent contentDeserialized = gson.fromJson(out, PasteContent.class);
        assertEquals(contentDeserialized.getOriginalValue(), "HELLO WORLD");
    }
}
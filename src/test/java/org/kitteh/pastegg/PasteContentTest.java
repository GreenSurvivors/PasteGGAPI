package org.kitteh.pastegg;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Narimm on 28/02/2020.
 */
public class PasteContentTest {
    private static final @NotNull Gson GSON = new Gson();

    @Test
    public void getValue() {
        PasteContent content = new PasteContent(PasteContent.ContentType.TEXT, "HELLO WORLD");
        assertEquals(content.getValue(),"HELLO WORLD");
    }
    @Test
    public void getGZIPValue() {
        PasteContent content = new PasteContent(PasteContent.ContentType.GZIP, "HELLO WORLD");
        assertEquals(content.getValue(),"HELLO WORLD");
    }

    @Test
    public void testGzip() {
        PasteContent content = new PasteContent(PasteContent.ContentType.GZIP, "HELLO WORLD");
        assertEquals(content.getValue(),"HELLO WORLD");
        String out = GSON.toJson(content);
        assert(out.equals("{\"format\":\"gzip\",\"value\":\"H4sIAAAAAAAA__Nw9fHxVwj3D_JxAQBbhuWHCwAAAA\\u003d\\u003d\"}"));
    }

    @Test
    public void testBase64() {
        PasteContent content = new PasteContent(PasteContent.ContentType.BASE64, "HELLO WORLD");
        assertEquals(content.getValue(),"HELLO WORLD");
        String out = GSON.toJson(content);
        assert(out.equals("{\"format\":\"base64\",\"value\":\"SEVMTE8gV09STEQ\\u003d\"}"));
    }

    @Test
    public void getBase64Value() {
        PasteContent content = new PasteContent(PasteContent.ContentType.BASE64, "HELLO WORLD");
        assertEquals(content.getValue(),"HELLO WORLD");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getException() {
        new PasteContent(PasteContent.ContentType.XZ, "HELLO WORLD");
    }
}
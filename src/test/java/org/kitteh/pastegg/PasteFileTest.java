package org.kitteh.pastegg;

import org.junit.Before;
import org.junit.Test;
import org.kitteh.pastegg.client.FormatCodec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PasteFileTest {
    private PasteFile test;

    @Before
    public void Setup() {
        test = new PasteFile("Test", FormatCodec.TEXT_TO_TEXT.encode("HELLO WORLD"), HighlightLanguage.Java);
    }

    @Test
    public void getContent() {
        assertNotNull(test.content().value());
    }

    @Test
    public void getFormat() {
        assertEquals(PasteContentFormat.TEXT, test.content().format());
    }

    @Test
    public void getHighlightLanguage() {
        assertEquals(HighlightLanguage.Java, test.highlightLanguage());
    }

    @Test
    public void getName() {
        assertEquals("Test", test.name());
    }
}
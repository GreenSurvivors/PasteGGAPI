package org.kitteh.pastegg;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PasteFileTest {
    private PasteFile test;

    @Before
    public void Setup() {
        test = new PasteFile("1", "Test",
                new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD"));
    }

    @Test
    public void getContent() {
        assertEquals("HELLO WORLD", test.content().getValue());
    }

    @Test
    public void getId() {
        assertEquals("1", test.id());
    }

    @Test
    public void getName() {
        assertEquals("Test", test.name());
    }
}
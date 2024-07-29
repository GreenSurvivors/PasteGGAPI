package org.kitteh.pastegg;

import org.junit.Test;
import org.kitteh.pastegg.pasteresult.APasteResult;
import org.kitteh.pastegg.pasteresult.PasteResultError;
import org.kitteh.pastegg.pasteresult.PasteResultSuccess;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class PasteBuilderTest {

    @Test
    public void build() throws IOException {
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD");
        assertEquals("HELLO WORLD", content.getOriginalValue());
        ZonedDateTime time = ZonedDateTime.now().plusMinutes(1);
        APasteResult result = new PasteBuilder().name("TEST!").addFile(
                        new PasteFile("jkcclemens.txt",
                                new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD")))
                .visibility(Visibility.UNLISTED)
                .expires(time)
                .debug(true)
                .description("this is a test.")
                .build();
        assertSame(APasteResult.PasteStatus.SUCCESS, result.getStatus());
        assertTrue(result instanceof PasteResultSuccess);
        PasteResultSuccess pasteResultSuccess = (PasteResultSuccess) result;
        assertNotNull(pasteResultSuccess.getPaste());
        assertNull(pasteResultSuccess.getMessage());
        Paste paste = pasteResultSuccess.getPaste();
        assertNotNull(paste.getId());
        assertNotNull(paste.getExpires());
        assertEquals(Visibility.UNLISTED, paste.getVisibility());
        assertTrue(paste.getDeletionKey().isPresent());
        assertEquals("this is a test.", paste.getDescription());
        System.out.println("Successful paste status: " + result.getStatus());

        PasteResultError deletionResult = ConnectionProvider.deletePaste(paste.getId(), paste.getDeletionKey().get());

        if (deletionResult != null) {
            System.out.println("Error when trying to delete the paste: " + deletionResult.getError());

            if (deletionResult.getMessage() != null) {
                System.out.println("Error message: " + deletionResult.getMessage());
            }

            fail();
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        time = ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
        Date localDate = cal.getTime();
        PasteBuilder builder = new PasteBuilder().name("TEST!").addFile(
                        new PasteFile("jkcclemens.txt",
                                new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD")))
                .visibility(Visibility.UNLISTED)
                .expires(time);
        APasteResult result1 = builder.build();
        assertSame(APasteResult.PasteStatus.SUCCESS, result1.getStatus());
        assertTrue(result1 instanceof PasteResultSuccess);
        PasteResultSuccess pasteResultSuccess1 = (PasteResultSuccess) result1;
        assertNotNull(pasteResultSuccess1.getPaste());
        assertEquals(localDate.toString(), pasteResultSuccess1.getPaste().getExpires().toString());
        assertNotNull(pasteResultSuccess1.getPaste().getCreatedAt());
        assertNotNull(pasteResultSuccess1.getPaste().getUpdatedAt());
        assertEquals(pasteResultSuccess1.getPaste().getCreatedAt().toString(),
                pasteResultSuccess1.getPaste().getUpdatedAt().toString());
    }

    @Test
    public void buildWithAuthentication() throws IOException {
        String apiKey = System.getenv("PasteGGAPIKey");
        if ((apiKey == null) || !apiKey.isEmpty()) {
            Logger.getAnonymousLogger().info("No ApI Key given for testing");
            return;
        }
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "Hello World");
        APasteResult result = new PasteBuilder()
                .name("Test")
                .addFile(new PasteFile("Test.txt", content))
                .setApiKey(apiKey)
                .visibility(Visibility.PRIVATE)
                .debug(true)
                .description("this is a test.")
                .build();
        assertNotNull(result);
        assertSame(APasteResult.PasteStatus.SUCCESS, result.getStatus());
        assertTrue(result instanceof PasteResultSuccess);
        PasteResultSuccess pasteResultSuccess = (PasteResultSuccess) result;
        assertNotNull(pasteResultSuccess.getPaste());
        Paste paste = pasteResultSuccess.getPaste();
        assertEquals(Visibility.PRIVATE, paste.getVisibility());
        assertFalse(paste.getDeletionKey().isPresent());
        assertEquals("this is a test.", paste.getDescription());
        assertNotNull(paste.getId());
    }

    @Test(expected = InvalidPasteException.class)
    public void buildNullKeyPrivate() throws IOException {
        InvalidPasteException e = new InvalidPasteException("Some Message");
        assertEquals("Some Message", e.getMessage());
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "Hello World");
        new PasteBuilder()
                .name("Test")
                .addFile(new PasteFile("Test.txt", content))
                .setApiKey(null)
                .visibility(Visibility.PRIVATE)
                .build();
    }

    @Test
    public void buildWithBadKey() throws IOException {
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "Hello World");
        APasteResult result = new PasteBuilder()
                .name("Test")
                .addFile(new PasteFile("Test.txt", content))
                .setApiKey("someKey")
                .visibility(Visibility.PRIVATE)
                .build();
        assertSame(APasteResult.PasteStatus.ERROR, result.getStatus());
        assertTrue(result instanceof PasteResultError);

        PasteResultError pasteResultError = (PasteResultError) result;
        System.out.println("Successful got an error when trying to post a paste with a bad key: " + pasteResultError.getError());

        if (pasteResultError.getMessage() != null) {
            System.out.println("Error message: " + pasteResultError.getMessage());
        }
    }

    @Test
    public void testPasteResult() {
        PasteResultSuccess pasteResultSuccess = new PasteResultSuccess(APasteResult.PasteStatus.SUCCESS, null, null);
        assertNull(pasteResultSuccess.getMessage());
        assertNull(pasteResultSuccess.getPaste());
        assertSame(APasteResult.PasteStatus.SUCCESS, pasteResultSuccess.getStatus());
    }
}

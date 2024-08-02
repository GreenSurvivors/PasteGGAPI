package org.kitteh.pastegg;

import org.junit.Test;
import org.kitteh.pastegg.reply.ErrorReply;
import org.kitteh.pastegg.reply.ReplyStatus;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionProviderTest {

    @Test
    public void deleteInvalid() throws IOException {
        ErrorReply reply = ConnectionProvider.deletePaste("123", "invalid");

        assertNotNull(reply);
        assertEquals(ReplyStatus.ERROR, reply.status());
    }
}

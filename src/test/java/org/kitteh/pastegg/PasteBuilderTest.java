package org.kitteh.pastegg;

import org.junit.Test;
import org.kitteh.pastegg.client.FormatCodec;
import org.kitteh.pastegg.reply.ErrorReply;
import org.kitteh.pastegg.reply.IReply;
import org.kitteh.pastegg.reply.ReplyStatus;
import org.kitteh.pastegg.reply.SuccessReply;
import org.kitteh.pastegg.reply.content.PasteFileReply;
import org.kitteh.pastegg.reply.content.PasteResult;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class PasteBuilderTest {

    @Test
    public void buildContent() throws IOException {
        PasteContent content = FormatCodec.TEXT_TO_TEXT.encode("HELLO WORLD");
        assertEquals("HELLO WORLD", FormatCodec.TEXT_TO_TEXT.decode(content));
        ZonedDateTime time = ZonedDateTime.now().plusMinutes(1);
        IReply reply = new PasteBuilder().name("TEST!").addFile(
                        new PasteFile("jkcclemens.txt",
                                FormatCodec.TEXT_TO_TEXT.encode("{\"HELLO\": \"WORLD\"}"),
                                HighlightLanguage.Json))
                .visibility(Visibility.UNLISTED)
                .expires(time)
                .debug(true)
                .description("this is a test.")
                .build();
        assertSame(ReplyStatus.SUCCESS, reply.status());
        assertTrue(reply instanceof SuccessReply);
        SuccessReply successReplySingle = (SuccessReply) reply;
        assertNotNull(successReplySingle.result());
        PasteResult result = successReplySingle.result();
        assertNotNull(result.id());
        assertNotNull(result.expires());
        assertEquals(Visibility.UNLISTED, result.visibility());
        assertNotNull(result.deletionKey());
        assertEquals("this is a test.", result.description());
        assertEquals(1, result.files().length);
        assertEquals(HighlightLanguage.Json, result.files()[0].highlightLanguage());
        System.out.println("Successful paste status: " + reply.status());

        ErrorReply deletionResult = ConnectionProvider.deletePaste(result.id(), result.deletionKey());

        if (deletionResult != null) {
            System.out.println("Error when trying to delete the paste: " + deletionResult.error());

            if (deletionResult.message() != null) {
                System.out.println("Error message: " + deletionResult.message());
            }

            fail();
        }
    }

    @Test
    public void buildFormat() throws IOException {
        ZonedDateTime time = ZonedDateTime.now().plusMinutes(1);
        IReply reply = new PasteBuilder().name("TEST!").
                addFile(new PasteFile("cute_cow.png",
                        new PasteContent(PasteContentFormat.BASE64,
                                "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAA7EAAAOxAGVKw4bAAAQTUlEQVRo3uVaaXRUZbbd57u3qpJUUkkqJCEkjEkgxIAkQJgcEGwbB2xwaByeot2+VhpaVNqpe6k82+nZLhWcZ5/6HLp5rxVwABVBcEBBJiHEkACGzCSpJFWpVNW9334/qipBtH1q8FdXVta669a3bp1pn7P3qQL+1V6G4ZirlLEOQOKxeqaIOAzDfEApo/hnNV6JUTIsNzc4rqiQgIw6Zg4oNbI4v4AD0tKalGHk/WwOmIbzvVt/d6leeMnFWhnGI//UUaVON5TxrlJGpoi6TERc3xsYQz265LeX6SXzL9SmMlf8LMaLSNHYUaP043+6mv946lFOLi3VyjDuFRH1rTIT4/XJhTl0mI7DM6eN10oZD3x3ORqJhmE8dVL5eP3Wc4/zqVuu00MH5UQA8fzgoP7QgySmjB6eB2jCd/gQfn/JBbD/y75+667do0S4hmQZIGUOQxVkJCe489Md8DgHZFww5xdMzRq0ePeeykX19fWNfn/31wB3A7JFibFgzswTx805fSa7Og5D2zbyB+eYB+sb8wFsO6YOAEhwmQqaNj/6bBs2bN0NO+znuDzPbBE1OyvFhRQn4DIMCACtbWa6DRyq2oNXX1oGoTY0VG5DU3Purl2VU7Zu3XVFsicZK/62mk+++gZmji/mgGQ3RIQiEPIHVsZ33EtXSp0mkHFaI4+gQ4QNIuKaWFJ01WFfh0yYOA5XXTqXNbu2ywcrXwc0CUDY96kUEUn1ZmD+kj+y7ITJQgJKKYIiEAMgqbUWTY31H27mjTffI0Mz0/nlvgNyoK7+Tm1zEKG9AAJKqQMktxN4H2TbdzpgKMMBwe0el1xdmGEmJScqOgARatqktASIysOaZ589S26+Zj6CnR189bFHpbWxCSSPjhqVUjJ64iQsvusuuhwiAAEoRuEkIONOK0QiFusONcqpZ1zEjtZmGTPQhNspJCHUGhEt7OyxZV+b3R0Iy/2a/A+tbeubDhjmI8dlORZkuYHmLhu2RvQzqUESCU6F1ETBIb9CxpAijEvX8PtaQcSs5rdAD1MJLrp2CWbMng1Cf1djgMBEVU0tps84ByOSw0hxhNHYaSNsR00TmwAIl0OQ7TFQHwD2NFmPWra1qA8DIg6nwm+0Danv0AiGNXw9oAVTDGUwHLEk0SRSg2TQ0tK1rwrDCtJpgEKJRVxE+gAfiy4EbY0NFDEFiIACCqNBi5+BEEOGZLOkpFgqtm6mU2np7NHo0QadpikRy0KCQXoJOdSuYRqKDoXLLRt/AEAzFj0xBCBt9FgCpycbLyyahRMKc5GggNZACM9u+AL/vXEHOjv8mFGQBlMIEoAmREnccAgkmlchIBp5BfkQ0YDIN1paX9o0XA4Dr73yKE6cfh4SwgHcdvY0nFVWBDcIXzCCt7+sxo0vvIV0FQK1jlksABnvQgx3W/qlrrD81mVqBAIBfNXQgsLsNKYmOrB66242tLSivbMbJxV4aYqO20CJlTfjdwQQIQRkRnYexp1wIjRtUhMQgfQGHzHcEyKCNE8SV7z6GGbN+jV37K2GAnFm6Uj6QkE0+jphCmiIhj+kENZ8BrGHSB9NUB5lGOuzElmaYgKdYTJoiWiCGpRgBJiU72V+ulO0Zm8JxCqHIhJFnQBKke6kZFl83zLkjy5kLCcxsMfPCXHEfaWUAOCDD78oN978F7hNRYk9P9EJehxKukIaTSFZR9pn2pbd8602qpTyCOQJtynzUl1AoglQBCGL0O4MPP6fN6D85Gm4a9EiHK6rB/sgjDgERBFutxsL774HxWOOP+LEdwA42o0Qz4KIwLKJKSefj/aaPXAYhAIQtIDWoNbdFh6h8AbaOvR9cwCi1CRQrjCFJ7pMGRHWcDzz9DJccN4vCVDWv/kmXnngQUYilmitwRiIRQjTobDwrvs5rrw0htdo1I/MQB8AKCSOzAZFlLy9diPOP+8KmgpWj6WrbOAdAE9qrSt/yCA7OlI5Bfn5tTu3rTVMZfeWg6+tjbu3bJGaigq0NzdTlEhmziAcVz4BJRPKo+XVh9QjjI5eixjUoICxAMQdUEosrVA64YzQ3r2VeZr6cH+pxFkXXTRXGYaiUMcjisTkFGyubOSll/4GA7yeKA5inSgOUKi+zoq+uwQEhMLeyhp+tmUH5l80G6SONgSQhtI4//wznX+546uzQDzfTxZqvPjJprcY7q5hJFClrUAVIz3VfOyRezSguPTWm2gFq7UVrGGk+0D8X/deBw/qSPAgI8Gjrrur9axZM6iUwf1Vn+hwdxWt2PPtYDU3bXhdixgv9pvMuVyu0rElowDaAHR89qClpRUA0eXvhGUTO3btwY4de1Gz/xAOHDyE9tY2hCN2jAW6kO5Nw+DBAzF0cB7Gji1CaelotLd1gCQ6uwLI5YC+MaE1xo0ZBdNQxRHL7p8DI0YMzXE6DQoIagFEUQiUlY0FAK5Zux5/+/tqFI0ezXFjj0PBwGxMHz0KaSkJdCc4AQrCEYttgRBqm9tQc6iFq97agD17diMQ6GZWViaGDx8WHwmIkSa4XA4MHpw3pGb//n45oJKcjmTbssU0FURAQMnLf1+Npbc/yPPOnSOXzJqOk4ry6PI1iPL7IDoCoJnogSBEgCpqlwiQrclsj3DyL2GlXc7NNXXy0rpPMHHKGbx96fVyzq9OJWiLALAtzVSP293fElIUbbQ2NSArLw+RcAi/X7wUX1fXYsUd16LI0QMV8kFqO2Lg7evpvbQiOqbjkx8CDQm0w9Xtw0kuYtrssdh76gRce/+TWLt2PR5edhtMA2isr4Nt2/0WNFZ3sCfQ2dqU4vF6seSme6Aa6vjGwjPg0K1AD3rneTz1iBn/Db4m0fdiwyr+ZpQEWhEc54hg1dWzueSFd/C7BX/mX++4Br6WZnT5A4F+g/hQXVO91ix6e/UabNuwkeuWXi6mHQYZpQ8iCqt3VnNjxdey6LRyDPUmQ9t2NA8S50tKAIGArGn3y/Prt2FUzgBeWD5K4hPNJZTll83iaXe+KCv/dw3Ky0azoaHlYL8d8PsDO+vrDhe9vup9XHby8XBSQx/FDtwJiVj+xkY8tGoTZo4bhdeXzINitGNFkRllrP/22BtYsXE7tCaeWHAu4lORjDFZEnMmFeOddZ8i3ZuMUNj6/BhoYnl3wydfnN/Y2IzBI0ZCExRInOOQJE4amYfpYwv4wa59qKhtAg0DtGIMFL3QQMXBJmhqTi3Ox3mTSwDqPmJN0NIauZlpbKmow4ebtoOw1/0UTXz0kbRRBUNrS4sLk2fmpnLK6CFS6E0Fo3mIykOAtqZsrm5Gfo4X2UkOasYkFSRWaoLm7h5WNfpkUn4uHNBktC/30qSD7Z1cu/ugfNoewYebtvgONTTmgQz0MwP0VdXULi87vuRPr378JYYNTANJjMxIO4LhEIYIphUOBKChtcYRAi1GiohsdyKyC9xAlJKgd6VE4uv2LtT5uvDaRztRUFqG+samZf+f8T8wA4CIJLiTkj477dTpJaG6A3LVaeOR4UliQVaGpCW4eiN4tKSMNaYjGKgcoSGEokQ6gj3Y39LOho4uWb76U2YeVyYrV66u6A50jyd18Jg4ENUKxvDklJQPJ0+akFu160ucWz4S08eMgCfRhezUZHiTk+BxuSAkGAMw+qhnb8QpAn84BF8ghKbOADp7QnhvexVWfLYX5VOnYe176+o6OjpO1lrX/NS90Pdsps0hAD+eOm1KrtPhYsWOHTKxIBvj83M5ZnC2uE1BUkICEpxOOoSiDAMGSMumhG2NMDR7LEtC4Qh27G/k5qo6+bymCeXTptDWWt58ay21bRVrrff+YLL5Y9lpZmZm9ZLrFoxY/tDTzM/Pl7S0NGitWVmxV8JdPuQNSEFmipspiU5xOUxAhKGwJYGeEFq6gqxv65KE1AwUFo2iy+WQUE8PPv98Cy+++Dz5clcF31n7wfGk3vVzOWCWlJQEv9i8yugK+LF4/kJ8vGUHWgJhjD5uDIYNGwbTNOFKcME0TQCCSCQCrW0opWBZNiwrgqqqfaj9+iAmTyrD3Dmn4+yzTkFKSjL+eNPdeOjhZ2eReu0xX+7GXtmZA7ymiEaKO4Fz554mPFwNUBgI1cnhnQcQjBAF5VMZjNgSsS3YWtPpdEqqx428QRkcMiRPFi/4NYqLCuhMcIlhRjcVIiI5A7NIMutHRfRHypvMVE9yr8CaPOMUrHzhedjBINwmkexwYNqZs3HhokWxrVsvihHVyyYcphGdaiSitIgAou001ZMCgAN/PgdEkpwuJxUVKDayc7J4x7PP4d0VK9BQU8WMnIGYt+BKJLkTIGLyiEKlQMc7VO8KKcanADHA6N4UIiol2sV+BgcEhG1Z0tHeBk9GGpWIDMrLwfxrFhNCIXUsOYpaRyQStuFwOdnW0iwDsrKja7wjdklxHquh2dnaKsFgkAD9xzwDSqkEAH82Tcf85uZmNNYeREp6WjTzIiA0JPZHUSAU6g7sR0Z2DhCx0NZ4GBmZ2eiVBzHN0DsrtEZTfT0a6uqQ7vXe3eHzXQDgNtu2V/fbARExlDLevOH6RadcMG825s27khSBZVkQ0+j7QiC2rQAEoZ4edPsDTB9gg7aGpsVgtx/upMSYvOnTAyKCiG2BVoT1hxrxyEN3qZH5w0qvuPL6N3bu2nOZbVsv9peNnn/BvHNmLL3lGlBH2O7rlEhEo6e7mw5PynesDIG9ldV0aC3NdbUQwyCpZfv2CkydUsr4Ei+uJwBBMBAmSdm+u5I33DxCiovysfJ/nsHosdPv8wfsVwBa/XHglHN+dSqVRBX31CkT+cXOSqR505HiSelVWn0bWwtvr1mPHG8qp00cAw2NQCDEh594DVOnjge1HV3Y9e6EBZ3tPrT7unm4tQPFRQUUJRiYMwDlE8sy163fVAxg5092gFDpEvZLW0sjtu+u4f4Dh2Ttex9j0riR9PlSJc2bjs4OP1OSk2ImEYZp8t7lz8nIwqHQWrOhoUWGDh8WLx/RBHwdfnrT08Xf5YO/vZXrP94ilm3zgeXPyqKFl6K7q42AFgBG/zAA/dXuiv0Idgdx/c33oiTbjXferca/z58L4gAsK4KnnvsHzjx9BsaOKQAhSPMkY9qUUpx7xnSQgn0H67Dhk20xewQbN23Btm0VuOySOWiqrYWmjVdWvIXjsxLwzEMP4bMtX+DmP1yIHbsq2iHYA/ZPDzy6/ImXF44YlpdWkp0Eb6KBrGQHbly6DMvuug4rX74Pa9Zswvur38aMsnwEAgFs3VaBxKFFGDEsFySxfU8l9n6+GbdceSWSU9Ow6sPtSAoHMNCMYMyUCXj6xdfR3tSE4wszkJWajnfWrEO33492X+edIEPHQA+oE0RkxeC0xKyMZCdCWmFPXRu8HjcKvQlwuwx8ur8NvxidBQERtBTWVTZGcgYNtBSB+sam7uLslIzhGYkADKzZ04CTR2bDMA3IwHysWrMOQ7xuDEhJRMiyUdPst8Na32PZ1q3/dD//Y8mciCQAmE6gEEBAIB+J4BRSZgqYS8IVHUtCAm0ieFhre2VsjgwH8VeIDCVpxL/UFEGAwD4IXgPgosYIEbaTXAfg63+JX8/8H3MbqPPdn6VgAAAAAElFTkSuQmCC"),
                        null))
                .visibility(Visibility.UNLISTED)
                .expires(time)
                .debug(true)
                .build();
        assertSame(ReplyStatus.SUCCESS, reply.status());
        assertTrue(reply instanceof SuccessReply);
        SuccessReply successReplySingle = (SuccessReply) reply;
        assertNotNull(successReplySingle.result());
        PasteResult result = successReplySingle.result();
        assertNotNull(result.id());
        assertNotNull(result.expires());
        assertEquals(Visibility.UNLISTED, result.visibility());
        assertNotNull(result.deletionKey());
        assertEquals(1, result.files().length);
        assertNull(result.files()[0].highlightLanguage());
        System.out.println("Successful paste status: " + reply.status());

        ErrorReply deletionResult = ConnectionProvider.deletePaste(result.id(), result.deletionKey());

        if (deletionResult != null) {
            System.out.println("Error when trying to delete the paste: " + deletionResult.error());

            if (deletionResult.message() != null) {
                System.out.println("Error message: " + deletionResult.message());
            }

            fail();
        }
    }

    @Test
    public void base64Png() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/Cute-Cow-icon.png")) {
            PasteContent content = FormatCodec.BYTES_TO_BASE_64.encode(in.readAllBytes());
            ZonedDateTime time = ZonedDateTime.now().plusMinutes(1);
            IReply reply = new PasteBuilder().name("TEST!").
                    addFile(new PasteFile("cute_cow.png", content,
                            null))
                    .visibility(Visibility.UNLISTED)
                    .expires(time)
                    .debug(true)
                    .build();

            assertSame(ReplyStatus.SUCCESS, reply.status());
            assertTrue(reply instanceof SuccessReply);
            SuccessReply successReplySingle = (SuccessReply) reply;
            assertNotNull(successReplySingle.result());
        }
    }

    @Test
    public void buildTime() throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        ZonedDateTime time = ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
        Date localDate = cal.getTime();
        PasteBuilder builder = new PasteBuilder().name("TEST!").addFile(
                        new PasteFile("jkcclemens.txt",
                                new PasteContent(PasteContentFormat.TEXT, "HELLO WORLD")))
                .visibility(Visibility.UNLISTED)
                .expires(time);
        IReply reply = builder.build();
        assertSame(ReplyStatus.SUCCESS, reply.status());
        assertTrue(reply instanceof SuccessReply);
        SuccessReply successReply1 = (SuccessReply) reply;
        assertNotNull(successReply1.result());
        assertEquals(localDate.toString(), successReply1.result().expires().toString());
        assertNotNull(successReply1.result().createdAt());
        assertNotNull(successReply1.result().updatedAt());
        assertEquals(successReply1.result().createdAt().toString(), successReply1.result().updatedAt().toString());
    }

    @Test
    public void buildWithAuthentication() throws IOException {
        String apiKey = System.getenv("PasteGGAPIKey");
        if ((apiKey == null) || !apiKey.isEmpty()) {
            Logger.getAnonymousLogger().info("No ApI Key given for testing");
            return;
        }
        PasteContent content = new PasteContent(PasteContentFormat.TEXT, "Hello World");
        IReply reply = new PasteBuilder()
                .name("Test")
                .addFile(new PasteFile("Test.txt", content))
                .setApiKey(apiKey)
                .visibility(Visibility.PRIVATE)
                .debug(true)
                .description("this is a test.")
                .build();
        assertNotNull(reply);
        assertSame(ReplyStatus.SUCCESS, reply.status());
        assertTrue(reply instanceof SuccessReply);
        SuccessReply successReplySingle = (SuccessReply) reply;
        assertNotNull(successReplySingle.result());
        PasteResult result = successReplySingle.result();
        assertEquals(Visibility.PRIVATE, result.visibility());
        assertNull(result.deletionKey());
        assertEquals("this is a test.", result.description());
        assertNotNull(result.id());
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
        IReply reply = new PasteBuilder()
                .name("Test")
                .addFile(new PasteFile("Test.txt", content))
                .setApiKey("someKey")
                .visibility(Visibility.PRIVATE)
                .build();
        assertSame(ReplyStatus.ERROR, reply.status());
        assertTrue(reply instanceof ErrorReply);

        ErrorReply errorReply = (ErrorReply) reply;
        System.out.println("Successful got an error when trying to post a paste with a bad key: " + errorReply.error());

        if (errorReply.message() != null) {
            System.out.println("Error message: " + errorReply.message());
        }
    }

    @Test(expected = InvalidPasteException.class)
    public void buildInvalidNoFile() throws IOException {
        new PasteBuilder().name("TEST!")
                .visibility(Visibility.UNLISTED)
                .debug(true)
                .description("this is a test.")
                .build();
    }

    @Test
    public void testPasteResult() {
        SuccessReply successReply = new SuccessReply(ReplyStatus.SUCCESS, new PasteResult("1", null, Visibility.PRIVATE, new Date(), new Date(), null, new PasteFileReply[]{}, null));
        assertNotNull(successReply.result());
        assertEquals("1", successReply.result().id());
        assertNull(successReply.result().description());
        assertEquals(Visibility.PRIVATE, successReply.result().visibility());
        assertNull(successReply.result().expires());
        assertEquals(0, successReply.result().files().length);
        assertNull(successReply.result().deletionKey());
        assertSame(ReplyStatus.SUCCESS, successReply.status());

        ErrorReply errorReply = new ErrorReply(ReplyStatus.ERROR, "error", null);
        assertNull(errorReply.message());
        assertEquals("error", errorReply.error());
        assertSame(ReplyStatus.ERROR, errorReply.status());
    }
}

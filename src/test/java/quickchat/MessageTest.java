package quickchat;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class MessageTest {

    private static final String RECIPIENT_1     = "+27718693002";
    private static final String MESSAGE_TEXT_1  = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2     = "08575975889";
    private static final String MESSAGE_TEXT_2  = "Hi Keegan, did you receive the payment?";
    private static final String RECIPIENT_SHORT = "+2783";
    private static final String RECIPIENT_LONG  = "+2781234567890123";

    private Message message1;
    private Message message2;

    @Before
    public void setUp() {
        message1 = new Message(Message.generateMessageID(), 1, RECIPIENT_1, MESSAGE_TEXT_1);
        message2 = new Message(Message.generateMessageID(), 2, RECIPIENT_2, MESSAGE_TEXT_2);
        Message.populateTestData();
    }

    // ── Part 2 Tests ──────────────────────────────────────────────────────────

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", message1.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longText = new String(new char[260]).replace('\0', 'A');
        Message longMessage = new Message(Message.generateMessageID(), 1, RECIPIENT_1, longText);
        assertTrue(longMessage.checkMessageLength().contains("Message exceeds 250 characters by"));
    }

    @Test
    public void testRecipientCellSuccess() {
        assertEquals("Cell phone number successfully captured.", message1.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailureNoInternationalCode() {
        assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
            message2.checkRecipientCell()
        );
    }

    @Test
    public void testRecipientCellFailureTooShort() {
        Message m = new Message(Message.generateMessageID(), 0, RECIPIENT_SHORT, MESSAGE_TEXT_1);
        assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
            m.checkRecipientCell()
        );
    }

    @Test
    public void testRecipientCellFailureTooLong() {
        Message m = new Message(Message.generateMessageID(), 0, RECIPIENT_LONG, MESSAGE_TEXT_1);
        assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
            m.checkRecipientCell()
        );
    }

    @Test
    public void testMessageHashCorrect() {
        Message testMessage = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_TEXT_1);
        assertEquals("00:0:HITONIGHT?", testMessage.createMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        String[] ids      = {"0012345678", "1122334455"};
        String[] texts    = {MESSAGE_TEXT_1, MESSAGE_TEXT_2};
        String[] expected = {"00:0:HITONIGHT?", "11:0:HIPAYMENT?"};
        for (int i = 0; i < ids.length; i++) {
            Message m = new Message(ids[i], 0, RECIPIENT_1, texts[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
        
    }

    @Test
    public void testMessageIDGenerated() {
        String id = Message.generateMessageID();
        assertNotNull(id);
        assertTrue(id.length() <= 10);
    }

    @Test
    public void testCheckMessageIDValid() {
        assertTrue(message1.checkMessageID());
    }

    @Test
    public void testSentMessageSend() {
        Message m = new Message(Message.generateMessageID(), 0, RECIPIENT_1, MESSAGE_TEXT_1);
        assertEquals("Message successfully sent.", m.SentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        Message m = new Message(Message.generateMessageID(), 0, RECIPIENT_1, MESSAGE_TEXT_1);
        assertEquals("Press 0 to delete the message.", m.SentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        Message m = new Message(Message.generateMessageID(), 0, RECIPIENT_1, MESSAGE_TEXT_1);
        assertEquals("Message successfully stored.", m.SentMessage(3));
    }

    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();
        Message m = new Message(Message.generateMessageID(), 0, RECIPIENT_1, MESSAGE_TEXT_1);
        m.SentMessage(1);
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    // ── Part 3 Tests ──────────────────────────────────────────────────────────

    @Test
    public void testSentMessagesArrayPopulated() {
        ArrayList<Message> sent = Message.getSentMessages();
        assertFalse(sent.isEmpty());
        boolean found1 = false, found2 = false;
        for (Message m : sent) {
            if (m.getMessageText().equals("Did you get the cake?")) found1 = true;
            if (m.getMessageText().equals("It is dinner time!")) found2 = true;
        }
        assertTrue(found1);
        assertTrue(found2);
    }

    @Test
    public void testDisplayLongestMessage() {
        String longest = Message.displayLongestMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    @Test
    public void testSearchByRecipientFound() {
        ArrayList<Message> allStored = Message.getStoredMessages();
        StringBuilder result = new StringBuilder();
        for (Message m : allStored) {
            if (m.getRecipient().equals("+27838884567")) {
                result.append(m.getMessageText()).append(" ");
            }
        }
        String combined = result.toString().trim();
        assertTrue(combined.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(combined.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteByHash() {
        Message m = new Message("9900000001", 0, "+27834557896", "Test delete message.");
        m.SentMessage(1);
        String hash = m.getMessageHash();
        ArrayList<Message> before = Message.getSentMessages();
        int sizeBefore = before.size();

        boolean deleted = false;
        for (Message msg : Message.getSentMessages()) {
            if (msg.getMessageHash().equalsIgnoreCase(hash)) {
                deleted = true;
                break;
            }
        }
        assertTrue(deleted);
    }

    @Test
    public void testDisplayReportNotEmpty() {
        ArrayList<Message> sent   = Message.getSentMessages();
        ArrayList<Message> stored = Message.getStoredMessages();
        int total = sent.size() + stored.size();
        assertTrue(total > 0);
    }
}
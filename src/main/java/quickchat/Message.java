/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package quickchat;

/**
 *
 * @author Ntatiso
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Message {

    private String messageID;
    private int    messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String flag; // "Sent", "Stored", "Disregarded"

    private static ArrayList<Message> sentMessages        = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages      = new ArrayList<>();
    private static ArrayList<String>  messageHashes       = new ArrayList<>();
    private static ArrayList<String>  messageIDs          = new ArrayList<>();
    private static int totalMessagesSent = 0;

    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID     = messageID;
        this.messageNumber = messageNumber;
        this.recipient     = recipient;
        this.messageText   = messageText;
        this.messageHash   = createMessageHash();
        this.flag          = "";
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient == null) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        boolean hasInternationalCode = recipient.startsWith("+");
        boolean correctLength = recipient.length() == 12;
        String digitsOnly = recipient.substring(1);
        boolean onlyDigits = digitsOnly.matches("\\d+");
        if (hasInternationalCode && correctLength && onlyDigits) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String createMessageHash() {
        if (messageID == null || messageText == null || messageText.trim().isEmpty()) {
            return "";
        }
        String idPart    = messageID.substring(0, Math.min(2, messageID.length()));
        String[] words   = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord  = words[words.length - 1];
        String hash      = idPart + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    public String checkMessageLength() {
        if (messageText == null || messageText.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = messageText.length() - 250;
            return "Message exceeds 250 characters by " + excess + " characters; please reduce the size.";
        }
    }

    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                this.messageNumber = totalMessagesSent;
                this.messageHash   = createMessageHash();
                this.flag          = "Sent";
                sentMessages.add(this);
                messageHashes.add(this.messageHash);
                messageIDs.add(this.messageID);
                return "Message successfully sent.";
            case 2:
                this.flag = "Disregarded";
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3:
                this.flag = "Stored";
                storedMessages.add(this);
                messageHashes.add(this.messageHash);
                messageIDs.add(this.messageID);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice. Please select 1, 2, or 3.";
        }
    }

    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n===== Sent Messages =====\n");
        for (Message m : sentMessages) {
            sb.append("Message ID   : ").append(m.messageID).append("\n");
            sb.append("Message Hash : ").append(m.messageHash).append("\n");
            sb.append("Recipient    : ").append(m.recipient).append("\n");
            sb.append("Message      : ").append(m.messageText).append("\n");
            sb.append("-------------------------\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void storeMessage() {
        String jsonEntry = "{\n"
                + "  \"messageID\": \""   + messageID   + "\",\n"
                + "  \"messageHash\": \"" + messageHash + "\",\n"
                + "  \"recipient\": \""   + recipient   + "\",\n"
                + "  \"message\": \""     + messageText + "\",\n"
                + "  \"status\": \"stored\"\n"
                + "}\n";
        try (FileWriter file = new FileWriter("messages.json", true)) {
            file.write(jsonEntry);
            System.out.println("Message saved to messages.json");
        } catch (IOException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    // ── Part 3 Methods ────────────────────────────────────────────────────────

    public static void displayStoredMessagesMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages Menu ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display longest stored message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search all messages for a particular recipient");
            System.out.println("e) Delete a message using message hash");
            System.out.println("f) Display full report of all messages");
            System.out.println("g) Back to main menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    displaySenderAndRecipient();
                    break;
                case "b":
                    displayLongestMessage();
                    break;
                case "c":
                    searchByMessageID(scanner);
                    break;
                case "d":
                    searchByRecipient(scanner);
                    break;
                case "e":
                    deleteByHash(scanner);
                    break;
                case "f":
                    displayReport();
                    break;
                case "g":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please choose a-g.");
            }
        }
    }

    public static void displaySenderAndRecipient() {
        ArrayList<Message> allMessages = getAllMessages();
        if (allMessages.isEmpty()) {
            System.out.println("No messages found.");
            return;
        }
        System.out.println("\n===== Sender and Recipient of All Messages =====");
        for (Message m : allMessages) {
            System.out.println("Recipient : " + m.recipient);
            System.out.println("Message   : " + m.messageText);
            System.out.println("Flag      : " + m.flag);
            System.out.println("-------------------------");
        }
    }

    public static String displayLongestMessage() {
        ArrayList<Message> allMessages = getAllMessages();
        if (allMessages.isEmpty()) {
            return "No messages found.";
        }
        Message longest = allMessages.get(0);
        for (Message m : allMessages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        System.out.println("\nLongest Message: " + longest.messageText);
        return longest.messageText;
    }

    public static String searchByMessageID(Scanner scanner) {
        System.out.print("Enter Message ID to search: ");
        String searchID = scanner.nextLine().trim();
        ArrayList<Message> allMessages = getAllMessages();
        for (Message m : allMessages) {
            if (m.messageID.equals(searchID)) {
                System.out.println("Recipient : " + m.recipient);
                System.out.println("Message   : " + m.messageText);
                return m.messageText;
            }
        }
        System.out.println("Message ID not found.");
        return "Message ID not found.";
    }

    public static String searchByRecipient(Scanner scanner) {
        System.out.print("Enter recipient number to search: ");
        String searchRecipient = scanner.nextLine().trim();
        ArrayList<Message> allMessages = getAllMessages();
        StringBuilder result = new StringBuilder();
        boolean found = false;
        for (Message m : allMessages) {
            if (m.recipient.equals(searchRecipient)) {
                System.out.println("Message : " + m.messageText);
                result.append(m.messageText).append(" ");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No messages found for recipient: " + searchRecipient);
            return "No messages found.";
        }
        return result.toString().trim();
    }

    public static String deleteByHash(Scanner scanner) {
        System.out.print("Enter Message Hash to delete: ");
        String searchHash = scanner.nextLine().trim();
        ArrayList<Message> allMessages = getAllMessages();
        for (Message m : allMessages) {
            if (m.messageHash.equalsIgnoreCase(searchHash)) {
                String deletedText = m.messageText;
                sentMessages.remove(m);
                storedMessages.remove(m);
                disregardedMessages.remove(m);
                messageHashes.remove(m.messageHash);
                messageIDs.remove(m.messageID);
                String result = "Message: \"" + deletedText + "\" successfully deleted.";
                System.out.println(result);
                return result;
            }
        }
        System.out.println("Hash not found.");
        return "Hash not found.";
    }

    public static void displayReport() {
        ArrayList<Message> allMessages = getAllMessages();
        if (allMessages.isEmpty()) {
            System.out.println("No messages to display.");
            return;
        }
        System.out.println("\n===== Full Message Report =====");
        for (Message m : allMessages) {
            System.out.println("Message Hash : " + m.messageHash);
            System.out.println("Recipient    : " + m.recipient);
            System.out.println("Message      : " + m.messageText);
            System.out.println("Flag         : " + m.flag);
            System.out.println("-------------------------");
        }
    }

    private static ArrayList<Message> getAllMessages() {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);
        return all;
    }

    public static void populateTestData() {
        String id1 = generateMessageID();
        Message m1 = new Message(id1, 0, "+27834557896", "Did you get the cake?");
        m1.SentMessage(1);

        String id2 = generateMessageID();
        Message m2 = new Message(id2, 0, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        m2.SentMessage(3);

        String id3 = generateMessageID();
        Message m3 = new Message(id3, 0, "+27834484567", "Yohoooo, I am at your gate.");
        m3.SentMessage(2);

        String id4 = generateMessageID();
        Message m4 = new Message(id4, 0, "0838884567", "It is dinner time!");
        m4.SentMessage(1);

        String id5 = generateMessageID();
        Message m5 = new Message(id5, 0, "+27838884567", "Ok, I am leaving without you.");
        m5.SentMessage(3);
    }

    public static String generateMessageID() {
        Random random = new Random();
        long id = (long)(random.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(id);
    }

    public static void sendMessageFlow(Scanner scanner) {
        System.out.println("\n--- Send a New Message ---");
        String msgID = generateMessageID();
        System.out.println("Message ID generated: " + msgID);

        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (with international code e.g. +27821234567): ");
            recipient = scanner.nextLine().trim();
            Message temp = new Message(msgID, 0, recipient, "test");
            String cellCheck = temp.checkRecipientCell();
            System.out.println(cellCheck);
            if (cellCheck.equals("Cell phone number successfully captured.")) break;
        }

        String messageText;
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            messageText = scanner.nextLine().trim();
            Message temp = new Message(msgID, 0, recipient, messageText);
            String lengthCheck = temp.checkMessageLength();
            System.out.println(lengthCheck);
            if (lengthCheck.equals("Message ready to send.")) break;
        }

        Message message = new Message(msgID, totalMessagesSent, recipient, messageText);
        System.out.println("\nMessage Details:");
        System.out.println("  Message ID   : " + message.messageID);
        System.out.println("  Message Hash : " + message.messageHash);
        System.out.println("  Recipient    : " + message.recipient);
        System.out.println("  Message      : " + message.messageText);

        System.out.println("\nWhat would you like to do?");
        System.out.println("  1) Send Message");
        System.out.println("  2) Disregard Message");
        System.out.println("  3) Store Message to send later");
        System.out.print("Choose an option: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Message discarded.");
            return;
        }

        String result = message.SentMessage(choice);
        System.out.println(result);
    }

    public String getMessageID()     { return messageID; }
    public String getRecipient()     { return recipient; }
    public String getMessageText()   { return messageText; }
    public String getMessageHash()   { return messageHash; }
    public int    getMessageNumber() { return messageNumber; }
    public String getFlag()          { return flag; }
    public static ArrayList<Message> getSentMessages()        { return sentMessages; }
    public static ArrayList<Message> getStoredMessages()      { return storedMessages; }
    public static ArrayList<Message> getDisregardedMessages() { return disregardedMessages; }
}
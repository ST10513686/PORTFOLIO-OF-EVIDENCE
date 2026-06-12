/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package quickchat;

/**
 *
 * @author Ntatiso
 */
 
import java.util.Scanner;

public class QuickChat {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean loggedIn = Login.promptLogin(scanner);

        if (!loggedIn) {
            System.out.println("Login failed. Exiting QuickChat.");
            scanner.close();
            return;
        }

        int numMessages = 0;
        while (true) {
            System.out.print("How many messages would you like to send this session? ");
            try {
                numMessages = Integer.parseInt(scanner.nextLine().trim());
                if (numMessages > 0) break;
                System.out.println("Please enter a number greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        int messagesSentThisSession = 0;
        boolean running = true;

        while (running) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    if (messagesSentThisSession >= numMessages) {
                        System.out.println("You have reached your message limit (" + numMessages + " messages).");
                    } else {
                        Message.sendMessageFlow(scanner);
                        messagesSentThisSession++;
                    }
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    break;
                case "4":
                    Message.displayStoredMessagesMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3 or 4.");
            }
        }

        System.out.println("\n===== Session Summary =====");
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println(Message.printMessages());
        System.out.println("Thank you for using QuickChat. Goodbye!");

        scanner.close();
    }
}
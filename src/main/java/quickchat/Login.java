/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quickchat;

/**
 *
 * @author Ntatiso
 */

import java.util.Scanner;

public class Login {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String cellPhoneNumber;

    public Login(String firstName, String lastName, String userName, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public boolean checkUserName() {
        return userName.contains("_") && userName.length() <= 5;
    }

    public boolean checkPasswordComplexity() {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
        return password.matches(regex);
    }

    public boolean checkCellPhoneNumber() {
        String regex = "^\\+\\d{1,3}\\d{1,10}$";
        return cellPhoneNumber.matches(regex);
    }

    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain international code; please correct the number and try again.";
        }
        return "User has been registered successfully.";
    }

    public boolean loginUser(String enteredUserName, String enteredPassword) {
        return this.userName.equals(enteredUserName) && this.password.equals(enteredPassword);
    }

    public String returnLoginStatus(boolean loginSuccess) {
        if (loginSuccess) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public static boolean promptLogin(Scanner scanner) {
        System.out.println("WELCOME TO QUICKCHAT");

        System.out.println("\n=== REGISTRATION ===");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username (max 5 chars, must have _): ");
        String userName = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter cell phone number (with international code e.g. +27821234567): ");
        String cellPhoneNumber = scanner.nextLine();

        Login user = new Login(firstName, lastName, userName, password, cellPhoneNumber);

        String registerResult = user.registerUser();
        System.out.println(registerResult);

        if (!registerResult.equals("User has been registered successfully.")) {
            return false;
        }

        System.out.println("\n=== LOGIN ===");
        System.out.print("Enter username: ");
        String enteredUserName = scanner.nextLine();

        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();

        boolean loginSuccess = user.loginUser(enteredUserName, enteredPassword);
        System.out.println(user.returnLoginStatus(loginSuccess));

        return loginSuccess;
        
    }
}
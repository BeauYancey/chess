package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        printMenu(loggedIn);

        while (true) {
            printPrompt();
            String input = scanner.nextLine().toLowerCase();
            if (!loggedIn) {
                if (input.equals("register") || input.equals("1")) {
                    loggedIn = true;
                }
                else if (input.equals("login") || input.equals("2")) {
                    loggedIn = true;
                }
                else if (input.equals("help") || input.equals("3")) {
                    printHelp(loggedIn);
                }
                else if (input.equals("quit") || input.equals("4")) {
                    break;
                }
                else {
                    printUnrecognizedCmd(loggedIn);
                }

            }
            else {
                if (input.equals("help") || input.equals("5")) {
                    printHelp(loggedIn);
                }
                else if (input.equals("logout") || input.equals("6")) {
                    loggedIn = false;
                }
                else if (input.equals("quit") || input.equals("7")) {
                    break;
                }
                else {
                    System.out.println("You entered " + input);
                }
            }
        }
    }

    private void printPrompt() {
        System.out.print(">>> ");
    }

    private void printMenu(boolean loggedIn) {
        String menu;
        if (loggedIn) {
            menu = "\t1. Create Game\n" +
                    "\t2. List Games\n" +
                    "\t3. Join Game\n" +
                    "\t4. Observe Game\n" +
                    "\t5. Help\n" +
                    "\t6. Logout\n" +
                    "\t7. Quit\n";
        }
        else {
            menu = "\t1. Register\n" +
                    "\t2. Login\n" +
                    "\t3. Help\n" +
                    "\t4. Quit\n";
        }
        System.out.print(menu);
    }

    private void printHelp(boolean loggedIn) {
        System.out.println("Enter the number of one of the following choices");
        printMenu(loggedIn);
    }

    private void printUnrecognizedCmd(boolean loggedIn) {
        System.out.println("You entered an unrecognized command.");
        printHelp(loggedIn);
    }
}

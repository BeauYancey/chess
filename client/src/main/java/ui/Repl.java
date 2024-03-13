package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;

    public Repl(String serverURL) {
        client = new Client(serverURL, this);
    }

    public void run() {
        System.out.println(WHITE_ROOK + "WELCOME TO CS240 CHESS SERVER" + WHITE_ROOK);
        System.out.println("Sign in to start. Type 'help' to see a list of commands.");

        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            printPrompt();
            input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("quit")) {
                break;
            }
            client.eval(input);
        }
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_GREEN + ">> " + SET_TEXT_COLOR_WHITE);
    }

    public void printMsg(String msg) {
        System.out.println(msg);
    }

    public void printErr(String msg) {
        System.out.println(SET_TEXT_COLOR_RED + msg + SET_TEXT_COLOR_WHITE);
    }
}

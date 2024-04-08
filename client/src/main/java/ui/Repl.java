package ui;

import chess.ChessPosition;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    private final Scanner scanner;

    public Repl(String serverURL) {
        client = new Client(serverURL, this);
        scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println(WHITE_ROOK + "WELCOME TO CS240 CHESS SERVER" + WHITE_ROOK);
        System.out.println("Sign in to start. Type 'help' to see a list of commands.");

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

    public void printPrompt() {
        System.out.print(SET_TEXT_COLOR_GREEN + ">> " + SET_TEXT_COLOR_WHITE);
    }

    public void printMsg(String msg) {
        System.out.println(msg);
    }

    public void printErr(String msg) {
        System.out.println(SET_TEXT_COLOR_RED + msg + SET_TEXT_COLOR_WHITE);
    }

    public String scanWord() {
        return String.join("-", scanner.nextLine().trim().split(" "));
    }

    public ChessPosition scanPosition() {
        String columns = " abcdefgh";
        String posString = scanner.nextLine().trim();
        int row = Integer.parseInt(posString.substring(1));
        int col = columns.indexOf(posString.substring(0,1));
        return new ChessPosition(row, col);
    }
}

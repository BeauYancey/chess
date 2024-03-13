import chess.*;
import ui.Client;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        String serverURl = "http://localhost:8080";
        if (args.length > 0) {
            serverURl = args[1];
        }

        Repl repl = new Repl(serverURl);
        repl.run();
    }
}
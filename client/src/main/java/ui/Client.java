package ui;

public class Client {
    private final String serverURL;
    private final Repl repl;
    private State state = State.LOGGEDOUT;

    public Client(String serverURL, Repl repl) {
        this.serverURL = serverURL;
        this.repl = repl;
    }

    public void eval(String input){
        switch (input) {
            case "help" -> repl.printMsg("help");
            case "login" -> repl.printMsg("login");
            case "register" -> repl.printMsg("register");
            case "logout" -> repl.printMsg("logout");
            case "create" -> repl.printMsg("create game");
            case "list" -> repl.printMsg("list games");
            case "join" -> repl.printMsg("join game");
            case "observe" -> repl.printMsg("observe game");
        }
    }

    private void help() {

    }

    private void login() {

    }

    private void register() {

    }

    private void logout() {

    }

    private void create() {

    }

    private void list() {

    }

    private void join() {

    }

    private void observe() {
        
    }
}

package foundation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Cli extends Thread {

    BufferedReader in;
    PrintStream out;
    PrintStream err;

    public Cli() {
        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintStream(System.out);
        err = new PrintStream(System.err);
    }

    public void processCommand(String cmd) {
        if (cmd.equals("quit")) {
            System.exit(0);
        } else {
            out.println("unknown command. Enter \"help\" for more information.");
        }
    }

    @Override
    public void run() {
        out.println("Cli started:");
        out.print("cli>");

        try {
            String cmd;

            while ((cmd = in.readLine()) != null) {
                processCommand(cmd.trim());
                out.print("cli>");
            }
        } catch (Exception e) {
            e.printStackTrace(err);
        }
    }
}
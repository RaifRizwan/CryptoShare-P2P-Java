package org.myname.cryptoshare;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Start the mDNS service on port 9980 (to match Python)
        PeerService peerService = new PeerService();
        peerService.start();

        // 2. Create a peer (this loads keys, etc.)
        Peer peer = new Peer();
        System.out.println("DEBUG - Peer 'peer_01' initialized on port 9900");
        System.out.println("DEBUG - Starting broadcast and discovery services");

        // 3. Start the peer
        peer.start();

        // 4. Start a simple CLI loop
        startCLI(peer);

        // 5. Stop the service when done
        peerService.stop();
    }

    private static void startCLI(Peer peer) throws IOException {
        System.out.println("DEBUG - Starting CLI");
        System.out.println("DEBUG - Listening for user commands (type 'help' for options, 'exit' to quit)");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();

            switch (input.toLowerCase()) {
                case "help":
                    System.out.println("Available commands:");
                    System.out.println("  help       - Show this help message");
                    System.out.println("  exit       - Exit the CLI");
                    System.out.println("  migrate    - Simulate key migration");
                    System.out.println("  anything else - For demonstration, just echo your input");
                    break;

                case "exit":
                    System.out.println("Exiting CLI...");
                    running = false;
                    break;

                case "migrate":
                    try {
                        peer.migrateKeys();
                    } catch (Exception e) {
                        System.err.println("Failed to migrate keys: " + e.getMessage());
                    }
                    break;

                default:
                    // For demonstration, we just echo what the user typed
                    // but here you could implement actual commands like sending a file, etc.
                    System.out.println("You typed: " + input);
                    break;
            }
        }

        System.out.println("CLI session ended.");
    }
}



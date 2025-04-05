package org.myname.cryptoshare;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.List;
import java.util.Scanner;

public class Simulation {
    public static void main(String[] args) {
        try {
            // We create two peers: Peer A (acting as the sender) and Peer B (acting as the receiver).
            Peer peerA = new Peer();
            Peer peerB = new Peer();

            // Mutual Authentication
            // Peer A begins by generating a random challenge.
            byte[] challenge = peerA.generateChallenge();
            System.out.println("Peer A generated challenge.");

            // Peer B receives the challenge and signs it using its Ed25519 private key.
            byte[] challengeSignature = peerB.signChallenge(challenge);
            System.out.println("Peer B signed the challenge.");

            // Peer A now verifies that the signature provided by Peer B is valid,
            // using Peer B's public key. This confirms that Peer B is who it claims to be.
            boolean authSuccess = peerA.verifyChallenge(challenge, challengeSignature, peerB.getEdKeyPair().getPublic());
            System.out.println("Mutual authentication success: " + authSuccess);
            if (!authSuccess) {
                System.err.println("Authentication failed. Aborting transfer.");
                return; // Stop the simulation if authentication fails.
            }

            //Key Exchange and Session Key Derivation
            // Both peers perform an X25519 key exchange to compute a shared secret.
            byte[] sharedSecretA = peerA.computeSharedSecret(peerB.getXKeyPair().getPublic());
            byte[] sharedSecretB = peerB.computeSharedSecret(peerA.getXKeyPair().getPublic());
            System.out.println("Shared secrets match: " + java.util.Arrays.equals(sharedSecretA, sharedSecretB));

            // Each peer derives an AES session key from the shared secret using HKDF.
            byte[] aesKeyA = peerA.deriveAESKey(sharedSecretA);
            byte[] aesKeyB = peerB.deriveAESKey(sharedSecretB);
            System.out.println("AES keys match: " + java.util.Arrays.equals(aesKeyA, aesKeyB));

            // File Listing and User Consent
            // We simulate a file sharing scenario by listing available files.
            FileManager fileManager = new FileManager();
            List<String> files = fileManager.listAvailableFiles();
            System.out.println("Available files: " + files);
            System.out.println("Enter the name of the file you wish to request:");
            Scanner scanner = new Scanner(System.in);
            String requestedFile = scanner.nextLine();

            // Simulate that Peer B (the sender) is asked for consent before sending the file.
            System.out.println("Peer B: Do you consent to send the file '" + requestedFile + "'? (yes/no)");
            String consent = scanner.nextLine();
            if (!consent.equalsIgnoreCase("yes")) {
                System.out.println("Peer B did not consent. Aborting transfer.");
                return;
            }

            //File Transfer
            // For demonstration, we simulate reading a file by using a hardcoded string.
            byte[] fileData = "This is a secure file content".getBytes(StandardCharsets.UTF_8);

            // Peer A signs the file data to ensure its authenticity.
            byte[] signature = peerA.signData(fileData);
            // Peer A encrypts the file data using AES-GCM with the derived AES key.
            Peer.EncryptionResult encResult = peerA.encryptData(aesKeyA, fileData);
            System.out.println("Peer A signed and encrypted the file.");

            // We simulate secure local storage by having Peer A store the encrypted file.
            fileManager.storeEncryptedFile("encrypted_" + requestedFile, encResult.ciphertext);

            //File Reception and Verification
            // Peer B receives the encrypted package.
            // It decrypts the ciphertext using the shared AES key and the provided nonce.
            byte[] decryptedData = peerB.decryptData(aesKeyB, encResult.nonce, encResult.ciphertext);
            // Peer B verifies that the signature on the decrypted data matches what was sent by Peer A.
            boolean verified = peerB.verifySignature(decryptedData, signature, peerA.getEdKeyPair().getPublic());
            System.out.println("Signature verified: " + verified);
            System.out.println("Decrypted file: " + new String(decryptedData, StandardCharsets.UTF_8));

            //Key Migration Simulation
            //We simulate a key migration scenario where Peer A might need to generate new keys.
            System.out.println("Simulate key migration? (yes/no)");
            String migrateResponse = scanner.nextLine();
            if (migrateResponse.equalsIgnoreCase("yes")) {
                peerA.migrateKeys();
                // In a complete system, Peer A would notify its contacts of the new public keys.
            }

        } catch (Exception e) {
            // If any error occurs during the simulation, print the error message and stack trace.
            System.err.println("Error during simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


package org.myname.cryptoshare;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IntegrationTest {

    @Test
    public void testFileTransferSimulation() throws Exception {
        // Create two peers: one will act as the sender (Peer A) and one as the receiver (Peer B).
        Peer peerA = new Peer();
        Peer peerB = new Peer();

        // Mutual authentication simulation:
        byte[] challenge = peerA.generateChallenge();
        byte[] challengeSignature = peerB.signChallenge(challenge);
        boolean authSuccess = peerA.verifyChallenge(challenge, challengeSignature, peerB.getEdKeyPair().getPublic());
        assertTrue(authSuccess, "Mutual authentication should succeed");

        // Key exchange simulation:
        byte[] sharedSecretA = peerA.computeSharedSecret(peerB.getXKeyPair().getPublic());
        byte[] sharedSecretB = peerB.computeSharedSecret(peerA.getXKeyPair().getPublic());
        assertArrayEquals(sharedSecretA, sharedSecretB, "Shared secrets should match");

        // Derive AES session key using HKDF.
        byte[] aesKeyA = peerA.deriveAESKey(sharedSecretA);
        byte[] aesKeyB = peerB.deriveAESKey(sharedSecretB);
        assertArrayEquals(aesKeyA, aesKeyB, "AES session keys should match");

        // Simulate file listing and user consent:
        FileManager fileManager = new FileManager();
        List<String> files = fileManager.listAvailableFiles();
        assertNotNull(files, "File list should not be null");

        // For this test, use a sample file content.
        String originalContent = "This is a secure file content";
        byte[] fileData = originalContent.getBytes(StandardCharsets.UTF_8);

        // Peer A signs and encrypts the file.
        byte[] signature = peerA.signData(fileData);
        Peer.EncryptionResult encResult = peerA.encryptData(aesKeyA, fileData);

        // Simulate storing the encrypted file.
        String testFileName = "encrypted_testFile.txt";
        fileManager.storeEncryptedFile(testFileName, encResult.ciphertext);

        // Peer B simulates receiving the package and decrypting the file.
        byte[] decryptedData = peerB.decryptData(aesKeyB, encResult.nonce, encResult.ciphertext);
        boolean verified = peerB.verifySignature(decryptedData, signature, peerA.getEdKeyPair().getPublic());
        assertTrue(verified, "Signature verification should pass");
        assertEquals(originalContent, new String(decryptedData, StandardCharsets.UTF_8),
                "Decrypted file content should match the original content");

        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(testFileName));
    }
}


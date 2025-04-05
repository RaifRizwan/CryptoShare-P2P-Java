package org.myname.cryptoshare;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class PeerTest {

    @Test
    public void testSharedSecretDerivation() throws Exception {
        Peer peerA = new Peer();
        Peer peerB = new Peer();

        // Each peer computes a shared secret using the other's X25519 public key.
        byte[] secretA = peerA.computeSharedSecret(peerB.getXKeyPair().getPublic());
        byte[] secretB = peerB.computeSharedSecret(peerA.getXKeyPair().getPublic());

        // Verify that the derived shared secrets are identical.
        assertArrayEquals(secretA, secretB, "Shared secrets should match");
    }

    @Test
    public void testEncryptionDecryption() throws Exception {
        // Create a peer and a sample message.
        Peer peer = new Peer();
        String originalMessage = "This is a test message for encryption.";

        // For testing, use a dummy shared secret to derive an AES key.
        byte[] dummySecret = "dummySharedSecretForTesting".getBytes();
        byte[] aesKey = peer.deriveAESKey(dummySecret);

        // Encrypt the message.
        Peer.EncryptionResult result = peer.encryptData(aesKey, originalMessage.getBytes());

        // Decrypt the ciphertext using the same AES key and nonce.
        byte[] decrypted = peer.decryptData(aesKey, result.nonce, result.ciphertext);
        String decryptedMessage = new String(decrypted);

        // Verify that the decrypted message matches the original message.
        assertEquals(originalMessage, decryptedMessage, "Decrypted text should match the original message");
    }

    @Test
    public void testSignatureVerification() throws Exception {
        // Create a peer and a sample message.
        Peer peer = new Peer();
        String message = "Test signature message";

        // Generate a digital signature for the message.
        byte[] signature = peer.signData(message.getBytes());

        // Verify the signature using the peer's Ed25519 public key.
        boolean isValid = peer.verifySignature(message.getBytes(), signature, peer.getEdKeyPair().getPublic());

        // The signature should be valid.
        assertTrue(isValid, "Signature verification should pass");
    }

    @Test
    public void testKeyMigration() throws Exception {
        // Create a peer and store its initial key pair.
        Peer peer = new Peer();
        byte[] originalEdPublic = peer.getEdKeyPair().getPublic().getEncoded();
        byte[] originalXPublic = peer.getXKeyPair().getPublic().getEncoded();

        // Perform key migration.
        peer.migrateKeys();

        // Get new keys.
        byte[] newEdPublic = peer.getEdKeyPair().getPublic().getEncoded();
        byte[] newXPublic = peer.getXKeyPair().getPublic().getEncoded();

        // Ensure that the keys have changed after migration.
        assertFalse(Arrays.equals(originalEdPublic, newEdPublic), "Ed25519 public key should change after migration");
        assertFalse(Arrays.equals(originalXPublic, newXPublic), "X25519 public key should change after migration");
    }
}

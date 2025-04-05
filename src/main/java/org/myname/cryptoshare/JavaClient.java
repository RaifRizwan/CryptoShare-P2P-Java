package org.myname.cryptoshare;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class JavaClient {
    public static void main(String[] args) {
        // The serverAddress is set to localhost (127.0.0.1) for testing on your local machine.
        // Change this to the target IP if your server is running on a different machine.
        String serverAddress = "127.0.0.1";
        // We use port 6000 as the communication port; this should match the port used by the server.
        int port = 6000;
        try (
                // Create a new Socket connection to the server at the given address and port.
                Socket socket = new Socket(serverAddress, port);
                // Obtain the output stream of the socket to send our data package.
                OutputStream os = socket.getOutputStream()
        ) {
            // For demonstration purposes, we are using sample data.
            byte[] signature = "SampleSignature".getBytes();
            byte[] nonce = "SampleNonce12".getBytes(); // This must be exactly 12 bytes for AES-GCM
            byte[] ciphertext = "SampleCiphertext".getBytes();

            // We are sending our data with length prefixes.
            os.write(intToBytes(signature.length));
            os.write(signature);
            // Next, write the length of the nonce and then the nonce.
            os.write(intToBytes(nonce.length));
            os.write(nonce);
            // Finally, write the length of the ciphertext and then the ciphertext.
            os.write(intToBytes(ciphertext.length));
            os.write(ciphertext);

            System.out.println("Package sent to server.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts an integer to a 4-byte array in big-endian order.
     * This is used to prefix our data with its length, so the receiver can parse the data correctly.
     *
     * @param value The integer value to convert.
     * @return A 4-byte array representing the integer.
     */
    private static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
}

package org.myname.cryptoshare;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class JavaServer {
    public static void main(String[] args) {
        int port = 9000;
        try (
                // Create a ServerSocket that listens for incoming connections on the specified port.
                ServerSocket serverSocket = new ServerSocket(port)
        ) {
            System.out.println("Java server listening on port " + port);

            // Wait for a client to connect.
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Get the input stream from the connected client to read the incoming data package.
            InputStream is = clientSocket.getInputStream();

            // Prepare a buffer to read a 4-byte integer which indicates the length of the signature.
            byte[] intBuffer = new byte[4];
            if (is.read(intBuffer) != 4)
                throw new Exception("Could not read signature length");
            // Convert the 4-byte array into an integer (big-endian order).
            int sigLen = ByteBuffer.wrap(intBuffer).getInt();

            // Create a byte array to hold the signature based on the length we just read.
            byte[] signature = new byte[sigLen];
            is.read(signature);

            // Read the length of the nonce in the same way.
            is.read(intBuffer);
            int nonceLen = ByteBuffer.wrap(intBuffer).getInt();

            // Create a byte array to store the nonce.
            byte[] nonce = new byte[nonceLen];
            is.read(nonce);

            // Read the length of the ciphertext.
            is.read(intBuffer);
            int ctLen = ByteBuffer.wrap(intBuffer).getInt();

            // Allocate a byte array to hold the ciphertext.
            byte[] ciphertext = new byte[ctLen];
            is.read(ciphertext);

            System.out.println("Received package:");
            System.out.println("Signature length: " + sigLen);
            System.out.println("Nonce length: " + nonceLen);
            System.out.println("Ciphertext length: " + ctLen);

            // Close the client socket to free up resources.
            clientSocket.close();
        } catch (Exception e) {
            // If any exception occurs (e.g., network error, read failure), print the error stack trace.
            e.printStackTrace();
        }
    }
}

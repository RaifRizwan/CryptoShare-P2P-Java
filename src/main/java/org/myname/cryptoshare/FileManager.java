package org.myname.cryptoshare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {

    /**
     * Returns a simulated list of files that are available for sharing.
     * In a real application, this method would dynamically list files from a specified directory.
     *
     * @return A list of file names.
     */
    public List<String> listAvailableFiles() {
        return List.of("file1.txt", "file2.txt", "image.png");
    }

    /**
     * Reads the contents of a file from disk.
     * This method simulates reading file data, which might be later processed (e.g., encrypted).
     *
     * @param filePath The path to the file that needs to be read.
     * @return The file contents as a byte array.
     * @throws IOException If the file cannot be read.
     */
    public byte[] readFile(String filePath) throws IOException {
        // Use Java NIO to read all bytes from the file.
        return Files.readAllBytes(Paths.get(filePath));
    }

    /**
     * Securely stores an encrypted file on disk.
     * In a real system, you might encrypt not only the file data but also the file name or metadata,
     * to enhance security. Here we simply write the encrypted data to a file.
     *
     * @param filePath      The destination path where the encrypted file will be saved.
     * @param encryptedData The encrypted file data as a byte array.
     * @throws IOException If the file cannot be written.
     */
    public void storeEncryptedFile(String filePath, byte[] encryptedData) throws IOException {
        // Write the encrypted data to the given file path.
        Files.write(Paths.get(filePath), encryptedData);
        // Provide a simple confirmation that the file was stored.
        System.out.println("Encrypted file stored at: " + filePath);
    }
}

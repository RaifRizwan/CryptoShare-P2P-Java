package org.myname.cryptoshare;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileManagerTest {

    @Test
    public void testListAvailableFiles() {
        FileManager fm = new FileManager();
        List<String> files = fm.listAvailableFiles();

        // Verify the file list is not null, not empty, and contains expected file names.
        assertNotNull(files, "File list should not be null");
        assertFalse(files.isEmpty(), "File list should not be empty");
        assertTrue(files.contains("file1.txt"), "File list should contain 'file1.txt'");
        assertTrue(files.contains("file2.txt"), "File list should contain 'file2.txt'");
        assertTrue(files.contains("image.png"), "File list should contain 'image.png'");
    }

    @Test
    public void testStoreAndReadFile() throws IOException {
        FileManager fm = new FileManager();
        String testFileName = "test_encrypted_file.txt";
        byte[] sampleData = "Sample encrypted data".getBytes();

        // Store the sample data to a file.
        fm.storeEncryptedFile(testFileName, sampleData);

        // Read the file back.
        byte[] readData = fm.readFile(testFileName);

        // Verify the stored data matches the sample data.
        assertArrayEquals(sampleData, readData, "Stored file data should match the original data");

        // Clean up the test file.
        Files.deleteIfExists(Path.of(testFileName));
    }

    @Test
    public void testReadNonExistentFile() {
        FileManager fm = new FileManager();
        String nonExistentFile = "non_existent_file.txt";
        // Expect an IOException when attempting to read a file that does not exist.
        assertThrows(IOException.class, () -> {
            fm.readFile(nonExistentFile);
        });
    }
}


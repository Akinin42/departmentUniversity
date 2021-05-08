package org.university.io;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.university.exceptions.NoFileException;

class FileReaderTest {

    private final FileReader reader = new FileReader();
    private static final String FILE_LINES = "testfile.txt";
    private static final String EMPTY_FILE = "emptyfile.txt";
    private static final String NON_EXISTING_FILE = "nonexisting.txt";

    @Test
    void readShouldReturnListStringsFromFileWhenInputFileContainsLine() {
        assertThat(reader.read(FILE_LINES)).containsExactly("Math", "Physics", "Biology");
    }

    @Test
    void readShouldReturnEmptyListWhenInputEmptyFile() {
        assertThat(reader.read(EMPTY_FILE)).isEmpty();
    }

    @Test
    void readShouldThrowNoFileExceptionWhenInputNonExistingFile() {
        assertThatThrownBy(() -> reader.read(NON_EXISTING_FILE)).isInstanceOf(NoFileException.class);
    }

    @Test
    void readShouldThrowIllegalArgumentExceptionWhenNull() {
        assertThatThrownBy(() -> reader.read(null)).isInstanceOf(IllegalArgumentException.class);
    }
}

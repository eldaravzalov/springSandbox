package com.example.app1.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileManager {

    private final String filePath;

    private File storage;

    public FileManager(@Value("${app.file.path:}") String filePath) {
        this.filePath = filePath;
    }

    @PostConstruct
    private void init() {
        storage = new File(filePath);
        if (!storage.exists() && !storage.mkdir()) {
            throw new RuntimeException("Failed to create file storage directory");
        }
    }

    /**
     * Сохраняет содержимое в файловой системе.
     * @param content содержимое
     * @return идентификатор файла
     */
    public String save(byte[] content) {
        String fileId = UUID.randomUUID().toString();
        File outputFile = new File(Path.of(storage.getPath(), fileId).toString());
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileId;
    }

    /**
     * Удаляет файл из фс.
     * @param fileId идентификатор файла
     */
    public void delete(String fileId) {
        File existingFile = new File(Path.of(storage.getPath(), fileId).toString());
        existingFile.delete();
    }

    /**
     * Получает контент из фс.
     * @param fileId идентификатор файла
     */
    public byte[] getContent(String fileId) throws IOException {
        File existingFile = new File(Path.of(storage.getPath(), fileId).toString());
        return  Files.readAllBytes(existingFile.toPath());
    }
}

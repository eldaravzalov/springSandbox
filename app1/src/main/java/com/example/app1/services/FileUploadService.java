package com.example.app1.services;

import com.example.app1.domain.FileEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FileUploadService {

    private final EntityManager entityManager;

    private final FileManager fileManager;

    public FileUploadService(JpaContext jpaContext, FileManager fileManager) {
        this.entityManager = jpaContext.getEntityManagerByManagedType(FileEntity.class);
        this.fileManager = fileManager;
    }

    @Transactional
    public String upload(byte[] content) {
        String fileId = fileManager.save(content);
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setId(UUID.randomUUID().toString());
            fileEntity.setFileId(fileId);
            entityManager.persist(fileEntity);
            return fileEntity.getId();
        } catch (Exception ex) {
            fileManager.delete(fileId);
            throw new RuntimeException(ex);
        }
    }
}

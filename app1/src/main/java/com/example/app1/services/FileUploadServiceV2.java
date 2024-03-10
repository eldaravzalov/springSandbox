package com.example.app1.services;

import com.example.app1.domain.FileEntity;
import com.example.app1.domain.FileEntityV2;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadServiceV2 {
    private final EntityManager entityManager;

    public FileUploadServiceV2(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public String upload(MultipartFile file) throws IOException {
        FileEntityV2 fileEntity = new FileEntityV2();
        fileEntity.setId(UUID.randomUUID().toString());
        fileEntity.setFileName(file.getName());
        fileEntity.setContent(file.getInputStream().readAllBytes());
        entityManager.persist(fileEntity);
        return fileEntity.getId();
    }

}

package com.example.app1.listeners;

import com.example.app1.domain.FileEntityV2;
import com.example.app1.events.FileEntityPersistEvent;
import com.example.app1.services.FileManager;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Component
public class FileEntityV2Listener {

    private final FileManager fileManager;

    private final ApplicationEventPublisher applicationEventPublisher;

    private static final Logger LOG = LoggerFactory.getLogger(FileEntityV2Listener.class);

    public FileEntityV2Listener(FileManager fileManager, ApplicationEventPublisher applicationEventPublisher) {
        this.fileManager = fileManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PrePersist
    public void onPrePersist(final FileEntityV2 fileEntity) {
        LOG.info("Setting entity props");
        if (fileEntity.getContent() == null) {
            fileEntity.setFileSize(0L);
            return;
        }
        String fileId = fileManager.save(requireNonNull(fileEntity.getContent()));
        fileEntity.setFileSize((long) fileEntity.getContent().length);
        fileEntity.setFileId(fileId);
    }

    @PostPersist
    public void onPostPersist(final FileEntityV2 fileEntity) {
        applicationEventPublisher.publishEvent(new FileEntityPersistEvent(fileEntity));
    }

    @PostLoad
    public void onPostLoad(final FileEntityV2 fileEntity) throws IOException {
        if (fileEntity.getFileId() != null) {
            LOG.info("Getting content from fs");
            fileEntity.setContent(fileManager.getContent(fileEntity.getFileId()));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onAfterTransactionRollback(FileEntityPersistEvent fileEntityPersistEvent) {
        Object source = fileEntityPersistEvent.getSource();
        if (source instanceof FileEntityV2 && ((FileEntityV2) source).getFileId() != null) {
            LOG.info("Deleting file from fs");
            fileManager.delete(((FileEntityV2) source).getFileId());
        }
    }

}

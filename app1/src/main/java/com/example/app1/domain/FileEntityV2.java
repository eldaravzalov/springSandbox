package com.example.app1.domain;

import com.example.app1.listeners.FileEntityV2Listener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(FileEntityV2Listener.class)
@Table(name = "fileEntityV2")
public class FileEntityV2 {
    @Getter
    @Setter
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String fileId;

    @Getter
    @Setter
    @Column(nullable = false)
    private String fileName;

    @Getter
    @Setter
    @Column(nullable = false)
    private Long fileSize;

    @Getter
    @Setter
    @Transient
    private byte[] content;
}

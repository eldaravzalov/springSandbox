package com.example.app1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fileEntity")
public class FileEntity {
    @Getter
    @Setter
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String fileId;
}

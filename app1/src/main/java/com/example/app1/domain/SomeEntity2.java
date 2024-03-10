package com.example.app1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class SomeEntity2 {
    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "related_object_id")
    SomeEntity relatedObject;
}

package com.example.app1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
public class SomeEntity {
    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Version
    @Column(nullable = false)
    private int version;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @OneToMany(mappedBy = "relatedObject")
    private List<SomeEntity2> subItems;
}

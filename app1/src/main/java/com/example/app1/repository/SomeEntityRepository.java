package com.example.app1.repository;

import com.example.app1.domain.SomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SomeEntityRepository extends JpaRepository<SomeEntity, String> {
}

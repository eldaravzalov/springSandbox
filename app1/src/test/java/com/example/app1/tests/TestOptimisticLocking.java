package com.example.app1.tests;

import com.example.app1.domain.SomeEntity;
import com.example.app1.domain.SomeEntity2;
import com.example.app1.repository.SomeEntity2Repository;
import com.example.app1.repository.SomeEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.PessimisticLockScope;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestOptimisticLocking {
    @SpyBean
    private EntityManager entityManager;

    @Autowired
    private SomeEntityRepository someEntityRepository;

    @Autowired
    private SomeEntity2Repository someEntity2Repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private String id;

    @BeforeEach
    void setup() {
        transactionTemplate.execute(status -> {
            id = UUID.randomUUID().toString();
            SomeEntity someEntity = new SomeEntity();
            someEntity.setId(id);
            someEntity.setName("someName");
            entityManager.persist(someEntity);

            //id = UUID.randomUUID().toString();
            SomeEntity2 someEntity2 = new SomeEntity2();
            someEntity2.setId(id);
            someEntity2.setName("someName");
            someEntity2.setRelatedObject(someEntity);
            entityManager.persist(someEntity2);

            entityManager.flush();
            entityManager.refresh(someEntity);
            entityManager.refresh(someEntity2);


            return status;
        });

    }

    @AfterEach
    void cleanup() {
        someEntity2Repository.deleteAll();
        someEntityRepository.deleteAll();
    }

    @Test
    @DisplayName("Test optimictic lock")
    public void test() throws InterruptedException {
        //t1 should fail updating entity cause of optimistic lock
        Thread t1 = new Thread(() -> {
            SomeEntity someEntity = someEntityRepository.findById(id).orElse(null);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            someEntity.setName("name1");
            assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
                someEntityRepository.save(someEntity);
            });

        });

        Thread t2 = new Thread(() -> {
            SomeEntity someEntity = someEntityRepository.findById(id).orElse(null);
            someEntity.setName("name1");
            someEntityRepository.save(someEntity);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @Test
    @DisplayName("Test pessimistic lock 1")
    public void test1() throws InterruptedException {
        //t1 locks object with LockModeType.PESSIMISTIC_WRITE
        //t2 should fail to oibtain entity
        Thread t1 = new Thread(() -> {
            transactionTemplate.execute(status -> {

                //PESSIMISTIC_FORCE_INCREMENT work in the same manner, but increments version
                SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id,
                        LockModeType.PESSIMISTIC_WRITE);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                someEntity2.setName("name1");
                entityManager.persist(someEntity2);
                return status;
            });

        });

        //t2 should fail to get the entity
        Thread t2 = new Thread(() -> {
            Assertions.assertThrows(PessimisticLockException.class,
                    () -> transactionTemplate.execute(status -> {
                        SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id);
                        return status;
                    }));
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        SomeEntity2 result = entityManager.find(SomeEntity2.class, id);
        assertEquals("name1", result.getName());
    }

    @Test
    @DisplayName("Test pessimistic lock 2")
    public void test2() throws InterruptedException {
        //t1 locks object with LockModeType.PESSIMISTIC_READ
        //t2 should succeed ti obtain entity, but fail to update it
        Thread t1 = new Thread(() -> {
            transactionTemplate.execute(status -> {

                Map<String, Object> properties = new HashMap<>();
                properties.put("jakarta.persistence.lock.timeout", 5000L);

                SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id,
                        LockModeType.PESSIMISTIC_READ, properties);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                someEntity2.setName("name1");
                entityManager.persist(someEntity2);
                return status;
            });

        });

        //t2 should fail to update entity
        Thread t2 = new Thread(() -> {

            Assertions.assertDoesNotThrow(() -> transactionTemplate.execute(status -> {
                        SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id);
                        return status;
                    })
            );

            Assertions.assertThrows(PessimisticLockException.class, () -> transactionTemplate.execute(status -> {
                        SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id);
                        someEntity2.setName("name2");
                        entityManager.persist(someEntity2);
                        return status;
                    })
            );
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        SomeEntity2 result = entityManager.find(SomeEntity2.class, id);
        assertEquals("name1", result.getName());
    }

    @Disabled
    @Test
    @DisplayName("Test pessimistic lock 3")
    public void test3() throws InterruptedException {
        //t1 locks object with LockModeType.PESSIMISTIC_WRITE and PessimisticLockScope.EXTENDED
        //t2 should fail to obtain entity
        Thread t1 = new Thread(() -> {
            transactionTemplate.execute(status -> {

                Map<String, Object> properties = new HashMap<>();
                properties.put("jakarta.persistence", PessimisticLockScope.EXTENDED);

                SomeEntity someEntity = entityManager.find(SomeEntity.class, id,
                        LockModeType.PESSIMISTIC_WRITE, properties);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return status;
            });

        });

        //t2 should fail to update entity
        Thread t2 = new Thread(() -> {

            Assertions.assertDoesNotThrow(() -> transactionTemplate.execute(status -> {
                        SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id);
                        return status;
                    })
            );

            Assertions.assertThrows(PessimisticLockException.class, () -> transactionTemplate.execute(status -> {
                        SomeEntity2 someEntity2 = entityManager.find(SomeEntity2.class, id);
                        someEntity2.setName("name2");
                        entityManager.persist(someEntity2);
                        return status;
                    })
            );
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        SomeEntity2 result = entityManager.find(SomeEntity2.class, id);
        assertEquals("someName", result.getName());
    }
}

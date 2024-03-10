package com.example.app1.tests;

import com.example.app1.controllers.FileUploadController;
import com.example.app1.domain.FileEntityV2;
import com.example.app1.services.FileManager;
import jakarta.persistence.EntityManager;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@AutoConfigureMockMvc
public class TestFileUploadControllerv2 {
    @Value("${app.file.path}")
    private String filePath;

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private EntityManager entityManager;

    @SpyBean
    private FileManager fileManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMultipartFile mockMultipartFile;

    public TestFileUploadControllerv2() {
    }

    @BeforeEach
    private void setFile() {
        mockMultipartFile = new MockMultipartFile("file",
                "someFile.name",
                "text/plain",
                ("fileData").getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    private void deleteFiles() throws IOException {
        FileUtils.cleanDirectory(new java.io.File(filePath));
    }

    @Test
    @DisplayName("Test success file upload")
    @Transactional
    public void testSuccessUpload() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(
                FileUploadController.CONTROLLER_PATH + "/v2")
                        .file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String fileId = result.getResponse().getContentAsString();

        FileEntityV2 savedFileEntity = entityManager.find(FileEntityV2.class, fileId);
        assertNotNull(savedFileEntity);

        assertTrue(Arrays.equals(savedFileEntity.getContent(), mockMultipartFile.getBytes()));
        assertTrue(savedFileEntity.getFileSize() == mockMultipartFile.getBytes().length);
        assertTrue(Files.exists(Path.of(filePath, savedFileEntity.getFileId())));

    }

    @Test
    @DisplayName("Test throwing exception during file saving")
    @Transactional
    public void testFailedUploadDuringFileSaving() throws Exception {
        doThrow(new RuntimeException("Some exception")).when(fileManager).save(ArgumentMatchers.any());
        Assertions.assertThatThrownBy(() ->mockMvc.perform(
                        MockMvcRequestBuilders.multipart(FileUploadController.CONTROLLER_PATH + "/v2")
                                .file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn()).hasCauseInstanceOf(RuntimeException.class);

        assertTrue((new java.io.File(filePath)).list().length == 0);
        assertTrue(jdbcTemplate.queryForObject("select count(*) from file_entityv2", Integer.class).equals(0));

    }

    @Test
    @DisplayName("Test throwing exception during entity saving")
    @Transactional
    public void testFailedUploadDuringEntitySaving() throws Exception {
        doThrow(new RuntimeException("Some exception")).when(entityManager).persist(ArgumentMatchers.any());

        Assertions.assertThatThrownBy(() ->mockMvc.perform(
                MockMvcRequestBuilders.multipart(FileUploadController.CONTROLLER_PATH + "/v2")
                        .file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn()).hasCauseInstanceOf(RuntimeException.class);

        assertTrue((new java.io.File(filePath)).list().length == 0);
        assertTrue(jdbcTemplate.queryForObject("select count(*) from file_entityv2", Integer.class).equals(0));

    }
}

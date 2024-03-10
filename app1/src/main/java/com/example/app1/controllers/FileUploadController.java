package com.example.app1.controllers;

import com.example.app1.services.FileUploadService;
import com.example.app1.services.FileUploadServiceV2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(FileUploadController.CONTROLLER_PATH)
public class FileUploadController {

    public static final String CONTROLLER_PATH = "/api/file_upload";

    private final FileUploadService fileUploadService;

    private final FileUploadServiceV2 fileUploadServiceV2;

    public FileUploadController(FileUploadService fileUploadService,
                                FileUploadServiceV2 fileUploadServiceV2) {
        this.fileUploadService = fileUploadService;
        this.fileUploadServiceV2 = fileUploadServiceV2;
    }

    @PostMapping
    @RequestMapping("v1")
    public String uploadFile(@RequestParam("file") final MultipartFile file) throws IOException {
        return fileUploadService.upload(file.getBytes());
    }

    @PostMapping
    @RequestMapping("v2")
    public String uploadFile2(@RequestParam("file") final MultipartFile file) throws IOException {
        return fileUploadServiceV2.upload(file);
    }
}

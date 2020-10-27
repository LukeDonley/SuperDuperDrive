package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class FileController {

    private FileService fileService;
    private UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/file")
    public String createNewFile(Authentication authentication, @RequestParam("fileUpload") MultipartFile fileUpload) throws IOException {

        if(fileUpload.isEmpty()) {
            return "redirect:/result?errorMessage";
        }

        String fileName = fileUpload.getOriginalFilename();
        Integer userId = this.userService.getUserId(authentication.getName());

        if(fileService.getFileByFilename(userId, fileName) != null) {
            String errorMessage = "Error: Filename in use.";
            return "redirect:/result?errorMessage=" + errorMessage;
        }
        fileService.addFile(fileUpload, userId);

        return "redirect:/result?success";
    }

    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(Authentication authentication, @RequestParam(name = "fileId") Integer fileId) {
        File file = fileService.getFile(fileId);
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format(
                "attachment; filename=", file.getFileName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ByteArrayResource resource = new ByteArrayResource(file.getFileData());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(Long.parseLong(file.getFileSize()))
                .body(resource);
    }

    @GetMapping("/file/delete")
    public String deleteFile(Authentication authentication, @RequestParam(name = "fileId") Integer fileId) {
        fileService.deleteFile(fileId);
        return "redirect:/result?success";
    }
}

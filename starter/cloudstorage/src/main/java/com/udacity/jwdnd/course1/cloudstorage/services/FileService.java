package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    private FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public void addFile(MultipartFile file, Integer userId) throws IOException {

        File newFile = new File(
                null,
                file.getOriginalFilename(),
                file.getContentType(),
                String.valueOf(file.getSize()),
                userId,
                file.getBytes()
        );

        this.fileMapper.insert(newFile);
    }

    public List<File> getFiles(Integer userId) {
        return this.fileMapper.getFilesForUser(userId);
    }

    public File getFile(Integer fileId) {
        return this.fileMapper.getFile(fileId);
    }

    public Boolean deleteFile(Integer fileId) {
        this.fileMapper.delete(fileId);
        return true;
    }
}

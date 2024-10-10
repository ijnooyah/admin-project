package com.yoonji.adminproject.file.service;

import com.yoonji.adminproject.file.entity.File;
import com.yoonji.adminproject.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    private final FileRepository fileRepository;

    @Transactional
    public File storeFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리의 절대 경로를 얻기
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // 원본 파일 이름을 정리 (경로 구분자 등을 제거)
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 유니크한 파일 이름을 생성
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 전체 파일 경로를 생성
        Path filePath = uploadPath.resolve(uniqueFileName);

        // 파일을 지정된 경로에 복사
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileRepository.save(File.createFile(originalFileName, filePath.toString(), file.getContentType(), file.getSize()));
    }

    public String getFileUrl(File file) {
        if (file == null) {
            return null;
        }
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = uploadPath.relativize(Paths.get(file.getFilePath()));
        return baseUrl + "/" + filePath.toString().replace("\\", "/");
    }

    @Transactional
    public void deleteFile(File file) {
        if (file == null) {
            return;
        }
//            Path filePath = Paths.get(file.getFilePath());
//            Files.deleteIfExists(filePath);
            File findFile = fileRepository.findById(file.getId())
                    .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없음"));
            findFile.delete();
    }
}

package com.daymemory.service;

import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads/gifts}")
    private String uploadDir;

    /**
     * 파일 업로드 (로컬 파일 시스템)
     * TODO: AWS S3 연동 시 이 메서드를 S3 업로드로 변경
     */
    public String uploadFile(MultipartFile file) {
        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 (UUID + 원본 파일 확장자)
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 파일 저장
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded successfully: {}", newFilename);

            // 파일 URL 반환 (상대 경로)
            return "/uploads/gifts/" + newFilename;

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                return;
            }

            // URL에서 파일명 추출
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);

            // 파일 삭제
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", filename);

        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            // 파일 삭제 실패는 치명적이지 않으므로 예외를 던지지 않음
        }
    }

    /**
     * 파일 유효성 검사
     */
    public boolean validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 파일 크기 체크 (5MB 제한)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 파일 타입 체크 (이미지만 허용)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        return true;
    }
}

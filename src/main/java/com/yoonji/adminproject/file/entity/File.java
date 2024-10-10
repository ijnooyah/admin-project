package com.yoonji.adminproject.file.entity;

import com.yoonji.adminproject.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class File extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    // == 생성 메서드 ==
    public static File createFile(String fileName, String filePath, String fileType, Long fileSize) {
        File file = new File();
        file.fileName = fileName;
        file.filePath = filePath;
        file.fileType = fileType;
        file.fileSize = fileSize;

        return file;
    }

    // == 삭제 메서드 ==
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

}
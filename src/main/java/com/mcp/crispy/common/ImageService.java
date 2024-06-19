package com.mcp.crispy.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${file.upload-dir.sign.window}")
    private String signPathWin;

    @Value("${file.upload-dir.sign.mac}")
    private String signPathMac;

    @Value("${file.upload-dir.profile.window}")
    private String profilePathWin;

    @Value("${file.upload-dir.profile.mac}")
    private String profilePathMac;

    @Value("${file.default-profile-img.window}")
    private String defaultProfileImageWin;

    @Value("${file.default-profile-img.mac}")
    private String defaultProfileImageMac;

    // 운영체제에 따른 경로 반환
    private String getProfilePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return profilePathWin;
        } else {
            return profilePathMac;
        }
    }

    private String getSignPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return signPathWin;
        } else {
            return signPathMac;
        }
    }

    private String getDefaultProfileImagePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return defaultProfileImageWin;
        } else {
            return defaultProfileImageMac;
        }
    }

    public String storeProfileImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalStateException("업로드된 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;
        Path destinationPath = Paths.get(getProfilePath()).resolve(storedFileName);

        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return storedFileName;
    }

    public String storeSignatureImage(String signData, int empNo) throws IOException {
        if (signData == null || signData.isEmpty()) {
            throw new IllegalStateException("제공된 서명 데이터가 없습니다.");
        }
        String fileName = "emp_sign_" + empNo + "_" + System.currentTimeMillis() + ".png";
        Path directoryPath = Paths.get(getSignPath());
        Path filePath = directoryPath.resolve(fileName);

        byte[] signBytes = Base64.getDecoder().decode(signData.split(",")[1]);
        Files.write(filePath, signBytes);

        return fileName;
    }
}

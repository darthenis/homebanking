package com.mindhub.homebanking.utils;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class UploadFileUtil {

    final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/portfolioap-102b7.appspot.com/o/%s?alt=media";

    public String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("portfolioap-102b7.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("firebaseConfig.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    public String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String upload(MultipartFile multipartFile, Long userId) {

        String route = "clientImages/"+userId;

        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = userId.toString().concat(this.getExtension(fileName));
            File file = this.convertToFile(multipartFile, fileName);   // to convert multipartFile to File
            String pathToUpload = route.concat(this.getExtension(fileName));
            String url = this.uploadFile(file, pathToUpload);                                   // to get uploaded file link
            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unsuccessfully Uploaded";
        }

    }
}

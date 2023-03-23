package com.mindhub.homebanking.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.mindhub.homebanking.models.FirebaseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class UploadFileUtil {

    final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/portfolioap-102b7.appspot.com/o/%s?alt=media";



    private String json(String privateKeyID, String privateKey, String clientId) throws JsonProcessingException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        return ow.writeValueAsString(new FirebaseConfig(privateKeyID, privateKey, clientId));

    }

    private InputStream getInputStream(String privateKeyId, String privateKey, String clientId) throws IOException{

        return new ByteArrayInputStream(json(privateKeyId, privateKey, clientId).getBytes());

    }

    public String uploadFile(File file, String fileName, String privateKeyId, String privateKey, String clientId) throws IOException {
        BlobId blobId = BlobId.of("portfolioap-102b7.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Credentials credentials = GoogleCredentials.fromStream(getInputStream(privateKeyId, privateKey, clientId));
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

    public String upload(MultipartFile multipartFile, Long userId,  String privateKeyId, String privateKey, String clientId) {

        String route = "clientImages/"+userId;

        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = userId.toString().concat(this.getExtension(fileName));
            File file = this.convertToFile(multipartFile, fileName);   // to convert multipartFile to File
            String pathToUpload = route.concat(this.getExtension(fileName));
            String url = this.uploadFile(file, pathToUpload, privateKeyId, privateKey, clientId);                                   // to get uploaded file link
            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unsuccessfully Uploaded";
        }

    }
}

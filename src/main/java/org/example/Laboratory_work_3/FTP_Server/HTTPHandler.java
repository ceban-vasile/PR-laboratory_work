package org.example.Laboratory_work_3.FTP_Server;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;

import java.io.File;

public class HTTPHandler {

    public void sendMultipartRequest(File file) {
        try {
            Request.post("http://localhost:8000/upload")
                    .body(MultipartEntityBuilder.create()
                            .addBinaryBody("file'", file)
                            .build())
                    .execute()
                    .returnContent();
            System.out.println("File sent to LAB2 webserver: " + file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

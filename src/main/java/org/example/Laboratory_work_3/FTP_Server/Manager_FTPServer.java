package org.example.Laboratory_work_3.FTP_Server;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Manager_FTPServer {

    String server;
    int port;
    String user;
    String pass;
    String localFilePath;
    String remoteFilePath;
    String localFilePathD;

    public Manager_FTPServer(String server, int port, String user, String pass, String localFilePath, String remoteFilePath, String localFilePathD){
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.localFilePath = localFilePath;
        this.remoteFilePath = remoteFilePath;
        this.localFilePathD = localFilePathD;
        this.startService();
    }

    public void startService(){
        FTPHandler ftpHandler = new FTPHandler();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            while (true) {
                try {
                    ftpHandler.uploadFileToFTP(server, port, user, pass, localFilePath, remoteFilePath);
                    System.out.println("File uploaded successfully!");
                    System.out.println("Processed file uploaded to FTP server: " + localFilePath);
                    Thread.sleep(30_000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        executor.submit(() -> {
            while (true) {
                try {
                    File file = ftpHandler.downloadFileFromFTP(server, port, user, pass, localFilePathD, remoteFilePath);
                    System.out.println("File fetched from FTP server: " + file.getName());
                    new HTTPHandler().sendMultipartRequest(file);
                    Thread.sleep(30_000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        executor.shutdown();
    }
}

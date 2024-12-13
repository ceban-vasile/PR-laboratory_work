package org.example.Laboratory_work_3.FTP_Server;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class FTPHandler {

    public void uploadFileToFTP(String server, int port, String user, String pass, String localFilePath, String remoteFilePath) throws IOException {

        new DataProcessor().generateProcessedFile(localFilePath);

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            try (InputStream inputStream = new FileInputStream(localFilePath)) {
                boolean done = ftpClient.storeFile(remoteFilePath, inputStream);
                if (done) {
                    System.out.println("File uploaded successfully to FTP server.");
                } else {
                    System.out.println("Failed to upload file.");
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public File downloadFileFromFTP(String server, int port, String user, String pass, String localFilePath, String remoteFilePath) {
        FTPClient ftpClient = new FTPClient();
        File file = new File(localFilePath);
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (OutputStream outputStream = new FileOutputStream(localFilePath)) {
                boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
                if (success) {
                    System.out.println("File downloaded successfully to: " + localFilePath);
                } else {
                    System.out.println("Failed to download the file.");
                }
            }

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return file;
    }
}

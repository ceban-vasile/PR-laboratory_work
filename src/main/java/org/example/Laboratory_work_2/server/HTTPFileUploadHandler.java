package org.example.Laboratory_work_2.server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.*;
import java.util.List;
import java.util.Map;

public class HTTPFileUploadHandler implements HttpHandler {

    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            // Check if it's a multipart request
            Map<String, List<String>> headers = exchange.getRequestHeaders();
            String contentType = headers.getOrDefault("Content-type", List.of("")).get(0);

            if (contentType != null && contentType.contains("multipart/form-data")) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);

                try {
                    // Ensure the directory exists
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }

                    // Parse the request
                    InputStream requestBody = exchange.getRequestBody();
                    List<FileItem> items = upload.parseRequest(new InputStreamRequestContext(requestBody, contentType));

                    // Process the uploaded items
                    for (FileItem item : items) {
                        if (!item.isFormField()) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = UPLOAD_DIR + File.separator + fileName;
                            File storeFile = new File(filePath);
                            item.write(storeFile);
                            System.out.println("File uploaded to: " + filePath);
                        }
                    }

                    // Send response
                    String response = "File uploaded successfully.";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    String response = "File upload failed: " + ex.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                String response = "Invalid request content type";
                exchange.sendResponseHeaders(400, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            String response = "Method not allowed";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Helper class to adapt InputStream to ServletFileUpload's RequestContext
    private static class InputStreamRequestContext implements RequestContext {
        private final InputStream inputStream;
        private final String contentType;

        public InputStreamRequestContext(InputStream inputStream, String contentType) {
            this.inputStream = inputStream;
            this.contentType = contentType;
        }

        @Override
        public String getCharacterEncoding() {
            return "";
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public int getContentLength() {
            return -1; // Content length can be determined from headers
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return inputStream;
        }
    }
}

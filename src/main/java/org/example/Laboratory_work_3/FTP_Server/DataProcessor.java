package org.example.Laboratory_work_3.FTP_Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DataProcessor {

    public void generateProcessedFile(String localFilePath) throws IOException {
        File file = new File(localFilePath);

        String data = fetchProductDataFromAPI();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        }
    }

    private static String fetchProductDataFromAPI() throws IOException {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;

        try {
            URL url = new URL("http://localhost:8000/displayProduct?offset=6&limit=10");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine()+"\n");
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }
}

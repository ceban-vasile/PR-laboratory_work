package org.example.Laboratory_work_1;

import org.example.Laboratory_work_1.Model.Product;
import org.example.Laboratory_work_1.requests.Scraper;
import org.example.Laboratory_work_1.services.ParsingHTML;
import org.example.Laboratory_work_1.services.Processor;
import org.example.Laboratory_work_1.services.Serialization;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void run() throws Exception {
        ParsingHTML parsingHTML = new ParsingHTML();
        Serialization serialization = new Serialization();
        Processor processor = new Processor();
        Scraper scraper = new Scraper();

        String pageContent = scraper.fetchPage(
                "www.asos.com", 443, "/men/new-in/cat/?cid=27110&ctaref=searcherrorpage|men"
        );

        List<Product> products = parsingHTML.parseProducts(pageContent);

        processor.convertCurrency(products);
        List<Product> productRange = processor.filterByPriceRange(products, 200, 500);
        String serializedData = serialization.serialize(productRange);
        Product productJson = new Product();
        productJson.setPrice(productRange.get(0).getPrice());
        String message = "{\n" +
                "    \"name\": \"" + productRange.getFirst().getName() + "\",\n" +
                "    \"color\": \"" + (productRange.getFirst().getColor() != null ? productRange.getFirst().getColor() : "Black") + "\",\n" +
                "    \"price\": " + Double.parseDouble(String.valueOf(productRange.getFirst().getPrice())) + ",\n" +
                "    \"currency\": \"" + productRange.getFirst().getCurrency().split("/")[1] + "\",\n" +
                "    \"time_converted\": \"" + productRange.getFirst().getTime().substring(0, 10).replace("-", "/") + "\",\n" +
                "    \"link\": \"" + productRange.getFirst().getLink().split("/")[2] + "\"\n" +
                "}";
        scraper.publishToRabbitMQ(message);



        System.out.println(" [x] Serialized and published products to RabbitMQ");
    }
}

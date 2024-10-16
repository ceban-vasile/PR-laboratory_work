package org.example;

import org.example.Model.Product;
import org.example.requests.Scraper;
import org.example.services.ParsingHTML;
import org.example.services.Processor;
import org.example.services.Serialization;

import java.io.IOException;
import java.net.URISyntaxException;
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
        // Initialize services
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
//        String testare = serialization.serialize(productRange);
//        System.out.println(testare);
//        List<Product> t = (List<Product>) serialization.deserialize(testare);
//        System.out.println(t);
//        System.out.println(serialization.serialize(t));

        double sum = processor.sumPrices(productRange);

        productRange.forEach(product -> {
            try {
                processProduct(product, parsingHTML, serialization);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Total sum: " + sum + " Lei");
    }

    private void processProduct(Product product, ParsingHTML parsingHTML, Serialization serialization) throws IOException, URISyntaxException {
        parsingHTML.fetchProductDetails(product);
        if (parsingHTML.validateProduct(product)) {
            //System.out.println(serialization.serialize(product));
        } else {
            System.err.println("Invalid product: " + product.getName());
        }
    }
}

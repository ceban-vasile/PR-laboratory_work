package org.example;

import org.example.Model.Product;
import org.example.requests.Scraper;
import org.example.services.ParsingHTML;
import org.example.services.Processor;
import org.example.services.Serialization;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            ParsingHTML parsingHTML = new ParsingHTML();
            Serialization serialization = new Serialization();
            Processor processor = new Processor();

            String pageContent = new Scraper().fetchPage("www.asos.com", 443, "/men/new-in/cat/?cid=27110&ctaref=searcherrorpage|men");

            List<Product> products = parsingHTML.parseProducts(pageContent);

//            processor.convertCurrency(products);
//
//            List<Product> productRange = processor.filterByPriceRange(products, 400, 800);
//
//            Double sun = processor.sumPrices(productRange);
//
//            System.out.println(sun);

            for (Product product : products) {
                parsingHTML.fetchProductDetails(product);
                if (parsingHTML.validateProduct(product)) {
                    System.out.println(serialization.serializeToJson(product));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package org.example.services;

import org.example.Model.Product;
import org.example.requests.Scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ParsingHTML {

    public ParsingHTML(){}

    public List<Product> parseProducts(String html) {
        List<Product> products = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Elements articles = doc.select("article[class=productTile_U0clN]");

        for(Element article : articles){
            String link = article.select("a[href]").attr("href");
            String name = article.select("p[class=productDescription_sryaw]").text( );
            String priceText = article.select("span[class=price__B9LP]").text();
            String currency = priceText.substring(0, 1);
            Double price = Double.valueOf(priceText.replace("£", "").trim());

            products.add(new Product(name, price, currency, link));
        }
        return products;
    }

    public void fetchProductDetails(Product product) throws IOException, URISyntaxException {
        URI uri = new URI(product.getLink());

        String host = uri.getHost();
        int port = 443;
        String path = uri.getPath();

        String productPage = new Scraper().fetchPage(host, port, path);
        Document doc = Jsoup.parse(productPage);
        String color = doc.select("p[class=aKxaq hEVA6]").text();
        product.setColor(color);
    }

    public boolean validateProduct(Product product) {
        return product.getName() != null && !product.getName().isEmpty() && product.getPrice() > 0;
    }
}



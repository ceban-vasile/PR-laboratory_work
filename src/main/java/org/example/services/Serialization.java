package org.example.services;
import org.example.Model.Product;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Serialization {

    public String serializeToJson(Product product) {
        return "{\n" +
                "\"name\": \"" + product.getName() + "\",\n" +
                "\"color\": \"" + product.getColor() + "\",\n" +
                "\"price\": " + product.getPrice() + ",\n" +
                "\"currency\": " + product.getCurrency() + ",\n" +
                "\"current conversion time:\": " + product.getTime() + ",\n" +
                "\"link\": \"" + product.getLink() + "\"\n" +
                "}";
    }

    public String serializeToXml(Product product) {
        return "<Product>\n" +
                "    <name>" + product.getName() + "</name>\n" +
                "    <color>" + product.getColor() + "</color>\n" +
                "    <price>" + product.getPrice() + "</price>\n" +
                "    <currency>" + product.getCurrency() + "</currency>\n" +
                "    <currentConversionTime>" + product.getTime() + "</currentConversionTime>\n" +
                "    <link>" + product.getLink() + "</link>\n" +
                "</Product>";
    }

    public String serialize(Object obj) {
        if (obj instanceof Product) {
            return serializeProduct((Product) obj);
        } else if (obj instanceof List) {
            return serializeList((List<?>) obj);
        } else if (obj instanceof Map) {
            return serializeMap((Map<?, ?>) obj);
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + obj.getClass());
        }
    }

    public String serializeProduct(Product product) {
        StringBuilder sb = new StringBuilder("\n   P:{");
        Class<?> productClass = product.getClass();

        Field[] fields = productClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(product);

                if (value != null) {
                    String fieldName = field.getName();
                    String fieldType = field.getType().getSimpleName();

                    String serializedType = mapToSerializedType(fieldType);

                    sb.append("\n").append("      ").append(fieldName).append(":").append(serializedType).append(":").append(value).append("| ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private String mapToSerializedType(String javaType) {
        switch (javaType) {
            case "String":
                return "S";
            case "Integer":
            case "int":
                return "I";
            case "Double":
            case "double":
                return "D";
            case "LocalDateTime":
                return "Time";
            default:
                return "Unknown";
        }
    }

    public String serializeList(List<?> list) {
        StringBuilder sb = new StringBuilder("L:{");
        for (Object item : list) {
            sb.append(serializeProduct((Product) item));
        }
        return sb.append("}").toString();
    }

    private String serializeMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("M:");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(serialize(entry.getKey()));
            sb.append(serialize(entry.getValue()));
        }
        return sb.append("}").toString();
    }

    public Object deserialize(String data) {
        if (data == null || data.isEmpty()) return null;

        char type = data.charAt(0);
        String content = data.substring(2);

        return switch (type) {
            case 'P' -> deserializeProduct(content);
            case 'L' -> deserializeList(content);
            case 'M' -> deserializeMap(content);
            default -> throw new IllegalArgumentException("Unsupported data format: " + type);
        };
    }

    private Product deserializeProduct(String data) {
        String regex = "(?<key>name|price|currency|time|link|color):(?<type>S|D|I|Time):(?<value>[^|]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        Map<String, String> productData = new HashMap<>();

        while (matcher.find()) {
            String key = matcher.group("key").trim();
            String value = matcher.group("value").trim();

            productData.put(key, value);
        }

        String name = productData.getOrDefault("name", "Unknown");
        String color = productData.getOrDefault("color", "Unknown");
        double price = 0.0;
        try {
            price = Double.parseDouble(productData.getOrDefault("price", "0.0"));
        } catch (NumberFormatException e) {
            System.err.println("Invalid price format: " + productData.get("price"));
        }

        String currency = productData.getOrDefault("currency", "Unknown");
        String time = productData.getOrDefault("time", "Unknown");
        String link = productData.getOrDefault("link", "Unknown");

        return new Product(name, color, price, currency, time, link);
    }

    public List<Product> deserializeList(String data) {
        List<Product> products = new ArrayList<>();

        String[] productStrings = data.split("P:\\{");

        for (String productString : productStrings) {
            if (productString.trim().isEmpty()) {
                continue;
            }
            productString = productString.replaceFirst("\\|\\s*}$", "");

            try {
                Product product = deserializeProduct("P:{" + productString);
                products.add(product);
            } catch (Exception e) {
                System.err.println("Failed to deserialize product: " + productString);
                e.printStackTrace();
            }
        }
        return products;
    }

    private Map<Object, Object> deserializeMap(String data) {
        Map<Object, Object> map = new HashMap<>();
        int i = 0;

        while (i < data.length()) {
            char keyType = data.charAt(i);
            int keyEnd = data.indexOf("|", i);
            String key = data.substring(i + 2, keyEnd);

            i = keyEnd + 1;
            char valueType = data.charAt(i);
            int valueEnd = data.indexOf("|", i);
            String value = data.substring(i + 2, valueEnd);

            map.put(deserialize(keyType + ":" + key + "|"),
                    deserialize(valueType + ":" + value + "|"));
            i = valueEnd + 1;
        }
        return map;
    }
}

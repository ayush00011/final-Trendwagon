package com.cts.TrendWagon.dto;

public class ProductUserView {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imagePath;

    
    public ProductUserView(Long id, String name, String description, double price, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
    }

    
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
}

package com.byt.s30062;


public class Device extends Product {
    private String model;
    private Integer storageGB;
    private String color; // optional element

    public Device(int id, String name, double price, String model, Integer storageGB) {
        super(id, name, price);
        if (model == null || model.isBlank()) throw new IllegalArgumentException("model required");
        if (storageGB != null && storageGB <= 0) throw new IllegalArgumentException("storage must be positive");
        this.model = model;
        this.storageGB = storageGB;
    }

    public void setColor(String color) {
        this.color = (color == null || color.isBlank()) ? null : color;
    }

    public String getModel() { return model; }
    public Integer getStorageGB() { return storageGB; }
    public String getColor() { return color; }
}

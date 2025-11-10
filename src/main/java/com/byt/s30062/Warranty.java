package com.byt.s30062;


import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Warranty implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Warranty> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "warranty_extent.ser";

    private final int warrantyId;
    private final int purchaseId;
    private final int productId;
    private final LocalDate startDate;
    private LocalDate endDate;

    public Warranty(int warrantyId, int purchaseId, int productId, LocalDate startDate, LocalDate endDate) {
        if (warrantyId <= 0) throw new IllegalArgumentException("warrantyId positive");
        if (purchaseId <= 0) throw new IllegalArgumentException("purchaseId positive");
        if (productId <= 0) throw new IllegalArgumentException("productId positive");
        if (startDate == null || endDate == null) throw new IllegalArgumentException("dates required");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("endDate before startDate");
        if (endDate.isBefore(startDate.plusYears(1)) && endDate.isAfter(startDate)) {

            throw new IllegalArgumentException("warranty must be at least 1 year");
        }
        this.warrantyId = warrantyId;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        extent.add(this);
    }

    // derived
    public boolean isValid() {
        return LocalDate.now().isEqual(startDate) || (LocalDate.now().isAfter(startDate) && LocalDate.now().isBefore(endDate));
    }

    // prolong warranty
    public void extendYears(int years) {
        if (years <= 0) throw new IllegalArgumentException("years must be positive");
        this.endDate = this.endDate.plusYears(years);
    }

    public int getWarrantyId() { return warrantyId; }
    public int getPurchaseId() { return purchaseId; }
    public int getProductId() { return productId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }


    public static List<Warranty> getExtent() { return extent; }
    public static void saveExtent() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            out.writeObject(extent);
        }
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Warranty>) in.readObject();
        }
    }
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warranty)) return false;
        Warranty w = (Warranty) o;
        return warrantyId == w.warrantyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(warrantyId);
    }
}

package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Report> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "report_extent.ser";

    private final Manager manager;
    private final LocalDateTime dateGenerated;
    private final String content;

    public Report(Manager manager, String content) {
        if (manager == null) throw new IllegalArgumentException("manager cannot be null");
        if (content == null) throw new IllegalArgumentException("content cannot be null");
        if (content.isBlank()) throw new IllegalArgumentException("content cannot be empty or blank");
        if (content.length() < 10) throw new IllegalArgumentException("content must be at least 10 characters");
        if (content.length() > 10000) throw new IllegalArgumentException("content cannot exceed 10000 characters");
        
        this.manager = manager;
        this.content = content.trim();
        this.dateGenerated = LocalDateTime.now();
        extent.add(this);
    }

    public LocalDateTime getDateGenerated() { return dateGenerated; }
    public String getContent() { return content; }

    public static List<Report> getExtent() { return new ArrayList<>(extent); }

    public static void saveExtent() throws IOException {
        ExtentManager.saveExtent(extent, EXTENT_FILE);
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        extent = ExtentManager.loadExtent(EXTENT_FILE);
    }
    // For testing purposes only - clears extent
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        Report r = (Report) o;
        return manager.equals(r.manager) && dateGenerated.equals(r.getDateGenerated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, dateGenerated);
    }
}

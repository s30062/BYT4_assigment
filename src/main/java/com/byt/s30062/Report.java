package com.byt.s30062;


import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Report> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "report_extent.ser";

    private final int reportId;
    private final int managerId;
    private final LocalDateTime generatedAt;
    private final String content;

    public Report(int reportId, int managerId, String content) {
        if (reportId <= 0) throw new IllegalArgumentException("reportId positive");
        if (managerId <= 0) throw new IllegalArgumentException("managerId positive");
        if (content == null) throw new IllegalArgumentException("content required");
        this.reportId = reportId;
        this.managerId = managerId;
        this.content = content;
        this.generatedAt = LocalDateTime.now();
        extent.add(this);
    }

    public int getReportId() { return reportId; }
    public int getManagerId() { return managerId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public String getContent() { return content; }

    public static List<Report> getExtent() { return extent; }
    public static void saveExtent() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            out.writeObject(extent);
        }
    }
    @SuppressWarnings("unchecked")
    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Report>) in.readObject();
        }
    }
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        Report r = (Report) o;
        return reportId == r.reportId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }
}

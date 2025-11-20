package com.byt.s30062.model;


import com.byt.s30062.model.enums.Line;
import com.byt.s30062.model.enums.PortType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Device extends Product {
    private Line line;
    private List<PortType> ports = new ArrayList<>();
    private LocalDate releaseDate;


    public Device(Line line, List<PortType> ports, LocalDate releaseDate, String name, String color, double initialPrice) {
        super(name, color, initialPrice);
        if (line == null) throw new IllegalArgumentException("line cannot be null");
        if (ports == null) throw new IllegalArgumentException("ports list cannot be null");
        if (ports.isEmpty()) throw new IllegalArgumentException("device must have at least one port");
        if (ports.contains(null)) throw new IllegalArgumentException("ports list cannot contain null values");
        if (releaseDate == null) throw new IllegalArgumentException("release date cannot be null");
        if (releaseDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("release date cannot be in the future");
        if (releaseDate.isBefore(LocalDate.of(1970, 1, 1))) throw new IllegalArgumentException("release date cannot be before 1970");
        
        this.line = line;
        this.ports = new ArrayList<>(ports); // defensive copy
        this.releaseDate = releaseDate;
    }

    public Line getLine() {
        return line;
    }


    public List<PortType> getPorts() {
        return new ArrayList<>(ports); // defensive copy
    }


    public LocalDate getReleaseDate() {
        return releaseDate;
    }


}

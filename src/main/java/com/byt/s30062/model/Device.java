package com.byt.s30062.model;


import com.byt.s30062.model.enums.Line;
import com.byt.s30062.model.enums.PortType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Device extends Product {
    private Line line;
    private List<PortType> ports;
    private LocalDate releaseDate;
    private Set<Accessory> accessories = new HashSet<>();


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

    public Set<Accessory> getAccessories(){
        return new HashSet<>(accessories);
    }

    // Public API: add/remove from Device delegate to Accessory to keep qualified map authoritative
    public void addAccessory(Accessory accessory){
        if (accessory == null) throw new IllegalArgumentException("accessory cannot be null");
        // Delegates to Accessory; Accessory will call back linkAccessory
        accessory.addDesignedFor(this);
    }

    public void removeAccessory(Accessory accessory){
        if (accessory == null) return;
        // Remove by qualifier (device name); Accessory will call back unlinkAccessory
        accessory.removeDesignedFor(this.getName());
    }

    // Called by Accessory to maintain reverse connection (package-private intended usage)
    void linkAccessory(Accessory accessory){
        accessories.add(accessory);
    }

    // Called by Accessory to maintain reverse connection (package-private intended usage)
    void unlinkAccessory(Accessory accessory){
        accessories.remove(accessory);
    }

}

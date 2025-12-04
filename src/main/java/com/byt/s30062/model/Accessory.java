package com.byt.s30062.model;

import com.byt.s30062.model.enums.AccessoryType;

import java.util.*;

public class Accessory extends Product {

    private AccessoryType type;

    // Qualified association: <DeviceName, Device>
    private final Map<String, Device> designedFor = new HashMap<>();

    public Accessory(String name, String color, double initialPrice, AccessoryType type) {
        super(name, color, initialPrice);
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        
        this.type = type;
    }

    public AccessoryType getType() { return type; }

    public void setType(AccessoryType type) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        this.type = type;
    }

    // Qualified association: add device with device name as qualifier
    public void addDesignedFor(Device device){
        if (device == null) throw new IllegalArgumentException("device cannot be null");
        if (designedFor.containsKey(device.getName()))
            throw new IllegalArgumentException("The accessory is already designed for specified Device!");
        designedFor.put(device.getName(), device);
        device.linkAccessory(this); // maintain reverse link without search on Device side
    }

    // Remove association by qualifier (device name), keeping reverse link in sync
    public void removeDesignedFor(String deviceName){
        Device device = designedFor.remove(deviceName);
        if (device != null) {
            device.unlinkAccessory(this);
        }
    }

    // Retrieve device by qualifier (device name)
    public Device getDesignedForByName(String deviceName){
        return designedFor.getOrDefault(deviceName, null);
    }

    // Return a copy to preserve encapsulation
    public Map<String, Device> getAllDesignedFor(){
        return new HashMap<>(designedFor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Accessory)) return false;
        Accessory a = (Accessory) o;
        return super.equals(o) && type == a.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}

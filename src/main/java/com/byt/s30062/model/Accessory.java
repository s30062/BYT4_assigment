package com.byt.s30062.model;

import com.byt.s30062.model.enums.AccessoryType;
import java.util.Objects;

public class Accessory extends Product {

    private AccessoryType type;

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

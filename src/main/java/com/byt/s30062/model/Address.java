package com.byt.s30062.model;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private String street;
    private String city;
    private String postalCode;
    private String country;

    public Address(String street, String city, String postalCode, String country) {
        if (street == null) throw new IllegalArgumentException("street cannot be null");
        if (street.isBlank()) throw new IllegalArgumentException("street cannot be empty or blank");
        if (street.length() > 100) throw new IllegalArgumentException("street cannot exceed 100 characters");
        
        if (city == null) throw new IllegalArgumentException("city cannot be null");
        if (city.isBlank()) throw new IllegalArgumentException("city cannot be empty or blank");
        if (city.length() > 50) throw new IllegalArgumentException("city cannot exceed 50 characters");
        
        if (postalCode == null) throw new IllegalArgumentException("postal code cannot be null");
        if (postalCode.isBlank()) throw new IllegalArgumentException("postal code cannot be empty or blank");
        if (postalCode.length() > 20) throw new IllegalArgumentException("postal code cannot exceed 20 characters");
        
        if (country == null) throw new IllegalArgumentException("country cannot be null");
        if (country.isBlank()) throw new IllegalArgumentException("country cannot be empty or blank");
        if (country.length() > 50) throw new IllegalArgumentException("country cannot exceed 50 characters");
        
        this.street = street.trim();
        this.city = city.trim();
        this.postalCode = postalCode.trim();
        this.country = country.trim();
    }


    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address other = (Address) o;
        return street.equals(other.street) && city.equals(other.city)
                && postalCode.equals(other.postalCode) && country.equals(other.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postalCode, country);
    }

    @Override
    public String toString() {
        return street + ", " + city + ", " + postalCode + ", " + country;
    }
}

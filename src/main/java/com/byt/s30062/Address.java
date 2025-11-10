package com.byt.s30062;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private String street;
    private String city;
    private String postalCode;
    private String country;

    public Address(String street, String city, String postalCode, String country) {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("Street required");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("City required");
        if (postalCode == null || postalCode.isBlank()) throw new IllegalArgumentException("Postal code required");
        if (country == null || country.isBlank()) throw new IllegalArgumentException("Country required");
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
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

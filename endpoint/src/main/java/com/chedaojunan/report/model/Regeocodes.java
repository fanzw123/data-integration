package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class Regeocodes {

    @JsonProperty(value = "formattedAddress")
    private String formattedAddress;
    @JsonProperty(value = "addressComponent")
    private AddressComponent addressComponent;

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setAddressComponent(AddressComponent addressComponent) {
        this.addressComponent = addressComponent;
    }
    public AddressComponent getAddressComponent() {
        return addressComponent;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(formattedAddress)
                .append(addressComponent)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Regeocodes) == false) {
            return false;
        }
        Regeocodes rhs = ((Regeocodes) other);
        return new EqualsBuilder()
                .append(formattedAddress, rhs.formattedAddress)
                .append(addressComponent, rhs.addressComponent)
                .isEquals();
    }

}
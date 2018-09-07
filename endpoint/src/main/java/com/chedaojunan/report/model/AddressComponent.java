package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class AddressComponent {

    @JsonProperty(value = "country")
    private String country;
    @JsonProperty(value = "province")
    private String province;
    @JsonProperty(value = "city")
    private String city;
    @JsonProperty(value = "citycode")
    private String citycode;
    @JsonProperty(value = "district")
    private String district;
    @JsonProperty(value = "adcode")
    private String adcode;
    @JsonProperty(value = "township")
    private String township;
    @JsonProperty(value = "towncode")
    private String towncode;
    @JsonProperty(value = "neighborhood")
    private String neighborhood;
    @JsonProperty(value = "building")
    private String building;
    @JsonProperty(value = "streetNumber")
    private String streetNumber;
    @JsonProperty(value = "businessAreas")
    private String businessAreas;

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getProvince() {
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
    public String getCitycode() {
        return citycode;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
    public String getDistrict() {
        return district;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }
    public String getAdcode() {
        return adcode;
    }

    public void setTownship(String township) {
        this.township = township;
    }
    public String getTownship() {
        return township;
    }

    public void setTowncode(String towncode) {
        this.towncode = towncode;
    }
    public String getTowncode() {
        return towncode;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    public String getNeighborhood() {
        return neighborhood;
    }

    public void setBuilding(String building) {
        this.building = building;
    }
    public String getBuilding() {
        return building;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
    public String getStreetNumber() {
        return streetNumber;
    }

    public void setBusinessAreas(String businessAreas) {
        this.businessAreas = businessAreas;
    }
    public String getBusinessAreas() {
        return businessAreas;
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
                .append(country)
                .append(province)
                .append(city)
                .append(citycode)
                .append(district)
                .append(adcode)
                .append(township)
                .append(towncode)
                .append(neighborhood)
                .append(building)
                .append(streetNumber)
                .append(businessAreas)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AddressComponent) == false) {
            return false;
        }
        AddressComponent rhs = ((AddressComponent) other);
        return new EqualsBuilder()
                .append(country, rhs.country)
                .append(province, rhs.province)
                .append(city, rhs.city)
                .append(citycode, rhs.citycode)
                .append(district, rhs.district)
                .append(adcode, rhs.adcode)
                .append(township, rhs.township)
                .append(towncode, rhs.towncode)
                .append(neighborhood, rhs.neighborhood)
                .append(building, rhs.building)
                .append(streetNumber, rhs.streetNumber)
                .append(businessAreas, rhs.businessAreas)
                .isEquals();
    }

}
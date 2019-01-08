/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.builder.csv;

/**
 *
 * @author gregp
 */
public class WorldCity {
    private CharSequence country;//col 0
    private CharSequence city;//col 1
    private CharSequence accentCity;//col 2
    private CharSequence region;//col 3
    private int population;//col 4
    private double longitude;//col 5
    private CharSequence longitudeCharSequence;//col 5
    private double latitude;//col 6
    private CharSequence latitudeCharSequence;//col 6

    public CharSequence getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CharSequence getCity() {
        return city;
    }

    public void setCity(CharSequence city) {
        this.city = city;
    }

    public CharSequence getAccentCity() {
        return accentCity;
    }

    public void setAccentCity(CharSequence accentCity) {
        this.accentCity = accentCity;
    }

    public CharSequence getRegion() {
        return region;
    }

    public void setRegion(CharSequence region) {
        this.region = region;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public CharSequence getLongitudeCharSequence() {
        return longitudeCharSequence;
    }

    public void setLongitudeCharSequence(CharSequence longitudeCharSequence) {
        this.longitudeCharSequence = longitudeCharSequence;
    }

    public CharSequence getLatitudeCharSequence() {
        return latitudeCharSequence;
    }

    public void setLatitudeCharSequence(CharSequence latitudeCharSequence) {
        this.latitudeCharSequence = latitudeCharSequence;
    }
    
    public int getInt(){
        return 0;
    }

    @Override
    public String toString() {
        return "WorldCity{" + "country=" + country + ", city=" + city + ", AccentCity=" + accentCity + ", region=" + region + ", population=" + population + ", longitude=" + longitude + ", longitudeCharSequence=" + longitudeCharSequence + ", latitude=" + latitude + ", latitudeCharSequence=" + latitudeCharSequence + '}';
    }
    
}

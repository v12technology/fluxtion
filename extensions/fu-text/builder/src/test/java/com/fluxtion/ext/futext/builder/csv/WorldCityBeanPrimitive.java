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
public class WorldCityBeanPrimitive {
    private CharSequence country;//col 0
    private CharSequence City;//col 1
    private CharSequence AccentCity;//col 2
    private CharSequence Region;//col 3
    private CharSequence Population;//col 4
    private CharSequence longitude;//col 5
    private double latitude;//col 6

    public CharSequence getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CharSequence getCity() {
        return City;
    }

    public void setCity(CharSequence City) {
        this.City = City;
    }

    public CharSequence getAccentCity() {
        return AccentCity;
    }

    public void setAccentCity(CharSequence AccentCity) {
        this.AccentCity = AccentCity;
    }

    public CharSequence getRegion() {
        return Region;
    }

    public void setRegion(CharSequence Region) {
        this.Region = Region;
    }

    public CharSequence getPopulation() {
        return Population;
    }

    public void setPopulation(CharSequence Population) {
        this.Population = Population;
    }

    public CharSequence getLongitude() {
        return longitude;
    }

    public void setLongitude(CharSequence longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "WorldCityBean{" + "country=" + country + ", City=" + City + ", AccentCity=" + AccentCity + ", Region=" + Region + ", Population=" + Population + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }


    
}

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
public class WorldCityBean {
    private CharSequence country;//col 0
    private CharSequence city;//col 1
    private CharSequence accentCity;//col 2
    private CharSequence region;//col 3
    private CharSequence population;//col 4
    private CharSequence longitude;//col 5
    private CharSequence latitude;//col 6

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

    public CharSequence getPopulation() {
        return population;
    }

    public void setPopulation(CharSequence population) {
        this.population = population;
    }

    public CharSequence getLongitude() {
        return longitude;
    }

    public void setLongitude(CharSequence longitude) {
        this.longitude = longitude;
    }

    public CharSequence getLatitude() {
        return latitude;
    }

    public void setLatitude(CharSequence latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "WorldCityBean{" + "country=" + country + ", city=" + city + ", accentCity=" + accentCity + ", region=" + region + ", population=" + population + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }


    
}

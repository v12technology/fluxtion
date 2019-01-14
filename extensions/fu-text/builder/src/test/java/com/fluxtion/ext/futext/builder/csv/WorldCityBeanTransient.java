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

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.Event;

/**
 *
 * @author gregp
 */
public class WorldCityBeanTransient extends Event{
    private CharSequence country;
    private CharSequence city;
    private transient CharSequence accentCity;//derived - not in csv
    private transient CharSequence region;//derived - not in csv
    private CharSequence population;
    private transient CharSequence longitude;//derived - not in csv
    private transient CharSequence latitude;//derived - not in csv

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
    
    @OnEvent
    public void deriveValues(){
        if(accentCity==null){
            accentCity = "none provided";
        }
    }

    @Override
    public String toString() {
        return "WorldCityBean{" + "country=" + country + ", City=" + city + ", AccentCity=" + accentCity + ", Region=" + region + ", Population=" + population + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }


    
}

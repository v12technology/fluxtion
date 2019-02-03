/*
 * Copyright (C) 2018 greg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.ext.futext.example.flightdelay;

/**
 * Bean to hold the data for a grouping record in the flight analysis pipeline.
 *
 * @author greg
 */
public class CarrierDelay {

    private String carrierId;
    private int avgDelay;
    private int totalFlights;
    private int totalDelayMins;

    public String getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

    public int getAvgDelay() {
        return avgDelay;
    }

    public void setAvgDelay(int avgDelay) {
        this.avgDelay = avgDelay;
    }

    public int getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(int totalFlights) {
        this.totalFlights = totalFlights;
    }

    public int getTotalDelayMins() {
        return totalDelayMins;
    }

    public void setTotalDelayMins(int totalDelayMins) {
        this.totalDelayMins = totalDelayMins;
    }

    @Override
    public String toString() {
        return "CarrierDelay{" + "carrierId=" + carrierId + ", avgDelay=" + avgDelay + ", totalFlights=" + totalFlights + ", totalDelayMins=" + totalDelayMins + '}';
    }

}

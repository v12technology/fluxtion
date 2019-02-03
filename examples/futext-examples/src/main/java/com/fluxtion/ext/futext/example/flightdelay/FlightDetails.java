/*
 * Copyright (C) 2019 gregp
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

import com.fluxtion.ext.declarative.api.util.StringCache;

/**
 * A bean representing the flight details for a single arrival.
 * @author gregp
 */
public class FlightDetails {

    private CharSequence carrier;
    private StringCache cache = new StringCache();
    private int delay;

    public String getCarrier() {
        return carrier.toString();
    }

    public void setCarrier(CharSequence carrier) {
        this.carrier = cache.intern(carrier);
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

}

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
package com.fluxtion.ext.futext.builder.push;

/**
 *
 * @author greg
 */
public class SaleItem {
    private transient int quantity;
    private transient double cost;

    
    public void setQuantity(int quantity) {
        System.out.println("setQuantity::" + quantity);
        this.quantity = quantity;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "SaleItem{" + "quantity=" + quantity + ", cost=" + cost + '}';
    }
    
}

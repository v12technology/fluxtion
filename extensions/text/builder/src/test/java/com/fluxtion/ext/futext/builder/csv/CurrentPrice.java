/*
 * Copyright (C) 2019 V12 Technology Ltd.
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

import com.fluxtion.api.annotations.EventHandler;

/**
 *
 * @author V12 Technology Ltd.
 */
public class CurrentPrice {

    private PurchaseBean purchase;
    
    @EventHandler
    public boolean priceUpdate(PurchaseBean purchase){
        this.purchase = purchase;
        return true;
    }

    public String getName() {
        return purchase.getName();
    }

    public double getPrice() {
        return purchase.getPrice();
    }
    
    
    
}

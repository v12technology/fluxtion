/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.group;

/**
 *
 * @author greg
 */
public class DeliverySummary {

    private String customerId;
    private String productId;
    private double valueInLocalCcy;
    private double valueInDollars;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getValueInLocalCcy() {
        return valueInLocalCcy;
    }

    public void setValueInLocalCcy(double valueInLocalCcy) {
        this.valueInLocalCcy = valueInLocalCcy;
    }

    public double getValueInDollars() {
        return valueInDollars;
    }

    public void setValueInDollars(double valueInDollars) {
        this.valueInDollars = valueInDollars;
    }

    @Override
    public String toString() {
        return "DeliverySummary{" + "customerId=" + customerId + ", productId=" + productId + ", valueInLocalCcy=" + valueInLocalCcy + ", valueInDollars=" + valueInDollars + '}';
    }

}

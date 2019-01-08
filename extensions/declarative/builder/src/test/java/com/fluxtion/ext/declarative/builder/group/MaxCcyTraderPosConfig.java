package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.api.event.Event;

/**
 *
 * @author gregp
 */
public class MaxCcyTraderPosConfig extends Event {

    public static final Class<MaxCcyTraderPosConfig> TRADER_POS_CFG = MaxCcyTraderPosConfig.class;
    public int traderId;
    public String ccyPairManaged;
    public int maxPosition;

    public MaxCcyTraderPosConfig() {
    }

    public MaxCcyTraderPosConfig(int traderId, String ccyPair) {
        this.traderId = traderId;
        this.ccyPairManaged = ccyPair;
        this.maxPosition = (int) 1e6;
    }

    public int getTraderId() {
        return traderId;
    }

    public void setTraderId(int traderId) {
        this.traderId = traderId;
    }

    public String getCcyPairManaged() {
        return ccyPairManaged;
    }

    public void setCcyPairManaged(String ccyPairManaged) {
        this.ccyPairManaged = ccyPairManaged;
    }

    public int getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(int maxPosition) {
        this.maxPosition = maxPosition;
    }

    @Override
    public String toString() {
        return "MaxCcyTraderPosConfig{" + "traderId=" + traderId + ", ccyPairManaged=" + ccyPairManaged + ", maxPosition=" + maxPosition + '}';
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.learning.streaming;

import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import org.junit.Test;
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import static com.fluxtion.ext.streaming.builder.factory.FilterByNotificationBuilder.filterOnNotify;

/**
 *
 * @author gregp
 */
public class FilterByNotificationTest extends BaseSepInprocessTest {

    @Test
    public void filterEvent() {
        reuseSep = true;
        fixedPkg = true;
        sep(c -> {
            filterOnNotify(select(Double.class), filter("tick"::equalsIgnoreCase))
                .log("update:");
        });
        onEvent("tick");
        onEvent(1.0);
        onEvent(2.0);
        onEvent(3.0);
        onEvent("tick");
        onEvent(4.0);
    }
    
}

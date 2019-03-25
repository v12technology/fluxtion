package com.fluxtion.ext.declarative.builder.push;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.builder.factory.PushBuilder;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.helpers.DealEvent;
import com.fluxtion.ext.declarative.builder.helpers.TradeEvent;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.declarative.builder.stream.StreamBuilder;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Push_2Test extends StreamInprocessTest{
 
    @Test
    public void pushNotification(){
        fixedPkg = true;
        sep((c) -> {
            Wrapper<DealEvent> inSD = select(DealEvent.class);
            Wrapper<DataEvent> inDA = select(DataEvent.class);
            UpdateCount counter = c.addNode(new UpdateCount(), "counter");
            //push
            PushBuilder.push(inSD, counter);
            PushBuilder.push(inDA, counter);
        });
        
        sep.onEvent(new DealEvent());
        sep.onEvent(new DataEvent());
        sep.onEvent(new DealEvent());
        sep.onEvent(new TradeEvent());
        UpdateCount counter = getField("counter");
        Assert.assertThat(counter.count, is(3));
    }
    
    @Test
    public void pushNotificationDataViaStream(){
        fixedPkg = true;
        sep((c) -> {
            Wrapper<DealEvent> inSD = select(DealEvent.class);
            Wrapper<DataEvent> inDA = select(DataEvent.class);
            UpdateCount counter = c.addNode(new UpdateCount(), "counter");
            PushTarget pushTarget = c.addNode(new PushTarget(), "target");
            //push
            PushBuilder.push(inSD, counter);
            PushBuilder.push(inDA, counter);
            //push data
            PushBuilder.push(counter::getCount, pushTarget::setVal);
            
            StreamBuilder.stream(pushTarget).id("streamedCumSum")
                    .filter(PushTarget::getVal, gt(25))
                    .console("[above 25]");
        });
        
        sep.onEvent(new DealEvent());
        sep.onEvent(new DataEvent());
        sep.onEvent(new DealEvent());
        sep.onEvent(new TradeEvent());
        UpdateCount counter = getField("counter");
        PushTarget target = getField("target");
        Assert.assertThat(counter.count, is(3));
        Assert.assertThat(target.count, is(3));
        Assert.assertThat(target.val, is(30));
    }
    
    @Test
    public void pushNotificationData(){
        fixedPkg = true;
        sep((c) -> {
            Wrapper<DealEvent> inSD = select(DealEvent.class);
            Wrapper<DataEvent> inDA = select(DataEvent.class);
            UpdateCount counter = c.addNode(new UpdateCount(), "counter");
            PushTarget pushTarget = c.addNode(new PushTarget(), "target");
            //push
            PushBuilder.push(inSD, counter);
            PushBuilder.push(inDA, counter);
            //push data
            PushBuilder.push(counter::getCount, pushTarget::setVal);
            
        });
        
        sep.onEvent(new DealEvent());
        sep.onEvent(new DataEvent());
        sep.onEvent(new DealEvent());
        sep.onEvent(new TradeEvent());
        UpdateCount counter = getField("counter");
        PushTarget target = getField("target");
        Assert.assertThat(counter.count, is(3));
        Assert.assertThat(target.count, is(3));
        Assert.assertThat(target.val, is(30));
    }
    
    
    public static class UpdateCount{
        public int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
        
       @OnEvent
       public void update(){
           count++;
       }
        
    }
    
    public static class PushTarget{
        public int count;
        public int val;
        
       @OnEvent
       public void update(){
           count++;
       }

        public void setVal(int val) {
            this.val = val*10;
        }

        public int getCount() {
            return count;
        }

        public int getVal() {
            return val;
        }
        
    }

}

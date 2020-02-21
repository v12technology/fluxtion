package com.fluxtion.ext.declarative.builder.function;

import com.fluxtion.ext.streaming.api.MergingWrapper;
import static com.fluxtion.ext.streaming.api.MergingWrapper.merge;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.lt;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.declarative.builder.helpers.MyData;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.builder.factory.LibraryFunctionsBuilder.cumSum;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class NumericFunctionTest extends StreamInprocessTest {

    @Test
    public void complexFilterTest() {
        sep((c) -> {
            MergingWrapper<MyData> merged = merge(select(MyData.class, "EU", "EC"));
            merged.filter(MyData::getIntVal, gt(200)).map(cumSum(), MyData::getIntVal).id("cumGt200");
            merged.filter(MyData::getIntVal, lt(200)).map(cumSum(), MyData::getIntVal).id("cumLt200");
            select(MyData.class).map(cumSum(), MyData::getIntVal).id("cumSumAll");
        });

        Number cumGt200 = getWrappedField("cumGt200");
        Number cumLt200 = getWrappedField("cumLt200");
        Number cumSumAll = getWrappedField("cumSumAll");

        MyData de1 = new MyData(600, 600, "EU");
        sep.onEvent(de1);
        sep.onEvent(de1);
        Assert.assertThat(cumGt200.intValue(), is(1200));
        Assert.assertThat(cumLt200.intValue(), is(0));
        Assert.assertThat(cumSumAll.intValue(), is(1200));

        de1 = new MyData(100, 100, "EU");
        sep.onEvent(de1);
        Assert.assertThat(cumGt200.intValue(), is(1200));
        Assert.assertThat(cumLt200.intValue(), is(100));
        Assert.assertThat(cumSumAll.intValue(), is(1300));

        //fire some events for EQ - should be ignored
        de1 = new MyData(600, 600, "EQ");
        sep.onEvent(de1);
        sep.onEvent(de1);
        Assert.assertThat(cumGt200.intValue(), is(1200));
        Assert.assertThat(cumLt200.intValue(), is(100));
        Assert.assertThat(cumSumAll.intValue(), is(2500));

        de1 = new MyData(100, 100, "EC");
        sep.onEvent(de1);
        sep.onEvent(de1);
        Assert.assertThat(cumGt200.intValue(), is(1200));
        Assert.assertThat(cumLt200.intValue(), is(300));
        Assert.assertThat(cumSumAll.intValue(), is(2700));

    }

    @Test
    public void pushTest() {
        sep((c) -> {
            ResultReceiver recv = c.addPublicNode(new ResultReceiver(), "receiver");
            select(MyData.class)
                    .push(MyData::getByteVal, recv::setMyByte)
                    .push(MyData::getCharVal, recv::setMyChar)
                    .push(MyData::getShortVal, recv::setMyShort)
                    .push(MyData::getIntVal, recv::setMyInt)
                    .push(MyData::getDoubleVal, recv::setMyDouble);
        });

        ResultReceiver recv = getField("receiver");
        MyData de1 = new MyData('a', 600, "EU");
        sep.onEvent(de1);

        Assert.assertThat(recv.getMyByte(), is((byte)97));
        Assert.assertThat(recv.getMyChar(), is('a'));
        Assert.assertThat(recv.getMyShort(), is((short)97));
        Assert.assertThat(recv.getMyInt(), is(97));
        Assert.assertThat(recv.getMyDouble(), is(600.0));
    }

}

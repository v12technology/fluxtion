package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrimitiveStreamBuilderTest extends MultipleSepTargetInProcessTest {
    public PrimitiveStreamBuilderTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void notifyTest() {
//        addAuditor();
        StreamBuildTest.NotifyAndPushTarget notifyAndPushTarget = new StreamBuildTest.NotifyAndPushTarget();
        sep(c -> subscribe(String.class)
                .filter(NumberUtils::isNumber)
                .mapToInt(StreamBuildTest::parseInt)
                .mapToInt(PrimitiveStreamBuilderTest::multiplyX10)
                .filter(PrimitiveStreamBuilderTest::gt10)
                .notify(notifyAndPushTarget)
                .push(notifyAndPushTarget::setPushValue)
        );
        StreamBuildTest.NotifyAndPushTarget notifyTarget = getField("notifyTarget");
        assertThat(0, is(notifyTarget.getOnEventCount()));
        onEvent("sdsdsd 230");
        onEvent("230");
        assertThat(notifyTarget.getOnEventCount(), is(1));
        assertThat(notifyTarget.getPushValue(), is(2300));
    }

    public static int multiplyX10(int input){
        System.out.println("result:" + input * 10);
        return input * 10;
    }

    public static Boolean gt10(int i) {
        return i > 10;
    }

}

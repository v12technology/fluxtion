package com.fluxtion.extension.functional.test;

import com.fluxtion.extension.declarative.builder.util.LambdaReflection;
import com.fluxtion.extension.functional.helpers.StaticFunctions;
import java.lang.reflect.Modifier;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class SerializableLambdaTest {

    @Test
    public void lmabdaTest1() {
        StaticFunctions sf_0 = new StaticFunctions();
        StaticFunctions sf_1 = new StaticFunctions();
        ref(StaticFunctions::add, null);
        ref(StaticFunctions::intToString, null);
        ref(sf_0::instanceMethod, sf_0);
        ref(sf_1::instanceMethod, sf_1);
    }

    public static <T> LambdaReflection.SerializableConsumer<T> ref(LambdaReflection.SerializableConsumer<T> r, Object instance) {
        if (!Modifier.isStatic(r.method().getModifiers())) {
            assertEquals(instance, r.captured()[0]);
        }else{
            assertEquals(0, r.captured().length);
        }
        return r;
    }

}

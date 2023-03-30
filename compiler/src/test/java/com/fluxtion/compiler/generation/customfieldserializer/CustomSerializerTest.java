package com.fluxtion.compiler.generation.customfieldserializer;

import com.fluxtion.compiler.generation.serialiser.FieldContext;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;

public class CustomSerializerTest extends MultipleSepTargetInProcessTest {

    public CustomSerializerTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void durationTest() {
//        writeSourceFile = true;
        sep(c -> {
            DurationHolder durationHolder = new DurationHolder(Duration.ofSeconds(200, 5_000_000));
            durationHolder.setDurationAsProperty(Duration.ofMillis(750));
            c.addNode(durationHolder, "node");
        });
        DurationHolder durationHolder = getField("node", DurationHolder.class);
        Assert.assertEquals(Duration.ofSeconds(200, 5_000_000), durationHolder.getDuration());
        Assert.assertEquals(Duration.ofMillis(750), durationHolder.getDurationAsProperty());
    }

    @Test
    public void addCustomClass() {
//        writeSourceFile = true;
        sep(c -> {
            c.addNode(new ObjectHolder(WithBuilder.createWithBuilder("ID_1")), "holder");
            c.addClassSerializer(WithBuilder.class, this::genWithBuilder);
        });
        WithBuilder custom = getField("holder", ObjectHolder.class).getDelegate();
        Assert.assertEquals("ID_1", custom.getId());
    }

    @Test
    public void addCustomClassSerialiserFromFactory() {
//        writeSourceFile = true;
        sep(c -> {
            c.addNode(new ObjectHolder(WithBuilderFactory.createWithBuilder("ID_1")), "holder");
        });
        WithBuilderFactory custom = getField("holder", ObjectHolder.class).getDelegate();
        Assert.assertEquals("ID_1", custom.getId());
    }

    private String genWithBuilder(FieldContext<WithBuilder> fieldContext) {
        fieldContext.getImportList().add(WithBuilder.class);
        WithBuilder withBuilder = fieldContext.getInstanceToMap();
        return "WithBuilder.createWithBuilder(\"" + withBuilder.getId() + "\")";
    }

    public static class DurationHolder {

        private final Duration duration;

        private Duration durationAsProperty;

        public Duration getDurationAsProperty() {
            return durationAsProperty;
        }

        public void setDurationAsProperty(Duration durationAsProperty) {
            this.durationAsProperty = durationAsProperty;
        }

        public DurationHolder(Duration duration) {
            this.duration = duration;
        }

        @OnEventHandler
        public boolean update(String in) {
            return true;
        }

        public Duration getDuration() {
            return duration;
        }
    }

    public static class WithBuilder {
        private final String id;

        private WithBuilder(String id) {
            this.id = id;
        }

        public static WithBuilder createWithBuilder(String id) {
            return new WithBuilder(id);
        }

        public String getId() {
            return id;
        }
    }

    public static class WithBuilderFactory {
        private final String id;

        private WithBuilderFactory(String id) {
            this.id = id;
        }

        public static WithBuilderFactory createWithBuilder(String id) {
            return new WithBuilderFactory(id);
        }

        public String getId() {
            return id;
        }
    }

    public static class ObjectHolder {
        private final Object delegate;

        public ObjectHolder(Object delegate) {
            this.delegate = delegate;
        }

        public <T> T getDelegate() {
            return (T) delegate;
        }

    }
}

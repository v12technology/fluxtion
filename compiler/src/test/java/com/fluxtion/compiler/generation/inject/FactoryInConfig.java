package com.fluxtion.compiler.generation.inject;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.event.Signal.IntSignal;
import com.fluxtion.runtime.node.NamedNode;
import org.junit.Assert;
import org.junit.Test;

public class FactoryInConfig extends MultipleSepTargetInProcessTest {

    public FactoryInConfig(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void injectFactoryAsConfig() {
        sep(c -> {
            c.addNode(
                    Radiator.build("lounge_rad1", "lounge_sensor"),
                    Radiator.build("lounge_rad2", "lounge_sensor"),
                    Radiator.build("lounge_rad3", "lounge_sensor"),
                    Radiator.build("kitchen_rad1", "kitchen_sensor")
            );
            c.registerInjectable("lounge_sensor", new RoomSensor("lounge_temp_sensor"))
                    .registerInjectable("shed_sensor", new RoomSensor("shed_temp_sensor"))
                    .registerInjectable("bedroom_sensor", new RoomSensor("bedroom_temp_sensor"))
                    .registerInjectable("kitchen_sensor", new RoomSensor("kitchen_temp_sensor"));
        });

        sep.publishIntSignal("lounge_temp_sensor", 10);
        Assert.assertTrue(getField("lounge_rad1", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad2", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad3", Radiator.class).isOn());
        Assert.assertFalse(getField("kitchen_rad1", Radiator.class).isOn());


        sep.publishIntSignal("lounge_temp_sensor", 22);
        Assert.assertFalse(getField("lounge_rad1", Radiator.class).isOn());
        Assert.assertFalse(getField("lounge_rad2", Radiator.class).isOn());
        Assert.assertFalse(getField("lounge_rad3", Radiator.class).isOn());
        Assert.assertFalse(getField("kitchen_rad1", Radiator.class).isOn());


        sep.publishIntSignal("lounge_temp_sensor", 23);
        Assert.assertFalse(getField("lounge_rad1", Radiator.class).isOn());
        Assert.assertFalse(getField("lounge_rad2", Radiator.class).isOn());
        Assert.assertFalse(getField("lounge_rad3", Radiator.class).isOn());
        Assert.assertFalse(getField("kitchen_rad1", Radiator.class).isOn());


        sep.publishIntSignal("lounge_temp_sensor", 15);
        Assert.assertTrue(getField("lounge_rad1", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad2", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad3", Radiator.class).isOn());
        Assert.assertFalse(getField("kitchen_rad1", Radiator.class).isOn());


        sep.publishIntSignal("kitchen_temp_sensor", 19);
        Assert.assertTrue(getField("lounge_rad1", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad2", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad3", Radiator.class).isOn());
        Assert.assertTrue(getField("kitchen_rad1", Radiator.class).isOn());

        sep.publishIntSignal("kitchen_temp_sensor", 26);
        Assert.assertTrue(getField("lounge_rad1", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad2", Radiator.class).isOn());
        Assert.assertTrue(getField("lounge_rad3", Radiator.class).isOn());
        Assert.assertFalse(getField("kitchen_rad1", Radiator.class).isOn());
    }

    @Test
    public void injectFactoryByInterfaceAsConfig() {
        sep(c -> {
            c.addNode(
                    new Display("display_Kitchen", "kitchen_sensor")
            );
            c.registerInjectable("lounge_sensor", new RoomSensor("lounge_temp_sensor"))
                    .registerInjectable("shed_sensor", new RoomSensor("shed_temp_sensor"))
                    .registerInjectable("kitchen_sensor", new RoomSensor("kitchen_temp_sensor"));
        });

        sep.publishIntSignal("kitchen_temp_sensor", 19);
        Assert.assertTrue(getField("display_Kitchen", Display.class).isOn());

        sep.publishIntSignal("kitchen_temp_sensor", 25);
        Assert.assertFalse(getField("display_Kitchen", Display.class).isOn());
    }

    public interface Sensor {
        boolean isOn();
    }

    public static class Radiator implements NamedNode {
        private final String radiatorName;
        @Inject(factoryVariableName = "sensorName")
        private final RoomSensor roomSensor;

        private transient String sensorName;

        public Radiator(String radiatorName, RoomSensor roomSensor) {
            this.radiatorName = radiatorName;
            this.roomSensor = roomSensor;
        }

        public static Radiator build(String radiatorName, String sensorName) {
            Radiator radiator = new Radiator(radiatorName, null);
            radiator.sensorName = sensorName;
            return radiator;
        }

        @OnTrigger
        public boolean triggerRadiator() {
            return false;
        }

        @Override
        public String getName() {
            return radiatorName;
        }

        public boolean isOn() {
            return roomSensor.on;
        }
    }

    public static class RoomSensor implements Sensor {
        private final String name;
        private boolean on = false;

        public RoomSensor(@AssignToField("name") String name) {
            this.name = name;
        }

        @OnEventHandler(filterVariable = "name")
        public boolean tempSignal(IntSignal intSignal) {
            boolean oldControl = on;
            on = intSignal.getValue() < 20;
            return on != oldControl;
        }

        public boolean isOn() {
            return on;
        }
    }

    public static class Display implements NamedNode {
        private final String displayName;
        private final String sensorName;
        @Inject(factoryVariableName = "sensorName")
        public Sensor sensor;

        public Display(
                @AssignToField("displayName") String displayName,
                @AssignToField("sensorName") String sensorName) {
            this.displayName = displayName;
            this.sensorName = sensorName;
        }

        @Override
        public String getName() {
            return displayName;
        }

        public boolean isOn() {
            return sensor.isOn();
        }
    }

}

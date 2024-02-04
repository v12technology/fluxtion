package com.fluxtion.runtime.ml;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnEventHandler;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class RegressionTest extends MultipleSepTargetInProcessTest {
    public RegressionTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void simpleTest() {
        sep(c -> c.addNode(new PredictiveLinearRegressionModel(new AreaFeature()), "predictiveModel"));

        //initial prediction is NaN
        PredictiveModel predictiveModel = getField("predictiveModel");
        Assert.assertTrue(Double.isNaN(predictiveModel.predictedValue()));

        //set calibration prediction is 0
        sep.getExportedService(CalibrationProcessor.class).setCalibration(
                Arrays.asList(
                        Calibration.builder()
                                .featureClass(AreaFeature.class)
                                .weight(2)
                                .co_efficient(1.5)
                                .featureVersion(0)
                                .build()));
        Assert.assertEquals(0, predictiveModel.predictedValue(), 0.000_1);

        //send record to generate a prediction
        onEvent(new HouseDetails(12, 3));
        Assert.assertEquals(36, predictiveModel.predictedValue(), 0.000_1);

    }

    public static class AreaFeature extends AbstractFeature implements @ExportService CalibrationProcessor {

        @OnEventHandler
        public boolean processRecord(HouseDetails houseDetails) {
            value = houseDetails.area * co_efficient * weight;
            return true;
        }

    }

    @Value
    public static class HouseDetails {
        double area;
        double distance;
    }
}

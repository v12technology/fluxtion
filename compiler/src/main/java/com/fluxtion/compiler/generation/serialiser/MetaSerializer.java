package com.fluxtion.compiler.generation.serialiser;

import com.fluxtion.compiler.generation.model.Field;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeProperty;
import com.fluxtion.runtime.partition.LambdaReflection;

import java.io.File;

public interface MetaSerializer {

    static String classToSource(FieldContext<Class<?>> fieldContext) {
        fieldContext.getImportList().add(File.class);
        Class<?> clazz = fieldContext.getInstanceToMap();
        return clazz.getCanonicalName() + ".class";
    }

    static String mergePropertyToSource(FieldContext<MergeProperty<?, ?>> fieldContext) {
        fieldContext.getImportList().add(MergeProperty.class);
        MergeProperty<?, ?> mergeProperty = fieldContext.getInstanceToMap();
        LambdaReflection.SerializableBiConsumer<?, ?> setValue = mergeProperty.getSetValue();
        String containingClass = setValue.getContainingClass().getSimpleName();
        String methodName = setValue.method().getName();
        String lambda = containingClass + "::" + methodName;
        String triggerName = "null";
        //
        FlowFunction<?> trigger = mergeProperty.getTrigger();
        for (Field nodeField : fieldContext.getNodeFields()) {
            if (nodeField.instance == trigger) {
                triggerName = nodeField.name;
                break;
            }
        }
        return "new MergeProperty<>("
                + triggerName + ", " + lambda + "," + mergeProperty.isTriggering() + "," + mergeProperty.isMandatory() + ")";
    }

    static String methodReferenceToSource(FieldContext<LambdaReflection.MethodReferenceReflection> fieldContext) {
        LambdaReflection.MethodReferenceReflection ref = fieldContext.getInstanceToMap();
        fieldContext.getImportList().add(ref.getContainingClass());
        String sourceString = "";
        boolean foundMatch = false;
        if (ref.isDefaultConstructor()) {
            sourceString = ref.getContainingClass().getSimpleName() + "::new";
        } else if (ref.captured().length > 0) {
            //see if we can find the reference and set the instance
            Object functionInstance = ref.captured()[0];
            for (Field nodeField : fieldContext.getNodeFields()) {
                if (nodeField.instance == functionInstance) {
                    sourceString = nodeField.name + "::" + ref.method().getName();
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                sourceString = "new " + ref.getContainingClass().getSimpleName() + "()::" + ref.method().getName();
            }
        } else {
            if (ref.getContainingClass().getTypeParameters().length > 0) {
                String typeParam = "<Object";
                for (int i = 1; i < ref.getContainingClass().getTypeParameters().length; i++) {
                    typeParam += ", Object";
                }
                typeParam += ">";
                sourceString = ref.getContainingClass().getSimpleName() + typeParam + "::" + ref.method().getName();
            } else {
                sourceString = ref.getContainingClass().getSimpleName() + "::" + ref.method().getName();
            }
        }
        return sourceString;
    }
}

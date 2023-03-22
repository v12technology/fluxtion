package com.fluxtion.compiler.generation.serialiser;

import org.apache.commons.text.StringEscapeUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public interface FormatSerializer {

    static String simpleDataFormatToSource(FieldContext<SimpleDateFormat> fieldContext) {
        fieldContext.getImportList().add(SimpleDateFormat.class);
        SimpleDateFormat uri = fieldContext.getInstanceToMap();
        return "new SimpleDateFormat(" +
                "\"" + StringEscapeUtils.escapeJava(uri.toLocalizedPattern()) + "\")";
    }

    static String decimalFormatToSource(FieldContext<DecimalFormat> fieldContext) {
        fieldContext.getImportList().add(DecimalFormat.class);
        DecimalFormat uri = fieldContext.getInstanceToMap();
        return "new DecimalFormat(" +
                "\"" + StringEscapeUtils.escapeJava(uri.toPattern()) + "\")";
    }

}

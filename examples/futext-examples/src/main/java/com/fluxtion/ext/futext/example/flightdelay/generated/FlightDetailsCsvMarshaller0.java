package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.ext.futext.api.csv.RowProcessor;
import com.fluxtion.ext.futext.api.csv.ValidationLogger;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static com.fluxtion.ext.futext.api.ascii.Conversion.*;

/**
 * Fluxtion generated CSV marshaller wrapper.
 *
 * target class  : FlightDetails
 * 
 * @author Greg Higgins
 */

public class FlightDetailsCsvMarshaller0 implements RowProcessor<FlightDetails> {

    @Inject
    @Config(key = "id", value = "validationLog")
    @PushReference
    public ValidationLogger errorLog;
    //buffer management
    private final char[] chars = new char[4096];
    private final int[] delimIndex = new int[1024];
    private int fieldIndex = 0;
    private int writeIndex = 0;
    //target
    private FlightDetails target;
    //source field index: 14
    private final StringBuilder setDelay = new StringBuilder();
    private final int fieldIndex_14 = 14;
    //source field index: 8
    private final StringBuilder setCarrier = new StringBuilder();
    private final int fieldIndex_8 = 8;
    //processing state and meta-data
    private int rowNumber;
    private final HashMap fieldMap = new HashMap<>();
    private static final int HEADER_ROWS = 1;
    private static final int MAPPING_ROW = 1;
    private boolean passedValidation;

    @EventHandler
    public boolean charEvent(CharEvent event) {
        final char character = event.getCharacter();
        passedValidation = true;
        if(character == '\r'){
            return false;
        }
        if (character == '\n') {
            return processRow();
        }
        if (character == ',') {
            updateFieldIndex();
        }
        chars[writeIndex++] = character;
        return false;
    }

    @EventHandler
    public boolean eof(EofEvent eof){
        return writeIndex==0?false:processRow();
    }

    private boolean processRow() {
        boolean targetChanged = false;
        rowNumber++;
        if (HEADER_ROWS < rowNumber) {
            //updateTarget();
            targetChanged = updateTarget();
        } else if(rowNumber==MAPPING_ROW){
            mapHeader();
        } 
        writeIndex = 0;
        fieldIndex = 0;
        return targetChanged;
    }

    private void mapHeader(){
        String header = new String(chars).trim();
        header = header.replace("\"", "");
        List<String> headers = new ArrayList();
        for (String colName : header.split(",")) {
            char c[] = colName.trim().replace(" ", "").toCharArray();
            c[0] = Character.toLowerCase(c[0]);
            headers.add(new String(c));
        }
    }

    private boolean updateTarget() {
        try{
            updateFieldIndex();
            extractCharSequence(setDelay, fieldIndex_14);
            extractCharSequence(setCarrier, fieldIndex_8);
            //target
            return pushData();
        } catch (Exception e) {
            passedValidation = false;
            return false;
        } finally {
            fieldIndex = 0;
        }
    }

    private void updateFieldIndex() {
        fieldIndex++;
        delimIndex[fieldIndex] = writeIndex + 1;
    }

    private boolean pushData(){
        try{
            fieldIndex = fieldIndex_14;
            target.setDelay(atoi(setDelay));
            fieldIndex = fieldIndex_8;
            target.setCarrier(setCarrier);
            return true;
        } catch (Exception e) {
            logException("problem pushing data from row:", false, e);
            passedValidation = false;
            return false;
        } finally {
            fieldIndex = 0;
        }
    }

    private final void extractCharSequence(StringBuilder source, int fieldIndex){
        try {
            source.setLength(0);
            source.append(chars, delimIndex[fieldIndex], delimIndex[fieldIndex + 1] - delimIndex[fieldIndex] - 1);
        } catch (Exception e) {
            logException("problem extracting value from row:", true, e);
        }
    }

    private final void extractTrimmedCharSequence(StringBuilder source, int fieldIndex) {
        try {
            source.setLength(0);
            int len = delimIndex[fieldIndex + 1] - delimIndex[fieldIndex] - 1;
            int st = delimIndex[fieldIndex];
            char[] val = chars;
            while ((st < len) && (val[st] <= ' ')) {
                st++;
            }
            while ((st < len) && (val[len - 1] <= ' ')) {
                len--;
            }
            if (st != delimIndex[fieldIndex]) {
                len -= st;
            }
            source.append(chars, st, len);
        } catch (Exception e) {
            logException("problem extracting value from row:", true, e);
        }
    }

    private void logException(String prefix, boolean fatal, Exception e) {
        errorLog.getSb().append(prefix)
                .append(rowNumber).append(" fieldIndex:")
                .append(fieldIndex).append(" targetMethod:")
                .append(fieldMap.get(fieldIndex));
        if(fatal){
            errorLog.logFatal("");
            throw new RuntimeException(errorLog.getSb().toString(), e);
        }
        errorLog.logError(prefix);
    }


    private void logHeaderProblem(String prefix, boolean fatal, Exception e) {
        errorLog.getSb().append(prefix).append(rowNumber);
        if(fatal){
            errorLog.logFatal("");
            throw new RuntimeException(errorLog.getSb().toString(), e);
        }
        errorLog.logError("");
    }

    @Override
    public FlightDetails event() {
        return target;
    }

    @Override
    public Class<FlightDetails> eventClass() {
        return FlightDetails.class;
    }

    @Initialise
    public void init(){
        target = new FlightDetails();
        fieldMap.put(fieldIndex_14, "setDelay");
        fieldMap.put(fieldIndex_8, "setCarrier");
    }

    @Override
    public boolean passedValidation() {
        return passedValidation;
    }

    @Override
    public int getRowNumber() {
        return rowNumber;
    }

}



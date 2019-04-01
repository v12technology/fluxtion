package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence;
import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence.CharSequenceView;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.csv.ValidationLogger;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static com.fluxtion.ext.text.api.ascii.Conversion.*;

/**
 * Fluxtion generated CSV decoder.
 *
 * <p>target class : FlightDetails
 *
 * @author Greg Higgins
 */
public class FlightDetailsCsvDecoder0 implements RowProcessor<FlightDetails> {

  @Inject
  @Config(key = "id", value = "validationLog")
  @PushReference
  public ValidationLogger errorLog;
  //buffer management
  private final char[] chars = new char[4096];
  private final int[] delimIndex = new int[1024];
  private final CharArrayCharSequence seq = new CharArrayCharSequence(chars);
  private int fieldIndex = 0;
  private int writeIndex = 0;
  //target
  private FlightDetails target;
  //source field index: 14
  private final CharSequenceView setDelay = seq.view();
  private final int fieldIndex_14 = 14;
  //source field index: 8
  private final CharSequenceView setCarrier = seq.view();
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
    if (character == '\r') {
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
  public boolean eof(EofEvent eof) {
    return writeIndex == 0 ? false : processRow();
  }

  private boolean processRow() {
    boolean targetChanged = false;
    rowNumber++;
    if (HEADER_ROWS < rowNumber) {
      targetChanged = updateTarget();
    } else if (rowNumber == MAPPING_ROW) {
      mapHeader();
    }
    writeIndex = 0;
    fieldIndex = 0;
    return targetChanged;
  }

  private void mapHeader() {
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
    try {
      updateFieldIndex();
      fieldIndex = fieldIndex_14;
      setDelay.subSequence(delimIndex[fieldIndex_14], delimIndex[fieldIndex_14 + 1] - 1);
      target.setDelay(atoi(setDelay));

      fieldIndex = fieldIndex_8;
      setCarrier.subSequence(delimIndex[fieldIndex_8], delimIndex[fieldIndex_8 + 1] - 1);
      target.setCarrier(setCarrier);

    } catch (Exception e) {
      logException("problem pushing data from row:", false, e);
      passedValidation = false;
      return false;
    } finally {
      fieldIndex = 0;
    }
    return true;
  }

  private void updateFieldIndex() {
    fieldIndex++;
    delimIndex[fieldIndex] = writeIndex + 1;
  }

  private void logException(String prefix, boolean fatal, Exception e) {
    errorLog
        .getSb()
        .append(prefix)
        .append(rowNumber)
        .append(" fieldIndex:")
        .append(fieldIndex)
        .append(" targetMethod:")
        .append(fieldMap.get(fieldIndex));
    if (fatal) {
      errorLog.logFatal("");
      throw new RuntimeException(errorLog.getSb().toString(), e);
    }
    errorLog.logError("");
  }

  private void logHeaderProblem(String prefix, boolean fatal, Exception e) {
    errorLog.getSb().append(prefix).append(rowNumber);
    if (fatal) {
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
  public void init() {
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

package org.greg;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.streaming.api.log.LogService;
import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence;
import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence.CharSequenceView;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.csv.ValidationLogger;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.ext.text.api.util.CharStreamer;
import com.fluxtion.ext.text.api.util.marshaller.CsvRecordMarshaller;
import com.fluxtion.ext.text.builder.util.StringDriver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.greg.Data1;
import static com.fluxtion.ext.text.api.ascii.Conversion.*;
import static com.fluxtion.ext.text.api.csv.Converters.*;

/**
 * Fluxtion generated CSV decoder.
 *
 * <p>target class : Data1
 *
 * @author Greg Higgins
 */
public class Data1CsvDecoder0 implements RowProcessor<Data1> {

  @Inject
  @Config(key = "id", value = "validationLog")
  @PushReference
  public ValidationLogger errorLog;
  //buffer management
  private final char[] chars = new char[4096];
  private final int[] delimIndex = new int[1024];
  private StringBuilder msgSink = new StringBuilder(256);
  private final CharArrayCharSequence seq = new CharArrayCharSequence(chars);
  private int fieldIndex = 0;
  private int writeIndex = 0;
  //target
  private Data1 target;
  //source field index: -1
  private final CharSequenceView setAge = seq.view();
  private int fieldName_age = -1;
  //source field index: -1
  private final CharSequenceView setEventTime = seq.view();
  private int fieldName_eventTime = -1;
  //source field index: -1
  private final CharSequenceView setName = seq.view();
  private int fieldName_f__NAME = -1;
  //source field index: -1
  private final CharSequenceView setHalfAge = seq.view();
  private int fieldName_halfAge = -1;
  //source field index: -1
  private final CharSequenceView setLastName = seq.view();
  private int fieldName_lastName = -1;
  //processing state and meta-data
  private int rowNumber;
  private final HashMap fieldMap = new HashMap<>();
  private static final int HEADER_ROWS = 1;
  private static final int MAPPING_ROW = 1;
  private boolean passedValidation;

  @EventHandler
  @Override
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
  @Override
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
    header = header.replaceAll("\\P{InBasic_Latin}", "");
    header = header.replace("\"", "");
    List<String> headers = new ArrayList();
    for (String colName : header.split("[,]")) {
      headers.add(getIdentifier(colName));
    }
    fieldName_age = headers.indexOf("age");
    fieldMap.put(fieldName_age, "setAge");
    if (fieldName_age < 0) {
      logHeaderProblem("problem mapping field:'age' missing column header, index row:", true, null);
    }
    fieldName_eventTime = headers.indexOf("eventTime");
    fieldMap.put(fieldName_eventTime, "setEventTime");
    fieldName_f__NAME = headers.indexOf("f__NAME");
    fieldMap.put(fieldName_f__NAME, "setName");
    if (fieldName_f__NAME < 0) {
      logHeaderProblem(
          "problem mapping field:'f__NAME' missing column header, index row:", true, null);
    }
    fieldName_halfAge = headers.indexOf("halfAge");
    fieldMap.put(fieldName_halfAge, "setHalfAge");
    fieldName_lastName = headers.indexOf("lastName");
    fieldMap.put(fieldName_lastName, "setLastName");
    if (fieldName_lastName < 0) {
      logHeaderProblem(
          "problem mapping field:'lastName' missing column header, index row:", true, null);
    }
  }

  private boolean updateTarget() {
    try {
      updateFieldIndex();
      fieldIndex = fieldName_age;
      setAge.subSequenceNoOffset(delimIndex[fieldName_age], delimIndex[fieldName_age + 1] - 1);
      target.setAge(atoi(setAge));

      fieldIndex = fieldName_eventTime;
      if (fieldIndex > -1) {
        setEventTime.subSequenceNoOffset(
            delimIndex[fieldName_eventTime], delimIndex[fieldName_eventTime + 1] - 1);
      }
      target.setEventTime(atol(setEventTime));

      fieldIndex = fieldName_f__NAME;
      setName.subSequenceNoOffset(
          delimIndex[fieldName_f__NAME], delimIndex[fieldName_f__NAME + 1] - 1);
      target.setName(setName.toString());

      fieldIndex = fieldName_halfAge;
      if (fieldIndex > -1) {
        setHalfAge.subSequenceNoOffset(
            delimIndex[fieldName_halfAge], delimIndex[fieldName_halfAge + 1] - 1);
      }
      target.setHalfAge(target.fun_halfAge(setHalfAge));

      fieldIndex = fieldName_lastName;
      setLastName.subSequenceNoOffset(
          delimIndex[fieldName_lastName], delimIndex[fieldName_lastName + 1] - 1);
      target.setLastName(target.fun_lastName(setLastName));

      target.postRecordRead();
    } catch (Exception e) {
      logException(
          "problem pushing '"
              + seq.subSequence(delimIndex[fieldIndex], delimIndex[fieldIndex + 1] - 1).toString()
              + "'"
              + " from row:'"
              + rowNumber
              + "'",
          false,
          e);
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
        .append("Data1CsvDecoder0 ")
        .append(prefix)
        .append(" fieldIndex:'")
        .append(fieldIndex)
        .append("' targetMethod:'Data1#")
        .append(fieldMap.get(fieldIndex))
        .append("' error:'")
        .append(e.toString())
        .append("'");
    if (fatal) {
      errorLog.logFatal("");
      throw new RuntimeException(errorLog.getSb().toString(), e);
    }
    errorLog.logError("");
  }

  private void logHeaderProblem(String prefix, boolean fatal, Exception e) {
    errorLog.getSb().append("Data1CsvDecoder0 ").append(prefix).append(rowNumber);
    if (fatal) {
      errorLog.logFatal("");
      throw new RuntimeException(errorLog.getSb().toString(), e);
    }
    errorLog.logError("");
  }

  @Override
  public Data1 event() {
    return target;
  }

  @Override
  public Class<Data1> eventClass() {
    return Data1.class;
  }

  @Initialise
  @Override
  public void init() {
    target = new Data1();
    fieldMap.put(fieldName_age, "setAge");
    fieldMap.put(fieldName_eventTime, "setEventTime");
    fieldMap.put(fieldName_f__NAME, "setName");
    fieldMap.put(fieldName_halfAge, "setHalfAge");
    fieldMap.put(fieldName_lastName, "setLastName");
  }

  @Override
  public boolean passedValidation() {
    return passedValidation;
  }

  @Override
  public int getRowNumber() {
    return rowNumber;
  }

  @Override
  public void setErrorLog(ValidationLogger errorLog) {
    this.errorLog = errorLog;
  }

  public static String csvHeader() {
    String out = "";
    out += "age,";
    out += "eventTime,";
    out += "halfAge,";
    out += "lastName,";
    out += "f__NAME";
    return out;
  }

  public static void asCsv(Data1 src, StringBuilder msgSink) throws IOException {
    msgSink.append(src.getAge());
    msgSink.append(",");
    msgSink.append(src.getEventTime());
    msgSink.append(",");
    msgSink.append(src.getHalfAge());
    msgSink.append(",");
    msgSink.append(src.getLastName());
    msgSink.append(",");
    msgSink.append(src.getName());
    msgSink.append("\n");
  }

  public static void asCsv(Data1 src, Appendable target) throws IOException {
    StringBuilder sb = new StringBuilder();
    asCsv(src, sb);
    target.append(sb);
  }

  @Override
  public String csvHeaders() {
    return csvHeader();
  }

  @Override
  public Appendable toCsv(Data1 src, Appendable target) throws IOException {
    msgSink.setLength(0);
    asCsv(src, msgSink);
    target.append(msgSink);
    msgSink.setLength(0);
    return target;
  }

  public static CsvRecordMarshaller marshaller() {
    return new CsvRecordMarshaller(new Data1CsvDecoder0());
  }

  public static void stream(StaticEventProcessor target, String input) {
    CsvRecordMarshaller marshaller = marshaller();
    marshaller.handleEvent(new RegisterEventHandler(target));
    StringDriver.streamChars(input, marshaller);
    target.onEvent(EofEvent.EOF);
  }

  public static void stream(StaticEventProcessor target, File input) throws IOException {
    CsvRecordMarshaller marshaller = marshaller();
    marshaller.handleEvent(new RegisterEventHandler(target));
    CharStreamer.stream(input, marshaller).sync().stream();
    target.onEvent(EofEvent.EOF);
  }

  public static void stream(StaticEventProcessor target, File input, LogService validationLogger)
      throws IOException {
    CsvRecordMarshaller marshaller = marshaller();
    marshaller.init();
    marshaller.handleEvent(new RegisterEventHandler(target));
    marshaller.handleEvent(LogControlEvent.setLogService(validationLogger));
    CharStreamer.stream(input, marshaller).noInit().sync().stream();
    target.onEvent(EofEvent.EOF);
  }
}

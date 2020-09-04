package com.gregso;

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
import com.gregso.MyData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static com.fluxtion.ext.text.api.ascii.Conversion.*;
import static com.fluxtion.ext.text.api.csv.Converters.*;

/**
 * Fluxtion generated CSV decoder.
 *
 * <p>target class : MyData
 *
 * @author Greg Higgins
 */
public class MyDataCsvDecoder0 implements RowProcessor<MyData> {

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
  private MyData target;
  //source field index: -1
  private final CharSequenceView setAge = seq.view();
  private int fieldName_F__NAME = -1;
  //source field index: -1
  private final CharSequenceView setEventTime = seq.view();
  private int fieldName_eventTime = -1;
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
    fieldName_F__NAME = headers.indexOf("F__NAME");
    fieldMap.put(fieldName_F__NAME, "setAge");
    if (fieldName_F__NAME < 0) {
      logHeaderProblem(
          "problem mapping field:'F__NAME' missing column header, index row:", true, null);
    }
    fieldName_eventTime = headers.indexOf("eventTime");
    fieldMap.put(fieldName_eventTime, "setEventTime");
  }

  private boolean updateTarget() {
    try {
      updateFieldIndex();
      fieldIndex = fieldName_F__NAME;
      setAge.subSequenceNoOffset(
          delimIndex[fieldName_F__NAME], delimIndex[fieldName_F__NAME + 1] - 1);
      target.setAge(atoi(setAge));

      fieldIndex = fieldName_eventTime;
      if (fieldIndex > -1) {
        setEventTime.subSequenceNoOffset(
            delimIndex[fieldName_eventTime], delimIndex[fieldName_eventTime + 1] - 1);
      }
      target.setEventTime(atol(setEventTime));

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
        .append("MyDataCsvDecoder0 ")
        .append(prefix)
        .append(" fieldIndex:'")
        .append(fieldIndex)
        .append("' targetMethod:'MyData#")
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
    errorLog.getSb().append("MyDataCsvDecoder0 ").append(prefix).append(rowNumber);
    if (fatal) {
      errorLog.logFatal("");
      throw new RuntimeException(errorLog.getSb().toString(), e);
    }
    errorLog.logError("");
  }

  @Override
  public MyData event() {
    return target;
  }

  @Override
  public Class<MyData> eventClass() {
    return MyData.class;
  }

  @Initialise
  @Override
  public void init() {
    target = new MyData();
    fieldMap.put(fieldName_F__NAME, "setAge");
    fieldMap.put(fieldName_eventTime, "setEventTime");
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
    out += "F__NAME,";
    out += "eventTime";
    return out;
  }

  public static void asCsv(MyData src, StringBuilder msgSink) throws IOException {
    msgSink.append(src.getAge());
    msgSink.append(",");
    msgSink.append(src.getEventTime());
    msgSink.append("\n");
  }

  public static void asCsv(MyData src, Appendable target) throws IOException {
    StringBuilder sb = new StringBuilder();
    asCsv(src, sb);
    target.append(sb);
  }

  @Override
  public String csvHeaders() {
    return csvHeader();
  }

  @Override
  public Appendable toCsv(MyData src, Appendable target) throws IOException {
    msgSink.setLength(0);
    asCsv(src, msgSink);
    target.append(msgSink);
    msgSink.setLength(0);
    return target;
  }

  public static CsvRecordMarshaller marshaller() {
    return new CsvRecordMarshaller(new MyDataCsvDecoder0());
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

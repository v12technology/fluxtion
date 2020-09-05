package com.fluxtion.integrations.dispatch.eventflowtest_simpleflow_1599338346712;

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
import com.fluxtion.integrations.dispatch.EventFlowTest.Iris;
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
 * <p>target class : Iris
 *
 * @author Greg Higgins
 */
public class IrisCsvDecoder0 implements RowProcessor<Iris> {

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
  private Iris target;
  //source field index: -1
  private final CharSequenceView setName = seq.view();
  private int fieldName_f_name = -1;
  //source field index: -1
  private final CharSequenceView setSize = seq.view();
  private int fieldName_size = -1;
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
    fieldName_f_name = headers.indexOf("f_name");
    fieldMap.put(fieldName_f_name, "setName");
    if (fieldName_f_name < 0) {
      logHeaderProblem(
          "problem mapping field:'f_name' missing column header, index row:", true, null);
    }
    fieldName_size = headers.indexOf("size");
    fieldMap.put(fieldName_size, "setSize");
    if (fieldName_size < 0) {
      logHeaderProblem(
          "problem mapping field:'size' missing column header, index row:", true, null);
    }
  }

  private boolean updateTarget() {
    try {
      updateFieldIndex();
      fieldIndex = fieldName_f_name;
      setName.subSequenceNoOffset(
          delimIndex[fieldName_f_name], delimIndex[fieldName_f_name + 1] - 1);
      target.setName(setName.toString());

      fieldIndex = fieldName_size;
      setSize.subSequenceNoOffset(delimIndex[fieldName_size], delimIndex[fieldName_size + 1] - 1);
      target.setSize(atoi(setSize));

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
        .append("IrisCsvDecoder0 ")
        .append(prefix)
        .append(" fieldIndex:'")
        .append(fieldIndex)
        .append("' targetMethod:'Iris#")
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
    errorLog.getSb().append("IrisCsvDecoder0 ").append(prefix).append(rowNumber);
    if (fatal) {
      errorLog.logFatal("");
      throw new RuntimeException(errorLog.getSb().toString(), e);
    }
    errorLog.logError("");
  }

  @Override
  public Iris event() {
    return target;
  }

  @Override
  public Class<Iris> eventClass() {
    return Iris.class;
  }

  @Initialise
  @Override
  public void init() {
    target = new Iris();
    fieldMap.put(fieldName_f_name, "setName");
    fieldMap.put(fieldName_size, "setSize");
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
    out += "f_name,";
    out += "size";
    return out;
  }

  public static void asCsv(Iris src, StringBuilder msgSink) throws IOException {
    msgSink.append(src.getName());
    msgSink.append(",");
    msgSink.append(src.getSize());
    msgSink.append("\n");
  }

  public static void asCsv(Iris src, Appendable target) throws IOException {
    StringBuilder sb = new StringBuilder();
    asCsv(src, sb);
    target.append(sb);
  }

  @Override
  public String csvHeaders() {
    return csvHeader();
  }

  @Override
  public Appendable toCsv(Iris src, Appendable target) throws IOException {
    msgSink.setLength(0);
    asCsv(src, msgSink);
    target.append(msgSink);
    msgSink.setLength(0);
    return target;
  }

  public static CsvRecordMarshaller marshaller() {
    return new CsvRecordMarshaller(new IrisCsvDecoder0());
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

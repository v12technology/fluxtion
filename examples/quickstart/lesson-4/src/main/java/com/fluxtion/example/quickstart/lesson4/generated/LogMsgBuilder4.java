package com.fluxtion.example.quickstart.lesson4.generated;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.log.LogMsgBuilder;
/**
 * Generated notificationToLogger.
 *
 * @author Greg Higgins
 */
public class LogMsgBuilder4 extends LogMsgBuilder {

  //source operand inputs
  @NoEventReference
  public com.fluxtion.example.quickstart.lesson4.generated.Map_List_With_formatTradeList0
      source_Map_List_With_formatTradeList0_3;

  public Object logNotifier;
  private boolean notificationToLog;

  @OnParentUpdate(value = "logNotifier")
  public void postLog(Object logNotifier) {
    notificationToLog = true;
  }

  @OnEvent
  public boolean logMessage() {
    if (notificationToLog & isGoodToLog()) {
      msgSink.append(" ");
      msgSink.append(
          ((java.lang.String) source_Map_List_With_formatTradeList0_3.event()).toString());
      msgSink.append("");
      notificationToLog = false;
      log();
      return true;
    }
    notificationToLog = false;
    return false;
  }
}

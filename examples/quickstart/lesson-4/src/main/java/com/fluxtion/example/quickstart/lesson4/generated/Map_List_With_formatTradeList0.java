package com.fluxtion.example.quickstart.lesson4.generated;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.example.quickstart.lesson4.TradeMonitor;
import com.fluxtion.ext.streaming.api.stream.AbstractFilterWrapper;
import java.util.List;

/**
 * Generated mapper function wrapper for a reference type.
 * <pre>
 *  <ul>
 *      <li>template file: template/MapperTemplate.vsl
 *      <li>output class : {@link String}
 *      <li>input class  : {@link List}
 *      <li>map function : {@link TradeMonitor#formatTradeList}
 *      <li>multiArg     : false
 *  </ul>
 * <pre>
 * stateful = false
 * @author Greg Higgins
 */
public class Map_List_With_formatTradeList0 extends AbstractFilterWrapper<String> {

  public GetField_SubList_List0 filterSubject;
  private String result;

  @OnEvent
  public boolean onEvent() {
    boolean updated = true;
    if (recalculate) {
      String oldValue = result;
      result = TradeMonitor.formatTradeList((List) ((List) filterSubject.event()));
      return !notifyOnChangeOnly || (!result.equals(oldValue));
    }
    recalculate = true;
    return updated;
  }

  @Override
  public String event() {
    return result;
  }

  @Override
  public Class<String> eventClass() {
    return String.class;
  }

  @Override
  public void reset() {
    result = null;
    recalculate = true;
    reset = false;
  }
}

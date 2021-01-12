package com.fluxtion.example.quickstart.lesson4.generated;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.streaming.api.SubList;
import com.fluxtion.ext.streaming.api.stream.AbstractFilterWrapper;
import java.util.List;

/**
 * Generated get field template.
 *
 * <pre>
 *  <ul>
 *     <li>template file    : template/MapFieldTemplate.vsl
 *     <li>output class     : {@link List}
 *     <li>input class      : {@link SubList}
 *     <li>source function  : {@link SubList#collection}
 *     <li>primitive number : false
 *  </ul>
 * </pre>
 *
 * @author Greg Higgins
 */
public class GetField_SubList_List0 extends AbstractFilterWrapper<List> {

  public SubList filterSubject;
  private List result;

  @OnEvent
  public boolean onEvent() {
    result = filterSubject.collection();
    return true;
  }

  @Override
  public List event() {
    return result;
  }

  @Override
  public Class<List> eventClass() {
    return List.class;
  }

  @Override
  public void reset() {
    result = null;
  }
}

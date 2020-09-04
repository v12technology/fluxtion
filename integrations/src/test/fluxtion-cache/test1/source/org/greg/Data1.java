// Copyright (C) 2018 V12 Technology Ltd.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the Server Side Public License, version 1,
// as published by MongoDB, Inc.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// Server Side License for more details.
//
// You should have received a copy of the Server Side Public License
// along with this program.  If not, see 
// <http://www.mongodb.com/licensing/server-side-public-license>.
package org.greg;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.DefaultEvent;
import com.fluxtion.ext.text.api.annotation.ColumnName;
import com.fluxtion.ext.text.api.annotation.ConvertField;
import com.fluxtion.ext.text.api.annotation.OptionalField;
import lombok.Data;

@Data
public final class Data1 extends DefaultEvent {
  private int age;

  @ColumnName("f__NAME")
  private String name;

  @ConvertField("fun_lastName")
  private String lastName;

  /**
   * derived field
   */
  @OptionalField
  @ConvertField("fun_halfAge")
  private int halfAge;

  /**
   * converter calculation for {@link #lastName} field
   */
  public String fun_lastName(CharSequence input) {
    return input.toString().toUpperCase();
  }

  /**
   * derived calculation for {@link #halfAge} field
   */
  public int fun_halfAge(CharSequence input) {
    //some comments
    return age/2;
  }

  /**
   * operations post row read, before publishing record
   */
  @OnEvent
  public void postRecordRead() {
    //no-op demo callback 
  }
}

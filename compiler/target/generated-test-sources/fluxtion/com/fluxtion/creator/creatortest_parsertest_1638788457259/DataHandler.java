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
package com.fluxtion.creator.creatortest_parsertest_1638788457259;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.TearDown;

public final class DataHandler {
  @EventHandler(
      propagate = true
  )
  public boolean handlerCharEvent(CharEvent event) {
    return true;
  }

  @Initialise
  public void init() {
  }

  @TearDown
  public void teardown() {
  }
}

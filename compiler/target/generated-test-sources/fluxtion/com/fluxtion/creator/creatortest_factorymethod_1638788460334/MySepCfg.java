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
package com.fluxtion.creator.creatortest_factorymethod_1638788460334;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.creator.MathFactory;
import com.fluxtion.creator.MyPredefinedNode;
import com.fluxtion.creator.TestAuditor;
import java.lang.Override;

public final class MySepCfg extends SEPConfig {
  @Override
  public void buildConfig() {
    // creating node instance
    DataHandler dataHandler = addPublicNode(new DataHandler(), "dataHandler");
    MyPredefinedNode myProcessor = addPublicNode(new MyPredefinedNode(), "myProcessor");
    MathFactory.Value max;
    max = com.fluxtion.creator.MathFactory.max(myProcessor);
    addPublicNode(max, "max");
    // setting node reference
    myProcessor.parent = dataHandler;
    // adding auditor
    addAuditor(new TestAuditor(), "auditor") ;
  }
}

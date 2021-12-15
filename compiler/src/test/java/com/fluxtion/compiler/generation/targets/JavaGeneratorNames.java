/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.targets;

/**
 *
 * @author Greg Higgins
 */
public enum JavaGeneratorNames {
    packageDefault("com.fluxtion.test.generation.java"),
    Test1NoFilter("Test1NoFilter"),
    Test1Filtered("Test1Filtered"),
    testDirtyFilterOnEventHandler("TestDirtyFilterOnEventHandler"),
    trace_int_0_test1("Trace_int_0_test1"),
    trace_int_0_test2("Trace_int_0_test2"),
    trace_int_0_test3("Trace_int_0_test3"),
    trace_int_0_test4("Trace_int_0_test4"),
    trace_int_0_test5("Trace_int_0_test5"),
    trace_0_test1("Trace_0_test1"),
    trace_subclass_test1("Trace_subclass_test1"),
    trace_diamond_test1("Trace_diamond_test1"),
    trace_dirty_test1("Trace_dirty_test1"),
    trace_eventlifecycle_test1("Trace_eventlifecycle_test1"),
    trace_mapdispatch_test1("Trace_mapdispatch_test1"),
    test_wc("Test_wc"),
    test_wc_generic("Test_wc_generic"),
    test_wc_generic_arrays("Test_wc_generic_arrays"),
    test_wc_inline_event_handling("Test_wc_inline_event_handling"),
    test_enumField("Test_enumField"),
    test_defaulthandler("Test_defaulthandler"),
    test_privateAssignment("Test_privateassignment"),
    test_privateDispatch("Test_privatedispatch"),
    test_injected_factory("Test_injected_factory"),
    test_injected_factory_variable_config("Test_injected_factory_variable_config"),
    ;  
    
    
    public final String name;

    JavaGeneratorNames(String name) {
        this.name = name;
    }
    
    
}

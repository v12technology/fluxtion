---
title: Audit logging
parent: Cookbook
has_children: false
nav_order: 4
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/auditlog
---

## Introduction

An EventProcessor graph can become complicated quite quickly making uderstanding the event processing difficult. 
Tracing the event propagation through the nodes helps the developer understand the runtime behaviour. Fluxtion provides 
an audit log facility that traces the call chain through the graph and records audit records as a yaml document.

The audit log must be included at graph build time. Audit logging has a runtime overhead so it is not included by default 
in the EventProcessor. 

Once added the audit log level is set to INFO by default.

## Configuring audit logging

The audit log facility is added to the EventProcessor with this call

{% highlight java %}
Fluxtion.interpret(cfg -> {
  cfg.addEventAudit(LogLevel.INFO);
});
{% endhighlight %}

The log level in the argument specifies the trace level of the audit logger. The default behaviour is to only record
a node log list element if the node records an entry in the EventLogger. The trace facility records a node log entry
even if a node does not record a map entry. The trace is working if the runtime log level is less than or equal to the
trace level set at build time.

Setting the audit logging level to NONE at build time removes the tracing from the event processor.

## Sample log file

{% highlight yaml %}
---
eventLogRecord:
eventTime: 1677529450524
logTime: 1677529450524
groupingId: null
event: CalculateEvent
eventToString: {CalculateEvent[name=AB]}
nodeLogs:
- calcHandler_A: { method: recalculate, matchedRecalcId: false}
- calcHandler_AB: { method: recalculate, matchedRecalcId: true}
- calcHandler_ABC: { method: recalculate, matchedRecalcId: false}
- calcHandler_AC: { method: recalculate, matchedRecalcId: false}
- publishCalcHandler_0: { method: calculatorTriggered, recalculate: true}
endTime: 1677529450524
{% endhighlight %}

The log file entry the audit log has the following characteristics:
- An entry is recorded as a yaml document for each event processed
- The header records various items 
  - eventTime: can be set the EventFeed
  - logTime: is the wall clock time
  - event: The class shortname of event
- A nodeLogs element records the progress through the node graph as a list element
  - Only nodes that are visited are recorded in the list
  - The order of the list mirrors the order of execution
  - The key is same name of the node variable in the generated code
  - A map of values is written for each node visited. 
  - Map elements are written in the user code of the node via [EventLogger]({{site.fluxtion_src_runtime}}/audit/EventLogger.java)
  - User node has access to the EventLogger by extending [EventLogNode]({{site.fluxtion_src_runtime}}/audit/EventLogNode.java)

## Audit log example

[A code example]({{page.example_src}}) demonstrate the use of audit logging. A complicated graph of nodes within 
an EventProcessor is created in the example. Understanding runtime behaviour is difficult and requires either runtime 
debugging or custom logging to give the developer insight to the event dispatch with the graph.

### Sample audit log

{% highlight yaml %}
eventLogRecord:
eventTime: 1677533837735
logTime: 1677533837736
groupingId: null
event: DataEvent
eventToString: {DataEvent[name=A]}
nodeLogs:
- dataHandler_A: { method: dataUpdated}
- calcHandler_A: { method: triggered}
- dataHandler_B: { method: dataUpdated}
- calcHandler_AB: { method: triggered}
- dataHandler_C: { method: dataUpdated}
- calcHandler_ABC: { method: triggered}
- calcHandler_AC: { method: triggered}
endTime: 1677533837757
---
eventLogRecord:
eventTime: 1677533837757
logTime: 1677533837757
groupingId: null
event: DataEvent
eventToString: {DataEvent[name=B]}
nodeLogs:
- dataHandler_A: { method: dataUpdated}
- dataHandler_B: { method: dataUpdated}
- calcHandler_AB: { method: triggered}
- dataHandler_C: { method: dataUpdated}
- calcHandler_ABC: { method: triggered}
endTime: 1677533837757
---
eventLogRecord:
eventTime: 1677533837758
logTime: 1677533837758
groupingId: null
event: PublishEvent
eventToString: {PublishEvent[]}
nodeLogs:
- publishCalcHandler_0: { method: publish}
endTime: 1677533837759
---
eventLogRecord:
eventTime: 1677533837759
logTime: 1677533837759
groupingId: null
event: CalculateEvent
eventToString: {CalculateEvent[name=ABC]}
nodeLogs:
- calcHandler_A: { method: recalculate, matchedRecalcId: false}
- calcHandler_AB: { method: recalculate, matchedRecalcId: false}
- calcHandler_ABC: { method: recalculate, matchedRecalcId: true}
- calcHandler_AC: { method: recalculate, matchedRecalcId: false}
- publishCalcHandler_0: { method: calculatorTriggered, recalculate: true, recalculateWarn: true}
endTime: 1677533837761
---
eventLogRecord:
eventTime: 1677533837761
logTime: 1677533837761
groupingId: null
event: ConfigEvent
eventToString: {ConfigEvent[]}
nodeLogs:
- configHandler_1: { method: configUpdated}
endTime: 1677533837761
---
eventLogRecord:
eventTime: 1677533837761
logTime: 1677533837761
groupingId: null
event: DataEvent
eventToString: {DataEvent[name=EFG]}
nodeLogs:
- dataHandler_A: { method: dataUpdated}
- dataHandler_B: { method: dataUpdated}
- dataHandler_C: { method: dataUpdated}
endTime: 1677533837761
---

XXXXXX uping the trace level  to DEBUG XXXX

Feb 27, 2023 9:37:17 PM com.fluxtion.runtime.audit.EventLogManager calculationLogConfig
INFO: updating event log config:EventLogConfig{level=DEBUG, logRecordProcessor=null, sourceId=null, groupId=null}
eventLogRecord:
eventTime: -1
logTime: 1677533837762
groupingId: null
event: EventLogControlEvent
eventToString: {EventLogConfig{level=DEBUG, logRecordProcessor=null, sourceId=null, groupId=null}}
nodeLogs:
endTime: 1677533837797
---
eventLogRecord:
eventTime: 1677533837798
logTime: 1677533837798
groupingId: null
event: DataEvent
eventToString: {DataEvent[name=C]}
nodeLogs:
- dataHandler_A: { method: dataUpdated}
- dataHandler_B: { method: dataUpdated}
- dataHandler_C: { method: dataUpdated}
- calcHandler_ABC: { method: triggered}
- calcHandler_AC: { method: triggered}
endTime: 1677533837798
---
eventLogRecord:
eventTime: 1677533837798
logTime: 1677533837798
groupingId: null
event: CalculateEvent
eventToString: {CalculateEvent[name=AB]}
nodeLogs:
- calcHandler_A: { method: recalculate, matchedRecalcId: false}
- calcHandler_AB: { method: recalculate, matchedRecalcId: true}
- calcHandler_ABC: { method: recalculate, matchedRecalcId: false}
- calcHandler_AC: { method: recalculate, matchedRecalcId: false}
- publishCalcHandler_0: { method: calculatorTriggered, recalculateDebug: true, recalculate: true, recalculateWarn: true}
endTime: 1677533837798
---
eventLogRecord:
eventTime: 1677533837798
logTime: 1677533837798
groupingId: null
event: CalculateEvent
eventToString: {CalculateEvent[name=ABNHGH]}
nodeLogs:
- calcHandler_A: { method: recalculate, matchedRecalcId: false}
- calcHandler_AB: { method: recalculate, matchedRecalcId: false}
- calcHandler_ABC: { method: recalculate, matchedRecalcId: false}
- calcHandler_AC: { method: recalculate, matchedRecalcId: false}
endTime: 1677533837798
---
eventLogRecord:
eventTime: 1677533837798
logTime: 1677533837798
groupingId: null
event: ConfigEvent
eventToString: {ConfigEvent[]}
nodeLogs:
- configHandler_1: { method: configUpdated}
endTime: 1677533837798
---
eventLogRecord:
eventTime: 1677533837798
logTime: 1677533837798
groupingId: null
event: PublishEvent
eventToString: {PublishEvent[]}
nodeLogs:
- publishCalcHandler_0: { method: publish}
endTime: 1677533837799
---

Process finished with exit code 0

{% endhighlight %}





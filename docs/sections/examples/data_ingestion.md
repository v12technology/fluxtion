---
title: Realtime data ingestion
parent: Examples
has_children: false
nav_order: 4
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/develop/cookbook/src/main/java/com/fluxtion/example/cookbook
---

## Introduction

This example demonstrates real-time data ingestion using the Fluxtion event processing framework.

- Stream of string data csv records
- Process each record from CSV to a java object
- Perform basic field validation
  - log invalid input records
- For valid records
  - Transform each record with lookups + calculations
  - Validate the transformed record
  - write valid records to CSV
  - write valid records to a binary format
- Record realtime statistic of processing that can be queried

## Process flow diagram

```mermaid
flowchart TB
  subgraph Fluxtion [Fluxtion event processor]
    direction TB
    csv_validator(csv field validator) -- invalid --> invalid(invalid log)
    csv_validator -- valid --> x-former
    x-former --> record_validator(record validator)
    record_validator -- invalid --> invalid
    record_validator -- valid --> csv_writer(csv writer) & binary_writer(binary writer) 
    record_validator & csv_validator --> stats
  end
  input(Csv record stream) --> csv_validator 
  config(x-former config) --> x-former 


    


```

## Code example

[See the example here]({{page.example_src}}/dataingestion)


[Pipe line builder]({{page.example_src}}/dataingestion/DataIngestionPipelineBuilder.java)

{% highlight java %}

{% endhighlight %}

### Running the example prints this to console:

{% highlight console %}

{% endhighlight %}

---
title: Realtime data ingestion
parent: Examples
has_children: false
nav_order: 4
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook
---

https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/dataingestion

## Introduction

This example demonstrates real-time data ingestion using the Fluxtion event processing framework. The requirements for
the processing are:


- Subscribe to a stream of string data csv records
- Process each string and validate it is a valid CSV record
  - log invalid input records
- For valid records
  - Transform each record with a user supplied function
  - Validate the transformed record
    - log invalid input records
  - write valid records to CSV
  - write valid records to a binary format
- Record realtime statistic of processing that can be queried
  - count of all records
  - count of invalid csv records
  - count of failed validation records
  - 
- 

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
## Solution design

## API

## User functions

## Pipeline building

## Testing

## Running



## Code example

[See the example here]({{page.example_src}}/dataingestion)


[Pipe line builder]({{page.example_src}}/dataingestion/PipelineBuilder.java)

{% highlight java %}

{% endhighlight %}

### Running the example prints this to console:

{% highlight console %}

{% endhighlight %}

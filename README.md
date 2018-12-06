# fluxtion

[![Build Status](https://travis-ci.org/v12technology/fluxtion.svg?branch=master)](https://travis-ci.org/v12technology/fluxtion)

Fluxtion is a tool for generating high performance event stream processing applications. 
The ideas behind Fluxtion has been used extensively in the low latency high 
frequency trading space, where low response time for complex calculation graphs 
is the main requirement.

Uniquely among stream process applications Fluxtion interjects in the standard build 
process and seamlessly adds a second compiler stage. The generated code is optimised
for performance and low memory usage to reduce processing cost. 

We generate code and not byte code for three reasons: 
* Most costs are in maintenance which is easier and faster if the code is accessible 
* Multiple target languages are supported, not solely Java
* Fluxtion supports complex constructs, such as recursive compilation, that would be difficult to develop in byte code alone

A maven plugin is provided that integrates Fluxtion generator into a standard developer build process.

This README is a work in progress and will be updating regularly



---
title: Integrate event processor
has_children: true
nav_order: 8
published: true
---

# Introduction
{: .no_toc }

Building and executing an event processor are independent functions that can run in separate processes. This section
documents integrating an event processor into an application and unit testing the event processor.

There are three steps to use Fluxtion, step 3 is covered here:

## Three steps to using Fluxtion
{: .no_toc }

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - **Integrate the event processor in the app and feed it events**
{: .fs-4 }

![](../images/integration_overview-running.drawio.png)

Once an event processor has been generated then it will need to integrated into the business application. In general there
are three integration points for an event processor within an application

* Integrating the event processor into the deployed application
* Integrating event sourcing for recording and replay of production events for offline analysis
* Integrating generated event processors into the project testing framework

These three types of integration are detailed in this section.
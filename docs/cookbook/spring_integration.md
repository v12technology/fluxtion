---
title: Spring integration
parent: Cookbook
has_children: false
nav_order: 1
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/spring
---

## Introduction

Fluxtion acts as dependency injection container for event driven applications routing service calls to managed
instances, a service interface is exposed at the container level by adding annotations to the application classes. Any
instance referencing the service handler will be triggered by the container as the event propagates through the object
graph.

Spring is a popular DI container in the java world, the tutorial demonstrates how the construction logic of spring can
be combined with the dispatching logic of Fluxtion to simplify building event driven applications. The goal is to allow
the developer to concentrate on developing application logic while the container automatically builds the object graph and
constructs event dispatch logic.

This example constructs a small banking application, that supports credit, debit, account query, credit checking,
opening hours and persistence functions. The methods are grouped into service interfaces that are exposed by the
container.

The steps to combine spring and fluxtion:

- Create service interfaces that define the api of the banking app
- Create implementing classes for the service interfaces
- Create a spring config file defining instances the DI container will manage
- Use Fluxtion annotations to export services and define event notification methods
- Pass the spring config file to the Fluxtion compiler and generate the DI container AOT
- Create an instance of the DI container and locate the service interfaces
- Use the service interfaces in the sample application

## Application structure

The top level java package is, com.fluxtion.example.cookbook.spring.service, see
the [example here]({{page.example_src}}).

sample app package structure is:

- top level: Banking app and a sample main
- service: interfaces the sample main and banking app invoke
- node: implementations of the service interfaces
- data: data types used by services
- generation: location of the Fluxtion ahead of time generated DI container

## Invoking a service

The main method acquires references to the exported service interfaces via tbe BankingApp. The BankingApp instance
creates an instance of the Fluxtion DI container and passes references to exported services back to the client code. The
reference the client code receives is a proxy handler the DI container creates, the proxy handler routes method calls
to managed instances in the container.

Services the DI container exposes are event driven, they are designed to be invoked asynchronously and not return a
value to client code. A service can optionally return a boolean value that is used by the container as a dirty flag,
if the flag is true then child references are notified the parent has been updated. Child instances are notified by
calling a method if it is annotated with an OnTrigger annotation. OnTrigger methods return a boolean flag that is used
to signal children should be notified of a change.

The fluxtion DI container manages all the proxy creation, event dispatch to services, monitoring dirty flags and
propagating event notifications to child references.

### Main method execution

{% highlight java %}
public class Main {

    public static void main(String[] args) {
        BankingApp bankingApp = new BankingApp(GenerationStrategy.USE_AOT);
        //get services
        Account accountService = bankingApp.getBankAccount();
        BankingOperations bankControllerService = bankingApp.getBankingOperations();
        CreditCheck creditCheckService = bankingApp.getCreditCheck();
        //persistence
        FileDataStore fileDataStore = new FileDataStore(Paths.get("data/spring/bank"));
        bankControllerService.setDataStore(fileDataStore);
        //replay state
        fileDataStore.replay(bankingApp.getEventConsumer());

        bankingApp.start();
        //should reject unknown account
        accountService.deposit(999, 250.12);

        //get opening balance for acc 100
        accountService.publishBalance(100);

        //should reject bank closed
        accountService.openAccount(100);
        accountService.deposit(100, 250.12);

        //open bank
        bankControllerService.openForBusiness();
        accountService.deposit(100, 250.12);

        //blacklist an account
        creditCheckService.blackListAccount(100);
        accountService.deposit(100, 46.90);

        //remove account from blacklist
        creditCheckService.whiteListAccount(100);
        accountService.deposit(100, 46.90);

        //close bank
        bankControllerService.closedForBusiness();
        accountService.deposit(100, 13);
    }

}
{% endhighlight %}

## Exporting a service





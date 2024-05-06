---
title: Spring integration
parent: Examples
has_children: false
nav_order: 1
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/spring
resources_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/resources/com/fluxtion/example/cookbook/spring
---

## Introduction

Spring is a popular DI container in the java world, this tutorial demonstrates how the construction logic of spring can
be combined with the dispatching logic of Fluxtion to simplify building event driven applications. The goal is to allow
the developer to concentrate on developing application logic while the container automatically builds the object graph
and constructs event dispatch logic.

Fluxtion is a dependency injection container specialised for event driven application deployments. The container
exposes event consumer end-points, routing events as methods calls to beans within the running container. A bean
registers a method as an event-handler by using Fluxtion annotations. Any beans referencing an event-handler bean will
be triggered by the container as the internal dispatcher propagates an event notification through the object graph.

All methods on an interface can be exported by annotating the interface in an implementing bean, the container exports
the interface methods as a single service. A client can look up an exported service by interface type using the
container apis. All method calls on the service proxy are routed through the container's internal dispatcher.

This example builds a small banking application, that supports credit, debit, account query, credit checking,
opening hours and persistence functions. The methods are grouped into service interfaces that are exposed by the
container.

The steps to combine spring and fluxtion:

- Create service interfaces that define the api of the banking app
- Create implementing classes for the service interfaces
- Create a spring config file declaring instances the DI container will manage
- Use Fluxtion annotations to export services and define event notification methods
- Pass the spring config file to the Fluxtion compiler and generate the DI container AOT
- Create an instance of the DI container and locate the service interfaces
- Use the service interfaces in the sample application

## Application structure

[See the example on GitHUb]({{page.example_src}}), top level package is `com.fluxtion.example.cookbook.spring.service`.

Package structure:

- **top level**: Banking app and a sample main
- **service**: interfaces the sample main and banking app invoke
- **node**: implementations of the service interfaces
- **data**: data types used by services
- **generation**: location of the Fluxtion ahead of time generated DI container

## Spring beans
Fluxtion provides support for building the DI container using spring configuration. The example uses [a spring 
configuration]({{page.resources_src}}/spring-account.xml) file to declare the beans that will be managed by the Fluxtion DI container:

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="
http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="accountBean" class="com.fluxtion.example.cookbook.spring.node.AccountNode">
        <property name="responsePublisher" ref="responsePublisher" />
    </bean>

    <bean id="creditCheck" class="com.fluxtion.example.cookbook.spring.node.CreditCheckNode">
        <property name="transactionSource" ref="accountBean"/>
        <property name="responsePublisher" ref="responsePublisher" />
    </bean>

    <bean id="transactionStore" class="com.fluxtion.example.cookbook.spring.node.CentralTransactionProcessor">
        <property name="transactionSource" ref="creditCheck"/>
        <property name="responsePublisher" ref="responsePublisher" />
    </bean>

    <bean id="responsePublisher" class="com.fluxtion.example.cookbook.spring.node.ResponsePublisher">
    </bean>
</beans>
{% endhighlight %}

Once the file is created the file location can be passed to Fluxtion to build the container:

{% highlight java %}
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("com/fluxtion/example/cookbook/spring/spring-account.xml");
eventProcessor = FluxtionSpring.interpret(context);
{% endhighlight %}

## Invoking a service

The BankingApp instance creates an instance of the AOT generated Fluxtion DI container and provides access to container
exported services. The service reference the client code receives is a proxy the DI container creates, the
proxy handler routes method calls to instances managed by the container.

Services the DI container exposes are event driven, they are designed to be invoked asynchronously and do not return
application values to client code. A service method can optionally return a boolean value that is used by the container
as an event propagation flag. If the flag is true then child references are notified the parent has changed due to an
external event. Child instances are notified of event propagation by the container calling a trigger method. A trigger
method is any zero argument method marked with an `OnTrigger` annotation. `OnTrigger` methods return an event
propagation flag to control event notification dispatch in the same was as exported service methods.

The fluxtion DI container manages all the proxy creation, event dispatch to services, monitoring dirty flags and
propagating event notifications to child references. To access an exported service client code calls:

{% highlight java %}
T exportedService = eventProcessor.getExportedService();
{% endhighlight %}

### Main method execution

The main method creates an instance of the BankingApp, rerieves service interfaces and invokes application methods on
the interfaces. It is expected the BankingApp would be instantiated and used within a larger application that marshalls
client requests from the network and then invokes the BankingApp appropiately.

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

running the main method prints the following to the console:

{% highlight text %}
[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - deposit request:Transaction[accountNumber=999, amount=250.12, debit=false]
[INFO] AccountNode - reject unknown account:999
[INFO] ResponsePublisher - response reject:Transaction[accountNumber=999, amount=250.12, debit=false]
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[INFO] AccountNode - ------------------------------------------------------
[INFO] ResponsePublisher - account:100, balance:3267.22
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - opened account:100
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - deposit request:Transaction[accountNumber=100, amount=250.12, debit=false]
[INFO] CreditCheckNode - credit check passed
[WARN] CentralTransactionProcessor - reject bank closed
[INFO] ResponsePublisher - response reject:Transaction[accountNumber=100, amount=250.12, debit=false]
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[INFO] CentralTransactionProcessor - open accepting transactions
[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - deposit request:Transaction[accountNumber=100, amount=250.12, debit=false]
[INFO] CreditCheckNode - credit check passed
[INFO] CentralTransactionProcessor - accept bank open
[INFO] AccountNode - updated balance:3517.3399999999997 account:100
[INFO] ResponsePublisher - response accept:Transaction[accountNumber=100, amount=250.12, debit=false]
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[INFO] CreditCheckNode - credit check blacklisted:100
[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - deposit request:Transaction[accountNumber=100, amount=46.9, debit=false]
[WARN] CreditCheckNode - credit check failed
[INFO] ResponsePublisher - response reject:Transaction[accountNumber=100, amount=46.9, debit=false]
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[INFO] CreditCheckNode - credit check whitelisted:100
[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - deposit request:Transaction[accountNumber=100, amount=46.9, debit=false]
[INFO] CreditCheckNode - credit check passed
[INFO] CentralTransactionProcessor - accept bank open
[INFO] AccountNode - updated balance:3564.24 account:100
[INFO] ResponsePublisher - response accept:Transaction[accountNumber=100, amount=46.9, debit=false]
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------

[WARN] CentralTransactionProcessor - closed rejecting all transactions
[INFO] AccountNode - ------------------------------------------------------
[INFO] AccountNode - deposit request:Transaction[accountNumber=100, amount=13.0, debit=false]
[INFO] CreditCheckNode - credit check passed
[WARN] CentralTransactionProcessor - reject bank closed
[INFO] ResponsePublisher - response reject:Transaction[accountNumber=100, amount=13.0, debit=false]
[INFO] AccountNode - request complete
[INFO] AccountNode - ------------------------------------------------------


Process finished with exit code 0
{% endhighlight %}

## Exporting a service

To export a service the following steps are required:

- Create an interface and then implement the interface with a concrete class
- The implementation class must extend ```ExportFunctionNode```
- Mark the interface to export with ```@ExportService``` annotation

For example to export the CreditCheck service:

### CreditCheck interface

{% highlight java %}
public interface CreditCheck {
    void blackListAccount(int accountNumber);
    void whiteListAccount(int accountNumber);
}

{% endhighlight %}

### CreditCheckNode concrete class

The CreditCheckNode implements two interfaces CreditCheck and TransactionProcessor. Only the CreditCheck interface
methods are exported as this is only interface marked with ```@ExportService```

{% highlight java %}
public class CreditCheckNode extends ExportFunctionNode implements @ExportService CreditCheck, TransactionProcessor {

    private transient Set<Integer> blackListedAccounts = new HashSet<>();
    private TransactionProcessor transactionSource;
    private ResponsePublisher responsePublisher;

    @Override
    @NoPropagateFunction
    public void blackListAccount(int accountNumber) {
        log.[INFO]("credit check blacklisted:{}", accountNumber);
        blackListedAccounts.add(accountNumber);
    }

    @Override
    @NoPropagateFunction
    public void whiteListAccount(int accountNumber) {
        log.[INFO]("credit check whitelisted:{}", accountNumber);
        blackListedAccounts.remove(accountNumber);
    }

    public boolean propagateParentNotification(){
        Transaction transaction = transactionSource.currentTransactionRequest();
        int accountNumber = transaction.accountNumber();
        if(blackListedAccounts.contains(accountNumber)){
            log.[WARN]("credit check failed");
            transactionSource.rollbackTransaction();
            responsePublisher.rejectTransaction(transaction);
            return false;
        }
        log.[INFO]("credit check passed");
        return true;
    }

    @Override
    public Transaction currentTransactionRequest() {
        return transactionSource.currentTransactionRequest();
    }

    @Override
    public void rollbackTransaction() {
        transactionSource.rollbackTransaction();
    }

    @Override
    public void commitTransaction(){
        transactionSource.commitTransaction();
    }

}
{% endhighlight %}

Notice the two CreditCheck methods are annotated with ```@NoPropagateFunction```, telling Fluxtion that no event
propagation will occur when either of these methods is invoked. The credit black list is a map and these methods should
only change the state of the internal map and not cause further processing to occur in the object graph.

## Locating a service

The steps required to locate a service and invoke methods on it are:

- Build the DI container using one of the Fluxtion build methods
- To correctly intitialise the container call ```eventProcessor.init()``` on the DI instance
- To access the service call ```T service = eventProcessor.getExportedService()``` with the desired service type T

### Accessing CreditCheck service

The code below uses an enum to allow the user to select the DI generation strategy, in this example we are using the
AOT strategy. After eventprocessor generation the exported service are located and assigned to member variables in
the BankingApp class.

```java
public class BankingApp {

    private final EventProcessor<?> eventProcessor;
    private final Account bankAccount;
    private final CreditCheck creditCheck;
    private final BankingOperations bankingOperations;
    private final Consumer eventConsumer;

    @SneakyThrows
    public BankingApp(GenerationStrategy generationStrategy) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("com/fluxtion/example/cookbook/spring/spring-account.xml");
        eventProcessor = switch (generationStrategy) {
            case USE_AOT -> new SpringBankEventProcessor();
            case INTERPRET -> FluxtionSpring.interpret(context);
            case COMPILE -> FluxtionSpring.compile(context);
            case GENERATE_AOT -> FluxtionSpring.compileAot(context, c -> {
                c.setPackageName("com.fluxtion.example.cookbook.spring.generated");
                c.setClassName("SpringBankEventProcessor");
            });
        };
        eventProcessor.init();
        bankAccount = eventProcessor.getExportedService();
        creditCheck = eventProcessor.getExportedService();
        bankingOperations = eventProcessor.getExportedService();
        eventConsumer = eventProcessor::onEvent;
    }

    public CreditCheck getCreditCheck() {
        return creditCheck;
    }
}
```

**line 22-24** acquire the exported services from the container by interface type


---
title: Node callback pattern
parent: Cookbook
has_children: false
nav_order: 3
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/nodecallback
---

## Introduction

This example demonstrates invoking instances in a graph by direct function calls and the instance method triggering a graph processing
cycle. Dependencies of the callback node are triggered as if the method was annotated with an EventHandler annotation.
The callback pattern allows targeting of a single instance in a graph.


[See the example here]({{page.example_src}}/Main.java)


Sometimes a user will want to target a graph node function directly to trigger a graph cycle. Using EventHandler 
annotation on a method is analogous to listening to a topic in a message system, while the node call back pattern is 
like having a queue with a single listener. 

## Steps to register a call back
In order for a node to available as a uniquely addressable call back node the following steps are required

1. The node must inherit from [CallBackNode]({{site.fluxtion_src_runtime}}/callback/CallBackNode.java)
2. The node must call the super constructor with a unique name, this allows the node to found via its name
3. To trigger a graph cycle from a method call [CallBackNode#triggerGraphCycle]({{site.fluxtion_src_runtime}}/callback/CallBackNode.java#52)
4. Add the node as normal to Fluxtion, build the graph and call init() on the event processor

## Accessing the node
Once the graph is built and init() is called the callback node can be invoked to trigger graph cycles. But first the node must
be discovered and referenced from outside the graph. There are a number of strategies to do this:

1. Call  ```EventProcessor.getNodeById('node_name');``` using the name supplied in the node constructor
2. Pass a mutable event into the graph the node is listening for. The node adds its reference to the event
3. Inject an instance to the node and the node registers itself with the injected instance at startup
4. Create an interface with default methods implementations using (1) above, make the EventProcessor implement the interface

The example uses the first of these approaches, ```EventProcessor.getNodeById('node_name');```

## Example

[See the example here]({{page.example_src}}/Main.java)

The example tracks voting candidates and gathers the results into a collector for printing. The classes in the example are:
- [Main]({{page.example_src}}/Main.java) method, builds the graph, finds the callback nodes by name and invokes functions on the nodes directly
- [CandidateVoteHandler]({{page.example_src}}/CandidateVoteHandler.java) is a call back node that can trigger graph cycles
- [ElectionTracker]({{page.example_src}}/ElectionTracker.java) Aggregates CandidateVoteHandlers, annotated methods receive callbacks during a graph cycle and prints results.
- [VoteData]({{page.example_src}}/VoteData.java) Generic data record that holds voting information updates.

Although each CandidateVoteHandler has the same method signature only the targeted callback instance is invoked during
execution. 
  
### CandidateVoteHandler
Extends CallBackNode and uses base methods to trigger graph cycles.

{% highlight java %}
public class CandidateVoteHandler extends CallBackNode {

    private int updateId;
    private String lastNewsStory = "";
    private int totalVotes;

    public CandidateVoteHandler(String name) {
        super(name);
    }

    public void newStory(VoteData<String> argument, int updateId) {
        this.updateId = updateId;
        this.lastNewsStory = argument.value();
        super.triggerGraphCycle();
    }

    public void voteCountUpdate(VoteData<Integer> argument, int updateId) {
        this.updateId = updateId;
        this.totalVotes += argument.value();
        super.triggerGraphCycle();
    }

    @Override
    public String toString() {
        return getName() +
                ", totalVotes=" + totalVotes +
                ", updateId=" + updateId +
                ", lastNewsStory='" + lastNewsStory + '\''
                ;
    }
}
{% endhighlight %}


### Main
Builds the graph, finds CandidateVoteHandler nodes by name and invokes methods on the instances directly to trigger 
graph process cycles.

{% highlight java %}
public class Main {

    public static void main(String[] args) throws NoSuchFieldException {
        var voteProcessor = Fluxtion.interpret(c -> c.addNode(new ElectionTracker(List.of(
                new CandidateVoteHandler("Red_party"),
                new CandidateVoteHandler("Blue_party"),
                new CandidateVoteHandler("Green_party")
        ))));
        voteProcessor.init();

        //get the nodes by name from the graph
        CandidateVoteHandler redPartyNode = voteProcessor.getNodeById("Red_party");
        CandidateVoteHandler bluePartyNode = voteProcessor.getNodeById("Blue_party");
        CandidateVoteHandler greenPartyNode = voteProcessor.getNodeById("Green_party");

        //invoke functions directly on nodes - creates a
        redPartyNode.voteCountUpdate(new VoteData<>(25), 1);
        bluePartyNode.voteCountUpdate(new VoteData<>(12), 2);
        bluePartyNode.voteCountUpdate(new VoteData<>(19), 3);
        bluePartyNode.voteCountUpdate(new VoteData<>(50), 4);
        redPartyNode.newStory(new VoteData<>("red alert!!"), 5);
        greenPartyNode.newStory(new VoteData<>("green and gone :("), 6);
        greenPartyNode.voteCountUpdate(new VoteData<>(2), 1);
    }
}
{% endhighlight %}


### ElectionTracker
Aggregated CandidateVoteHandlers and annotated methods receive callbacks during a graph cycle

{% highlight java %}
public record ElectionTracker(List<CandidateVoteHandler> candidateVoteHandlers) {

    @OnParentUpdate
    public void updatedCandidateStatus(CandidateVoteHandler candidateVoteHandler) {
        System.out.println("update for:" + candidateVoteHandler.getName());
    }

    @OnTrigger
    public boolean printLatestResults() {
        String result = candidateVoteHandlers.stream()
                .map(Objects::toString)
                .collect(Collectors.joining("\n\t", "\t", "\n\n"));
        System.out.println(result);
        return true;
    }
}
{% endhighlight %}


### VoteData
Generic data holder used as function arguments

{% highlight java %}
public record VoteData<T>(T value) { }
{% endhighlight %}


## Execution output


Running the sample produces this output:

{% highlight console %}
update for:Red_party
Red_party, totalVotes=25, updateId=1, lastNewsStory=''
Blue_party, totalVotes=0, updateId=0, lastNewsStory=''
Green_party, totalVotes=0, updateId=0, lastNewsStory=''


update for:Blue_party
Red_party, totalVotes=25, updateId=1, lastNewsStory=''
Blue_party, totalVotes=12, updateId=2, lastNewsStory=''
Green_party, totalVotes=0, updateId=0, lastNewsStory=''


update for:Blue_party
Red_party, totalVotes=25, updateId=1, lastNewsStory=''
Blue_party, totalVotes=31, updateId=3, lastNewsStory=''
Green_party, totalVotes=0, updateId=0, lastNewsStory=''


update for:Blue_party
Red_party, totalVotes=25, updateId=1, lastNewsStory=''
Blue_party, totalVotes=81, updateId=4, lastNewsStory=''
Green_party, totalVotes=0, updateId=0, lastNewsStory=''


update for:Red_party
Red_party, totalVotes=25, updateId=5, lastNewsStory='red alert!!'
Blue_party, totalVotes=81, updateId=4, lastNewsStory=''
Green_party, totalVotes=0, updateId=0, lastNewsStory=''


update for:Green_party
Red_party, totalVotes=25, updateId=5, lastNewsStory='red alert!!'
Blue_party, totalVotes=81, updateId=4, lastNewsStory=''
Green_party, totalVotes=0, updateId=6, lastNewsStory='green and gone :('


update for:Green_party
Red_party, totalVotes=25, updateId=5, lastNewsStory='red alert!!'
Blue_party, totalVotes=81, updateId=4, lastNewsStory=''
Green_party, totalVotes=2, updateId=1, lastNewsStory='green and gone :('
{% endhighlight %}







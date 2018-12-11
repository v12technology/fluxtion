# Vision

We hope that in the longer term Fluxtion will help reduce some of the energy consumed in software. Presently 10% of the world's energy is expended in IT, greater than the whole airline transport industry. Some forecast that by 2040 all the world's energy will be consumed in IT. If we can help in some small way that would be a vicory for our project.

Producing correct solutions is hard, producing correct, performant and highly efficient solutions requires years of experience in the team. We hope that by moving a lot of the heavy lifting to Fluxtion, teams can concentrate on correctness while Fluxtion keeps the energy use in check. Our hope is using Fluxtion is like adding several world class developers to your team.

Fluxtion works deep in meta-space. In our declarative extensions module we use recursive compilation where each stage of output is analysed for new meta information creating further constructs and possible new compilation stages. We think there maybe a requirement for a new class of developer who works in meta-space. Their meta work will be consumed by the general developer population via Fluxtion, software engineering re-use but not necessarily component re-use.

## Philosophy
Every decision we make about Fluxtion is driven by increasing efficiency, we want to reduce cost. Where we differ from other philosophies is the inputs that make up our analysis. Just reducing cpu cycles is not our end goal, we want to be broad in our ambition.

On a personal level solving a coding problem may bring a feeling of self satisifaction. But the hundreds of billions of dollars of investment in IT are made because they make a real reduction on the bottom line. Information technology is an efficiency play. We dont have the space for a full discussion here, but rather list some of the non-obvious sources of cost we would like to address:

### Component re-use
Component re-use is proferred as a goal because it reduces the lines of custom code to write and therefore saves money. There are hidden costs in using someone else's framework; integration costs, learning costs, understanding unexpected behaviour and supporting someone else's product in your system. Generating code means more lines, but only some are manually typed. The generated solution can now fit our problem more exactly, is easier to understand, debug and support. 

### If
Reasoning about code that has multiple if statements is not only hard but it is also expensive to compute due to pipelining failures on the cpu. State-machine code is simple to understand and has minimal conditional branching, but is expensive to construct. Having a system that can cheaply produce statemachine-like transitions for application code would be beneficial. Minimising user written condtional branching, reduces bugs and saving computational resource.  

### Concryption
Some systems are completely assembled by configuration that can be spread over several documents. The configuration can become a language in itself, but with almost no type checking. Worse it may become both order and state dependent, evolving into a dialect that only one or two developers can support for your system. We call this effect concryption - encryption by configuration. Components are sometimes so decoupled we have no hope of understanding the linkages. This creates inertia in development slowing delivery, deployment and fault finding. Sometimes it would be just cheaper and easier to specify the component configuration in code.

### Fear of starting again
Once we build a set of complex of application logic we can become highly resistant to pulling it down and building it again. The construction cost may be high, logic may be spread over multiple components. We then start making compromised decisions that eventaully lead to fragile systems. We should be happy to destroy a solution and build it again if the assembly cost is cheap enough.

### Staying small
As developers we have a wonderful eye for detail, usually our unit tests are great. But when it comes to the large the complexity overwhelms us, integration tests are usually much less effective. When we create larger logical processing units they are less reliable. It would be better to leave the correct combination of smaller logical units to an automated process at the macro-level and allow developers to create the intricate logic at the micro-level.

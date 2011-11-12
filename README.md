Instrumentation mechanism
=========================

Instrumentation mechanism is responsible for inserting code that performs measurements in the key parts of the application that is instrumented. There are different ways in which instrumentation can be realized e.g. either by direct source code editing, by changing the already compiled sources (byte-code manipulation) or by following a
mixed approach in which source code is marked, using annotations for example, to be instrumented by byte-code manipulation. The byte-code instrumentation can be done at compile time or at runtime.

SIM-Instrumentation uses AspectJ (http://www.eclipse.org/aspectj), a Java aspect oriented framework implementation. AspectJ allows SIM to inject instrumentation code into specific locations, using byte-code manipulation. The aop.xml file defines the aspects to apply on code at runtime and is read at startup by aspectjweaver javaagent. 


SIM-Instrumentation internals
-----------------------------

### Structure

SIM-instrumentation is structured into three main (sub)components/phases:
* code injection (done with the help of ApectJ constructs);
* metrics measurements and collection;
* agent communication.


### Code injection
sim.instrumentation.aop.aspectj.AbstractMethodInterceptor is the base aspect that handles all the AspectJ and metrics measurements plumbing. It defines the abstract pointcut methodExecution() which represents the place where the code injection will take place and where measurements will be performed. Concrete aspects extending
AbstractMethodInterceptor need to implement this pointcut using standard AspectJ pointcut definition. For example to instrument all LarKC plugins one could define pointcut methodToInstrument() as:

public pointcut methodsToInstrument(): within(eu.larkc.plugin.Plugin) && execution(∗ ∗(..));

Another option is to use the @Instrument annotation. For example to instrument all Larkc plugins one needs to annotate eu.larkc.plugin.Plugin with @Instrument annotation.

    @Instrument public abstract class Plugin { ... }

Context creation is handled by AbstractContextCreator, base abstract aspect used to indicate a method or constructor where a new context should be created for the current execution flow. 

A Context is a container of information for an execution flow subgraph. It is uniquely identify by an id, it has a name and a tag and is linked to its parent Context through parentContextId property (which can be null in case this context is the root context). Any system can be seen as a black box that takes external input, processes it
and produces the output. We call this process, of taking the input and producing the output, an "execution flow". So an execution flow can be seen as a series of operations, that can be executed either one after the other or in parallel or both. These operations can be grouped based on logical function they perform. Tracking these logical groups is made possible by creating one Context for each of them. Operations from one logical group will then use the Context of that logical group to publish information. For example, in case of an ETL (Extract, Transform, Load) type of execution flow, we would like to group the operations of this execution flow into the three logical
functions, in our case: extract, transform, load. This can be done by creating three Context instances for each logical function. Operations executing for logical function Extract would operate on the Extract Context, those for Transform would operate on the Transform Context and those for Load would operate on the Load Context.

In order to create a new context for the current execution flow, one needs to define a concrete aspect of AbstractContextCreator that implements the methodToCreateNewContext pointcut, which defines the method or constructor that will create a new context when executed. Optionally, the concrete aspect can also override get-
ContextNameAndTag method in order to define a custom name and tag for the new created context. Default implementation returns the class name of this joint point as the Name of the new context and the package name as the Tag. An example on how to create a new context when method myMethod from MyClass is given below:

    public aspect MyNewContext extends AbstractContextCreator {
      public pointcut methodToCreateNewContext(): execution(∗ MyClass.myMethod(..));
      protected String[] getContextNameAndTag (JoinPoint jp) {
        return new String[]{"CustomContextName", "CustomContextTag"};
      }
    }

The same thing can be accomplished by using the @CreateContext annotation:

    public class MyClass {
      @CreateContext (name="CustomContextName", tag="CustomContextTag")
      public void myMethod() {..}
    }

Writing information into Context is done using AbstractContextWriter, base abstract aspect used to indicate a join point where values that are available at that joinpoint should be written/published to the context of the current execution flow. 

The placeToTriggerTheContextWrite pointcut defines the joint point where the reading of the values we want to publish to the context should happen. It can be a method or a constructor execution, a field get or set or an exception handler. 

In order to publish values to the current context of the execution flow, one needs to define a concrete aspect of AbstractContextWriter that implements the placeToTriggerTheContextWrite pointcut. Optionally, the concrete aspect can also override readValuesBefore and/or readValuesAfter methods in order to publish custom values to the context. Default implementation will write to context the arguments (if there are any) in case the executing joint point is a method or constructor, the field value in case of a field set and the exception message in case of exception. Also returns the return value (if there is any) in case the executing joint point is a method and the field value in case of a field get. An example on how to publish values to the context when method myMethod from MyClass is given below:


    public aspect MyContextWriter extends AbstractContextWriter {
      public pointcut placeToTriggerTheContextWrite(): execution(∗ MyClass.myMethod(..));
    }


The same thing can be accomplished by using the @WriteToContext annotation:

    public class MyClass {
      @WriteToContext
      public void myMethod() {..}
    }


### Information about collected metrics

The generic part of SIM (sim-instrumentation) allows for the measuring of the following metrics on method invocation:

* wall clock time - elapsed time between method entry and method exit (ms)
* thread user cpu time - user CPU time spent by current thread executing this method (ms)
* thread system cpu time - system CPU time spent by current thread executing this method (ms)
* thread total cpu time - total CPU time spent by current thread executing this method (user time + system time) (ms)
* process total cpu time - total CPU time spent by current process (all threads from the application) while executing this method (ms)
* thread count - how many threads did this method invocation create (integer)
* thread block count - the total number of times that the current thread executing this method entered the BLOCKED state (integer)
* thread block time - the total accumulated time the current thread executing this method has been in the BLOCKED (ms)
* thread wait count - the number of times that the current thread executing this method has been in the WAITING or TIMED_WAITING state (integer)
* thread wait time - the total accumulated time the current thread executing this method has been in the WAITING or TIMED_WAITING state (ms)
* gcc count - total number of collections that have occurred while executing this method
* gcc time - approximate accumulated collection elapsed time in milliseconds while executing this method
* endedWithError - tells if the method ended with an uncaught exception exception - in case the method execution ended with an exception this is the exception.toString result

Every five seconds also generic platform measurement are collected:

* gcc count - total number of collections since platform start (count)
* gcc time - total accumulated collection elapsed time since platform start (ms)
* cpu time - total CPU time spent by current instrumented application since platform start (ms)
* uptime - total time since platform start (ms)
* average cpu usage - average cpu usage since platform start (%)
* cpu usage - cpu usage for the last 5 seconds (%)
* used memory - the amount of current used memory in bytes (bytes)

Generic metrics measurements such as wallClockTime, threadUserCpuTime, thread Count, threadBlockTime, threadWaitTime, threadGccTime are provided by the Util class sim.instrumentation.data.Metrics. In order to perform method metrics measurements sim.instrumentation.data.Probe builder should be used.

    // before method invocation
    MethodProbe mp = Probe.createMethodProbe(class_name, method_name);
    mp.start();
    // invoke method
    ...
    // after method invocation
    mp.end();

When mp.end() is called, metrics measurements is finalized and the measurements are also published to the sim.instrumentation.data.Collector, class responsible for collecting measurements and sending them to the agent.


### Agent communication

The class sim.instrumentation.data.Collector.AgentComunicator implements the communication with profiling agents. AgentComunicator is running in a separate threa and sends every 5 seconds the collected metrics measurements to the agent using standard Java serialization over TCP sockets.


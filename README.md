# Overview
This project is a showcase for some of the stream processing ideas presented
in several of the texts and videos I've been studying.  The goal is to see
how quickly we can boostrap an implementation using AWS.

## Concepts
Four types of messages stream travel through the system: `Command`, `Query`
`Event` and `Error`.  The Command and Query messages typically originate from API
clients, such as a JavaScript-based web application.  The Event messages
normally originate from back-end systems but API clients can also inject Events
as well.

* **Command** - a request to perform some task at some point in the future, eg.
*Please order a large pepperoni pizza*
* **Event** - something that has occurred in the past, a fact, eg.
*The pizza has been ordered*
* **Query** - a variant of the Command where no state change in implied,
we are just fetching data from the system, eg *Please give me the details for
that pizza order*
* **Error** - a variant of an Event describing a failure in processing, eg
*The command to order pizza failed due to a message parsing issue*

Messages captured at the system's ingress point are immediately sent to a stream,
which in our case is Kinesis.  New messages are archived to S3 prior to any
transformation and other processing.  The storage of the raw messages are
important for auditing and replay scenarios.  Kinesis supports multiple readers
so all processing of streams is effectively done in parallel.  As the messages
are being journaled, a Router examines the message and, based on its type,
writes the message to one of 4 possible streams:

* **Command Stream** where Command messages go to be processed
* **Query Stream** where Query messages go to be processed
* **Event Stream** where Event messages go to be processed
* **Error Stream** where unrouted messages to go to be processed

In general, the input of a message to a processor will result in the writing of
a new message to one of two streams: Event or Error.  This is much like the
Unix model where a program accepts a message from `stdin` and directs the results
to either `stdout` or `stderr`.

# Prerequisites

* [JDK](http://www.oracle.com/technetwork/java/index.html) installed and working

# Building
Use `./gradlew` to execute the [Gradle](https://gradle.org/) build script.

# Installation
* [Avro Tools](http://avro.apache.org/releases.html) downloaded into the project directory

# Tips and Tricks

# Troubleshooting

# License and Credits
This project is licensed under the [Apache License Version 2.0, January 2004](http://www.apache.org/licenses/).

* [Event Streams in Action: Unified log processing with Kafka and Kinesis](https://www.manning.com/books/event-streams-in-action)
* [Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems](http://shop.oreilly.com/product/0636920032175.do)
* [Kafka Streams in Action](https://www.manning.com/books/kafka-streams-in-action)
* [Streaming Data: Understanding the real-time pipeline](https://www.manning.com/books/streaming-data)
* [Big Data: Principles and best practices of scalable realtime data systems](https://www.manning.com/books/big-data)
* [Kafka The Definitive Guide: Real-Time Data and Stream Processing at Scale](http://shop.oreilly.com/product/0636920044123.do)

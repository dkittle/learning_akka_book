# learning_akka

[![Join the chat at https://gitter.im/dkittle/learning_akka](https://badges.gitter.im/dkittle/learning_akka.svg)]
(https://gitter.im/dkittle/learning_akka?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This project is code from a study of the book
["Learning Akka" by Jason Goodwin](https://www.packtpub.com/application-development/learning-akka),
published by Packt Publishing.

## Chapter 2

The code in Chapter 2 was to build a Pong actor that would return Pong if you sent it a Ping message.
We added the ability to also handle a Pong message which would return the string Deja Vu.
Finally, we explored (via tests) a pattern for returning either the result returned by an Actor or an error as
appropriate.

### Going beyond

We've added a REST endpoint `/reverse` that takes a string as a path parameter and returns a JSON result with a
status code and the result of the operation (either a reversed string or an error message).

A StringReversingService interacts with the actor and returns a tuple used by the controller. This service is
injected into the controller.

```curl -i http://localhost:9000/reverse/fizzbuzz```

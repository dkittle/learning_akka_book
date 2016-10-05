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

*Sample `curl` to reverse one string*
```curl -i http://localhost:9000/reverse/fizzbuzz```

*Sample 'curl' to reverse more than one string*
```curl -i "http://localhost:9000/reverse-all?phrase=foo&phrase=bar"```

## Chapter 3 Mob Coding

Hello bookworms, next week we become code-monkeys! Let's take what we learned in Chapter 3 and apply it to create an
system that grabs and stores content. You are encouraged to play with `ask` and `tell` to design a solution that
makes your coding-self happy.

If you can, bring a laptop with the current *Learning Akka* code checked out and built for the first time. If you don't
have a laptop suitable for devleopment, you'll likely end up on a team where you can drive on someone elses for a while.

### Structure
We’ll split up into groups of 2-3 people and work on a problem together. One person will “drive” at any given time,
actively coding at the keyboard. People “riding shotgun” will help discuss possible solutions, suggest good test cases
and chime in when the driver make that inevitable typo.

If people already using Akka want to pair, they’ll work in pairs. For people new to Scala and/or Akka, you’ll get into
groups of 3.

### Ground Rules
**Be nice to one another**, ask if you can make a suggestion, suggest tests that might prove a point you are trying to make.

**Collaborate**, if the “driver” seems to struggle, ask if you can help out by establishing the basic structure of a
solution. Then you can pass the keyboard back and they can write some tests. Talk out loud about your thoughts if you
are driving. If you are assisting and the driver is quiet for a while, ask what they are thinking and discuss.

**Try to do TDD**, write a test against an unimplemented method (???). Starting with a test will help you preplan what
you code is going to do and will often naturally help you arrive at a clean solution.

### Prep Work
Fork, clone or download either `master` on or before Tuesday, October 11, 2016 or grab the branch named "chapter3a" from
 this repo: `https://github.com/dkittle/learning_akka`. If you've not used git before, try the following:
```
git clone https://github.com/dkittle/learning_akka
git checkout -b chapter3a
git pull
```

Load the project into either IntelliJ or Eclipse. You can usually do this easily by opening the `build.sbt` file with
your ide. If you are brave, use VIM, VI or Emacs.

Make sure your project builds for the first time. This will download all the dependencies in the `build.sbt` file and, in
the case of IntelliJ, index your project. This can take a while to do on a slow internet connection, which is what we
typically have at the book club. IDEs will usually do this when you import the project. For Vi or Emacs users, type
`sbt compile`.

To run the Play application, type `sbt run`. This will start up a Netty server listening on port 9000. You can then use
`curl` or `wget` to call your endpoints or even call your endpoints in a browser.

### Exercise Instructions


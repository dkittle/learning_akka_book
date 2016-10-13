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
have a laptop suitable for development, you'll likely end up on a team where you can drive on someone else's for a while.

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
your IDE. If you are brave, use VIM, VI or Emacs.

Make sure your project builds for the first time. This will download all the dependencies in the `build.sbt` file and, in
the case of IntelliJ, index your project. This can take a while to do on a slow internet connection, which is what we
typically have at the book club. IDEs will usually do this when you import the project. For Vi or Emacs users, type
`sbt compile`.

To run the Play application, type `sbt run`. This will start up a Netty server listening on port 9000. You can then use
`curl` or `wget` to call your endpoints or even call your endpoints in a browser.

### Exercise Instructions
We will be creating a system that fetches content from a URL and stores it in an in-memory database. You’ll interact
with the system through REST endpoints. The system does not need to survive a shutdown - do not use any durable persistence.

#### Requirement 1
1) Create a REST endpoint that accepts a URL of content that should be downloaded via an RSS feed. The application will
retrieve the RSS feed from the URL provided, parse out the articles (items) and store the content of those articles in
the AkkaDB we created in Chapter 2.

When storing content in the AkkaDb, ou should use the `<guid> element as the key for each `<item>` in the RSS feed. The value
stored in the AkkaDb could be the `<description>` element of the item or you may opt to download the content from the
item's URL found in the '<link>' of the item. The description element is often wrapped in a CDATA which you’ll need to discard.

If you grab the content from the URL in the `<link>` element, you can use the BoilerPipe library included in the Learning
Akka project to retrieve the content stripped of extraneous HTML elements/tags from it’s specific URL. For example:
`de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(new java.net.URL(“http://www.cbc.ca/news/canada/ottawa/ottawa-weather-forecast-october-5-1.3791908?cmp=rss"))``

The endpoint should be
```
POST http://localhost:9000/contents/url
```

The request body should be JSON indicating the URL to fetch the content from.
```
{
    “url”, “[url]”
}
```

If content cannot be downloaded from the URL ,return a 404 error, Not Found.

If the URL has content but it’s not in the format you expect, return a 400 error, Bad Request. If the supplied URL is invalid, return a 400 error, Bad Request.

#### Requirement 2
Create a REST endpoint that will return the content for a given guid.
```
GET http://localhost:9000/content/guid/[guid]
```

If the guid (key) is not in the AkkaDB, return a 404 error, Not Found.

#### Requirement 3
Create a REST endpoint that will return the guids for all content in the system. You do not need to paginate this data.
```
GET http://localhost:9000/contents/guids
```

For a basic system, you likely want a controller, a DB (cache) actor, an HTTP client actor and possible an XML parser actor. You may consider creating a service (or actor) between the controller and actors doing the real work.

#### Bonus
Ok, you were up for a big challenge and took on RSS parsing. RSS feeds include many articles described in <item> elements.
Create an actor that monitors the state of parsing each of the items in a feed. It should log the title of each item in
the feed as the parser parses that item. It also logs when all items from a feed have been parsed.  Content and any
additional metadata (like title) should be returned in JSON format.


#### Example Curls

Parse an RSS feed
```
curl -i -H "Content-Type: application/json" -X POST -d '{"url":"http://www.cbc.ca/cmlink/1.394"}' http://localhost:9000/contents/url
```

List the guid keys in the DB
```
curl -i http://localhost:9000/contents/guids
```

Retrieve a piece of content
```curl -i http://localhost:9000/contents/guid/[a guid from the content in the DB]```

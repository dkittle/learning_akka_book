name := "learning_akka"

version := "1.0"

lazy val `learning_akka` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc , cache , ws,
  "com.typesafe.akka" %% "akka-actor" % "2.4.9-RC1",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.9-RC1",
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  specs2 % Test
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases" ,
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

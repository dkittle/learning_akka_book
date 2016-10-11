case class Rectangle(largeur: Int, longueur: Int)


val rectangle = new Rectangle(4,5)

rectangle match {
  case Rectangle(4, 9) => println("Hello World")
  case r: Rectangle => println(s"Hello World ${r.largeur}")
  case r@Rectangle(4,5) => println(s"Hello World ${r.largeur}")
  case _ => println("Joker")
}
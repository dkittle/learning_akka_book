package chapter4

import chapter4.TicTacToeActor.{Player, Blank}

object TicTacToeGame {

  def areSameNotBlank(xs: Array[Player]): Boolean = {
    xs.length > 1 && xs(0) != Blank && xs.count(_ == xs(0)) == xs.length
  }

  def gameWon(game: Array[Player]): Boolean = game match {
    case Array(a, b, c, _*) if areSameNotBlank(Array(a,b,c)) => true
    case Array(_, _, _, a, b, c, _*) if areSameNotBlank(Array(a,b,c)) => true
    case Array(_, _, _, _, _, _, a, b, c) if areSameNotBlank(Array(a,b,c)) => true
    case Array(a, _, _, b, _, _, c, _, _) if areSameNotBlank(Array(a,b,c)) => true
    case Array(_, a, _, _, b, _, _, c, _) if areSameNotBlank(Array(a,b,c)) => true
    case Array(_, _, a, _, _, b, _, _, c) if areSameNotBlank(Array(a,b,c)) => true
    case Array(_, _, a, _, b, _, c, _, _) if areSameNotBlank(Array(a,b,c)) => true
    case Array(a, _, _, _, b, _, _, _, c) if areSameNotBlank(Array(a,b,c)) => true
    case _ => false
  }

}

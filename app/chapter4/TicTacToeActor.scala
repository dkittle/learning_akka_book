package chapter4

import akka.actor.FSM
import chapter4.TicTacToeActor._

class TicTacToeActor extends FSM[State, GameData] {

  startWith(Playing, GameData(X, Array.fill(9){' '}))

  when(Playing) {

    case Event(WhoPlays, g: GameData) =>
      sender() ! whoPlays(g.player)
      stay using g

    case Event(Play(p), g: GameData) if p < 0 || p >= g.board.length =>
        sender() ! IllegalMove
        stay using g

    case Event(Play(p), g: GameData) if g.board(p) == ' ' =>
        sender() ! whoPlays(whoPlaysNext(g.player))
        stay using g.copy(player = whoPlaysNext(g.player), g.board.updated(p, g.player))

    case Event(Play(p), g: GameData) =>
        sender() ! PositionOccupied
        stay using g

    case x =>
      println("uhh didn't quite get that: " + x)
      stay()
  }

  when(Done) {
    case x =>
      println("The game is done")
      sender() ! GameOver
      stay()
  }

  def whoPlays(p: Player): PlayingMessage = p match {
    case X => XPlays
    case _ => OPlays
  }

  def whoPlaysNext(p: Player): Player = p match {
    case X => O
    case _ => X
  }

  initialize()

}

object TicTacToeActor {

  type Player = Character
  val X: Player = 'X'
  val O: Player = 'O'

  case class GameData(player: Player, board: Array[Player])

  sealed trait State
  case object Playing extends State
  case object Done extends State

  sealed trait GameResult
  sealed trait PlayingMessage extends GameResult
  case object WhoPlays extends PlayingMessage
  case class Play(position: Int) extends PlayingMessage
  case object PositionOccupied extends PlayingMessage
  case object IllegalMove extends PlayingMessage
  case object XPlays extends PlayingMessage
  case object OPlays extends PlayingMessage
  sealed trait WinningMessage extends GameResult
  case object XWon extends WinningMessage
  case object YWon extends WinningMessage
  case object NoWinner extends WinningMessage
  case object GameOver extends WinningMessage
}


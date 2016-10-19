package chapter4

import akka.actor.{ActorRef, FSM}
import chapter4.TicTacToeActor._

class TicTacToeActor extends FSM[State, GameData] {

  startWith(Playing, GameData(X, Array.fill(9){Blank}))

  when(Playing) {

    case Event(WhoPlays, g: GameData) =>
      sender() ! whoPlays(g.player)
      stay using g

    case Event(Play(i), g: GameData) if i < 0 || i >= g.board.length =>
        sender() ! IllegalMove
        stay using g

    case Event(Play(i), g @ GameData(player, board)) if board(i) == Blank =>
        val newBoard = board.updated(i, player)
        if (gameWon(newBoard)) {
          sender() ! whoWon(player)
          goto(Done)
        }
        else if (!newBoard.contains(Blank)) {
          sender() ! NoWinner
          goto(Done)
        }
        else {
          sender() ! whoPlays(whoPlaysNext(player))
          stay using g.copy(player = whoPlaysNext(player), newBoard)
        }

    case Event(Play(i), g: GameData) =>
        sender() ! PositionOccupied
        stay using g

    case x =>
      sender() ! "unknown message"
      stay()
  }

  when(Done) {
    case x =>
      sender() ! GameOver
      stay()
  }

  def gameWon(board: Array[Player]): Boolean = TicTacToeGame.gameWon(board)

  def whoPlays(p: Player): PlayingMessage = p match {
    case X => XPlays
    case _ => OPlays
  }

  def whoWon(p: Player): WinningMessage = p match {
    case X => XWon
    case _ => OWon
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
  val Blank: Player = ' '

  case class GameData(player: Player, board: Array[Player])

  sealed trait State
  case object Playing extends State
  case object CheckBoard extends State
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
  case object OWon extends WinningMessage
  case object NoWinner extends WinningMessage
  case object GameOver extends WinningMessage
}


package nikita.kalinskiy

import akka.actor.typed.ActorSystem

import scala.util.Random

object Main extends App {
  Random.setSeed(Settings.seed)
  val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(), "Cafe")
  system ! Cafe.Start
}

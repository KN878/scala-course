package nikita.kalinskiy

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import nikita.kalinskiy.RandomGenerator.Command.GenerateDouble

import scala.util.Random

object RandomGenerator {
  sealed trait Command
  object Command {
    final case class GenerateDouble(min: Double, max: Double, replyTo: ActorRef[Double]) extends Command
  }

  def apply(seed: Long): Behavior[Command] = Behaviors.setup { ctx =>
    val random = new Random(seed)
    Behaviors.receiveMessage {
      case GenerateDouble(min, max, replyTo) =>
        val rand = (max - min) * random.nextDouble() + min
        replyTo ! rand
        Behaviors.same
    }
  }

}

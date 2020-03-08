package nikita.kalinskiy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import nikita.kalinskiy.Waiter.TakeOrder

import scala.concurrent.duration._
import scala.util.Random

object Customer {

  sealed trait Command

  case object Start extends Command

  case class LeaveOrder(order: CustomerOrder) extends Command

  case object Eat extends Command

  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command], order: CustomerOrder): Behavior[Command] = start(order, waiter)

  def start(order: CustomerOrder, waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val decidingTimeRange = Settings.decidingTimeRange
          val decidingTime = (decidingTimeRange._2 - decidingTimeRange._1) * Random.nextDouble() + decidingTimeRange._1
          ctx.scheduleOnce(decidingTime.seconds, ctx.self, LeaveOrder(order))
          leaveOrder(waiter)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! TakeOrder(order, ctx.self)
        waitForEat
      case _ => Behaviors.same
    }
  }

  def waitForEat: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat =>
        ctx.log.info(s"Now eating")
        val eatingTimeRange = Settings.eatingTimeRange
        val eatingTime = (eatingTimeRange._2 - eatingTimeRange._1) * Random.nextDouble() + eatingTimeRange._1
        ctx.scheduleOnce(eatingTime.seconds, ctx.self, Leave)
        waitToLeave
      case _ => Behaviors.same
    }
  }

  def waitToLeave: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Leave =>
        ctx.log.info(s"Now leaving")
        Behaviors.stopped
      case _ => Behaviors.same
    }
  }

}

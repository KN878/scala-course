package nikita.kalinskiy

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import nikita.kalinskiy.Waiter.TakeOrder

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Customer {

  sealed trait Command

  case object Start extends Command

  case class LeaveOrder(order: CustomerOrder) extends Command

  case object Eat extends Command

  case object Leave extends Command

  private case class WrappedRandomResponse(result: Double) extends Command
  private case object WrappedRandomException               extends Command

  implicit val timeout: Timeout = 1.second

  def apply(
      waiter: ActorRef[Waiter.Command],
      order: CustomerOrder,
      random: ActorRef[RandomGenerator.Command]
  ): Behavior[Command] = start(order, waiter, random)

  def start(
      order: CustomerOrder,
      waiter: ActorRef[Waiter.Command],
      random: ActorRef[RandomGenerator.Command]
  ): Behavior[Command] =
    Behaviors.setup { ctx =>
      val config = CustomerConfig()
      Behaviors.receiveMessage {
        case Start =>
          val (min, max) = config.getDecidingTime
          randomTime(min, max, ctx, random)
          Behaviors.same
        case WrappedRandomResponse(result) =>
          ctx.scheduleOnce(result.seconds, ctx.self, LeaveOrder(order))
          leaveOrder(waiter, random, config)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(
      waiter: ActorRef[Waiter.Command],
      random: ActorRef[RandomGenerator.Command],
      config: CustomerConfig
  ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! TakeOrder(order, ctx.self)
          waitForEat(random, config)
        case _ => Behaviors.same
      }
    }

  def waitForEat(random: ActorRef[RandomGenerator.Command], config: CustomerConfig): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Eat =>
          ctx.log.info(s"Now eating")
          val (min, max) = config.getEatingTime
          randomTime(min, max, ctx, random)
          Behaviors.same
        case WrappedRandomResponse(result) =>
          ctx.scheduleOnce(result.seconds, ctx.self, Leave)
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

  private def randomTime(
      minValue: Double,
      maxValue: Double,
      ctx: ActorContext[Customer.Command],
      random: ActorRef[RandomGenerator.Command]
  ): Unit = {
    ctx.ask(random, (ref: ActorRef[Double]) => RandomGenerator.Command.GenerateDouble(minValue, maxValue, ref)) {
      case Success(value) => WrappedRandomResponse(value)
      case Failure(exception) =>
        ctx.log.error(exception.getMessage)
        WrappedRandomException
    }
  }

}

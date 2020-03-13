package nikita.kalinskiy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import nikita.kalinskiy.Waiter.ServeOrder

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Chef {

  sealed trait Command

  case class TakeOrder(
      order: Order,
      replyTo: ActorRef[Result],
      waiter: ActorRef[Waiter.Command],
      customer: ActorRef[Customer.Eat.type]
  ) extends Command

  case class FinishOrder(orderId: Int, waiter: ActorRef[Waiter.Command], customer: ActorRef[Customer.Eat.type])
      extends Command

  sealed trait State

  case object Free extends State

  case object Cooking extends State

  private case class WrappedRandomResponse(
      newTotalCookTime: Double,
      order: Order,
      waiter: ActorRef[Waiter.Command],
      customer: ActorRef[Customer.Eat.type]
  ) extends Command
  private case object WrappedRandomException extends Command

  implicit val timeout: Timeout = 1.second

  def apply(random: ActorRef[RandomGenerator.Command], config: ChefConfig): Behavior[Command] =
    withState(Free, random, config)

  def withState(state: State, random: ActorRef[RandomGenerator.Command], config: ChefConfig): Behavior[Command] =
    Behaviors.setup { ctx =>
      def decideOnCookingTime(
          order: Order,
          totalCookTime: Double,
          waiter: ActorRef[Waiter.Command],
          customer: ActorRef[Customer.Eat.type]
      ): Unit = {
        for (khinkaly <- order.dishes) {
          val (minTime, maxTime) = khinkaly.stuffing match {
            case Stuffing.Beef               => config.getBeefTime
            case Stuffing.Mutton             => config.getMuttonTime
            case Stuffing.CheeseAndMushrooms => config.getCheeseAndMushroomsTime
          }
          ctx.ask(random, (ref: ActorRef[Double]) => RandomGenerator.Command.GenerateDouble(minTime, maxTime, ref)) {
            case Success(value) =>
              val remainedOrder = Order(order.orderId, order.dishes.tail)
              WrappedRandomResponse(totalCookTime + value * khinkaly.amount, remainedOrder, waiter, customer)
            case Failure(exception) =>
              ctx.log.error(exception.getMessage)
              WrappedRandomException
          }
        }
      }

      Behaviors.receiveMessage {
        case TakeOrder(order, replyTo, waiter, customer) =>
          if (state == Free) {
            val newState = Cooking
            ctx.log.info(s"Started cooking order ${order.orderId}: ${order.dishes.toString}")
            replyTo ! Result.Ok
            decideOnCookingTime(order, 0, waiter, customer)
            withState(newState, random, config)
          } else {
            ctx.log.info(s"Cannot start order ${order.orderId}, busy with other order")
            replyTo ! Result.Busy
            Behaviors.same
          }
        case FinishOrder(orderId, waiter, customer) =>
          val newState = Free
          ctx.log.info(s"Order $orderId is finished")
          waiter ! ServeOrder(orderId, customer)
          withState(newState, random, config)
        case WrappedRandomResponse(totalCookTime, order, waiter, customer) =>
          if (order.dishes.nonEmpty)
            decideOnCookingTime(order, totalCookTime, waiter, customer)
          else
            ctx.scheduleOnce(totalCookTime.seconds, ctx.self, FinishOrder(order.orderId, waiter, customer))
          Behaviors.same
        case _ => Behaviors.same
      }
    }

}

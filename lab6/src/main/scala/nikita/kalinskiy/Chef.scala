package nikita.kalinskiy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import nikita.kalinskiy.Waiter.ServeOrder

import scala.concurrent.duration._
import scala.util.Random

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

  def apply(): Behavior[Command] = withState(Free)

  def withState(state: State): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    def decideOnCookingTime(order: Order): Double = {
      order.dishes.foldLeft(0.0) { (totalTime, khinkali) =>
        khinkali.stuffing match {
          case Stuffing.Beef =>
            val beefTimeRange = Settings.beefTimeRange
            totalTime + randomCookingTime(beefTimeRange) * khinkali.amount
          case Stuffing.Mutton =>
            val muttonTimeRange = Settings.muttonTimeRange
            totalTime + randomCookingTime(muttonTimeRange) * khinkali.amount
          case Stuffing.CheeseAndMushrooms =>
            val cheeseAndMushroomsTimeRange = Settings.cheeseAndMushroomsTimeRange
            totalTime + randomCookingTime(cheeseAndMushroomsTimeRange) * khinkali.amount
        }
      }
    }

    def randomCookingTime(timeRange: (Double, Double)): Double = (timeRange._2 - timeRange._1) * Random.nextDouble() + timeRange._1

    msg match {
      case TakeOrder(order, replyTo, waiter, customer) =>
        if (state == Free) {
          val newState = Cooking
          ctx.log.info(s"Started cooking order ${order.orderId}: ${order.dishes.toString}")
          replyTo ! Result.Ok
          val cookingTime = decideOnCookingTime(order)
          ctx.scheduleOnce(cookingTime.seconds, ctx.self, FinishOrder(order.orderId, waiter, customer))
          withState(newState)
        } else {
          ctx.log.info(s"Cannot start order ${order.orderId}, busy with other order")
          replyTo ! Result.Busy
          Behaviors.same
        }
      case FinishOrder(orderId, waiter, customer) =>
        val newState = Free
        ctx.log.info(s"Order $orderId is finished")
        waiter ! ServeOrder(orderId, customer)
        withState(newState)
    }
  }

}

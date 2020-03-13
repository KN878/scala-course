package nikita.kalinskiy

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import nikita.kalinskiy.Result.{Busy, Ok}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Waiter {

  sealed trait Command

  final case class Start(chefs: Seq[ActorRef[Chef.Command]]) extends Command

  final case class TakeOrder(order: CustomerOrder, customer: ActorRef[Customer.Eat.type]) extends Command

  final case class ServeOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  final case class RestartQueryingChef(order: Order, customer: ActorRef[Customer.Eat.type]) extends Command

  private final case class WrappedResultResponse(
      result: Result,
      order: Order,
      customer: ActorRef[Customer.Eat.type],
      nonAskedChefs: Seq[ActorRef[Chef.Command]]
  ) extends Command

  implicit val timeout: Timeout = Timeout(1.second)
  private val chefQueryTimeout = 3.seconds

  def apply(): Behavior[Command] = start

  def start: Behavior[Command] = Behaviors.receiveMessage {
    case Start(chefs) =>
      processOrder(chefs, 0)
    case _ => Behaviors.same
  }

  def processOrder(chefs: Seq[ActorRef[Chef.Command]], prevOrderId: Int): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      def queryChef(
          ctx: ActorContext[Command],
          order: Order,
          nonAskedChefs: Seq[ActorRef[Chef.Command]],
          customer: ActorRef[Customer.Eat.type]
      ): Unit = {
        //ctx.log.info(s"Asking chef ${nonAskedChefs.head.path.toString} to take order ${order.orderId}: ${order.dishes}")
        ctx.ask(nonAskedChefs.head, (ref: ActorRef[Result]) => Chef.TakeOrder(order, ref, ctx.self, customer)) {
          case Success(result) => WrappedResultResponse(result, order, customer, nonAskedChefs.tail)
          case Failure(_)      => WrappedResultResponse(Result.Busy, order, customer, nonAskedChefs.tail)
        }
      }

      msg match {
        case TakeOrder(order, customer) =>
          val wrappedOrder = order.toOrder(prevOrderId + 1)
          ctx.log.info(s"Accepted order ${wrappedOrder.orderId} : ${wrappedOrder.dishes.toString}")
          queryChef(ctx, wrappedOrder, chefs, customer)
          processOrder(chefs, prevOrderId + 1)
        case RestartQueryingChef(order, customer) =>
          ctx.log.info(s"Restarting query a chef for order ${order.orderId} : ${order.dishes.toString}")
          queryChef(ctx, order, chefs, customer)
          Behaviors.same
        case WrappedResultResponse(result, order, customer, nonAskedChefs) =>
          result match {
            case Ok =>
              ctx.log.info(s"Order ${order.orderId} has been taken by chef")
              Behaviors.same
            case Busy =>
              if (nonAskedChefs.nonEmpty) {
                queryChef(ctx, order, nonAskedChefs, customer)
                Behaviors.same
              } else {
                ctx.log.info(s"All chefs are busy, asking again")
                ctx.scheduleOnce(chefQueryTimeout, ctx.self, RestartQueryingChef(order, customer))
                Behaviors.same
              }
          }
        case ServeOrder(orderId, customer) =>
          ctx.log.info(s"Waiter is serving the order $orderId")
          customer ! Customer.Eat
          Behaviors.same
        case _ => Behaviors.same
      }
  }
}

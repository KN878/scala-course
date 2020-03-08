package nikita.kalinskiy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, Terminated}

object Cafe {

  sealed trait Command

  case object Start extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val waiter = ctx.spawn(Waiter(), "Waiter")
        val customers = (1 to Settings.customers).map { i =>
          ctx.spawn(Customer(waiter, CustomerOrder(List(Khinkali(Stuffing.Beef, 10)))), s"Customer$i")
        }
        val chefs = (1 to Settings.chefs).map { i => ctx.spawn(Chef(), s"Chef$i") }

        waiter ! Waiter.Start(chefs)

        val startTime = System.currentTimeMillis()
        customers.foreach { c =>
          c ! Customer.Start
          ctx.watch(c)
        }
        waitForCustomersToFinish(0, startTime)
    }
  }

  def waitForCustomersToFinish(finishedCustomers: Int, startTime: Long): Behavior[Command] = Behaviors.receiveSignal {
    case (ctx, Terminated(_)) =>
      if (finishedCustomers + 1 == Settings.customers) {
        val totalServingTime = (System.currentTimeMillis() - startTime)
        ctx.log.info(s"Overall system time: $totalServingTime milliseconds")
        Behaviors.stopped
      } else {
        waitForCustomersToFinish(finishedCustomers + 1, startTime)
      }
  }
}

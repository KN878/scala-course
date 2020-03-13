package nikita.kalinskiy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, Terminated}

object Cafe {

  sealed trait Command

  case object Start extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val config          = CafeConfig()
        val randomGenerator = ctx.spawn(RandomGenerator(config.getSeed), "Random")
        val waiter          = ctx.spawn(Waiter(), "Waiter")
        val customers = (1 to config.getCustomers).map { i =>
          ctx.spawn(Customer(waiter, CustomerOrder(List(Khinkali(Stuffing.Beef, 10))), randomGenerator), s"Customer$i")
        }
        val chefs = (1 to config.getChefs).map { i => ctx.spawn(Chef(randomGenerator, ChefConfig()), s"Chef$i") }

        waiter ! Waiter.Start(chefs)

        val startTime = System.currentTimeMillis()
        customers.foreach { c =>
          c ! Customer.Start
          ctx.watch(c)
        }
        waitForCustomersToFinish(0, startTime, config)
    }
  }

  def waitForCustomersToFinish(finishedCustomers: Int, startTime: Long, config: CafeConfig): Behavior[Command] =
    Behaviors.receiveSignal {
      case (ctx, Terminated(_)) =>
        if (finishedCustomers + 1 == config.getCustomers) {
          val totalServingTime = (System.currentTimeMillis() - startTime)
          ctx.log.info(s"Overall system time: $totalServingTime milliseconds")
          Behaviors.stopped
        } else {
          waitForCustomersToFinish(finishedCustomers + 1, startTime, config)
        }
    }
}

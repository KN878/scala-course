package nikita.kalinskiy

import akka.actor.testkit.typed.FishingOutcome
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import nikita.kalinskiy.Chef.{Cooking, FinishOrder, Free}
import nikita.kalinskiy.Stuffing.{Beef, Mutton}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class ChefSpec extends AnyFlatSpec with Matchers {

  import ChefSpec._

  "Chef" should "take order if not cooking at the moment" in {
    val testKit       = ActorTestKit()
    val randomProbe   = testKit.createTestProbe[RandomGenerator.Command]("Random")
    val resultProbe   = testKit.createTestProbe[Result]("Result")
    val waiterProbe   = testKit.createTestProbe[Waiter.Command]("Waiter")
    val customerProbe = testKit.createTestProbe[Customer.Eat.type]("Customer")
    val chef          = testKit.spawn(Chef(randomProbe.ref, config))
    chef ! Chef.TakeOrder(orders.head, resultProbe.ref, waiterProbe.ref, customerProbe.ref)
    resultProbe.expectMessage(Result.Ok)
  }

  "Chef" should "not take order if cooking at the moment" in {
    val testKit       = ActorTestKit()
    val randomProbe   = testKit.createTestProbe[RandomGenerator.Command]("Random")
    val resultProbe   = testKit.createTestProbe[Result]("Result")
    val waiterProbe   = testKit.createTestProbe[Waiter.Command]("Waiter")
    val customerProbe = testKit.createTestProbe[Customer.Eat.type]("Customer")
    val chef          = testKit.spawn(Chef.withState(Cooking, randomProbe.ref, config))
    chef ! Chef.TakeOrder(orders.head, resultProbe.ref, waiterProbe.ref, customerProbe.ref)
    resultProbe.expectMessage(Result.Busy)
  }

  "Chef" should "return an order when it is cooked" in {
    val testKit       = ActorTestKit()
    val random        = testKit.spawn(RandomGenerator(123))
    val resultProbe   = testKit.createTestProbe[Result]("Result")
    val waiterProbe   = testKit.createTestProbe[Waiter.Command]("Waiter")
    val customerProbe = testKit.createTestProbe[Customer.Eat.type]("Customer")
    val chefProbe     = testKit.createTestProbe[Chef.Command]("Chef")
    val chef          = testKit.spawn(Behaviors.monitor(chefProbe.ref, Chef.withState(Free, random, config)))
    chef ! Chef.TakeOrder(orders.head, resultProbe.ref, waiterProbe.ref, customerProbe.ref)
    chefProbe.fishForMessage(5.seconds) {
      case FinishOrder(orderId, inboxWaiterRef, inboxCustomerRef) =>
        FishingOutcome.Complete
      case _ => FishingOutcome.Continue
    }
  }
}

object ChefSpec {
  val orders = List(Order(0, List(Khinkali(Beef, 2))), Order(1, List(Khinkali(Mutton, 2))))
  val config = ChefConfig()
}

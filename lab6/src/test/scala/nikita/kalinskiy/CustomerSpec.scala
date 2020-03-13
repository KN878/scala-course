package nikita.kalinskiy

import akka.actor.testkit.typed.FishingOutcome
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit}
import akka.actor.typed.scaladsl.Behaviors
import nikita.kalinskiy.Customer.{Leave, LeaveOrder, Start}
import nikita.kalinskiy.Stuffing.CheeseAndMushrooms
import nikita.kalinskiy.Waiter.TakeOrder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class CustomerSpec extends AnyFlatSpec with Matchers {

  import CustomerSpec._

  "Customer" should "decide on order for some time" in {
    val testKit       = ActorTestKit()
    val random        = testKit.spawn(RandomGenerator(123))
    val customerProbe = testKit.createTestProbe[Customer.Command]("Customer")
    val waiterProbe   = testKit.createTestProbe[Waiter.Command]("Waiter")
    val customer      = testKit.spawn(Behaviors.monitor(customerProbe.ref, Customer(waiterProbe.ref, order, random)))
    customer ! Start
    customerProbe.fishForMessage(5.seconds) {
      case LeaveOrder(order) => FishingOutcome.Complete
      case _                 => FishingOutcome.Continue
    }
  }

  "Customer" should "leave order to waiter" in {
    val testKit       = ActorTestKit()
    val random        = testKit.spawn(RandomGenerator(123))
    val customerProbe = testKit.createTestProbe[Customer.Command]("Customer")
    val waiterProbe   = testKit.createTestProbe[Waiter.Command]("Waiter")
    val customer =
      testKit.spawn(Behaviors.monitor(customerProbe.ref, Customer.leaveOrder(waiterProbe.ref, random, config)))

    customer ! Customer.LeaveOrder(order)
    waiterProbe.expectMessage(TakeOrder(order, customer))
  }

  "Customer" should "eat for some time when they receive their order" in {
    val testKit       = ActorTestKit()
    val random        = testKit.spawn(RandomGenerator(123))
    val customerProbe = testKit.createTestProbe[Customer.Command]("Customer")
    val customer      = testKit.spawn(Behaviors.monitor(customerProbe.ref, Customer.waitForEat(random, config)))

    customer ! Customer.Eat
    customerProbe.fishForMessage(5.seconds) {
      case Leave => FishingOutcome.Complete
      case _     => FishingOutcome.Continue
    }
  }

  "Customer" should "be stopped when they have left" in {
    val testKit = BehaviorTestKit(Customer.waitToLeave, "customer")
    testKit.run(Customer.Leave)
    testKit.currentBehavior shouldBe Behaviors.stopped
  }
}

object CustomerSpec {
  val order: CustomerOrder = CustomerOrder(List(Khinkali(CheeseAndMushrooms, 5)))
  val config               = CustomerConfig()
}

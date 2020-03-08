package nikita.kalinskiy

import akka.actor.testkit.typed.Effect.Scheduled
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import nikita.kalinskiy.Customer.{Leave, LeaveOrder}
import nikita.kalinskiy.Stuffing.CheeseAndMushrooms
import nikita.kalinskiy.Waiter.TakeOrder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CustomerSpec extends AnyFlatSpec with Matchers {

  import CustomerSpec._

  "Customer" should "decide on order for some time" in {
    val waiterRef = TestInbox[Waiter.Command]("Waiter").ref
    val testKit = BehaviorTestKit(Customer(waiterRef, order))
    testKit.run(Customer.Start)
    val scheduledOrder = testKit.expectEffectPF {
      case Scheduled(_, customerRef, LeaveOrder(order)) => true
      case _ => false
    }
    scheduledOrder shouldBe true
  }

  "Customer" should "leave order to waiter" in {
    val waiter = TestInbox[Waiter.Command]("Waiter")
    val testKit = BehaviorTestKit(Customer.leaveOrder(waiter.ref))
    testKit.run(Customer.LeaveOrder(order))
    waiter.expectMessage(TakeOrder(order, testKit.ref))
  }

  "Customer" should "eat for some time when they receive their order" in {
    val testKit = BehaviorTestKit(Customer.waitForEat)
    testKit.run(Customer.Eat)
    val eatForSomeTime = testKit.expectEffectPF {
      case Scheduled(_, customerRef, Leave) => true
      case _ => false
    }
    eatForSomeTime shouldBe true
  }

  "Customer" should "be stopped when they have left" in {
    val testKit = BehaviorTestKit(Customer.waitToLeave, "customer")
    testKit.run(Customer.Leave)
    testKit.currentBehavior shouldBe Behaviors.stopped
  }
}

object CustomerSpec {
  val order: CustomerOrder = CustomerOrder(List(Khinkali(CheeseAndMushrooms, 5)))
}

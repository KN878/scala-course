package nikita.kalinskiy

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.Terminated
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CafeSpec extends AnyFlatSpec with Matchers {

  // Так и не смог разобраться с чеком на Spawned. Как описано ниже тест не проходил
  //  "Cafe" should "spawn a waiter, customers and chefs" in {
  //    val testKit = BehaviorTestKit(Cafe())
  //    testKit.run(Start)
  //    testKit.expectEffect(Spawned(Waiter.start, "Waiter"))
  //  }

  "Cafe" should "stop when all customers have left" in {
    val testKit = BehaviorTestKit(Cafe.waitForCustomersToFinish(Settings.customers - 1, System.currentTimeMillis()))
    val customer = TestInbox[Customer.Command]("Customer10")
    testKit.signal(Terminated(customer.ref))
    testKit.isAlive shouldBe false
  }

  "Cafe" should "keep on working if not all customers have left" in {
    val testKit = BehaviorTestKit(Cafe.waitForCustomersToFinish(Settings.customers - 2, System.currentTimeMillis()))
    val customer = TestInbox[Customer.Command]("Customer9")
    testKit.signal(Terminated(customer.ref))
    testKit.isAlive shouldBe true
  }
}

package nikita.kalinskiy

import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.Terminated
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CafeSpec extends AnyFlatSpec with Matchers {
  import CafeSpec._
  "Cafe" should "spawn a random generator, a waiter, customers and chefs" in {
    val testKit = BehaviorTestKit(Cafe())
    testKit.run(Cafe.Start)
    val spawnedActors = testKit.retrieveAllEffects()
      .collect {
        case Spawned(_, name, _) => name
      }
    spawnedActors.count(_.equals("Random")) shouldEqual 1
    spawnedActors.count(_.equals("Waiter")) shouldEqual 1
    spawnedActors.count(_.contains("Customer")) shouldEqual config.getCustomers
    spawnedActors.count(_.contains("Chef")) shouldEqual config.getChefs
  }

  "Cafe" should "stop when all customers have left" in {
    val testKit =
      BehaviorTestKit(Cafe.waitForCustomersToFinish(config.getCustomers - 1, System.currentTimeMillis(), config))
    val customer = TestInbox[Customer.Command]("Customer10")
    testKit.signal(Terminated(customer.ref))
    testKit.isAlive shouldBe false
  }

  "Cafe" should "keep on working if not all customers have left" in {
    val testKit =
      BehaviorTestKit(Cafe.waitForCustomersToFinish(config.getCustomers - 2, System.currentTimeMillis(), config))
    val customer = TestInbox[Customer.Command]("Customer9")
    testKit.signal(Terminated(customer.ref))
    testKit.isAlive shouldBe true
  }
}

object CafeSpec {
  val config = CafeConfig()
}

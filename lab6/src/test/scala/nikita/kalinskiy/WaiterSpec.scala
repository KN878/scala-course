package nikita.kalinskiy

import akka.actor.testkit.typed.FishingOutcome
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import nikita.kalinskiy.Stuffing.CheeseAndMushrooms
import nikita.kalinskiy.Waiter.RestartQueryingChef
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class WaiterSpec extends AnyFlatSpec with Matchers {

  import WaiterSpec._

  implicit val timeout: Timeout = Timeout(1.second)

  "Waiter" should "query an order to chef when receives one" in {
    val testKit     = ActorTestKit()
    val randomProbe = testKit.createTestProbe[RandomGenerator.Command]("Random")
    val chefProbe1  = testKit.createTestProbe[Chef.Command]("Chef1")
    val mockedChef1 = testKit.spawn(Behaviors.monitor(chefProbe1.ref, mockedChefOkBehavior))

    val waiter   = testKit.spawn(Waiter.processOrder(List(mockedChef1), 0), "Waiter")
    val customer = testKit.spawn(Customer.leaveOrder(waiter, randomProbe.ref, CustomerConfig()))
    waiter ! Waiter.TakeOrder(order, customer)

    chefProbe1.expectMessageType[Chef.TakeOrder]
  }

  "Waiter" should "query another chef if previous chefs are busy" in {
    val testKit     = ActorTestKit()
    val randomProbe = testKit.createTestProbe[RandomGenerator.Command]("Random")
    val chefProbe1  = testKit.createTestProbe[Chef.Command]("Chef1")
    val chefProbe2  = testKit.createTestProbe[Chef.Command]("Chef2")
    val mockedChef1 = testKit.spawn(Behaviors.monitor(chefProbe1.ref, mockedChefBusyBehavior))
    val mockedChef2 = testKit.spawn(Behaviors.monitor(chefProbe2.ref, mockedChefOkBehavior))

    val waiter   = testKit.spawn(Waiter.processOrder(List(mockedChef1, mockedChef2), 0), "Waiter")
    val customer = testKit.spawn(Customer.leaveOrder(waiter, randomProbe.ref, CustomerConfig()))
    waiter ! Waiter.TakeOrder(order, customer)

    chefProbe1.expectMessageType[Chef.TakeOrder]
    chefProbe2.expectMessageType[Chef.TakeOrder]
  }

  "Waiter" should "restart querying chefs if all of them are busy now" in {
    val testKit     = ActorTestKit()
    val randomProbe = testKit.createTestProbe[RandomGenerator.Command]("Random")
    val chefProbe1  = testKit.createTestProbe[Chef.Command]("Chef1")
    val chefProbe2  = testKit.createTestProbe[Chef.Command]("Chef2")
    val mockedChef1 = testKit.spawn(Behaviors.monitor(chefProbe1.ref, mockedChefBusyBehavior))
    val mockedChef2 = testKit.spawn(Behaviors.monitor(chefProbe2.ref, mockedChefBusyBehavior))

    val waiterProbe = testKit.createTestProbe[Waiter.Command]("Waiter")
    val waiter =
      testKit.spawn(Behaviors.monitor(waiterProbe.ref, Waiter.processOrder(List(mockedChef1, mockedChef2), 0)))
    val customer = testKit.spawn(Customer.leaveOrder(waiter, randomProbe.ref, CustomerConfig()))
    waiter ! Waiter.TakeOrder(order, customer)

    chefProbe1.expectMessageType[Chef.TakeOrder]
    chefProbe2.expectMessageType[Chef.TakeOrder]
    waiterProbe.fishForMessage(7.second) {
      case RestartQueryingChef(_, _) => FishingOutcome.Complete
      case _                         => FishingOutcome.Continue
    }
  }

  "Waiter" should "serve order to customer when order is finished" in {
    val testKit  = BehaviorTestKit(Waiter.processOrder(Nil, 0), "Waiter")
    val customer = TestInbox[Customer.Eat.type]("Customer")
    testKit.run(Waiter.ServeOrder(1, customer.ref))
    customer.expectMessage(Customer.Eat)
  }
}

object WaiterSpec {
  val order: CustomerOrder = CustomerOrder(List(Khinkali(CheeseAndMushrooms, 5)))

  val mockedChefBusyBehavior: Behavior[Chef.Command] = Behaviors.receiveMessage[Chef.Command] {
    case Chef.TakeOrder(_, replyTo, _, _) =>
      replyTo ! Result.Busy
      Behaviors.same
    case _ => Behaviors.same
  }

  val mockedChefOkBehavior: Behavior[Chef.Command] = Behaviors.receiveMessage[Chef.Command] {
    case Chef.TakeOrder(_, replyTo, _, _) =>
      replyTo ! Result.Ok
      Behaviors.same
    case _ => Behaviors.same
  }
}

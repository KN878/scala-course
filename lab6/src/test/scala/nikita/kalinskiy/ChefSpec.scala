package nikita.kalinskiy

import akka.actor.testkit.typed.Effect.Scheduled
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import nikita.kalinskiy.Chef.FinishOrder
import nikita.kalinskiy.Stuffing.{Beef, Mutton}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChefSpec extends AnyFlatSpec with Matchers {

  import ChefSpec._

  "Chef" should "take order if not cooking at the moment" in {
    val testKit = BehaviorTestKit(Chef())
    val inboxResult = TestInbox[Result]("Result")
    val inboxWaiter = TestInbox[Waiter.Command]("Waiter")
    val inboxCustomer = TestInbox[Customer.Eat.type]("Customer")
    testKit.run(Chef.TakeOrder(orders.head, inboxResult.ref, inboxWaiter.ref, inboxCustomer.ref))
    inboxResult.expectMessage(Result.Ok)
  }

  "Chef" should "not take order if cooking at the moment" in {
    val testKit = BehaviorTestKit(Chef.withState(Chef.Cooking))
    val inboxResult = TestInbox[Result]("Result")
    val inboxWaiter = TestInbox[Waiter.Command]("Waiter")
    val inboxCustomer = TestInbox[Customer.Eat.type]("Customer")
    testKit.run(Chef.TakeOrder(orders.head, inboxResult.ref, inboxWaiter.ref, inboxCustomer.ref))
    inboxResult.expectMessage(Result.Busy)
  }

  "Chef" should "return an order when it is cooked" in {
    val testKit = BehaviorTestKit(Chef())
    val inboxResultRef = TestInbox[Result]("Result").ref
    val inboxWaiterRef = TestInbox[Waiter.Command]("Waiter").ref
    val inboxCustomerRef = TestInbox[Customer.Eat.type]("Customer").ref
    testKit.run(Chef.TakeOrder(orders.head, inboxResultRef, inboxWaiterRef, inboxCustomerRef))
    val isOrderFinished = testKit.expectEffectPF {
      case Scheduled(_, chefRef, FinishOrder(orderId, inboxWaiterRef, inboxCustomerRef)) =>
        true
      case _ => false
    }
    isOrderFinished shouldBe true
  }
}

object ChefSpec {
  val orders = List(Order(0, List(Khinkali(Beef, 2))), Order(1, List(Khinkali(Mutton, 2))))
}

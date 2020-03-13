package nikita.kalinskiy

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RandomGeneratorSpec extends AnyFlatSpec with Matchers {
  private val seed = 1337

  "RandomGen" should "be deterministic" in {
    val kit1  = BehaviorTestKit(RandomGenerator(seed))
    val kit2  = BehaviorTestKit(RandomGenerator(seed))
    val inbox = TestInbox[Double]()

    kit1.run(RandomGenerator.Command.GenerateDouble(1, 5, inbox.ref))
    kit2.run(RandomGenerator.Command.GenerateDouble(1, 5, inbox.ref))
    val messages = inbox.receiveAll()

    messages.length shouldBe 2
    messages(0) shouldEqual messages(1)
  }

  it should "output different values in sequence" in {
    val kit       = BehaviorTestKit(RandomGenerator(seed))
    val testInbox = TestInbox[Double]()
    for { i <- 0 until 3 } kit.run(RandomGenerator.Command.GenerateDouble(1, 5, testInbox.ref))

    val messages = testInbox.receiveAll()
    messages shouldEqual Seq(3.6397191389792867, 3.756970696112405, 4.533090708649684)
  }
}

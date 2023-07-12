package quick.start

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertTrue

class PropertyTest {
    val logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun `custom한 property 테스트를 할 수 있다`() {
        data class Person(val name: String, val age: Int)

        val personArb: Arb<Person> = arbitrary {
            val name = Arb.string(10..12).bind()
            val age = Arb.int(21, 150).bind()
            Person(name, age)
        }

        runBlocking {
            personArb.checkAll(iterations = 10) { i ->
                assertTrue(i.age > 0)
            }
        }
    }
}
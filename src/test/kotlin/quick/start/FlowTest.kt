package quick.start

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FlowTest {

    @Test
    fun `producer는 sequence와 같이 lazy하다`() {
        val prod2 = GlobalScope.produce() {
            (1 .. 10).forEach { send(it) }
        }

        val flow: Flow<Int> = prod2.consumeAsFlow()
            .filter {
                println("filter : ${it}")
                it % 2 == 0 }
            .map {
                it * 2
            }
            .take(2)

        runBlocking {
            flow.collect { value -> println(value) }
        }
    }

}
package quick.start

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.junit.Test
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue

class ChannelTest {
    val queueItems = ConcurrentLinkedQueue<Int>()
    val logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun `channel에서 buffer 이상의 데이터가 인입되면 버려진다`() {
        val channel = Channel<Int>(
            5,
            // DROP_LATEST는 에러 알람이 동작하지 않음
            BufferOverflow.SUSPEND,
            onUndeliveredElement = { item: Int ->
                logger.error("Fail to deliver #${item}")
            }
        )

        repeat(2) { id ->
            CoroutineScope(Dispatchers.Default).launch {
                for (msg in channel) {
                    delay(Duration.ofSeconds(2).toMillis())
                    logger.info("#${msg} is received")
                }
            }
        }

        suspend fun func(items: List<Int>, logging: Boolean = true) {
            return items.chunked(10)
                .forEach { items: List<Int> ->
                    items.forEach {
                        if (logging) { logger.info("Send #${it}") }
                        channel
                            .trySend(it)
                            .onFailure { e ->
                                if (logging) { logger.error("Fail to send", e) }
                            }

                    }
                    delay(Duration.ofSeconds(1).toMillis())
                }
        }

        runBlocking {
            //val logging = false
            val logging = true
            func((1..100).toList(), logging)
            delay(Duration.ofSeconds(10).toMillis())
            func((100..200).toList(), logging)
        }
    }
}
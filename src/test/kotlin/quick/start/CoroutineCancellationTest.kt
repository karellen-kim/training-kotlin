package quick.start

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class CoroutineCancellationTest {

    suspend fun func(num: Int): Deferred<Int> {
        val job = CoroutineScope(Dispatchers.IO).async {
            withTimeout(5.seconds) {
                log("Job ${num} started...")
                if (num >= 5 && num <= 8) {
                    delay(30.seconds)
                } else {
                    delay(2.seconds)
                }
                log("Job ${num} end.")
                num
            }
        }
        job.invokeOnCompletion { e ->
            if (e != null) {
                log("Error : ${num} ${e.message}")
            }
        }

        return job
    }

    @Test
    fun `코루틴을 cancel 할 수 있다`() {
        runBlocking {
            (1 .. 10).map {
                func(it)
            }.awaitAll()
        }
    }
}
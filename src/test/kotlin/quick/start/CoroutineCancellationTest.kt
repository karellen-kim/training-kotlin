package quick.start

import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.Test
import reactor.core.publisher.Mono
import kotlin.time.Duration.Companion.seconds

class CoroutineCancellationTest {

    @Test
    fun `코루틴을 cancel 할 수 있다`() {
        runBlocking {
            (1 .. 10).map {
                cancelFun(it)
            }.awaitAll()
        }
    }

    fun cancelFun(num: Int): Deferred<Int> {
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
    fun `코루틴을 내부의 코루틴도 cancel될 까지 기다릴 수 있다`() {
        runBlocking {
            var started = true
            while(true) {
                if (started) {
                    started = false
                    (1..10).map {
                        waitCancelInnerFuc(it)
                    }.awaitAll()
                }
            }
        }
    }

    fun waitCancelInnerFuc(num: Int): Deferred<Int> {
        val job = CoroutineScope(Dispatchers.IO).async {
            withTimeout(5.seconds) {
                log("Job ${num} started...")
                val res = if (num >= 5 && num <= 8) {
                   Mono.defer {
                       Thread.sleep(10000)
                       Mono.just(num)
                    }.awaitSingle()
                } else {
                    delay(2.seconds)
                    num
                }
                log("Job ${res} end.")
                res
            }
        }
        job.invokeOnCompletion { e ->
            if (e != null) {
                log("Error : ${num} ${e.message}")
            }
        }

        return job
    }

}
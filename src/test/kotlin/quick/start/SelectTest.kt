package quick.start

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import org.junit.Test
import java.time.Duration

class SelectTest {

    suspend fun slowFunc(num: Int, delay: Duration): Int {
        log("Start ${num}")
        delay(delay.toMillis())
        log("End ${num}")
        return num
    }

    @Test
    fun `가장 빠른 결과를 리턴하고 나머지는 cancel 할 수 있다`() {
        runBlocking {
            val jobs = listOf(
                CoroutineScope(Dispatchers.IO).async { slowFunc(1, Duration.ofSeconds(10)) },
                CoroutineScope(Dispatchers.IO).async { slowFunc(2, Duration.ofSeconds(5)) },
                CoroutineScope(Dispatchers.IO).async { slowFunc(3, Duration.ofSeconds(3)) },
                CoroutineScope(Dispatchers.IO).async { slowFunc(4, Duration.ofSeconds(1)) }
            )

            val result = select<Int> {
                jobs.forEach { job ->
                    job.onAwait { it }
                }
            }

            jobs.forEach {
                if (it.isCancelled) {
                    println("job ${it} is isCancelled")
                } else {
                    println("job ${it} is isCompleted? ${it.isCompleted}")
                }
            }

            jobs.forEach {
                if (!it.isCancelled) {
                    it.cancel()
                }
            }
            log(result)
        }
    }

    @Test
    fun `조건에 맞는 가장 빠른 결과를 리턴하고 나머지는 cancel 할 수 있다`() {
        runBlocking {
            var jobs: List<Deferred<Unit>> = emptyList()
            val channel = produce<Int> {
                jobs = listOf(
                    CoroutineScope(Dispatchers.IO).async {
                        send(slowFunc(1, Duration.ofSeconds(10)))
                    },
                    CoroutineScope(Dispatchers.IO).async {
                        send(slowFunc(2, Duration.ofSeconds(5)))
                    },
                    CoroutineScope(Dispatchers.IO).async {
                        send(slowFunc(3, Duration.ofSeconds(3)))
                    },
                    CoroutineScope(Dispatchers.IO).async {
                        send(slowFunc(4, Duration.ofSeconds(1)))
                    },
                    CoroutineScope(Dispatchers.IO).async {
                        send(slowFunc(5, Duration.ofSeconds(10)))
                    }
                )
                jobs.awaitAll()
            }

            channel.consumeAsFlow().takeWhile {
                println("takeWhile ${it}")
                it != 3
            }.collect()

            jobs.forEach {
                println("job ${it} is isActive? ${it.isActive}")
            }

            jobs.forEach {
                if (it.isActive && !it.isCancelled) {
                    it.cancel()
                    println("job ${it} is Cancelled")
                }
            }
        }
    }

    suspend fun <T> CoroutineScope.awaitUntil(predicate: (T) -> Boolean, vararg body: suspend () -> T): List<T> {
        var jobs = emptyList<Deferred<Unit>>()
        val channel = produce<T> {
            jobs = body.map { b ->
                CoroutineScope(Dispatchers.IO).async {
                    send(b())
                }
            }
            jobs.awaitAll()
        }

        val result = channel.consumeAsFlow().takeWhile {
            println("takeWhile ${it}")
            predicate(it)
        }.toList()

        jobs.forEach {
            println("job ${it} is isActive? ${it.isActive}")
        }

        jobs.forEach {
            if (it.isActive && !it.isCancelled) {
                it.cancel()
                println("job ${it} is Cancelled")
            }
        }

        println("result : ${result}")
        return result
    }


    @Test
    fun `조건에 맞는 가장 빠른 결과를 리턴하고 나머지는 cancel 할 수 있는 가독성 있는 코드`() {
        runBlocking {
            val result = awaitUntil({ it != 3 },
                { slowFunc(1, Duration.ofSeconds(10)) },
                { slowFunc(2, Duration.ofSeconds(5)) },
                { slowFunc(3, Duration.ofSeconds(3)) },
                { slowFunc(4, Duration.ofSeconds(1)) },
                { slowFunc(5, Duration.ofSeconds(10)) }
            )
        }
    }
}

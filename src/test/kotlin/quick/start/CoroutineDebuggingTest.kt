package quick.start

import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes
import org.junit.Test

class CoroutineDebuggingTest {

    suspend fun run() {
        val tasks = mutableListOf<Deferred<Any>>()
        for (i in 0..5) {
            val task = CoroutineScope(Dispatchers.Default).async() {
                println("First ${i}: ${Thread.currentThread().name}")
                delay(1000)
            }
            tasks.add(task)
        }
        tasks.awaitAll()
    }

    @Test
    fun `coroutine stacktraces를 출력할 수 있다`() {
        runBlocking {
            DebugProbes.install()
            val deferred = async { run() }
            delay(1000)
            DebugProbes.dumpCoroutines()
            DebugProbes.printJob(deferred)
        }
    }
}
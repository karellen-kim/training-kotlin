package quick.start

import kotlinx.coroutines.*
import org.junit.Test

class CoroutineDeferredTest {

    suspend fun asyncFunc(jobName: String, execTime: Long): Deferred<String> {
        return GlobalScope.async {
            log("${jobName} job start!")
            delay(execTime + 1000)
            log("${jobName} job end!")
            "Success"
        }
    }

    @Test
    fun `Deferred는 async로 생성하며 완료될 때까지 기다린다`() {
        runBlocking {
            log("First job call start")
            val wait: Long = 5000
            val first = asyncFunc("First", wait)
            val second = asyncFunc("Second", wait)
            first.await()
            second.await()
            log("First job call end")
        }
    }

    @Test
    fun `Deferred의 await 위치에 유의하자`() {
        runBlocking {
            log("First job call start")
            val wait: Long = 5000
            val first = asyncFunc("First", wait).await()
            val second = asyncFunc("Second", wait).await()
            log("First job call end")
        }
    }


    @Test
    fun `Deferred의 Collection인 경우 awaitAll을 사용한다`() {
        runBlocking {
            val list = listOf("First", "Second", "Third")

            val wait: Long = 5000
            val result: List<Deferred<String>> = list.map {
                asyncFunc(it, wait)
            }
            result.awaitAll()
        }
    }
}
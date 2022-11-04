package quick.start

import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class CoroutineJobTest {

    suspend fun asyncJob(execTime: Long): Job {
        return GlobalScope.launch {
            log("job start!")
            delay(execTime + 1000)
            log("job end!")
        }
    }

    suspend fun asyncJobException(execTime: Long): Job {
        val job = GlobalScope.launch {
            log("job start!")
            delay(execTime + 1000)
            throw RuntimeException("에러남")
            log("job end!")
        }

        job.invokeOnCompletion {
                e -> println("Handle Error")
        }
        return job
    }

    suspend fun asyncLazyJob(execTime: Long): Job {
        return GlobalScope.launch(start = CoroutineStart.LAZY) {
            log("job start!")
            delay(execTime + 1000)
            log("job end!")
        }
    }

    @Test
    fun `Job은 launch로 생성하며 완료될 때까지 기다리지 않고 종료된다`() {
        runBlocking {
            log("First job call start")
            val wait: Long = 5000
            val job = asyncJob(wait)
            delay(wait)
            log("First job call end")
        }
    }

    @Test
    fun `한번 완료되면 재시작 되지 않는다`() {
        runBlocking {
            log("First job call start")
            val wait: Long = 5000
            val job = asyncJob(wait)
            delay(wait)
            log("First job call end")

            log("Try restart")
            job.start()
            delay(wait)
            log("Restart call end")
        }
    }

    @Test
    fun `예외는 전파된다`() {
        runBlocking {
            log("Error job call start")
            val wait: Long = 5000
            val job = asyncJobException(wait)
            delay(wait + 2000)
            log("Error job call end")
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun `Job의 라이프 사이클`() {
        runBlocking {
            log("First job call start")
            val wait: Long = 5000
            val job = asyncJob(wait)
            println("First job isActive=${job.isActive}, isCompleted=${job.isCompleted}, isCancelled=${job.isCancelled}")
            delay(wait + 2000)
            log("First job call end")
            println("First job isActive=${job.isActive}, isCompleted=${job.isCompleted}, isCancelled=${job.isCancelled}")

            log("Second job call start")
            val job2 = asyncJobException(wait)
            println("Second job isActive=${job2.isActive}, isCompleted=${job2.isCompleted}, isCancelled=${job2.isCancelled}")
            delay(wait + 2000)
            log("Second job call end")
            println("Second job isActive=${job2.isActive}, isCompleted=${job2.isCompleted}, isCancelled=${job2.isCancelled}")
            println("Second job error message: ${job2.getCancellationException().cause?.message}")
        }
    }

    @Test
    fun `Job의 join은 결과를 기다린다`() {
        runBlocking {
            log("Job call start")
            val wait: Long = 5000
            val job = asyncJob(wait)
            log("Job before job.join(): isActive=${job.isActive} \njob.join()")
            job.join()
            log("Job after job.join(): isActive=${job.isActive}")
            delay(wait)
            log("Job call end")
        }
    }

    @Test
    fun `Job이 lazy하게 시작되게 할 수 있다`() {
        runBlocking {
            log("Lazy job call start")
            val wait: Long = 5000
            val job = asyncLazyJob(wait)
            log("Lazy job before job.start(): isActive=${job.isActive} \njob.start()")
            job.start()
            log("Lazy job after job.start(): isActive=${job.isActive}")
            delay(wait)
            log("Lazy job call end")
        }
    }

    @Before
    fun before() {
        println("")
    }

    @After
    fun after() {
        println("")
    }
}
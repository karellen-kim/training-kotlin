package quick.start

import kotlinx.coroutines.*

suspend fun getFirst(): String {
    delay(2000)
    return "first"
}

suspend fun getSecond(): String {
    delay(2000)
    return "second"
}

val firstPool = newFixedThreadPoolContext(3, "firstPool")
val secondPool = newSingleThreadContext("secondPool")
suspend fun run() {
    println("Start : ${Thread.currentThread().name}")

    val tasks = mutableListOf<Deferred<Any>>()
    for (i in 0..2) {
        val first = CoroutineScope(firstPool).async() {
            println("First ${i}: ${Thread.currentThread().name}")
            getFirst()
        }
        val second = CoroutineScope(secondPool).async() {
            println("Second ${i}: ${Thread.currentThread().name}")
            getSecond()
        }
        tasks.add(first)
        tasks.add(second)
    }

    tasks.awaitAll()
    println("End: ${Thread.currentThread().name}")
}

suspend fun runWithCoroutineName() {
    val tasks = mutableListOf<Deferred<Any>>()
    for (i in 0..2) {
        val task = CoroutineScope(firstPool + CoroutineName("myname")).async() {
            println("First ${i}: ${Thread.currentThread().name}")
            getFirst()
        }
        tasks.add(task)
    }
    tasks.awaitAll()
}

fun main(args: Array<String>) {
    runBlocking {
        //println("# -Dkotlinx.coroutines.debug JVM 옵션으로 코루틴을 식별할 수 있다.")
        //run()

        println("# CoroutineName() 으로 특정 이름을 지정할 수 있다.")
        runWithCoroutineName()
    }
}
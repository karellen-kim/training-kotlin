package quick.start

import kotlinx.coroutines.*

fun runGlobalScope(num: Int): Deferred<Int> {
    return GlobalScope.async {
        log("First ${num}")
        delay(3000)
        log("Second ${num}")
        num
    }
}

fun runIOScope(num: Int) : Deferred<Int> {
    return CoroutineScope(Dispatchers.IO).async {
        log("First ${num}")
        delay(3000)
        log("Second ${num}")
        num
    }
}

fun main(args: Array<String>) {
    runBlocking {
        (1..5).map {
            runGlobalScope(it)
        }.awaitAll()
    }

    runBlocking {
        (1..5).map {
            runIOScope(it)
        }.awaitAll()
    }
}
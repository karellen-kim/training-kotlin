package quick.start

fun log(msg: String) {
    val threadName = Thread.currentThread().name
    val Current = if (threadName == "main" || threadName.startsWith("Test")) {
        println("[Main] ${msg}")
    } else {
        println("  [Coroutine: ${threadName}] ${msg}")
    }
}
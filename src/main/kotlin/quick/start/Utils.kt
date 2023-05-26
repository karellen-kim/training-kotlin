package quick.start

fun log(msg: Any) {
    val threadName = Thread.currentThread().name
    if (threadName == "main" || threadName.startsWith("Test")) {
        println("[Main] ${msg}")
    } else {
        println("  [Coroutine: ${threadName}] ${msg}")
    }
}
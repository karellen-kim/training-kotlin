package quick.start

fun log(title: String, msg: Sequence<Any>) {
    log("${title} : ${msg.joinToString(separator = ", ")}")
}

fun log(msg: Any) {
    val threadName = Thread.currentThread().name
    val Current = if (threadName == "main" || threadName.startsWith("Test")) {
        println("[Main] ${msg}")
    } else {
        println("  [Coroutine: ${threadName}] ${msg}")
    }
}
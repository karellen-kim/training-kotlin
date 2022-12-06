package quick.start

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger

println("# 원자성 위반에 해당한다.")
println("# 매번 실행할 때마다 counter 숫자가 달라지는 것을 확인")
var counter = 0

fun asyncIncrement(by: Int) = GlobalScope.async {
    for (i in 0 until by) {
        counter++
    }
}

runBlocking {

    val workerA = asyncIncrement(2000)
    val workerB = asyncIncrement(100)

    workerA.await()
    workerB.await()

    print("counter [$counter]")
}

println("# 코루틴을 단일 쓰레드로 한정")
println("# 매번 실행해도 2100으로 동일")
var counter1 = 0
val context1 = newSingleThreadContext("counter")

fun asyncIncrement1(by: Int) = GlobalScope.async(context1) {
    for (i in 0 until by) {
        counter1++
    }
}

runBlocking {
    val workerA = asyncIncrement1(2000)
    val workerB = asyncIncrement1(100)

    workerA.await()
    workerB.await()

    print("counter [$counter1]")
}

println("# 액터")
println("# 상태 액세스를 단일 쓰레드로 한정")
println("# 다른 쓰레드가 채널로 상태 수정을 요청할 수 있음")
var counter2 = 0
val context2 = newSingleThreadContext("counterActor")

//fun getCounter() = counter2

enum class Action {
    INCREASE,
    DECREASE
}

val actorCounter = GlobalScope.actor<Action>(context2) {
    for (msg in channel) {
        when(msg) {
            Action.INCREASE -> counter2++
            Action.DECREASE -> counter2--
        }
    }
}

fun asyncIncrement2(by: Int) = GlobalScope.async {
    for (i in 0 until by) {
        actorCounter.send(Action.INCREASE)
    }
}

fun asyncDecrement2(by: Int) = GlobalScope.async(Dispatchers.Default) {
    for (i in 0 until by) {
        actorCounter.send(Action.DECREASE)
    }
}

runBlocking {
    val workerA = asyncIncrement2(2000)
    val workerB = asyncIncrement2(100)
    val workerC = asyncDecrement2(1000)

    workerA.await()
    workerB.await()
    workerC.await()

    print("counter [${counter2}]")
}

println("# Buffered Actors")
println("# capacity 매개변수를 빌더에 전달하면 됨")
suspend fun bufferedActor() {
    val bufferedPrinter = GlobalScope.actor<String>(capacity = 10) {
        for (msg in channel) {
            println(msg)
        }
    }

    bufferedPrinter.send("hello")
    bufferedPrinter.send("world")

    bufferedPrinter.close()
}

runBlocking {
    bufferedActor()
}

println("# CoroutineContext를 갖는 액터")
suspend fun actorWithContext() {
    val dispatcher = newFixedThreadPoolContext(3, "pool")
    val actor = GlobalScope.actor<String>(dispatcher) {
        for (msg in channel) {
            println("Running in ${Thread.currentThread().name}")
        }
    }

    for (i in 1..10) {
        actor.send("a")
    }
}

runBlocking {
    actorWithContext()
}

println("# 액터는 생성되는 즉시 시작된다.")
println("# 하지만, CoroutineStart를 전달해 필요에 따라 동작을 변경할 수 있다.")
suspend fun lazyActor() {
    val actor = GlobalScope.actor<String>(start = CoroutineStart.LAZY) {
        for (msg in channel) {
            println(msg)
        }
    }

    actor.send("hello lazy")
}

runBlocking {
    lazyActor()
}

println("# 상호배제: 한 번에 하나의 코루틴만 코드 블록을 실행할 수 있도록 하는 동기화 메커니즘")
println("# 코틀린 뮤텍스(Mutex)")
var counter3 = 0
var mutex = Mutex()

fun asyncIncrement3(by: Int) = GlobalScope.async {
    println("# withLock 블록은 atomic하게 동작")
    for (i in 0 until by) {
        mutex.withLock {
            counter3++
        }
    }
}

runBlocking {
    val workerA = asyncIncrement3(2000)
    val workerB = asyncIncrement3(100)

    workerA.await()
    workerB.await()

    println("대개 withLock()을 사용하는 것으로 충분")
    print("counter [$counter3]")
}

suspend fun manualLock() {
    val mutex = Mutex()

    mutex.lock()
    println("I am now an atomic block")
    mutex.unlock()
}

suspend fun isLocked() {
    val mutex = Mutex()

    mutex.lock()
    println("mutex.isLocked: " + mutex.isLocked)
    mutex.unlock()
}

suspend fun tryLockFalse() {
    val mutex = Mutex()
    mutex.lock()
    val lockedByMe = mutex.tryLock()
    println("mutex.tryLock(): " + lockedByMe)
    mutex.unlock()
}

fun tryLockTrue() {
    val mutex = Mutex()

    val lockedByMe = mutex.tryLock()
    println("mutex.tryLock(): " + lockedByMe)
    mutex.unlock()
}

runBlocking {
    manualLock()
    isLocked()
    tryLockFalse()
    tryLockTrue()
}

println("# 휘발성 변수")
println("# JVM에서 각 쓰레드는 비활성 변수의 캐시된 사본을 가질 수 있음")
println("# 이 캐시는 항상 변수의 실제 값과 동기화되지는 않는다.")
println("# @Volatile은 Kotlin/JVM에서만 사용할 수 있음")
println("# @Volatile을 쓰려면 2가지 케이스에 모두 참이어야 한다.")
println("# 1. 변수 값의 변경은 현재 상태에 의존하지 않는다.")
println("# 2. 휘발성 변수는 다른 변수에 의존하지 않으며, 다른 변수도 휘발성 변수에 의존하지 않는다.")
println("# Something class은 위험")
class Something {

    @Volatile
    private var type = 0
    private var title = ""

    fun setTitle(newTitle: String) {
        when(type) {
            0 -> title = newTitle
            else -> throw Exception("Invalid State")
        }
    }
}

println("# DataProcessor class은 올바름")
class DataProcessor {
    @Volatile
    private var shutdownRequested = false

    fun shutdown() {
        shutdownRequested = true
    }

    fun process() {
        while (!shutdownRequested) {
            // process away
        }
    }
}

println("# 원자적 데이터 구조")
var counter4 = AtomicInteger()

fun asyncIncrement4(by: Int) = GlobalScope.async {
    for (i in 0 until by) {
        counter4.incrementAndGet()
    }
}

runBlocking {
    val workerA = asyncIncrement4(2000)
    val workerB = asyncIncrement4(100)

    workerA.await()
    workerB.await()

    print("counter [$counter4]")
}


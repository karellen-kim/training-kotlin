package quick.start

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

// https://kotlinlang.org/docs/sequences.html

// sequence와 iterator의 차이
println("# iterator은 eager하게 동작한다")
val nums1: List<Int> = listOf(1, 2, 3, 4, 5)
nums1
    .filter {
        println("filter : ${it}")
        it % 2 == 0 }
    .map {
        println("map : ${it}")
        it * 2 }
    .take(2)

println("# sequence는 lazy하게 동작한다")
val nums2: Sequence<Int> = sequenceOf(1, 2, 3, 4, 5)
val r = nums2
    .filter {
        println("filter : ${it}")
        it % 2 == 0 }
    .map {
        println("map : ${it}")
        it * 2 }
    .take(2)

r.toList()

println("# 함수를 이용하여 sequence를 생성할 수 있다")
val nums3 = generateSequence(1) {
    println("generate num : ${it + 1}")
    it + 1
}
nums3.take(5).toList()

println("# iterator는 statful하고 sequence는 stateless하다")
val iter: Iterator<Int> = listOf(1, 2, 3, 4, 5).iterator()
iter.next()
val seq: Sequence<Int> = sequenceOf(1, 2, 3, 4, 5)
seq.first()

iter.next()
seq.drop(1).first()

println("# producer는 iterator와 같이 stateful하다")
val prod1 = GlobalScope.produce() {
    send(1)
    send(2)
    send(3)
    send(4)
    send(5)
}

runBlocking {
    println("first : ${prod1.receive()}")
    println("second : ${prod1.receive()}")

    prod1.consumeEach {
        println("consume : ${it}")
    }
}

println("# producer는 sequence와 같이 lazy하다")
val prod2 = GlobalScope.produce() {
    (1 .. 10).forEach { send(it) }
}

val flow: Flow<Int> = prod2.consumeAsFlow()
    .filter {
        println("filter : ${it}")
        it % 2 == 0 }
    .map {
        it * 2
    }
    .take(2)



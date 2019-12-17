package main

/**
 * @author karellen
 */

fun main() {
    println("Hello world!")

    // Variables
    val a = 1
    val b: Int = 2
    val c: Int
    c = 3

    // Functions
    println(sum(a, b))
    println(sum(a, b, c))

    // String templates
    println("a is $a")
    println("a + b is ${sum(a, b)}")

    // Conditional expressions
    val str = if (a > 1) "greater than 1" else "smaller than 1"
    println(str)

    // for loop
    for (item in 0..10)
        println(item)

    // when
    println(desc(a))
    println(desc("str"))
    println(desc(listOf(1, 2, 3)))
}

fun sum(a: Int, b: Int): Int {
    return a + b
}

fun sum(a: Int, b: Int, c: Int) = a + b + c

fun desc(obj: Any): String? =
    when (obj) {
        1 -> "One"
        is Number -> "Number"
        is String -> "String"
        else -> null
    }

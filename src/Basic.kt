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
}

fun sum(a: Int, b: Int): Int {
    return a + b
}

fun sum(a: Int, b: Int, c: Int) = a + b + c

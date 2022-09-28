package com.pain.core.scala.basic

object CompanionApp {
    def main(args: Array[String]): Unit = {
        var objCounter = Counter()
        var classCounter = new Counter()
        classCounter()
    }
}

object Counter {
    println("Object Counter enter...")

    var count = 0

    def incr(): Unit = {
        count = count + 1
    }

    def apply() = {
        println("Object Counter apply...")
        new Counter
    }

    println("Object Counter leave...")
}

class Counter {
    def apply() = {
        println("Class Counter apply...")
    }
}
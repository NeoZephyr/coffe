package com.pain.core.scala.basic

import scala.util.control.Breaks

object LoopApp {
    def main(args: Array[String]): Unit = {
        println("to loop")
        for (e <- 1 to 5) {
            printf(s"${e} ")
        }
        println()

        println("until loop")
        for (e <- 1 until 5) {
            printf(s"${e} ")
        }
        println()

        println("range loop")
        for (e <- Range(1, 10, 3)) {
            printf(s"${e} ")
        }
        println()

        println("continue loop")
        for (e <- 1 until 5 if e % 2 == 0) {
            printf(s"${e} ")
        }
        println()

        println("break loop")
        Breaks.breakable {
            for (e <- Range(1, 10, 2)) {
                if (e == 5) {
                    Breaks.break()
                }

                printf(s"${e} ")
            }
            println()
        }
    }
}

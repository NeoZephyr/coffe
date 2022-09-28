package com.pain.core.scala

import com.pain.core.scala.ImplicitAspect.boyToAdultFunctions

object ImplicitApp {

    implicit class Calculator(num: Int) {
        def add(incr: Int): Int = {
            incr + num
        }
    }

    def main(args: Array[String]): Unit = {
        val boy = new Boy("jack")
        boy.play()
        boy.work()

        println(s"1 + 100 = ${1.add(100)}")
    }
}

class Boy(val name: String) {
    def play(): Unit = {
        println(s"boy $name is playing")
    }
}

class Adult(val name: String) {
    def work(): Unit = {
        println(s"adult $name is working")
    }
}

object ImplicitAspect {
    implicit def boyToAdultFunctions(boy: Boy): Adult = new Adult(boy.name)
}

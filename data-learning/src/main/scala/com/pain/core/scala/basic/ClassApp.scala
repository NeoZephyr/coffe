package com.pain.core.scala.basic

object ClassApp {

    def main(args: Array[String]): Unit = {
        val people = new People
        val student = new Student("jack", 20, "civil")
        people.printInfo()
        student.printInfo()
        println(student)
    }
}

class People {
    var name: String = _
    val age = 10

    private [this] val gender = "male"

    def printInfo(): Unit = {
        println("name: " + name + ", " + "gender: " + gender)
    }
}

class Student(name: String, age: Int, var school: String) extends People {
    override def toString: String = {
        s"name: $name, age: $age, school: $school"
    }
}
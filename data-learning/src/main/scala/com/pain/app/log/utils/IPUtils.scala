package com.pain.app.log.utils

object IPUtils {

    def ipToLong(ip: String) = {
        val items: Array[String] = ip.split("\\.")
        var ipNum = 0L

        for (i <- items.indices) {
            ipNum = items(i).toLong | ipNum << 8
        }

        ipNum
    }

    def main(args: Array[String]): Unit = {
        println(ipToLong("182.91.190.221"))
    }
}

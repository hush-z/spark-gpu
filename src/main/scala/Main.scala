package com.wyn.research.test

import com.amd.aparapi.Kernel
import com.amd.aparapi.Range


object Configurator {
    def reportConfiguration: String = "default configuration"
}

object AnotherMain {
    def main(args: Array[String]) {
        val a = List("summer", "autumn", "winter", "spring")
        a foreach println
        args.foreach(x => println(": " + x))
    }
}

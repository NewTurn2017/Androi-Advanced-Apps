package com.study.chapter3

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

fun main() {
    try {
        Thread{
            val port = 3001
            val server = ServerSocket(port)

            println("Server is running on port $port")
            while (true) {
                val socket = server.accept()
                socket.getInputStream()
                socket.getOutputStream()

                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val printer = PrintWriter(socket.getOutputStream())

                var input: String = "-1"
                while (input != null && input != "") {
                    input = reader.readLine()
                }

                println("Read Data: $input Client 접속됨")

                //Header
                printer.println("HTTP/1.1 200 OK")
                printer.println("Content-Type: text/html\r\n")
                //Body
                printer.println("{ \"name\": \"홍길동\", \"age\": 20 }")
                printer.println("\r\n")
                printer.flush()

                printer.close()
                reader.close()
                socket.close()
            }
        }.start()
    } catch (e: Exception) {
    }
}
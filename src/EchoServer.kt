//Activitat 1
//
//Modifica el codi de l'exemple EchoServer.kt de manera que el servidor pugui
// reconeixer certs protocols/comandes simples abans de fer el processament per defecte:
//
//Comanda TIME: El servidor respon amb la data i hora actuals del sistema.
//Comanda INFO: El servidor respon amb la IP i port del client connectat.
//Comanda REVERSE:text: Si el missatge comença per REVERSE:
// , el servidor respon amb el text invertit (p. ex., REVERSE:kotlin -> niltok).
//Altres textos: Manté el comportament per defecte (retorna el text en majúscules).
//
//Nota: Proveu d’obrir dues terminals de client simultàniament contra el mateix servidor
// i observa que passa.

import java.net.ServerSocket
import java.net.SocketException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val port = 9000
    println("[Servidor] Iniciant servei d'eco al port $port...")

    // Fem servir .use per assegurar el tancament en aturar el programa
    ServerSocket(port).use { serverSocket ->
        println("[Servidor] Escoltant connexions a ${serverSocket.inetAddress.hostAddress}:$port")

        // Bucle principal: El servidor mai s'atura després d'atendre un client
        while (true) {
            println("\n[Servidor] A l'espera d'un nou client...")

            // accept() és bloquejant: atura l'execució fins que entra una connexió
            val clientSocket = serverSocket.accept()
            println("[Servidor] Client connectat des de: ${clientSocket.remoteSocketAddress}")

            // Atenem el client connectat
            atendreClient(clientSocket)
        }
    }
}

private fun atendreClient(clientSocket: java.net.Socket) {
    // .use tanca automàticament clientSocket i els seus streams quan acaba el bloc
    clientSocket.use { socket ->
        val reader = socket.getInputStream().bufferedReader()
        val writer = socket.getOutputStream().bufferedWriter()

        try {
            var linia: String? = reader.readLine()

            // Llegim mentre el client mantingui la connexió oberta i no envii "sortir"
            while (linia != null && !linia.equals("sortir", ignoreCase = true)) {
                println("[Servidor <- ${socket.inetAddress.hostAddress}] Rebut: $linia")

               val resposta = when(linia) {
                    "TIME"->{
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                    }
                    "INFO"->{
                        "IP:${socket.inetAddress.hostAddress}/${clientSocket.port}"
                    }
                    linia.startsWith("REVERSE:").toString() ->{
                        linia.removePrefix("REVERSE:").reversed()
                    }
                   else -> {linia.uppercase()}

                }

                val mesatje = "[ECO] ${resposta}"
                writer.write(mesatje)
                writer.newLine()
                writer.flush() // Garanteix que les dades s'envien immediatament per la xarxa

                linia = reader.readLine() // Llegim la següent línia
            }
        } catch (e: SocketException) {
            println("[Servidor] La connexió amb el client s'ha interromput inesperadament. ${e.message}")
        }

        println("[Servidor] Finalitzada la seixó amb el client: ${socket.remoteSocketAddress}")
    }
}
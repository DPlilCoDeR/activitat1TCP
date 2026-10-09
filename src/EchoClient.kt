import java.net.Socket
import java.util.Scanner

fun main() {
    val host = "127.0.0.1" // localhost
    val port = 9000

    println("[Client] Intentant connectar a $host:$port...")

    Socket(host, port).use { socket ->
        println("[Client] Connexió establerta amb èxit!")

        val reader = socket.getInputStream().bufferedReader()
        val writer = socket.getOutputStream().bufferedWriter()
        val scanner = Scanner(System.`in`)

        while (true) {
            print("\nEscriu un text ('sortir' per acabar): ")
            val textUsuari = scanner.nextLine()

            // Enviar missatge al servidor
            writer.write(textUsuari)
            writer.newLine()
            writer.flush()

            if (textUsuari.equals("sortir", ignoreCase = true)) {
                println("[Client] Desconnectant del servidor...")
                break
            }

            // Llegir la resposta d'eco del servidor
            val resposta = reader.readLine()
            if (resposta == null) {
                println("[Client] El servidor ha tancat la connexió.")
                break
            }

            println("[Client <- Servidor] $resposta")
        }
    }
}
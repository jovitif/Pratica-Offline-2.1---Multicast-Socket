package GrupoFechado;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class DroneEnvio {
    private MulticastSocket ms;
    private InetAddress multicastIP;
    private int porta;
    private Drone drone;

    public DroneEnvio(String multicastIP, int porta, Drone drone) throws IOException {
        this.ms = new MulticastSocket();
        this.multicastIP = InetAddress.getByName(multicastIP);
        this.porta = porta;
        this.drone = drone;  // Associa o drone a essa instância de DroneEnvio
    }

    // Envia os dados do drone para o grupo multicast
    public void enviarDados() throws IOException {
        drone.gerarDadosAleatorios(); // Gera dados aleatórios antes de enviar
        String dados = drone.getDados(); // Obtém os dados formatados
        byte[] buffer = dados.getBytes(StandardCharsets.UTF_8);
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, multicastIP, porta);
        ms.send(pacote);
        System.out.println("Drone " + drone.getRegiao() + " enviou dados: " + dados);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Cria dois drones, um para o Norte e outro para o Sul
        Drone droneNorte = new Drone("Norte");
        Drone droneSul = new Drone("Sul");

        // Cria as instâncias de envio para cada drone
        DroneEnvio droneEnvioNorte = new DroneEnvio("localhost", 5000, droneNorte);
        DroneEnvio droneEnvioSul = new DroneEnvio("localhost", 5001, droneSul);

        while (true) {
            // Gera e envia os dados dos dois drones
            droneEnvioNorte.enviarDados();
            droneEnvioSul.enviarDados();

            Thread.sleep(3000); // Enviar dados a cada 3 segundos
        }
    }
}


package GrupoFechado;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class ServidorCaptura {
    private int id;
    private int portaRecepcao;
    private InetAddress multicastIP;
    private int portaMulticast;

    public ServidorCaptura(int id, int portaRecepcao, String multicastIP, int portaMulticast) throws IOException {
        this.id = id;
        this.portaRecepcao = portaRecepcao;
        this.multicastIP = InetAddress.getByName(multicastIP);
        this.portaMulticast = portaMulticast;
    }

    // Método para salvar os dados em um arquivo .txt
    public void salvarEmArquivo(String dados) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados_recebidos.txt", true))) {
            writer.write(dados);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados no arquivo: " + e.getMessage());
        }
    }

    public void iniciarRecepcao() throws IOException {
        try (MulticastSocket ms = new MulticastSocket(portaRecepcao)) {
            System.out.println("Servidor " + id + " aguardando dados na porta " + portaRecepcao);

            while (true) {
                // Recebe dados do drone
                byte[] dadosRecepcao = new byte[1024];
                DatagramPacket pacoteRecepcao = new DatagramPacket(dadosRecepcao, dadosRecepcao.length);
                ms.receive(pacoteRecepcao);

                String dados = new String(pacoteRecepcao.getData(), 0, pacoteRecepcao.getLength(), StandardCharsets.UTF_8);

                // Salva os dados no arquivo .txt
                salvarEmArquivo(dados);

                System.out.println("Servidor " + id + " recebeu: " + dados);

                // Retransmite para o grupo fechado
                DatagramPacket pacoteEnvio = new DatagramPacket(dadosRecepcao, pacoteRecepcao.getLength(), multicastIP, portaMulticast);
                ms.send(pacoteEnvio);
                System.out.println("Servidor " + id + " retransmitiu dados para o grupo.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServidorCaptura servidor1 = new ServidorCaptura(1, 5000, "225.7.8.9", 56789); // Servidor multicast
        ServidorCaptura servidor2 = new ServidorCaptura(2, 5001, "225.7.8.9", 56789);

        // Recebe dados dos drones e envia para o grupo multicast
        new Thread(() -> {
            try {
                servidor1.iniciarRecepcao(); // Porta do drone Norte
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                servidor2.iniciarRecepcao(); // Porta do drone Sul
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

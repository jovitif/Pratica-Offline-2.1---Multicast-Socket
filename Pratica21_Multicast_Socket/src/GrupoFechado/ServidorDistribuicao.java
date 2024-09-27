package GrupoFechado;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class ServidorDistribuicao implements Runnable {
	private int id;
	private InetAddress multicastIP;
	private int portaMulticast;
	private InetAddress externalMulticastIP; 
	private int portaExternal;

	public ServidorDistribuicao(int id, String multicastIP, int portaMulticast, String externalMulticastIP,
			int portaExternal) throws IOException {
		this.id = id;
		this.multicastIP = InetAddress.getByName(multicastIP);
		this.portaMulticast = portaMulticast;
		this.externalMulticastIP = InetAddress.getByName(externalMulticastIP); // Multicast externo
		this.portaExternal = portaExternal; 

	}



	@Override
	public void run() {
		try (MulticastSocket ms = new MulticastSocket(portaMulticast)) {
			NetworkInterface networkInterface = NetworkInterface.getByName("wlp2s0");
			InetSocketAddress grupo = new InetSocketAddress(multicastIP, portaMulticast);

			ms.joinGroup(grupo, networkInterface);
			System.out.println("Servidor " + id + " conectado ao grupo multicast " + multicastIP);

			while (true) {
				byte[] dadosRecepcao = new byte[1024];
				DatagramPacket pacoteRecepcao = new DatagramPacket(dadosRecepcao, dadosRecepcao.length);
				ms.receive(pacoteRecepcao);

				String dados = new String(pacoteRecepcao.getData(), 0, pacoteRecepcao.getLength(),
						StandardCharsets.UTF_8);
				System.out.println("Servidor " + id + " recebeu do grupo: " + dados);

				try (MulticastSocket msExternal = new MulticastSocket()) {
					byte[] dadosEnvio = dados.getBytes(StandardCharsets.UTF_8);
					// Cria pacote para enviar para o grupo multicast externo
					DatagramPacket pacoteEnvio = new DatagramPacket(dadosEnvio, dadosEnvio.length, externalMulticastIP,
							portaExternal);
					// Envia os dados
					System.out.println(
							"Enviando dados para grupo multicast: " + externalMulticastIP + ":" + portaExternal);
					msExternal.send(pacoteEnvio);
					System.out.println("Dados enviados.");

					System.out.println("Servidor " + id + " retransmitiu dados para o grupo externo: " + dados);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	public static void main(String[] args) throws IOException {
		new Thread(new ServidorDistribuicao(3, "225.7.8.9", 56789, "226.7.8.10", 56790)).start();
		new Thread(new ServidorDistribuicao(4, "225.7.8.9", 56789, "226.7.8.10", 56790)).start();

	}
}

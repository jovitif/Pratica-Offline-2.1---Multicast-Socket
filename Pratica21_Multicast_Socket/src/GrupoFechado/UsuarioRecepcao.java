package GrupoFechado;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class UsuarioRecepcao implements Runnable {
	private MulticastSocket ms;
	private InetAddress multicastIP;
	private int porta;

	public UsuarioRecepcao(String multicastAddress, int porta) throws IOException {
		this.multicastIP = InetAddress.getByName(multicastAddress);
		this.porta = porta;
		this.ms = new MulticastSocket(porta);
		NetworkInterface networkInterface = NetworkInterface.getByName("wlp2s0"); // Adaptar à interface de rede correta
		ms.joinGroup(new InetSocketAddress(multicastIP, porta), networkInterface);
	}

	@Override
	public void run() {
		System.out.println("usuario iniciado");

		try {
			byte[] buffer = new byte[1024];
			while (true) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				ms.receive(packet);
				System.out.println("Usuario recebeu dados");
				String dados = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
				System.out.println("Usuario recebeu: " + dados);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			ms.close();
		}
	}

	public static void main(String[] args) throws IOException {
		UsuarioRecepcao usuario = new UsuarioRecepcao("226.7.8.10", 56790);

		// Iniciar recebimento de dados do multicast
		new Thread(usuario).start();
	}
}

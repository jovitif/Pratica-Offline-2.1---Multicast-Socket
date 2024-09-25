package GrupoAberto.usuario;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class User {
	
	public void init() throws IOException{
		String grupoIp = "225.7.8.9";
		int portaServidor = 55555;
		Random random = new Random();
		int portaUsuario = random.nextInt(40000, 50000);
		
		MulticastSocket ms = new MulticastSocket(portaUsuario);
		System.out.println("Servidor: " +
		InetAddress.getLocalHost() +
		" -- executando na porta " +
		ms.getLocalPort());
		
		InetAddress multicastIP = InetAddress.getByName(grupoIp);
		System.out.println(multicastIP.toString().substring(1));
		InetSocketAddress grupo = new InetSocketAddress(multicastIP, portaServidor);
		NetworkInterface interfaceRede =
		NetworkInterface.getByName("WIFI");
		
		ms.joinGroup(grupo, interfaceRede);
		
		Thread recepcao = new Thread(()->{
			boolean flag = true;
			Scanner entrada = new Scanner(System.in);
			byte dadosEnvio[];
			DatagramPacket pacoteEnvio;
			
			try {
				while (flag) {
				System.out.println("\n>>> Digite a mensagem: ");
				String msg = entrada.nextLine();
				
				dadosEnvio = msg.getBytes(StandardCharsets.UTF_8);
				
				System.out.println("\nEnviando mensagem ao grupo " +
				"multicast...");
				pacoteEnvio = new DatagramPacket(
					dadosEnvio,
					dadosEnvio.length,
					InetAddress.getByName(
					multicastIP.toString().
					substring(1)),
					portaServidor);
				
				ms.send(pacoteEnvio);
				if (msg.equalsIgnoreCase("sair")) {
					flag = false;
					System.out.println("Processo " +
					InetAddress.getLocalHost() +
					" finalizou sua operação.");
				}
				}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				entrada.close();
				/*
				* Fechando o MS
				*/
				ms.close();
		});
		
		
	}
	
	public static void main(String[] args) {
		
	}
}

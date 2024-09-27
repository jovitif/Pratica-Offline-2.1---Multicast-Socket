package GrupoAberto.usuario;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class User {
	
	public void init() throws IOException{
		String grupoIp = "225.7.8.9";
		int portaServidor = 55555;
		Random random = new Random();
		int portaUsuario = random.nextInt(40000, 50000);
		
		MulticastSocket ms = new MulticastSocket(portaUsuario);
		System.out.println("Usuario: " +
		InetAddress.getLocalHost() +
		" -- executando na porta " +
		ms.getLocalPort());
		
		InetAddress multicastIP = InetAddress.getByName(grupoIp);
		System.out.println(multicastIP.toString().substring(1));
		InetSocketAddress grupo = new InetSocketAddress(multicastIP, portaServidor);
		NetworkInterface interfaceRede =
		NetworkInterface.getByName("WIFI");
		
		ms.joinGroup(grupo, interfaceRede);
		
		Thread envio = new Thread(()->{
			boolean flag = true;
			Scanner entrada = new Scanner(System.in);
			byte dadosEnvio[];
			DatagramPacket pacoteEnvio;
			
			try {
				while (flag) {
				System.out.println("\nEscolha uma opcao: "
						+ "\n1) Iniciar/Parar coleta de dados"
						+ "\n2) Visualizar dados coletados");
				String msg = entrada.nextLine();
				msg = msg +"--"+portaUsuario;
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
				if (msg.split("-")[0].equalsIgnoreCase("3")) {
					flag = false;
					System.out.println("Usuario " +
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
				
				ms.close();
		});
		envio.start();
		
		Thread recepcao = new Thread(()->{
			boolean flag = true;
			byte dadosRecepcao[] = new byte[102400];
			DatagramPacket pacoteRecepcao;
			String textoRecebido;
			List<String> listaDados;
			try {
				while (flag) {
					
					pacoteRecepcao = new DatagramPacket(
							dadosRecepcao,
							dadosRecepcao.length);
					ms.receive(pacoteRecepcao);
					
					System.out.println("\n*** Dados recebidos de: " +
					pacoteRecepcao.getAddress().toString() +
					":" + pacoteRecepcao.getPort() +
					" com tamanho: " +
					pacoteRecepcao.getLength());
					
					System.out.write(dadosRecepcao,0,pacoteRecepcao.getLength());
					
					dadosRecepcao = pacoteRecepcao.getData();
					textoRecebido = new String(dadosRecepcao,0,pacoteRecepcao.getLength());
					textoRecebido = textoRecebido.substring(1,textoRecebido.length()-1);
					listaDados = Arrays.asList(textoRecebido.split(","));
					
					listaDados.forEach((dado)->{
						System.out.println(
								"\nData da coleta: "+dado.split("=")[0] +
								" Temperatura(C°): "+dado.split("=")[1]);
					});
					
					
				}
			} catch (IOException e) {
			e.printStackTrace();
			}
		});
		recepcao.start();
	}
	
	public static void main(String[] args) throws IOException {
		new User().init();
	}
}

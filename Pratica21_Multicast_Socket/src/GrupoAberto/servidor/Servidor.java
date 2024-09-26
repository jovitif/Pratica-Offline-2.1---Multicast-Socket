package GrupoAberto.servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Servidor {
	
	private Map<String,String> listaDados;
	private Queue<String> filaRequisicoes;
	private boolean flagDeEnvio;
	
	public void init() throws IOException {
		String ip = "225.7.8.9";
		int porta = 55555;
		listaDados = new HashMap<String,String>();
		filaRequisicoes = new PriorityQueue<String>();
		
		MulticastSocket ms = new MulticastSocket(porta);
		System.out.println("Servidor: " +
		InetAddress.getLocalHost() +
		" -- executando na porta " +
		ms.getLocalPort());
		
		InetAddress multicastIP = InetAddress.getByName(ip);
		System.out.println(multicastIP.toString().substring(1));
		InetSocketAddress grupo = new InetSocketAddress(multicastIP, 55555);
		NetworkInterface interfaceRede =
		NetworkInterface.getByName("WIFI");
		
		ms.joinGroup(grupo, interfaceRede);

		Thread recepcao = new Thread(()->{
			boolean flag = true;
			byte dadosRecepcao[] = new byte[1024];
			DatagramPacket pacoteRecepcao;
			String textoRecebido;
			try {
				while (flag) {
					pacoteRecepcao = new DatagramPacket(dadosRecepcao,dadosRecepcao.length);
					ms.receive(pacoteRecepcao);
					
					System.out.write(dadosRecepcao, 0,
					pacoteRecepcao.getLength());
					
					textoRecebido = dadosRecepcao.toString();
					if(textoRecebido.split("--")[0].equals("2")) {
						filaRequisicoes.add(textoRecebido.split("-")[1]);
						
					}
					else {
						//System.out.println(textoRecebido);	
					    listaDados.put(
					    		textoRecebido.split("--")[0],
					    		textoRecebido.split("--")[0]);
					    
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		recepcao.start();
		
		Thread envio = new Thread(()->{
			boolean flag = true;
			byte dadosEnvio[];
			DatagramPacket pacoteEnvio;
			String textoLista = "";
			String msg = "";
			String portaUsuario = "";
			
			try {
				while (flag) {
				
					if(!filaRequisicoes.isEmpty()){
						textoLista = listaDados.toString();
						
						dadosEnvio = textoLista.toString().getBytes(StandardCharsets.UTF_8);
						portaUsuario = filaRequisicoes.poll();
						
						System.out.println("\nEnviando mensagem ao usuario " + portaUsuario +
						"multicast...");
						pacoteEnvio = new DatagramPacket(
							dadosEnvio,
							dadosEnvio.length,
							InetAddress.getByName(
							multicastIP.toString().
							substring(1)),
							Integer.parseInt(portaUsuario));
						
						ms.send(pacoteEnvio);
						if (msg.equalsIgnoreCase("3")) {
							flag = false;
							System.out.println("Usuario " +
							InetAddress.getLocalHost() +
							" finalizou sua operação.");
						}
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			ms.close();
			});
		envio.start();
	}
	public static void main(String[] args) throws IOException {
		new Servidor().init();
	}
}

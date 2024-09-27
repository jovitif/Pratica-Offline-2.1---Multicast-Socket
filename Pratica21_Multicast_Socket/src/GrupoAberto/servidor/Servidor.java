package GrupoAberto.servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Servidor {
	
	private Map<String,String> listaDados;
	private volatile Queue<Integer> filaRequisicoes;
	private volatile boolean flagSalvarDados;
	
	public void init() throws IOException {
		String ip = "225.7.8.9";
		int porta = 55555;
		listaDados = new HashMap<String,String>();
		filaRequisicoes = new PriorityQueue<Integer>();
		
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
					
					
					dadosRecepcao = pacoteRecepcao.getData();
					textoRecebido = new String(dadosRecepcao,0,pacoteRecepcao.getLength());
					if(textoRecebido.split("--")[0].equals("2")) {
						//System.out.println("Processar requisicao de usuario "+pacoteRecepcao.getPort());
						filaRequisicoes.add(pacoteRecepcao.getPort());
						
						
					}
					else if(textoRecebido.split("--")[0].equals("1")) {
						
						if(flagSalvarDados) { 
							flagSalvarDados = false;
							System.out.println("Parar da salvar dados");
						}
						else {
							flagSalvarDados = true;
							System.out.println("Começar a salvar dados");
						}	
					}
					else if (pacoteRecepcao.getPort()<30000 && flagSalvarDados){
						//System.out.println(textoRecebido);	
					    listaDados.put(
					    		textoRecebido.split("--")[0],
					    		textoRecebido.split("--")[1]);
					    
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
			
			int portaUsuario;
			
			try {
				while (flag) {
					
					if(!filaRequisicoes.isEmpty()) {
						textoLista = listaDados.toString();
						
						dadosEnvio = textoLista.toString().getBytes(StandardCharsets.UTF_8);
						portaUsuario = filaRequisicoes.poll();
						
						System.out.println("\nEnviando mensagem ao usuario " + portaUsuario +" multicast...");
						pacoteEnvio = new DatagramPacket(
							dadosEnvio,
							dadosEnvio.length,
							InetAddress.getByName(
							multicastIP.toString().
							substring(1)),
							portaUsuario);
						
						ms.send(pacoteEnvio);
						
						Thread.sleep(3000);
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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

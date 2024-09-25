package GrupoAberto.servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
	
	private Map<String,String> listaDados;
	
	public void init() throws IOException {
		String ip = "225.7.8.9";
		int porta = 55555;
		listaDados = new HashMap<String,String>();
		
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
			String textoRecebido;
			DatagramPacket pacoteRecepcao;
			try {
				while (flag) {
					pacoteRecepcao = new DatagramPacket(dadosRecepcao,dadosRecepcao.length);
					ms.receive(pacoteRecepcao);
					
					System.out.write(dadosRecepcao, 0,
					pacoteRecepcao.getLength());
					
					
					textoRecebido = dadosRecepcao.toString().split("[")[0]; 
					System.out.println(textoRecebido);	
				    listaDados.put(
				    		textoRecebido.split("--")[0],
				    		textoRecebido.split("--")[1].split("[")[0]);
				    
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		recepcao.start();
		//Thread tEnvio = new Thread(new ThreadEnvio(ms, multicastIP, porta));
		//tEnvio.start();


	}
	public static void main(String[] args) throws IOException {
		new Servidor().init();
	}
}

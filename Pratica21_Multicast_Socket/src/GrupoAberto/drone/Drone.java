package GrupoAberto.drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.time.ZonedDateTime;

public class Drone {
	
	private final String IP = "225.7.8.9";
	private final Integer portaServidor = 55555;
	
	public void init() throws IOException, InterruptedException{
		DatagramSocket ds = new DatagramSocket();
		System.out.println("Drone " +
		InetAddress.getLocalHost() +
		" enviando na porta " +
		ds.getLocalPort());
		
		boolean flag = true;
		byte bufferEnvio[];
		DatagramPacket pacoteEnvio;
		Random random = new Random();
		DecimalFormat df = new DecimalFormat();
		
		while (flag) {
			String msg = df.format(random.nextDouble(22.0d,36.0d));
			String time = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
			msg = time + "--" + msg;
			
			bufferEnvio = msg.getBytes(StandardCharsets.UTF_8);
			
			pacoteEnvio = new DatagramPacket(
			bufferEnvio,
			bufferEnvio.length,
			InetAddress.getByName(IP),
			portaServidor);
			/*
			* Fazendo o envio
			*/
			ds.send(pacoteEnvio);
			//System.out.println(msg);
			
			Thread.sleep(3000);
			if (msg.equalsIgnoreCase("sair")) {
				flag = false;
			}
		}
		
		ds.close();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new Drone().init();;
	}
}

package redes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Servidor {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Uso: java proxy <porta>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);

		System.out.println("Aguardando conexões...");	
		ServerSocket serverSocket = null;


		try  {
			//Cria um socket para a porta passada por parametro
			serverSocket = new ServerSocket(Integer.parseInt(args[0]));        	

			while(true) {
				//Aguarda ate que um cliente se conecte e estabeleca conexao na porta
				Socket clientSocket = serverSocket.accept();  

				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String inputLine;
				List<String> linhas = new ArrayList<String>();
				
				String[] campos;
				while ((inputLine = in.readLine()) != null) { 
					if (inputLine.equals("")) break;
	               	linhas.add(inputLine);	              
	               	System.out.println(inputLine);
	            }
 
				campos = linhas.get(0).split(" ");		

				if (!campos[1].contains("http://")) {
					campos[1] = "http://" + campos[1];
				}						

				String metodo = campos[0];
				String end = campos[1];
				String versao = campos[2];             

				URL url = new URL(end);  
				try {              	
					
					Socket socket = new Socket(url.getHost(), 80);
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					
					for (int i = 0; i < linhas.size(); i++) {
						writer.println(linhas.get(i));						
					}					
					writer.println("");
					writer.flush();

					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					for (String line; (line = reader.readLine()) != null;) {                          
						out.println(line);
						System.out.println(line);
					}
					out.flush();  
					socket.close();

					clientSocket.close();                   
				}
				catch (MalformedURLException e) {
					out.println("URL mal formatada!");
				}     



			}
		} catch (IOException e) {        	
			System.out.println("Exceção ao tentar ouvir a porta "
					+ portNumber);
			System.out.println(e.getMessage());
			e.printStackTrace();

			serverSocket.close();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("Não foi possível fechar o socket");
			}

		}

	}

}

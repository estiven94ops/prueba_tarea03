package prueba_tarea3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

    private static final String TASK_QUEUE_NAME = "task_queue";
    
   
    public static String encryptThisString(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-512 
            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
  
            // digest() method is called 
            // to calculate message digest of the input string 
            // returned as array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            // return the HashText 
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    } 
    
    
    
    public static void findAllFilesInFolder(File folder) {
		for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
				
				System.out.println(file.getName());
				
				JSONObject myObject = new JSONObject();
		    	JSONObject subdata = new JSONObject();
		    	File archivo;
		    	FileReader fr = null;
		    	BufferedReader br = null;
		    	
		    	Map<String, Integer> palabras = new HashMap<String, Integer>();

		    	
		    	try {
		    		archivo = new File("E:\\USUARIOS\\UEES\\Sistemas Operativos\\I parcial\\Tarea\\deber01\\archivos_ejemplo\\archivos\\" + file.getName());
		    		fr = new FileReader(archivo);
		    		br = new BufferedReader(fr);
		    		String nombre_archivo = archivo.getName();
		    	
		    		System.out.println("Lectura de archivo " + nombre_archivo);
		    		
		    		
		    		
		    		String linea;
		    		
		    		while ((linea = br.readLine()) != null) {
		    			System.out.println(linea);
		    			
		    			for (String palabra: linea.split(" ")) {
		    				palabras.put(palabra, palabras.containsKey(palabra) ? palabras.get(palabra) + 1 : 1);
		    			}
		    			
		      		}
		    		
		    		
		    		
			        for (HashMap.Entry<String, Integer> entry : palabras.entrySet()) {
			            System.out.printf("Palabra '%s' con frecuencia %d\n", entry.getKey(), entry.getValue());
			            subdata.put(entry.getKey(), entry.getValue());
			        }
			        
			        myObject.put("id", encryptThisString(archivo.getName()));
			        myObject.put("frecuencia", subdata);
			        System.out.println(myObject);


			        
		    	}catch (IOException e) {
		    		e.printStackTrace();
		    	}
				
			} else {
				findAllFilesInFolder(file);
			}
		}
	}
    
    

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.103");
        factory.setUsername("radmin");
        factory.setPassword("radmin");
        try (Connection connection = factory.newConnection();
        		
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            String message = String.join(" ", argv);


        	
        	try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("E:\\USUARIOS\\UEES\\Sistemas Operativos\\I parcial\\Tarea\\deber01\\archivos_ejemplo\\archivos"))) {
    		    for (Path file: stream) {
    		    	
    		        System.out.println(file.getFileName());
    		        JSONObject myObject = new JSONObject();
    		    	JSONObject subdata = new JSONObject();
    		    	File archivo;
    		    	FileReader fr = null;
    		    	BufferedReader br = null;
    		    	
    		    	Map<String, Integer> palabras = new HashMap<String, Integer>();

    		    	
    		    	try {
    		    		archivo = new File("E:\\USUARIOS\\UEES\\Sistemas Operativos\\I parcial\\Tarea\\deber01\\archivos_ejemplo\\archivos\\"+file.getFileName());
    		    		fr = new FileReader(archivo);
    		    		br = new BufferedReader(fr);
    		    		String nombre_archivo = archivo.getName();
    		    	
    		    		System.out.println("Lectura de archivo " + nombre_archivo);
    		    		
    		    		
    		    		
    		    		String linea;
    		    		
    		    		while ((linea = br.readLine()) != null) {
    		    			System.out.println(linea);
    		    			
    		    			for (String palabra: linea.split(" ")) {
    		    				palabras.put(palabra, palabras.containsKey(palabra) ? palabras.get(palabra) + 1 : 1);
    		    			}
    		    			
    		      		}
    		    		
    			        /*System.out.println("HashCode Generated by SHA-512 for: "); 
    			        
    			        String s1 = "GeeksForGeeks"; 
    			        System.out.println(" " + s1 + " : " + encryptThisString(s1)); 
    			  
    			        String s2 = "hello world"; 
    			        System.out.println(" " + s2 + " : " + encryptThisString(s2)); 
    			        
    			        String s3 = archivo.getName(); 
    			        System.out.println(" " + s3 + " : " + encryptThisString(s3)); */
    		    	
    		    		
    		    		
    			        for (HashMap.Entry<String, Integer> entry : palabras.entrySet()) {
    			            System.out.printf("Palabra '%s' con frecuencia %d\n", entry.getKey(), entry.getValue());
    			            subdata.put(entry.getKey(), entry.getValue());
    			        }
    			        
    			        myObject.put("id", encryptThisString(archivo.getName()));
    			        myObject.put("frecuencia", subdata);
    			        System.out.println(myObject);
    			        channel.basicPublish("", TASK_QUEUE_NAME,
    		                    MessageProperties.PERSISTENT_TEXT_PLAIN,
    		                    message.getBytes("UTF-8"));
    		            System.out.println(" [x] Sent '" + myObject + "'");

    			        
    		    	}catch (IOException e) {
    		    		e.printStackTrace();
    		    	}
    		    }
    		} catch (IOException | DirectoryIteratorException ex) {
    		    System.err.println(ex);
    		}

            
        }
    }

}
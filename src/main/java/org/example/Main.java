package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        //Declare variables
        String server = "localhost";
        int port = 2000;
        Integer chunkSize = null;
        String fileName = null;
        try(
                DatagramSocket s = new DatagramSocket(port)
                ){
            DatagramPacket p = new DatagramPacket(new byte[256], 256);
            s.receive(p);
            //Read the name of the file that the client requested
            fileName = new String(p.getData(), 0,p.getLength());
            System.out.println("The client requested the file [" + fileName +"]");

            //Send the response to the client
            p.setData("Ok! What is the size of the chunk?".getBytes());
            p.setLength(p.getData().length);
            s.send(p);
            //We wait for the client
            System.out.println("waiting for the size of the chunks");
            s.receive(p);
            chunkSize = Integer.valueOf(new String(p.getData(), 0 , p.getLength()));
            System.out.println("ChunkSize will be : " + chunkSize);
            try( FileInputStream fis = new FileInputStream("./files/" + fileName)){
                byte[] a = fis.readAllBytes();
                p.setData(String.valueOf((a.length / chunkSize ) + 1) .getBytes());
                p.setLength(p.getData().length);
                s.send(p);
                String fileContent = new String(a);
                //We start sending the file to the client
                p.setData(new byte[2000]);
                p.setLength(p.getData().length);
                for (int i = 1; i < (a.length / chunkSize) + 1; i++) {
                    byte[] chunk = Arrays.copyOfRange(a,(i-1) * chunkSize, i * chunkSize);
                    p.setData(chunk);
                    p.setLength(p.getData().length);
                    s.send(p);

                }
            }catch (FileNotFoundException e){
                System.out.println(e);
            }

        }catch (SocketException e){
            System.out.println(e);
        }
    }
}
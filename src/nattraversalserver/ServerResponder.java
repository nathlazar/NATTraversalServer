/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nattraversalserver;

/**
 *
 * @author Lazar
 */

import clientInfo.ClientInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerResponder {
    
    private DatagramSocket  socket1,socket2;
    private DatagramPacket sendPacket,receivePacket;
    private HashMap<String,ClientInfo> usersList=new HashMap<String,ClientInfo>();
    
    public ServerResponder(){
        
        try { 
            
            while(true){
                
                socket1=new DatagramSocket(7070);
                receivePacket=new DatagramPacket(new byte[1024],1024);
                socket1.receive(receivePacket);
                String host1=new String(receivePacket.getData());
                InetAddress IPAddress1 = receivePacket.getAddress();
                int port1 = receivePacket.getPort();
                String msgInfoOfClient1 = IPAddress1+"-"+port1+"-"+host1;
                System.out.println("Client1: " + msgInfoOfClient1);
                
                socket2=new DatagramSocket(7071);
                receivePacket=new DatagramPacket(new byte[1024],1024);
                socket2.receive(receivePacket);
                String host2=new String(receivePacket.getData());
                InetAddress IPAddress2 = receivePacket.getAddress();
                int port2 = receivePacket.getPort();
                String msgInfoOfClient2 = IPAddress2+"-"+port2+"-"+host2;

                System.out.println("Client2:" + msgInfoOfClient2);
                
                socket1.send(new DatagramPacket(msgInfoOfClient2.getBytes(),msgInfoOfClient2.getBytes().length,IPAddress1,port1));
                socket2.send(new DatagramPacket(msgInfoOfClient1.getBytes(),msgInfoOfClient1.getBytes().length,IPAddress2,port2));
                socket1.close();
                socket2.close();
                usersList.put(host1,new ClientInfo(IPAddress1.toString().substring(1),port1,port1,host1));
                usersList.put(host2,new ClientInfo(IPAddress2.toString().substring(1),port2,port2,host2));
                //broadcast(clientInfo);
                

            }  
            
        } 
        catch (SocketException ex) {
            
            Logger.getLogger(ServerResponder.class.getName()).log(Level.SEVERE, null, ex);
            
        } 
        catch (IOException ex) {
            
            Logger.getLogger(ServerResponder.class.getName()).log(Level.SEVERE, null, ex);
            
        }
       
        
    }
    
    private synchronized void broadcast(ClientInfo clientInfo){
        
        try {
            
            DatagramSocket socket=new DatagramSocket();
            for(String key:this.usersList.keySet()){
                
                for(String s:this.usersList.keySet()){
                    
                    if(!key.equals(s)){
                        
                        ByteArrayOutputStream bos=new ByteArrayOutputStream();
                        ObjectOutputStream os=new ObjectOutputStream(bos);
                        os.writeObject(usersList.get(key));
                        byte[] data=bos.toByteArray();
                        DatagramPacket sendPacket=new DatagramPacket(data,data.length,
                                InetAddress.getByName(usersList.get(s).getIPAddress()),usersList.get(s).getPort());
                        socket.send(sendPacket);
                        
                    }
                    
                }
                
            }
            socket.close();
            
        } 
        catch (SocketException ex) {
            
            Logger.getLogger(ServerResponder.class.getName()).log(Level.SEVERE, null, ex);
            
        } 
        catch (IOException ex) {
            Logger.getLogger(ServerResponder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}

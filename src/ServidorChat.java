import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorChat extends Thread{
    private int numClientes;
    private int chatPort;
    private List<ChatJuego> chats = new LinkedList<>();
    
    public ServidorChat(int chatPort)
    {
        numClientes = 0;
        this.chatPort = chatPort;
    }
    
    public void run(){
        try( ServerSocket serverSocket = new ServerSocket(chatPort); ){
            while(numClientes != 2)
            {
                Socket clientSocket = serverSocket.accept();
                numClientes++;
                ChatJuego chatJuego = new ChatJuego(chats, clientSocket);
                chatJuego.start();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void cierraChats() throws IOException
    {
        int cuantosChats = chats.size();
        for(int i = 0; i < cuantosChats; i++)
        {
            chats.get(0).yaHaTerminado();
        }
    }
    
    public class ChatJuego extends Thread
    {
        private boolean terminaJuego;
        private Socket socket;
        private DataOutputStream out;
        private DataInputStream in;
        private List<ChatJuego> chats = new LinkedList<>();
        
        public ChatJuego(List<ChatJuego> chats,Socket socket) throws IOException
        {
            terminaJuego = false;
            this.chats = chats;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }
        
        public void run()
        {
            try {
                synchronized (chats) {
                    chats.add(this);
                }
                while(!terminaJuego)
                {
                    String msg = leerMensajeDeJugador(socket);
                    int quienEs = chats.indexOf(this);
                    for(int i = 0; i < chats.size(); i++)
                    {
                        if(i != quienEs)
                            mandarMensajeAJugador(msg,chats.get(i).getSocket());
                    }
                }
            } catch (Exception ex) {
                System.exit(0);
            } finally {
                try{ 
                    socket.close();
                } catch(Exception ex){}
                synchronized (chats) {
                    chats.remove(this);
                }
            }
        }
        
        public void mandarMensajeAJugador(String msg,Socket socket) throws IOException{
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            try {
                out.writeUTF(msg);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.flush();
        }

        public String leerMensajeDeJugador(Socket socket) throws IOException
        {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            return(in.readUTF());
        }
        
        public Socket getSocket()
        {
            return socket;
        }
        
        public void yaHaTerminado() throws IOException
        {
            terminaJuego = true;
            socket.close();
            chats.remove(this);
        }
    }
}

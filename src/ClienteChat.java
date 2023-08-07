
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteChat extends Thread{
    private Jugador jugador;
    private Socket socketChat;
    private ClienteChatEscucha escucharChat;
    private ClienteChatEscribe escribirChat;
    
    public ClienteChat(Socket socketChat,Jugador jugador) throws IOException
    {
        this.jugador = jugador;
        this.socketChat = socketChat;
        escucharChat = new ClienteChatEscucha(socketChat,jugador);
        escribirChat = new ClienteChatEscribe(socketChat,jugador);
    }
    
    public void run()
    {
        escucharChat.start();
        escribirChat.start();
    }
    
    public class ClienteChatEscucha extends Thread
    {
        private Jugador jugador;
        private Socket socketChat;
        private DataOutputStream out;
        private DataInputStream in;
        public ClienteChatEscucha(Socket socketChat,Jugador jugador) throws IOException
        {
            this.jugador = jugador;
            this.socketChat = socketChat;
            in = new DataInputStream(socketChat.getInputStream());
            out = new DataOutputStream(socketChat.getOutputStream());
        }
        
        public void run()
        {
            while(!socketChat.isClosed())
            {
                try {
                    String msg = leerMensajeDeServidor();
                    jugador.actualizaChat(msg, false);
                } catch (IOException ex) {
                    System.exit(0);
                }
            }
        }
        public String leerMensajeDeServidor() throws IOException
        {
            return(in.readUTF());
        }
    }
    
    public class ClienteChatEscribe extends Thread
    {
        private Socket socketChat;
        private Jugador jugador;
        private DataOutputStream out;
        private DataInputStream in;
        public ClienteChatEscribe(Socket socketChat,Jugador jugador) throws IOException
        {
            this.jugador = jugador;
            this.socketChat = socketChat;
            in = new DataInputStream(socketChat.getInputStream());
            out = new DataOutputStream(socketChat.getOutputStream());
        }
        @Override
        public void run()
        {
            while(!socketChat.isClosed())
            {
                String msg = "";
                try {
                    msg = jugador.hanEscritoChat();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(!msg.equals(""))
                {
                    try {
                        mandarMensajeAServidor(msg);
                    } catch (IOException ex) {
                        Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        public void mandarMensajeAServidor(String msg) throws IOException{
            try {
                out.writeUTF(msg);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.flush();
        }
    }
}

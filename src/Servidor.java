import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.interrupted;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread{
    private boolean hayRegistroVictorias;
    private HashMap<String,Long> victorias;
    private ConfigServidor config;
    private int numClientes;
    private ServidorChat chat;
    private Tablero tablero;
    private int gamePort;
    private List<ClientThread> clients = new LinkedList<>();
    public static void main(String[] args) throws InterruptedException {
        Servidor server = new Servidor();
        server.start();
    }

    public Servidor() throws InterruptedException {
        victorias = new HashMap<String,Long>();
        try (BufferedReader br = new BufferedReader(new FileReader("registro.csv"))) {
            hayRegistroVictorias = true;
            ObjectInputStream entrada=new ObjectInputStream(new FileInputStream("registro.csv"));
            victorias = (HashMap<String,Long>) entrada.readObject();
            for(String nombre : victorias.keySet()) {
                System.out.println(nombre + " -> " + victorias.get(nombre));
            }
        } catch (Exception e) {
            System.out.println("no se ha podido abrir bien el fichero");
            hayRegistroVictorias = false;
        }
        config = new ConfigServidor();
        config.yaEsta();
        numClientes = 0;
        this.tablero = new Tablero();
        gamePort = config.getPuerto();
        chat = new ServidorChat(gamePort+1);
        chat.start();
    }
    
    public void escribeBin(Tablero tablero,Socket clientSocket) throws IOException {
        ObjectOutputStream salida=
                new ObjectOutputStream(
                        clientSocket.getOutputStream());
        salida.writeObject(tablero);
    }
    
    public void escribeMapa(HashMap<String,Long> map,Socket clientSocket) throws IOException {
        ObjectOutputStream salida=
                new ObjectOutputStream(
                        clientSocket.getOutputStream());
        salida.writeObject(map);
    }
    
    public Tablero leeBin(Socket clientSocket) throws IOException, ClassNotFoundException {
        ObjectInputStream entrada=new ObjectInputStream(
                clientSocket.getInputStream());
        return ((Tablero) entrada.readObject());
    }

    @Override
    public void run(){
        try( ServerSocket serverSocket = new ServerSocket(gamePort); ){
            while(numClientes != 2){
                Socket clientSocket = serverSocket.accept();
                escribeMapa(victorias,clientSocket);
                numClientes++;
                ClientThread clientThread = new ClientThread(clients, clientSocket,tablero);
                clientThread.start();
            }
        }catch(Exception ex){
            ex.printStackTrace();
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
    
    public class ClientThread extends Thread{
        private Tablero tablero;
        private List<ClientThread> clients;
        private Socket socket;
        private DataOutputStream out;

        public ClientThread(List<ClientThread> clients, Socket socket,Tablero tablero) {
            this.tablero = tablero;
            this.clients = clients;
            this.socket = socket;
        }
        
        synchronized public void sendMsg(String msg){
            try {
                out.writeUTF(msg);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                synchronized (clients) {
                    clients.add(this);
                }
                DataInputStream in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                Integer numClientes = clients.size();
                sendMsg(numClientes.toString());
                out.flush();
                escribeBin(tablero,socket);
                out.flush();
                if(clients.indexOf(this) == 1)
                {
                    jugar(clients);
                    clients.get(0).yaEsta();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if(clients.indexOf(this) == 0)
                {
                    synchronized(this)
                    {
                        try {
                            wait();
                            try{ 
                                socket.close();
                            } catch(Exception ex){}
                            synchronized (clients) {
                                clients.remove(this);
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                else if(clients.indexOf(this) == 1)
                {
                    try{ 
                        socket.close();
                    } catch(Exception ex){}
                    synchronized (clients) {
                        clients.remove(this);
                    }
                }
                try {
                    chat.cierraChats();
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
            
        public void jugar(List<ClientThread> clients) throws IOException, ClassNotFoundException
        {
            tablero.setTurnoJugador(1);
            while(!tablero.haGanadoJugador(1) && !tablero.haGanadoJugador(2) && !tablero.estaTableroLLeno())
            {
                mandarMensajeAJugador("tu turno",clients.get(0).getSocket());
                escribeBin(tablero,clients.get(0).getSocket());
                tablero = leeBin(clients.get(0).getSocket());
                if(!tablero.haGanadoJugador(1) && !tablero.estaTableroLLeno())
                {
                    mandarMensajeAJugador("tu turno",clients.get(1).getSocket());
                    escribeBin(tablero,clients.get(1).getSocket());
                    tablero = leeBin(clients.get(1).getSocket());
                }
            }
            if(tablero.haGanadoJugador(1))
            {
                mandarMensajeAJugador("has ganado",clients.get(0).getSocket());
                mandarMensajeAJugador("has perdido",clients.get(1).getSocket());
                System.out.println("gana jugador 1");
                String nombre = leerMensajeDeJugador(clients.get(0).getSocket());
                System.out.println("el nombre es "+nombre);
                if(victorias.get(nombre) == null)
                    victorias.put(nombre,(long) 0);
                victorias.replace(nombre, victorias.get(nombre)+1);
            }
            if(tablero.haGanadoJugador(2))
            {
                mandarMensajeAJugador("has ganado",clients.get(1).getSocket());
                mandarMensajeAJugador("has perdido",clients.get(0).getSocket());
                System.out.println("gana jugador 2");
                String nombre = leerMensajeDeJugador(clients.get(1).getSocket());
                System.out.println("el nombre es "+nombre);
                if(victorias.get(nombre) == null)
                    victorias.put(nombre,(long) 0);
                victorias.replace(nombre, victorias.get(nombre)+1);
            }
            if(tablero.estaTableroLLeno() && !tablero.haGanadoJugador(1) && !tablero.haGanadoJugador(2))
            {
                mandarMensajeAJugador("empate",clients.get(1).getSocket());
                mandarMensajeAJugador("empate",clients.get(0).getSocket());
                System.out.println("empate");
            }
            ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("registro.csv", false));
            salida.writeObject(victorias);
        }

        public Socket getSocket()
        {
            return socket;
        }
        
        public void yaEsta()
        {
            synchronized(this)
            {
                notify();
            }
        }
    }
}

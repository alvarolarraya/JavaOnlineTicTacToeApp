import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    private HashMap<String,Long> victorias;
    private MenuPrincipal menuPrincipal;
    private Jugador jugador;
    private Tablero tablero;
    private int queJugador;
    private String hostAddr;
    private int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ClienteChat chat;
    
    public Cliente() throws IOException, ClassNotFoundException, InterruptedException
    {
        menuPrincipal = new MenuPrincipal();
        while(!menuPrincipal.leHaDadoANuevaPartida())
        {
            menuPrincipal.yaEsta();
            port = menuPrincipal.getPuerto();
            hostAddr = menuPrincipal.getIpServidor();
            System.out.println("me llega ip "+hostAddr+" y puerto "+port);
            socket = new Socket(hostAddr, port);
            victorias = leeMap();
            menuPrincipal.setPuntuaciones(victorias);
            for(String nombre : victorias.keySet()) {
                System.out.println(nombre + " -> " + victorias.get(nombre));
            }
        }
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        String msg = in.readUTF();
        queJugador = Integer.parseInt(msg);
        tablero = leeBin();
        jugador = new Jugador(tablero,queJugador);
        jugador.mostrarInterfaz();
        chat = new ClienteChat(new Socket(hostAddr, port+1),jugador);
        chat.start();
    }
    
    public void escribeBin(Tablero tablero) throws IOException {
        ObjectOutputStream salida=
                new ObjectOutputStream(
                        socket.getOutputStream());
        salida.writeObject(tablero);
    }
    
    public Tablero leeBin() throws IOException, ClassNotFoundException {
        ObjectInputStream entrada=new ObjectInputStream(
                socket.getInputStream());
        return ((Tablero) entrada.readObject());
    }
    
    public HashMap<String,Long> leeMap() throws IOException, ClassNotFoundException
    {
        ObjectInputStream entrada=new ObjectInputStream(
                socket.getInputStream());
        return ((HashMap<String,Long>) entrada.readObject());
    }
    
    public String leerMensajeDeServidor() throws IOException
    {
        return(in.readUTF());
    }
    
    public void mandarMensajeAServidor(String msg) throws IOException{
        try {
            out.writeUTF(msg);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.flush();
    }
    
    public Jugador getJugador()
    {
        return jugador;
    }
    
    public void setJugador(Jugador jugador)
    {
        this.jugador = jugador;
    }
    
    public Tablero getTablero()
    {
        return tablero;
    }
    
    public void jugar() throws InterruptedException, IOException, ClassNotFoundException
    {
        String msg;
        while(true)
        {
            msg = leerMensajeDeServidor();
            if(!msg.equals("tu turno"))
                break;
            Tablero tablero = leeBin();
            Jugador jugador = getJugador();
            jugador.setTablero(tablero);
            jugador.actualizaInterfaz();
            jugador.avisame();
            setJugador(jugador);
            escribeBin(getJugador().getTablero());
        }
        System.out.println(msg);
        if(msg.equals("has ganado"))
            mandarMensajeAServidor(jugador.getNombre());
        getJugador().cierraGui();
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException
    {
        Cliente cliente = new Cliente();
        cliente.jugar();
    }
}

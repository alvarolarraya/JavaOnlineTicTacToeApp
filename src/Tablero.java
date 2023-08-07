import java.io.Serializable;

public class Tablero implements Serializable{
    private int turnoJugador;
    private int[][] matriz;
    private int ultimoMovimientoFila;
    private int ultimoMovimientoColumna;
    public Tablero()
    {
        turnoJugador = 0;
        matriz = new int[3][3];
        vaciarTablero();
        ultimoMovimientoFila = -1;
        ultimoMovimientoColumna = -1;
    }
    
    public boolean haGanadoJugador(int jugador)
    {
        if(ultimoMovimientoFila == -1 || ultimoMovimientoColumna == -1)
        {
            return false;
        }
        else
        {
            int enRaya = 0;
            for(int i = 0; i < 3; i++)
            {
                if(matriz[ultimoMovimientoFila][i] == jugador)
                    enRaya++;
                if(enRaya == 3)
                    return true;
            }
            enRaya = 0;
            for(int i = 0; i < 3; i++)
            {
                if(matriz[i][ultimoMovimientoColumna] == jugador)
                    enRaya++;
                if(enRaya == 3)
                    return true;
            }
            enRaya = 0;
            for(int i = 0;i < 3; i++)
            {
                if(matriz[i][i] == jugador)
                    enRaya++;
                if(enRaya == 3)
                    return true;
            }
            enRaya = 0;
            for(int i = 0;i < 3; i++)
            {
                if(matriz[i][2-i] == jugador)
                    enRaya++;
                if(enRaya == 3)
                    return true;
            }
        }
        return false;
    }
    public boolean estaTableroLLeno()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                if(matriz[i][j] == 0)
                    return false;
            }
        }
        return true;
    }
    public int[][] vaciarTablero()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                matriz[i][j] = 0;
            }
        }
        return matriz;
    }
    public int[][] getTablero()
    {
        return matriz;
    }
    public void actualizarCasillaTablero(int i,int j,int valor)
    {
        matriz[i][j] = valor;
    }
    public void setUltimoMovimientoFila(int ultimoMovimientoFila)
    {
        this.ultimoMovimientoFila = ultimoMovimientoFila;
    }
    public void setUltimoMovimientoColumna(int ultimoMovimientoColumna)
    {
        this.ultimoMovimientoColumna = ultimoMovimientoColumna;
    }
    
    public int getUltimoMovimientoFila()
    {
        return ultimoMovimientoFila;
    }
    public int getUltimoMovimientoColumna()
    {
        return ultimoMovimientoColumna;
    }
    
    public void setTurnoJugador(int valor)
    {
        turnoJugador = valor;
    }
    
    public int getTurnoJugador()
    {
        return turnoJugador;
    }
}

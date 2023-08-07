
import static java.lang.Thread.sleep;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuPrincipal extends javax.swing.JFrame implements Runnable{
    private Puntuaciones puntuaciones;
    private HashMap<String,Long> victorias;
    private boolean leHaDadoANuevaPartida;
    private ConfigCliente menuConfiguracion;
    private String ipServidor;
    private int puerto;
    public MenuPrincipal() {
        leHaDadoANuevaPartida = false;
        ipServidor = "localhost";
        puerto = 12000;
        setLocationRelativeTo(null);
        initComponents();
        muestraInterfaz();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TRES EN RAYA");

        jButton1.setText("Nueva Partida");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nuevaPartida(evt);
            }
        });

        jButton2.setText("Configuración red");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                configuracionRed(evt);
            }
        });

        jButton3.setText("Puntuación");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                puntuaciones(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nuevaPartida(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nuevaPartida
        synchronized(this)
        {
            setVisible(false);
            leHaDadoANuevaPartida = true;
            notify();
        }
    }//GEN-LAST:event_nuevaPartida
    
    public void setPuntuaciones(HashMap<String,Long> victorias)
    {
        this.victorias = victorias;
        puntuaciones = new Puntuaciones(victorias);
        System.out.println("creo puntuaciones");
    }
    
    @Override
    public void run() {
        menuConfiguracion = new ConfigCliente();
        try {
            menuConfiguracion.yaEsta();
        } catch (InterruptedException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        puerto = menuConfiguracion.getPuerto();
        ipServidor = menuConfiguracion.getIp();
        notify();
    }
    
    private void configuracionRed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_configuracionRed
        Thread thread = new Thread(this);
        thread.start();
    }//GEN-LAST:event_configuracionRed

    private void puntuaciones(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_puntuaciones
        notify();
        try {
            sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread thread = new Thread(this) {
            public void run() { 
                puntuaciones.lanzaPuntuaciones();
            }
        };
        thread.start();
    }//GEN-LAST:event_puntuaciones
    
    public boolean leHaDadoANuevaPartida()
    {
        return leHaDadoANuevaPartida;
    }
    
    public void yaEsta() throws InterruptedException
    {
        synchronized(this)
        {
            wait();
        }
    }

    public String getIpServidor() {
        return ipServidor;
    }

    public int getPuerto() {
        return puerto;
    }
    
    public void muestraInterfaz() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    // End of variables declaration//GEN-END:variables

}

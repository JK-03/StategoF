package Stratego;


import java.util.ArrayList;
import javax.swing.JFrame;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author jenniferbueso
 */
public class UniversoMarvel extends javax.swing.JFrame {
    ArrayList<UsuariosInfo> listaUsuarios;
    ArrayList<LogsInfo> listaLogs;
    ArrayList<UsuariosEliminadosInfo> listaUsuariosEliminados;
    
    //Atributos por Default de la Cuenta
    private boolean modoTutorial = true;
    
    //Atributos de Mostrar
    String usuarioGPerfil;
    
    public UniversoMarvel(ArrayList<UsuariosInfo> listaUsuariosExterna, ArrayList<LogsInfo> listaLogsExterna, String nombreUsuario, ArrayList<UsuariosEliminadosInfo> listaUsuariosEliminadosExterna, boolean ModoJuego) {
        
        // Asignar valores a las variables de instancia
        usuarioGPerfil = nombreUsuario;
        listaUsuarios = listaUsuariosExterna;
        listaLogs = listaLogsExterna;
        listaUsuariosEliminados = listaUsuariosEliminadosExterna;
        modoTutorial = ModoJuego;
        
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 626);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        BotonRanking = new javax.swing.JLabel();
        BotonBatallas = new javax.swing.JLabel();
        BotonRegresar = new javax.swing.JLabel();
        Fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BotonRanking.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FondosInterfaz/BotonRanking.png"))); // NOI18N
        BotonRanking.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BotonRanking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BotonRankingMouseClicked(evt);
            }
        });
        jPanel1.add(BotonRanking, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 270, -1, -1));

        BotonBatallas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FondosInterfaz/BotonBatallas.png"))); // NOI18N
        BotonBatallas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BotonBatallas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BotonBatallasMouseClicked(evt);
            }
        });
        jPanel1.add(BotonBatallas, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 370, 430, -1));

        BotonRegresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FondosInterfaz/BotonRegresar.png"))); // NOI18N
        BotonRegresar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BotonRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BotonRegresarMouseClicked(evt);
            }
        });
        jPanel1.add(BotonRegresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 440, -1, -1));

        Fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FondosInterfaz/UniversoMarvel.png"))); // NOI18N
        jPanel1.add(Fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 610));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonRegresarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BotonRegresarMouseClicked
        MenuPrincipal menuPrincipal = new MenuPrincipal(listaUsuarios,listaLogs, usuarioGPerfil,listaUsuariosEliminados,modoTutorial);
        menuPrincipal.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_BotonRegresarMouseClicked

    private void BotonRankingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BotonRankingMouseClicked
        Ranking ranking = new Ranking(listaUsuarios,listaLogs, usuarioGPerfil,listaUsuariosEliminados,modoTutorial);
        ranking.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_BotonRankingMouseClicked

    private void BotonBatallasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BotonBatallasMouseClicked
        Batallas batallas = new Batallas(this.listaUsuarios,this.listaLogs, usuarioGPerfil,listaUsuariosEliminados,modoTutorial);
        batallas.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_BotonBatallasMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UniversoMarvel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UniversoMarvel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UniversoMarvel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UniversoMarvel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UniversoMarvel(null,null,null,null,true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BotonBatallas;
    private javax.swing.JLabel BotonRanking;
    private javax.swing.JLabel BotonRegresar;
    private javax.swing.JLabel Fondo;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
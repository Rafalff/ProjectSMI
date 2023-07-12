package smi.aplikasi.form.oth;
import com.formdev.flatlaf.FlatClientProperties;
import smi.aplikasi.Application;

/**
 *
 * @author Raven
 */
public class FormTransferAlat extends javax.swing.JPanel {
    String level;
    String nama;
    String idCabang;
    
    public FormTransferAlat(String nama, String level, String idCabang) {       
        initComponents(); 
        setValues(level, nama, idCabang);
    }
    
    public void setValues(String nama, String level, String idCabang) {
        this.nama = nama;
        this.idCabang = idCabang;
        this.level = level;
    // Use the values as needed
    System.out.println("Received values in TransferAlat: " + nama + ", " + level + ", " + idCabang);
    // Pass the values to the FormAlat instance   
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(jButton1)
                .addContainerGap(637, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jButton1)
                .addContainerGap(367, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        setValues(level, nama, idCabang);
    }//GEN-LAST:event_jButton1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setValues(level, nama, idCabang);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}

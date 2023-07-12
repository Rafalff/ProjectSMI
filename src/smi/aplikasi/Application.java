package smi.aplikasi;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import smi.aplikasi.form.LoginForm;
import smi.aplikasi.form.MainForm;
import raven.toast.Notifications;
import smi.koneksi.CKoneksi;
import java.sql.Connection;
import smi.aplikasi.form.oth.FormTransferAlat;

/**
 *
 * @author Raven
 */
public class Application extends javax.swing.JFrame {

    private static Application app;
    private final MainForm mainForm;
    private final LoginForm loginForm;
    private FormTransferAlat formTransferAlat;
    String level = "A";
    String email = "v";
    String password = "a";
    String nama = "a";
    String idCabang = "1";
    Connection con = CKoneksi.getKoneksi();
    boolean main = false;   
    public Application() {
        initComponents();       
        setSize(new Dimension(1200, 768));
        setLocationRelativeTo(null);
        loginForm = new LoginForm(this);
        mainForm = new MainForm(nama, level, idCabang); 
        setContentPane(loginForm);                 
        Notifications.getInstance().setJFrame(this);
        data(level,email,password,nama,idCabang);        
    }
    
    public void data(String level, String email, String password, String nama, String idCabang) {
        this.level = level;
        this.email = email;
        this.password = password;
        this.nama = nama;
        this.idCabang = idCabang;
        mainForm.setValues(nama, level, idCabang);
        System.out.println("Received values: " + level + ", " + email + ", " + password + ", " + nama + ", " + idCabang);        
    }

    public static void showForm(Component component) {
        component.applyComponentOrientation(app.getComponentOrientation());
        app.mainForm.showForm(component);
    }

    public static void login() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.mainForm);
        app.mainForm.applyComponentOrientation(app.getComponentOrientation());
        setSelectedMenu(0, 0);
        app.mainForm.hideMenu();
        SwingUtilities.updateComponentTreeUI(app.mainForm);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void logout() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.loginForm);
        app.loginForm.applyComponentOrientation(app.getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(app.loginForm);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void setSelectedMenu(int index, int subIndex) {
        app.mainForm.setSelectedMenu(index, subIndex);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 719, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 521, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        FlatLaf.registerCustomDefaultsSource("raven.theme");
        FlatDarculaLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
         app = new Application();
         app.setVisible(true);   
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

package smi.aplikasi.form.oth;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import smi.aplikasi.Application;
import smi.koneksi.CKoneksi;

/**
 *
 * @author Raven
 */
public class FormTransferAlat extends javax.swing.JPanel {
    Connection conn = CKoneksi.getKoneksi();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    String sql, JK, kodeAlatOtomatis;
    String Kd, Kode, nol, PR;
    
    String level;
    String nama;
    String idCabang;
    String kodePegawai;
    
    public FormTransferAlat(String nama, String level, String idCabang, String kodePegawai) {       
        initComponents(); 
        setValues(level, nama, idCabang, kodePegawai);
        Cbox();
        ShowDataStok();
        ShowDataTransaksi();
    }
    
    public void setValues(String nama, String level, String idCabang, String kodePegawai) {
        this.nama = nama;
        this.idCabang = idCabang;
        this.level = level;
        this.level = kodePegawai;
    System.out.println("Received values in TransferAlat: " + nama + ", " + level + ", " + idCabang + ", kode :" + kodePegawai); 
    }
    
    private void Cbox() {
        try {
            st = conn.createStatement();
            rs = st.executeQuery("SELECT nama FROM tbl_marketing");

            while (rs.next()) {
                String nama = rs.getString("nama");
                cbMarketing.addItem(nama);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void ShowDataStok() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID STOK");
        model.addColumn("KODE ALAT");
        model.addColumn("NAMA ALAT");
        model.addColumn("HARGA JUAL");
        model.addColumn("STOK");
        model.addColumn("CABANG");

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        try {
            if (tfSearch.getText().isEmpty()) {
                sql = "SELECT tbl_stok.idStok, tbl_stok.KodeAlat, tbl_alat.NamaAlat, tbl_alat.HargaJual, tbl_stok.stock, tbl_cabang.namaCabang FROM tbl_stok INNER JOIN tbl_alat ON tbl_stok.kodeAlat = tbl_alat.KodeAlat INNER JOIN tbl_cabang ON tbl_stok.idCabang = tbl_cabang.idCabang";
            } else {
                String keyword = tfSearch.getText();
                sql = "SELECT tbl_stok.idStok, tbl_stok.KodeAlat, tbl_alat.NamaAlat, tbl_alat.HargaJual, tbl_stok.stock, tbl_cabang.namaCabang FROM tbl_stok INNER JOIN tbl_alat ON tbl_stok.kodeAlat = tbl_alat.KodeAlat INNER JOIN tbl_cabang ON tbl_stok.idCabang = tbl_cabang.idCabang WHERE tbl_alat.NamaAlat LIKE '%" + keyword + "%'";
            }
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("idStok");
                row[1] = rs.getString("KodeAlat");
                row[2] = rs.getString("NamaAlat");
                row[3] = decimalFormat.format(rs.getDouble("HargaJual"));
                row[4] = rs.getInt("stock");
                row[5] = rs.getString("namaCabang");
                model.addRow(row);
            }
            jTable2.setModel(model);
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    private void ShowDataTransaksi() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID TRANSAKSI");
        model.addColumn("TANGGAL");
        model.addColumn("KODE ALAT");
        model.addColumn("NAMA ALAT");
        model.addColumn("HARGA JUAL");
        model.addColumn("CABANG");
        model.addColumn("SUBTOTAL");
        model.addColumn("MARKETING");

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        try {
            if (tfSearch2.getText().isEmpty()) {
                sql = "SELECT idTransaksi, tanggalTransaksi, kodeAlat, namaAlat, harga, idCabang, subTotal, namaUser FROM tbl_transaksi";
            } else {
                String keyword = tfSearch2.getText();
                sql = "SELECT idTransaksi, tanggalTransaksi, kodeAlat, namaAlat, harga, idCabang, subTotal, namaUser FROM tbl_transaksi WHERE namaAlat LIKE '%" + keyword + "%'";
            }
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("idTransaksi");
                row[1] = rs.getString("tanggalTransaksi");
                row[2] = rs.getString("kodeAlat");
                row[3] = rs.getString("namaAlat");
                row[4] = decimalFormat.format(rs.getDouble("harga"));
                row[5] = rs.getString("idCabang");
                row[6] = decimalFormat.format(rs.getDouble("subTotal"));
                row[7] = rs.getString("namaUser");
                model.addRow(row);
            }
            jTable1.setModel(model);
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    
    private void AddTransaksi(){
        int row = jTable2.getSelectedRow();
        tfKode.setText(jTable2.getValueAt(row, 1).toString());
        tfNama.setText(jTable2.getValueAt(row, 2).toString());       
    }
    
    private void kodePenjualanOtomatis() {
        java.util.Date tanggalTransaksi = jdTanggal.getDate(); // Get the selected date from the JDateChooser
        String kodePenjualan = "";

        try {
            st = conn.createStatement();
            sql = "SELECT COUNT(*) AS total FROM tbl_transaksi";
            rs = st.executeQuery(sql);

            if (rs.next()) {
                int total = rs.getInt("total");
                int nextNomorUrut = total + 1;

                String kosong = "";
                switch (String.valueOf(nextNomorUrut).length()) {
                    case 1:
                        kosong = "000";
                        break;
                    case 2:
                        kosong = "00";
                        break;
                    case 3:
                        kosong = "0";
                        break;
                    case 4:
                        kosong = "";
                        break;
                    default:
                        break;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = sdf.format(tanggalTransaksi);

                kodePenjualan = "ID" + formattedDate + kosong + nextNomorUrut;
            }
            tfIdTransaksi.setText(kodePenjualan);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
        
    private void InputData() {
    String idTransaksi = tfIdTransaksi.getText();
    java.util.Date tanggalTransaksi = jdTanggal.getDate();
    String kodeAlat = tfKode.getText();
    String namaAlat = tfNama.getText();
    String hargaJual = jTable2.getValueAt(jTable2.getSelectedRow(), 3).toString().replace(",", "");
    String namaUser = cbMarketing.getSelectedItem().toString();
    String jumlah = tfJumlah.getText();

    // Format tanggalTransaksi as yyyyMMdd
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String formattedTanggalTransaksi = sdf.format(tanggalTransaksi);

    // Print the values before inserting
    System.out.println("idTransaksi: " + idTransaksi);
    System.out.println("tanggalTransaksi: " + formattedTanggalTransaksi);
    System.out.println("kodeAlat: " + kodeAlat);
    System.out.println("namaAlat: " + namaAlat);
    System.out.println("hargaJual: " + hargaJual);
    System.out.println("namaUser: " + namaUser);
    System.out.println("jumlah: " + jumlah);

    try {
        // Query to retrieve idCabang based on namaMarketing from tbl_marketing
        String getCabangIdQuery = "SELECT idCabang FROM tbl_marketing WHERE nama = ?";
        PreparedStatement getCabangIdStmt = conn.prepareStatement(getCabangIdQuery);
        getCabangIdStmt.setString(1, namaUser);
        ResultSet rs = getCabangIdStmt.executeQuery();

        if (rs.next()) {
            String idCabang = rs.getString("idCabang");

            // Query to retrieve kodeUser based on nama from tbl_user
            String getKodeUserQuery = "SELECT kodeUser FROM tbl_user WHERE nama = ?";
            PreparedStatement getKodeUserStmt = conn.prepareStatement(getKodeUserQuery);
            getKodeUserStmt.setString(1, namaUser);
            ResultSet kodeUserRS = getKodeUserStmt.executeQuery();

            if (kodeUserRS.next()) {
                String kodeUser = kodeUserRS.getString("kodeUser");
                String sql = "INSERT INTO tbl_transaksi (idTransaksi, tanggalTransaksi, kodeAlat, namaAlat, harga, idCabang, kodeUser, jumlah, namaUser) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, idTransaksi);
                ps.setString(2, formattedTanggalTransaksi);
                ps.setString(3, kodeAlat);
                ps.setString(4, namaAlat);
                ps.setString(5, hargaJual);
                ps.setString(6, idCabang);
                ps.setString(7, kodeUser);
                ps.setString(8, jumlah);
                ps.setString(9, namaUser);

                int rowsInserted = ps.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Data inserted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to insert data!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Failed to retrieve kodeUser!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Failed to retrieve idCabang!");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        e.printStackTrace();
    }
}










    
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tfKode = new javax.swing.JTextField();
        tfNama = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tfJumlah = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfTotal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbMarketing = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        tfSearch = new javax.swing.JTextField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel9 = new javax.swing.JLabel();
        jdTanggal = new com.toedter.calendar.JDateChooser();
        tfIdTransaksi = new javax.swing.JTextField();
        tfSearch2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        tfKode.setEditable(false);

        tfNama.setEditable(false);

        jLabel2.setText("Kode alat");

        jLabel3.setText("Nama alat");

        jLabel4.setText("Jumlah");

        tfJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfJumlahKeyReleased(evt);
            }
        });

        jLabel5.setText("Total");

        tfTotal.setEditable(false);

        jLabel6.setText("Marketing");

        jLabel7.setText("Tanggal");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jLabel8.setText("Data Transaksi");

        jToggleButton1.setText("Cari");

        jLabel9.setText("Tambah data transaksi");

        jdTanggal.setDateFormatString("yyyy-MM-dd");
        jdTanggal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jdTanggalMouseReleased(evt);
            }
        });

        tfIdTransaksi.setEditable(false);
        tfIdTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfIdTransaksiActionPerformed(evt);
            }
        });

        jButton2.setText("jButton1");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(10, 10, 10)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tfIdTransaksi)
                                .addComponent(cbMarketing, 0, 179, Short.MAX_VALUE)
                                .addComponent(tfKode)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4)
                                .addComponent(tfJumlah)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel3)
                                            .addComponent(tfNama, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                                            .addComponent(jLabel5)
                                            .addComponent(tfTotal))
                                        .addComponent(jdTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, Short.MAX_VALUE))))
                        .addComponent(jLabel9)
                        .addComponent(tfSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jToggleButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jdTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbMarketing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfIdTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tfSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        kodePenjualanOtomatis();
    }//GEN-LAST:event_jButton1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        kodePenjualanOtomatis();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tfIdTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfIdTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfIdTransaksiActionPerformed

    private void jdTanggalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdTanggalMouseReleased
      kodePenjualanOtomatis();
    }//GEN-LAST:event_jdTanggalMouseReleased

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        AddTransaksi();
    }//GEN-LAST:event_jTable2MouseClicked

    private void tfJumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfJumlahKeyReleased
        int row = jTable2.getSelectedRow();
        String harga = jTable2.getValueAt(row, 3).toString();
        String jumlah = tfJumlah.getText();

// Remove commas from the harga string
        harga = harga.replace(",", "");

// Perform the calculation
        double hargaJual = Double.parseDouble(harga);
        int jumlahInt = Integer.parseInt(jumlah);
        double total = hargaJual * jumlahInt;

// Format the total with commas
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedTotal = decimalFormat.format(total);

// Set the formatted total to a textField (tfTotal)
        tfTotal.setText(formattedTotal);
    }//GEN-LAST:event_tfJumlahKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       InputData();
       ShowDataTransaksi();
       ShowDataStok();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbMarketing;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JToggleButton jToggleButton1;
    private com.toedter.calendar.JDateChooser jdTanggal;
    private javax.swing.JTextField tfIdTransaksi;
    private javax.swing.JTextField tfJumlah;
    private javax.swing.JTextField tfKode;
    private javax.swing.JTextField tfNama;
    private javax.swing.JTextField tfSearch;
    private javax.swing.JTextField tfSearch2;
    private javax.swing.JTextField tfTotal;
    // End of variables declaration//GEN-END:variables
}

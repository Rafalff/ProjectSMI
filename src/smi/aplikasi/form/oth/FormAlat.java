package smi.aplikasi.form.oth;

import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import smi.koneksi.CKoneksi;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Raven
 */
public class FormAlat extends javax.swing.JPanel {

    Connection conn = CKoneksi.getKoneksi();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;
    String sql, JK, kodeAlatOtomatis;
    String Kd, Kode, nol, PR;
    String[] tipeAlat = {"Shoulder", "Arm", "Feet", "Knee", "e"};
    String[] Ukuran = {"-","S", "M", "XL", "XXL", "XXXL"};
    String[] Sisi = {"-","-RT", "-LT"};
    
    HashMap<String, Integer> startingValues = new HashMap<>();

// Add more types and their starting values as needed

    
    public FormAlat() {
        initComponents();
        showDataAlat();
        Prov();
        ComboAlat();
        KodeAlatOtomatis();
        btnUbah.setEnabled(false);
    }
    
    public void setValues(String level, String idCabang) {
        // Use the values as needed
        System.out.println("Received values in FormAlat: " + level + ", " + idCabang);

        if (level.equals("Admin")) {
            System.out.println("Oke");
        } else if (level.equals("Produksi")) {
            System.out.println("Tidak Oke");
        }
    }


    private void showDataAlat() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NO");
        model.addColumn("KODE ALAT");
        model.addColumn("NAMA ALAT");
        model.addColumn("MERK");
        model.addColumn("HARGA JUAL");
        model.addColumn("SETOR MSI");
        model.addColumn("POKOK");
        model.addColumn("PROFIT MSI");

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        try {
            if (tfSearch.getText().isEmpty()) {
                sql = "SELECT * FROM tbl_alat";
            } else {
                sql = "SELECT * FROM tbl_alat WHERE NamaAlat LIKE '%" + tfSearch.getText() + "%'";
            }
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("No");
                row[1] = rs.getString("KodeAlat");
                row[2] = rs.getString("NamaAlat");
                row[3] = rs.getString("Merk");
                row[4] = decimalFormat.format(rs.getDouble("HargaJual"));
                row[5] = decimalFormat.format(rs.getDouble("HargaSetorSMI"));
                row[6] = decimalFormat.format(rs.getDouble("Pokok"));
                row[7] = decimalFormat.format(rs.getDouble("ProfitSMI"));
                model.addRow(row);
            }
            jTable1.setModel(model);
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    private void KodeAlatOtomatis() {
        String kodeAlatOtomatis = "";
        try {
            st = conn.createStatement();
            sql = "SELECT * FROM tbl_alat ORDER BY KodeAlat DESC";
            rs = st.executeQuery(sql);

            // Store the maximum numeric value for each combination of equipment type and item name
            Map<String, Integer> maxValues = new HashMap<>();

            while (rs.next()) {
                String kodeAlat = rs.getString("KodeAlat");
                String tipe = kodeAlat.substring(2, 4).toUpperCase(); // Extract the two-letter equipment type
                String namaAlat = rs.getString("NamaAlat");

                String key = tipe + "-" + namaAlat;
                int numericValue = Integer.parseInt(kodeAlat.substring(4, 7)); // Extract the numeric part

                // Update the maximum numeric value for the combination of equipment type and item name
                if (!maxValues.containsKey(key) || numericValue > maxValues.get(key)) {
                    maxValues.put(key, numericValue);
                }
            }

            // Mengambil nilai dari komponen GUI
            String merk = tfMerk.getText();
            String merkInitial = "";
            if (!merk.isEmpty()) {
                merkInitial = String.valueOf(merk.charAt(0)); // Ambil huruf pertama dari merk
            }

            String tipe = cbTipe.getSelectedItem().toString();
            if (!tipe.equals("-")) {
                tipe = tipe.substring(0, 2).toUpperCase(); // Get the first two letters and convert to uppercase
            }

            String namaAlat = tfNama.getText().trim(); // Get the value from tfNama and trim leading/trailing spaces
            String ukuran = cbUkuran.getSelectedItem().toString();
            String sisi = cbSisi.getSelectedItem().toString();

            // Generate the code based on the uniqueness of the combination of equipment type and item name
            String key = tipe + "-" + namaAlat;
            int incrementedValue;
            if (maxValues.containsKey(key)) {
                if (tipe.equals(cbTipe.getSelectedItem().toString().substring(0, 2).toUpperCase()) && !namaAlat.equals(tfNama.getText().trim())) {
                    // Increment the numeric value for the same tipe but different name
                    incrementedValue = maxValues.get(key) + 1;
                } else {
                    // Reuse the existing numeric value for the same tipe and name
                    incrementedValue = maxValues.get(key);
                }
            } else {
                // Start from 001 for a new combination
                incrementedValue = 1;
            }

            String incrementedPart = String.format("%03d", incrementedValue); // Format incremented value with leading zeros

            // Menghasilkan kode alat otomatis
            kodeAlatOtomatis = merkInitial + "-" + tipe + incrementedPart + sisi + "-" + ukuran;

            tfKode.setText(kodeAlatOtomatis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void InputData() {
        String formattedValueHargaJual = tfHargaJual.getText();
        String cleanValueHargaJual = formattedValueHargaJual.replace(",", "");
        BigDecimal decimalValueHargaJual = new BigDecimal(cleanValueHargaJual);

        String formattedValuePokok = tfPokok.getText();
        String cleanValuePokok = formattedValuePokok.replace(",", "");
        BigDecimal decimalValuePokok = new BigDecimal(cleanValuePokok);

        try {
            String sql = "INSERT INTO tbl_alat (KodeAlat, NamaAlat, Merk, HargaJual, Pokok) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfKode.getText());
            ps.setString(2, tfNama.getText());
            ps.setString(3, tfMerk.getText());
            ps.setBigDecimal(4, decimalValueHargaJual);
            ps.setBigDecimal(5, decimalValuePokok);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Data inserted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to insert data!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateData() {
        String formattedValueHargaJual = tfHargaJual.getText();
        String formattedValuePokok = tfPokok.getText();

        try {
            BigDecimal decimalValueHargaJual = new BigDecimal(formattedValueHargaJual.replace(",", ""));
            BigDecimal decimalValuePokok = new BigDecimal(formattedValuePokok.replace(",", ""));

            // Check if the values are valid numbers
            if (isValidNumber(decimalValueHargaJual) && isValidNumber(decimalValuePokok)) {
                BigDecimal profit = decimalValueHargaJual.subtract(decimalValuePokok);

                // Check if Profit is negative
                if (profit.compareTo(BigDecimal.ZERO) >= 0) {
                    String sql = "UPDATE tbl_alat SET NamaAlat=?, Merk=?, HargaJual=?, Pokok=? WHERE KodeAlat=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, tfNama.getText());
                    ps.setString(2, tfMerk.getText());
                    ps.setBigDecimal(3, decimalValueHargaJual);
                    ps.setBigDecimal(4, decimalValuePokok);
                    ps.setString(5, tfKode.getText());

                    int rowsUpdated = ps.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Data updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update data!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input. Profit cannot be negative!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid input. Only numbers are allowed for HargaJual and Pokok.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Only numbers are allowed for HargaJual and Pokok.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean isValidNumber(BigDecimal value) {
        // Check if the value is positive or non-negative based on your requirements
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }


  
    private void DeleteData() {
        try {
            String sql = "DELETE FROM tbl_alat WHERE KodeAlat = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfKode.getText());

            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Data deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete data!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void Prov(){
        tfNama.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Alat");
    }
    
    private void ComboAlat() {
    for (String item : tipeAlat) {
        cbTipe.addItem(item); // Add items from the tipeAlat array to the combo box
    }
    if (tipeAlat.length > 0) {
        cbTipe.setSelectedIndex(0); // Set the selected index to the first item
    }

    for (String item1 : Sisi) {
        cbSisi.addItem(item1); // Add items from the Sisi array to the combo box
    }
    if (Sisi.length > 0) {
        cbSisi.setSelectedIndex(0); // Set the selected index to the first item
    }

    for (String item2 : Ukuran) {
        cbUkuran.addItem(item2); // Add items from the Ukuran array to the combo box
    }
    if (Ukuran.length > 0) {
        cbUkuran.setSelectedIndex(0); // Set the selected index to the first item
    }

        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tfSearch = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        tfKode = new javax.swing.JTextField();
        tfNama = new javax.swing.JTextField();
        cbTipe = new javax.swing.JComboBox<>();
        tfPokok = new javax.swing.JTextField();
        tfHargaJual = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        tfMerk = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnUbah = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        cbUkuran = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cbSisi = new javax.swing.JComboBox<>();
        btnUpdate = new javax.swing.JButton();
        btnBatal1 = new javax.swing.JButton();

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
        jTable1.setAutoscrolls(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        tfSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSearchActionPerformed(evt);
            }
        });

        jButton1.setText("Cari");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tfKode.setEditable(false);

        tfNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNamaActionPerformed(evt);
            }
        });

        cbTipe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cbTipeMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cbTipeMouseReleased(evt);
            }
        });
        cbTipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTipeActionPerformed(evt);
            }
        });

        tfPokok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPokokActionPerformed(evt);
            }
        });

        tfHargaJual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfHargaJualActionPerformed(evt);
            }
        });

        btnTambah.setText("Tambah alat");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        tfMerk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfMerkActionPerformed(evt);
            }
        });
        tfMerk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfMerkKeyReleased(evt);
            }
        });

        jLabel1.setText("Kode Alat");

        jLabel2.setText("Nama Alat");

        jLabel3.setText("Harga Jual");

        jLabel4.setText("Merk");

        jLabel5.setText("Tipe Alat");

        jLabel6.setText("Pokok");

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jLabel7.setText("Ukuran");

        cbUkuran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cbUkuranMouseReleased(evt);
            }
        });
        cbUkuran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUkuranActionPerformed(evt);
            }
        });

        jLabel8.setText("Sisi");

        cbSisi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cbSisiMouseReleased(evt);
            }
        });
        cbSisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSisiActionPerformed(evt);
            }
        });

        btnUpdate.setText("update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnBatal1.setText("Hapus");
        btnBatal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatal1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tfKode, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tfHargaJual, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tfPokok, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel8))
                            .addComponent(cbSisi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnTambah))
                                    .addComponent(cbUkuran, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel4)
                                    .addComponent(tfMerk, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                                    .addComponent(btnUbah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnBatal1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(cbTipe, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah)
                    .addComponent(btnUbah)
                    .addComponent(btnBatal1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfMerk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfHargaJual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUpdate)
                            .addComponent(btnBatal))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbTipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfPokok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbUkuran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tfNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNamaActionPerformed

    private void tfSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfSearchActionPerformed

    private void tfPokokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPokokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPokokActionPerformed

    private void tfHargaJualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfHargaJualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfHargaJualActionPerformed

    private void tfMerkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfMerkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfMerkActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showDataAlat();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        tfKode.setText(jTable1.getValueAt(row, 1).toString());
        tfNama.setText(jTable1.getValueAt(row, 2).toString());
        tfMerk.setText(jTable1.getValueAt(row, 3).toString());
        tfHargaJual.setText(jTable1.getValueAt(row, 4).toString());
        tfPokok.setText(jTable1.getValueAt(row, 6).toString());
        btnTambah.setEnabled(false);
        btnUbah.setEnabled(true);
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        updateData();
        showDataAlat();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        btnTambah.setEnabled(true);
        tfKode.setText("");
        tfNama.setText("");
        tfMerk.setText("");
        tfHargaJual.setText("");
        tfPokok.setText("");
        btnUbah.setEnabled(false);
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
          KodeAlatOtomatis();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
       InputData();
       showDataAlat();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnBatal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatal1ActionPerformed
       DeleteData();
       KodeAlatOtomatis();
    }//GEN-LAST:event_btnBatal1ActionPerformed

    private void tfMerkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfMerkKeyReleased
        KodeAlatOtomatis();
    }//GEN-LAST:event_tfMerkKeyReleased

    private void cbTipeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbTipeMouseReleased

    }//GEN-LAST:event_cbTipeMouseReleased

    private void cbSisiMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbSisiMouseReleased
       // TODO add your handling code here:
    }//GEN-LAST:event_cbSisiMouseReleased

    private void cbUkuranMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbUkuranMouseReleased
    // TODO add your handling code here:
    }//GEN-LAST:event_cbUkuranMouseReleased

    private void cbTipeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbTipeMouseClicked

    }//GEN-LAST:event_cbTipeMouseClicked

    private void cbTipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipeActionPerformed
      KodeAlatOtomatis();
    }//GEN-LAST:event_cbTipeActionPerformed

    private void cbSisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSisiActionPerformed
      KodeAlatOtomatis();
    }//GEN-LAST:event_cbSisiActionPerformed

    private void cbUkuranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUkuranActionPerformed
      KodeAlatOtomatis();
    }//GEN-LAST:event_cbUkuranActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBatal1;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cbSisi;
    private javax.swing.JComboBox<String> cbTipe;
    private javax.swing.JComboBox<String> cbUkuran;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField tfHargaJual;
    private javax.swing.JTextField tfKode;
    private javax.swing.JTextField tfMerk;
    private javax.swing.JTextField tfNama;
    private javax.swing.JTextField tfPokok;
    private javax.swing.JTextField tfSearch;
    // End of variables declaration//GEN-END:variables
}

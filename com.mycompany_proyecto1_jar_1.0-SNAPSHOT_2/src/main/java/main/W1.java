package main;

import primitivas.*;
import classes.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class W1 extends javax.swing.JFrame {
    public Semaphore onPlay;
    public Semaphore onPlayClock;
    public List readyList;
    public List allProcessList;
    public UtilityGraph w2;
    
    private void loadConfig() {
        String filePath = "configuracion.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            if (line != null) {
                String[] values = line.split(",");
                if (values.length < 2) {
                    System.out.println("Error: El archivo de configuración no tiene el formato correcto.");
                    return;
                }

                int selectedAlgorithm = Integer.parseInt(values[0]);
                int numberOfInstructions = Integer.parseInt(values[1]);

                selectDispatcher.setSelectedIndex(selectedAlgorithm);
                timeSlider.setValue(numberOfInstructions);
                this.instructionTime.setText(this.timeSlider.getValue() + " ms");
                System.out.println("Configuración cargada desde CSV.");
            }
        } catch (IOException e) {
            System.out.println("No se encontró el archivo de configuración. Se usarán valores por defecto.");
        }
    }
    
    public W1(Semaphore onPlay, Semaphore onPlay1, List readyList, List allProcess) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.onPlay = onPlay;
        this.onPlayClock = onPlay1;
        this.readyList = readyList;
        this.allProcessList = allProcess;
        w2 = new UtilityGraph("CPU usage");
        
        loadConfig();
        this.updatePCBs();
    }

    public W1() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }
    
    public synchronized void createNewProcess(List list, String name, String type, int duration){
        ProcessImage newProcess = new ProcessImage(list, type, readyList.getSize(), "ready", name, 1, 0, duration);
        readyList.appendLast(newProcess);
        allProcessList.appendLast(newProcess);
        updatePCBs();
    }

    public void updatePCBs(){
        NodoList pAux = readyList.getHead();
        String display = "";
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += this.makeString(process);
            pAux = pAux.getpNext();
        }
        this.updateProcess(display);
    }

    private String makeString(ProcessImage currentProcess){
        String display = Dispatcher.makeString(currentProcess);
        return display;
    }

    public void updateCPU(String input){
        this.cpuTextArea.setText(input);
    }

    public synchronized void updateDataset(int instruction, String type){
        w2.updateDataset(instruction, type);
    }
    
    public int getSelectAlgorithm(){
        return this.selectDispatcher.getSelectedIndex();
    }

    public void updateReady(String text){
        this.readyTextArea.setText(text);
    } 

    public void updateBlock(String text){
        this.blockedTextArea.setText(text);
    }

    public void updateExit(String text){
        this.exitTextArea.setText(text);
    }

    public void updateSuspended(String text){
        this.suspendedTextArea.setText(text);
    }
    
    public void updateProcess(String text){
        this.pcbTextArea.setText(text);
    }

    public int getTime(){
        return this.timeSlider.getValue();
    }

    public void updateCycle(int in){
        this.cycleTextField.setText(in + "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        selectDispatcher = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        readyTextArea = new javax.swing.JTextArea();
        playButton = new javax.swing.JButton();
        createProcessButton = new javax.swing.JButton();
        timeSlider = new javax.swing.JSlider();
        instructionTime = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cpuTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        blockedTextArea = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        exitTextArea = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        pcbTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        showUsageButton = new javax.swing.JButton();
        cycleTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        suspendedTextArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        selectDispatcher.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "Round Robin", "SPN", "SRT", "HRR", "Feedback" }));
        jPanel1.add(selectDispatcher, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 230, 190, -1));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setText("CPU");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, -1));

        readyTextArea.setEditable(false);
        readyTextArea.setColumns(20);
        readyTextArea.setRows(5);
        jScrollPane1.setViewportView(readyTextArea);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 150, 120));

        playButton.setText("Play");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        jPanel1.add(playButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 440, 140, -1));

        createProcessButton.setText("Create Process");
        createProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProcessButtonActionPerformed(evt);
            }
        });
        jPanel1.add(createProcessButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 400, 140, -1));

        timeSlider.setMaximum(5000);
        timeSlider.setMinimum(1);
        timeSlider.setValue(5000);
        timeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeSliderStateChanged(evt);
            }
        });
        jPanel1.add(timeSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 80, -1, -1));

        instructionTime.setText("5000 ms");
        jPanel1.add(instructionTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, 90, -1));

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 320, 140, -1));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setText("Exit");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 330, -1, -1));

        jLabel3.setText("Instruction Time");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 60, -1, 20));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 10, 460));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setText("Ready");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, -1, -1));

        cpuTextArea.setEditable(false);
        cpuTextArea.setColumns(20);
        cpuTextArea.setRows(8);
        jScrollPane2.setViewportView(cpuTextArea);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 110, 100));

        blockedTextArea.setEditable(false);
        blockedTextArea.setColumns(20);
        blockedTextArea.setRows(5);
        jScrollPane3.setViewportView(blockedTextArea);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 210, 150, 110));

        exitTextArea.setEditable(false);
        exitTextArea.setColumns(20);
        exitTextArea.setRows(5);
        jScrollPane4.setViewportView(exitTextArea);

        jPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 360, 150, 90));

        pcbTextArea.setEditable(false);
        pcbTextArea.setColumns(20);
        pcbTextArea.setRows(5);
        jScrollPane5.setViewportView(pcbTextArea);

        jPanel1.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 50, 150, 400));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel5.setText("PCBs");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, -1, -1));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel6.setText("Blocked");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 180, -1, -1));

        showUsageButton.setText("Show Usage");
        showUsageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUsageButtonActionPerformed(evt);
            }
        });
        jPanel1.add(showUsageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 280, 140, -1));

        cycleTextField.setEditable(false);
        cycleTextField.setText("0");
        jPanel1.add(cycleTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 20, 110, -1));

        jLabel7.setText("Cycle:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, -1, -1));

        jLabel8.setText("Scheduling Algorithm");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 210, -1, 20));

        suspendedTextArea.setEditable(false);
        suspendedTextArea.setColumns(20);
        suspendedTextArea.setRows(5);
        jScrollPane6.setViewportView(suspendedTextArea);

        jPanel1.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 150, 250));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel9.setText("Suspended");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        onPlay.release(1);
        onPlayClock.release();
        ProcessImageCSV.saveProcessesToCSV(readyList, "procesos.csv");
        this.createProcessButton.setEnabled(false);
        this.playButton.setEnabled(false);
    }//GEN-LAST:event_playButtonActionPerformed

    private void createProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProcessButtonActionPerformed
        CreateProcess newProcess = new CreateProcess(this);
        newProcess.setVisible(true);
    }//GEN-LAST:event_createProcessButtonActionPerformed

    private void timeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_timeSliderStateChanged
        this.instructionTime.setText(this.timeSlider.getValue() + " ms");
    }//GEN-LAST:event_timeSliderStateChanged

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        int selectedAlgorithm = selectDispatcher.getSelectedIndex();
        int numberOfInstructions = timeSlider.getValue();

        String filePath = "configuracion.csv"; 

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(selectedAlgorithm + "," + numberOfInstructions);
            writer.newLine();
            
            JOptionPane.showMessageDialog(this, "Configuración guardada en " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la configuración");
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void showUsageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUsageButtonActionPerformed
        w2.setSize(800, 400);
        w2.setLocationRelativeTo(null);
        w2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        w2.setVisible(true);
    }//GEN-LAST:event_showUsageButtonActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(W1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                W1 w1 = new W1();
                w1.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea blockedTextArea;
    private javax.swing.JTextArea cpuTextArea;
    private javax.swing.JButton createProcessButton;
    private javax.swing.JTextField cycleTextField;
    private javax.swing.JTextArea exitTextArea;
    private javax.swing.JLabel instructionTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea pcbTextArea;
    private javax.swing.JButton playButton;
    private javax.swing.JTextArea readyTextArea;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox<String> selectDispatcher;
    private javax.swing.JButton showUsageButton;
    private javax.swing.JTextArea suspendedTextArea;
    private javax.swing.JSlider timeSlider;
    // End of variables declaration//GEN-END:variables
}
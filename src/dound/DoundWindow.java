/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dound;

import javax.sound.sampled.*;
import com.sun.speech.freetts.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaFlacEncoder.FLAC_FileEncoder;

/**
 *
 * @author arefaey
 */
public class DoundWindow extends javax.swing.JFrame {

    /**
     * Creates new form DoundWindow
     */
    public DoundWindow() {
        initComponents();
        this.setExtendedState(this.MAXIMIZED_BOTH);
//        this.setUndecorated(true);
    }
    // path of the wav file
    File wavFile = new File("/tmp/RecordAudio.wav");
    File flacFile = new File("/tmp/RecordAudio.flac");
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    // the line from which audio data is captured
    TargetDataLine line;
    private String speaktext;

    void convertWavToFlac(File wavFile) {
        System.out.println("Started Flac encoding...");
        FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
        flacEncoder.encode(wavFile, flacFile);
        System.out.println("Flac encoding done");
    }

    void stt(File flacFile) {
        File output = flacFile;
        try {
            System.out.println(new Date().toString().substring(11, 20) + " =====> Send google api Start");

//            StringBuilder sb = new StringBuilder("https://www.google.com/speech-api/v1/recognize?client=chromium&lang=en-US&maxresults=10");
            StringBuilder sb = new StringBuilder("https://www.google.com/speech-api/v1/recognize?client=chromium&lang=en-US");
            // sb.append("&lang=en");
//            sb.append("&pfilter=0");
            // sb.append("&maxresults=2");
            URL url1 = new URL(sb.toString());
            URLConnection urlConn = url1.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "audio/x-flac; rate=16000");
            OutputStream outputStream1 = urlConn.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(output);
            byte[] buffer = new byte[1024];
            while ((fileInputStream.read(buffer, 0, 256)) != -1) {
                outputStream1.write(buffer, 0, 256);
                outputStream1.flush();
            }
            fileInputStream.close();
            outputStream1.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String response2 = br.readLine();
            br.close();

//        input.delete();
//            output.delete();
            System.out.println(new Date().toString().substring(11, 20) + " =====> receive google api result");
            String resp[] = response2.split("utterance");
            for (int k = 0; k < resp.length; k++) {
                resp[k] = resp[k].replaceAll("\":\"", "").replace("\"},{\"", "").replace("\"}]}", "");
                if (k == 1) {
                    String temp[] = resp[k].split("\",\"confidence\":");
                    System.out.println("Confidence :" + temp[1].replace("},{\"", ""));
                    System.out.println(k + " . " + temp[0]);
                    sttLabel.setText(temp[0]);
                } else if (k == resp.length - 1) {
                    System.out.println(k + " . " + resp[k]);
                    sttLabel.setText(resp[k]);
                } else if (k != 0) {
                    System.out.println(k + " . " + resp[k]);
                    sttLabel.setText(resp[k]);
                }

            }
            //out.println(response2+"<br>");
//          System.out.println(sampleRate);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DoundWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DoundWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doSpeak(String speak, String voice) {
        speaktext = speak;
        try {
            VoiceManager voiceManager = VoiceManager.getInstance();
            Voice voices = voiceManager.getVoice(voice);
            Voice sp = null;
            if (voices != null) {
                sp = voices;
            } else {
                System.out.println("No Voice Available");
            }
            // ==================================================
            sp.allocate();
            sp.speak(speaktext);
            sp.deallocate();
            // ==================================================
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 22050;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }
    /**
     * Captures the sound and record into a WAV file
     */
    Thread recorder;

    void start() {
        recorder = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AudioFormat format = getAudioFormat();
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                    // checks if system supports the data line
                    if (!AudioSystem.isLineSupported(info)) {
                        System.out.println("Line not supported");
                        System.exit(0);
                    }
                    line = (TargetDataLine) AudioSystem.getLine(info);
                    line.open(format);
                    line.start();   // start capturing

                    System.out.println("Start capturing...");

                    AudioInputStream ais = new AudioInputStream(line);

                    System.out.println("Start recording...");

                    // start recording
                    AudioSystem.write(ais, fileType, wavFile);

                } catch (LineUnavailableException ex) {
                    ex.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });
        recorder.start();
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sttLabel = new javax.swing.JLabel();
        sttTextField = new javax.swing.JTextField();
        sayButton = new javax.swing.JButton();
        recordButton = new javax.swing.JButton();
        close = new javax.swing.JButton();
        logoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sttLabel.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N

        sttTextField.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        sttTextField.setText("Add some to text to say it!");
        sttTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sttTextFieldActionPerformed(evt);
            }
        });

        sayButton.setText("Say it!");
        sayButton.setToolTipText("");
        sayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sayButtonActionPerformed(evt);
            }
        });

        recordButton.setText("Record");
        recordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordButtonActionPerformed(evt);
            }
        });

        close.setText("close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        logoLabel.setIcon(new javax.swing.ImageIcon("/home/arefaey/Desktop/994601_410463429079433_1032719732_n.jpg")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sttLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(close)
                    .addComponent(logoLabel)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(recordButton)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(sttTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13)
                            .addComponent(sayButton))))
                .addContainerGap(322, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sttLabel)
                .addGap(50, 50, 50)
                .addComponent(recordButton)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sttTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sayButton))
                .addGap(75, 75, 75)
                .addComponent(logoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(close)
                .addGap(36, 36, 36))
        );

        sttLabel.getAccessibleContext().setAccessibleName("stt");
        sayButton.getAccessibleContext().setAccessibleName("sayButton");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sayButtonActionPerformed
        String txt = sttTextField.getText();
        this.doSpeak(txt, "kevin");
        System.out.println(txt);
    }//GEN-LAST:event_sayButtonActionPerformed

    private void recordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordButtonActionPerformed
        if (recordButton.getText().startsWith("Record")) {
            start();
            recordButton.setText("Stop");
        } else {
            recorder.stop();
            finish();
            recordButton.setText("Record");
            //TODO
            convertWavToFlac(wavFile);
            //Send to google Speech API
            stt(flacFile);
            //Set Lable text with results
        }
    }//GEN-LAST:event_recordButtonActionPerformed

    private void sttTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sttTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sttTextFieldActionPerformed

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        System.exit(0);
    }//GEN-LAST:event_closeActionPerformed

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
            java.util.logging.Logger.getLogger(DoundWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DoundWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DoundWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DoundWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DoundWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton close;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton recordButton;
    private javax.swing.JButton sayButton;
    private javax.swing.JLabel sttLabel;
    private javax.swing.JTextField sttTextField;
    // End of variables declaration//GEN-END:variables
}

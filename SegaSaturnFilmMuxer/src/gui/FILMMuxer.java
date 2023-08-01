package gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import sega.film.FILMUtility;
import sega.film.FILMfile;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

public class FILMMuxer {

    private JFrame frame;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                    FILMMuxer window = new FILMMuxer();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public FILMMuxer() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 560, 360);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Sega Saturn FILM Tools");
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmQuit = new JMenuItem("Quit");
        mntmQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnFile.add(mntmQuit);
               
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 183, 532, -183);
        frame.getContentPane().setLayout(new GridLayout(1, 1));
        
        JPanel panel1 = new JPanel(false);
        JLabel filler = new JLabel("Muxer");
        filler.setHorizontalAlignment(JLabel.CENTER);
        filler.setFont(new Font("Arial", Font.PLAIN, 11));
        panel1.setLayout(null);
        panel1.add(filler);
        panel1.setPreferredSize(new Dimension(550, 225));
        
        
        TextField parseAudioInputFileDirField = new TextField();
        parseAudioInputFileDirField.setBounds(157, 41, 343, 22);
        panel1.add(parseAudioInputFileDirField);
        
        Button parseAudioInputDirSearchButton = new Button("...");
        parseAudioInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   parseAudioInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        parseAudioInputDirSearchButton.setBounds(502, 41, 22, 22);
        panel1.add(parseAudioInputDirSearchButton);
        
        Label inputAudioParseDirLabel = new Label("Audio Source File");
        inputAudioParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        inputAudioParseDirLabel.setBounds(10, 41, 148, 22);
        panel1.add(inputAudioParseDirLabel);
        
        TextField parseVideoInputFileDirField = new TextField();
        parseVideoInputFileDirField.setBounds(157, 69, 343, 22);
        panel1.add(parseVideoInputFileDirField);
        
        Button parseVideoInputDirSearchButton = new Button("...");
        parseVideoInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   parseVideoInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        parseVideoInputDirSearchButton.setBounds(502, 69, 22, 22);
        panel1.add(parseVideoInputDirSearchButton);
        
        Label inputVideoParseDirLabel = new Label("Video Source FILM File");
        inputVideoParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        inputVideoParseDirLabel.setBounds(10, 69, 125, 22);
        panel1.add(inputVideoParseDirLabel);
        
        Label parserTitle = new Label("Sega Saturn FILM Muxer");
        parserTitle.setFont(new Font("Arial", Font.BOLD, 14));
        parserTitle.setBounds(10, 13, 227, 22);
        panel1.add(parserTitle);
        
        Label outputParseDirLabel = new Label("Output File Directory");
        outputParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        outputParseDirLabel.setBounds(10, 97, 105, 22);
        panel1.add(outputParseDirLabel);
        
        TextField parseOutputFileDirField = new TextField();
        parseOutputFileDirField.setBounds(157, 97, 343, 22);
        panel1.add(parseOutputFileDirField);
        
        Button parseOutputDirSearchButton = new Button("...");
        parseOutputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   parseOutputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        parseOutputDirSearchButton.setBounds(502, 97, 22, 22);
        panel1.add(parseOutputDirSearchButton);
        
        JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Saturn Format PCM");
        chckbxNewCheckBox_1.setBounds(10, 144, 160, 23);
        panel1.add(chckbxNewCheckBox_1);
        
        JCheckBox chckbxNewCheckBox_1_1 = new JCheckBox("Big Endian");
        chckbxNewCheckBox_1_1.setBounds(10, 170, 160, 23);
        panel1.add(chckbxNewCheckBox_1_1);
        
        
        Button parseButton = new Button("Mux Audio and Video");
        parseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                try {

                    FILMfile file1 = new FILMfile();
                    FILMfile file2 = new FILMfile();
                    
                    if(parseAudioInputFileDirField.getText().endsWith(".ADX") || parseAudioInputFileDirField.getText().endsWith(".adx")) {
                    
                        FILMUtility.parse(parseVideoInputFileDirField.getText(), file2);
                        System.out.println("Attempting to ReMux ADX files...");
                        parseButton.setEnabled(false);
                        FILMfile newFilm = FILMUtility.swapAudioFromADXFile(parseAudioInputFileDirField.getText(), file2);
                        
                        File f = new File(parseVideoInputFileDirField.getText());
                        
                        FILMUtility.reconstruct(newFilm, parseOutputFileDirField.getText() + "\\NEW_" + f.getName());
                        parseButton.setEnabled(true);
                        
                    } else if(parseAudioInputFileDirField.getText().endsWith(".PCM") || parseAudioInputFileDirField.getText().endsWith(".pcm")) {
                        FILMUtility.parse(parseVideoInputFileDirField.getText(), file2);
                        System.out.println("Attempting to ReMux with PCM files...");
                        parseButton.setEnabled(false);

                        boolean satFormat = chckbxNewCheckBox_1.isSelected();
                        boolean bigEndian = chckbxNewCheckBox_1_1.isSelected();
                        FILMfile newFilm = FILMUtility.swapAudioFromPCMFile(parseAudioInputFileDirField.getText(), file2, satFormat, bigEndian);
                        
                        File f = new File(parseVideoInputFileDirField.getText());
                        
                        FILMUtility.reconstruct(newFilm, parseOutputFileDirField.getText() + "\\NEW_" + f.getName());
                        parseButton.setEnabled(true);
                        
                    } else if(parseAudioInputFileDirField.getText().endsWith(".WAV") || parseAudioInputFileDirField.getText().endsWith(".wav")) {
                        FILMUtility.parse(parseVideoInputFileDirField.getText(), file2);
                        System.out.println("Attempting to ReMux with WAV files...");
                        parseButton.setEnabled(false);

                        boolean satFormat = chckbxNewCheckBox_1.isSelected();
                        boolean bigEndian = chckbxNewCheckBox_1_1.isSelected();
                        FILMfile newFilm = FILMUtility.swapAudioFromWAVFile(parseAudioInputFileDirField.getText(), file2);
                        
                        File f = new File(parseVideoInputFileDirField.getText());
                        
                        FILMUtility.reconstruct(newFilm, parseOutputFileDirField.getText() + "\\NEW_" + f.getName());
                        parseButton.setEnabled(true);
                        
                    } else {
                        FILMUtility.parse(parseAudioInputFileDirField.getText(), file1);
                        FILMUtility.parse(parseVideoInputFileDirField.getText(), file2);
                    
                        System.out.println("Attempting to ReMux files...");
                        parseButton.setEnabled(false);
                        FILMfile newFilm = FILMUtility.swapAudio(file1, file2);
                        
                        File f = new File(parseAudioInputFileDirField.getText());
                        
                        FILMUtility.reconstruct(newFilm, parseOutputFileDirField.getText() + "\\NEW_" + f.getName());
                        parseButton.setEnabled(true);
                    }
                    
                   
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        parseButton.setFont(new Font("Arial", Font.PLAIN, 20));
        parseButton.setBounds(10, 204, 514, 57);
        panel1.add(parseButton);
        
        JPanel panel2 = new JPanel(false);
        JLabel filler2 = new JLabel("Audio Extractor");
        filler2.setHorizontalAlignment(JLabel.CENTER);
        filler2.setFont(new Font("Arial", Font.PLAIN, 11));
        panel2.setLayout(null);
        panel2.add(filler2);
        panel2.setPreferredSize(new Dimension(550, 225));
        
        
        
        TextField extractAudioInputFileDirField = new TextField();
        extractAudioInputFileDirField.setBounds(157, 41, 343, 22);
        panel2.add(extractAudioInputFileDirField);
        
        Button extractAudioInputDirSearchButton = new Button("...");
        extractAudioInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   extractAudioInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        extractAudioInputDirSearchButton.setBounds(502, 41, 22, 22);
        panel2.add(extractAudioInputDirSearchButton);
        
        Label extractAudioParseDirLabel = new Label("Audio Source FILM File");
        extractAudioParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        extractAudioParseDirLabel.setBounds(10, 41, 148, 22);
        panel2.add(extractAudioParseDirLabel);

        
        Label extractorTitle = new Label("Sega Saturn FILM Audio Extractor");
        extractorTitle.setFont(new Font("Arial", Font.BOLD, 14));
        extractorTitle.setBounds(10, 13, 249, 22);
        panel2.add(extractorTitle);
        
        Label outputExtractDirLabel = new Label("Output File Directory");
        outputExtractDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        outputExtractDirLabel.setBounds(10, 69, 125, 22);
        panel2.add(outputExtractDirLabel);
        
        TextField extractOutputFileDirField = new TextField();

        extractOutputFileDirField.setBounds(157, 69, 343, 22);
        panel2.add(extractOutputFileDirField);
        
        Button extractOutputDirSearchButton = new Button("...");
        extractOutputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   extractOutputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        extractOutputDirSearchButton.setBounds(502, 69, 22, 22);
        panel2.add(extractOutputDirSearchButton);
        JCheckBox chckbxNewCheckBox = new JCheckBox("WAV Output");
        chckbxNewCheckBox.setBounds(10, 96, 97, 23);
        panel2.add(chckbxNewCheckBox);
        
        Button extractButton = new Button("Extract Audio");
        extractButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                try {

                    FILMfile file1 = new FILMfile();
                
                    FILMUtility.parse(extractAudioInputFileDirField.getText(), file1);
                    
                    System.out.println("Attempting to Extract audio...");
                    extractButton.setEnabled(false);
                    boolean waveOut = chckbxNewCheckBox.isSelected();
                    File f = new File(extractAudioInputFileDirField.getText());
                    FILMUtility.extractAudio(file1, extractOutputFileDirField.getText() + "\\" + f.getName(), waveOut);
                    
                    extractButton.setEnabled(true);
                    
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        extractButton.setFont(new Font("Arial", Font.PLAIN, 20));
        extractButton.setBounds(10, 204, 514, 57);
        panel2.add(extractButton);
        
       // tabbedPane.setEnabledAt(1, true);
        tabbedPane.addTab("Muxer", panel1);
        
        
        tabbedPane.addTab("Extractor", panel2);
        
        
        frame.getContentPane().add(tabbedPane);

        frame.setVisible(true);
        
    }
}

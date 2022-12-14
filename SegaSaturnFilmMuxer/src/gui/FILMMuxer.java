package gui;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import sega.film.FILMUtility;
import sega.film.FILMfile;

public class FILMMuxer {

    private JFrame frame;
    
    private FILMfile file1 = new FILMfile();
    private FILMfile file2 = new FILMfile();
    
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
        frame.setBounds(100, 100, 550, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Sega Saturn FILM Muxer");
        
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
        frame.getContentPane().setLayout(null);
        
        
        TextField parseAudioInputFileDirField = new TextField();
        parseAudioInputFileDirField.setBounds(157, 41, 343, 22);
        frame.getContentPane().add(parseAudioInputFileDirField);
        
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
        frame.getContentPane().add(parseAudioInputDirSearchButton);
        
        Label inputAudioParseDirLabel = new Label("Audio Source FILM / ADX File");
        inputAudioParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        inputAudioParseDirLabel.setBounds(10, 41, 148, 22);
        frame.getContentPane().add(inputAudioParseDirLabel);
        
        TextField parseVideoInputFileDirField = new TextField();
        parseVideoInputFileDirField.setBounds(157, 69, 343, 22);
        frame.getContentPane().add(parseVideoInputFileDirField);
        
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
        frame.getContentPane().add(parseVideoInputDirSearchButton);
        
        Label inputVideoParseDirLabel = new Label("Video Source FILM File");
        inputVideoParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        inputVideoParseDirLabel.setBounds(10, 69, 125, 22);
        frame.getContentPane().add(inputVideoParseDirLabel);
        
        Label parserTitle = new Label("Sega Saturn FILM Muxer");
        parserTitle.setFont(new Font("Arial", Font.BOLD, 14));
        parserTitle.setBounds(10, 13, 227, 22);
        frame.getContentPane().add(parserTitle);
        
        Label outputParseDirLabel = new Label("Output File Directory");
        outputParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        outputParseDirLabel.setBounds(10, 97, 105, 22);
        frame.getContentPane().add(outputParseDirLabel);
        
        TextField parseOutputFileDirField = new TextField();
        parseOutputFileDirField.setBounds(157, 97, 343, 22);
        frame.getContentPane().add(parseOutputFileDirField);
        
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
        frame.getContentPane().add(parseOutputDirSearchButton);
        
        Button parseButton = new Button("Mux Audio and Video");
        parseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                try {
                    
                    if(!parseAudioInputFileDirField.getText().endsWith(".ADX") && !parseAudioInputFileDirField.getText().endsWith(".adx")) {
                    
                        FILMUtility.parse(parseAudioInputFileDirField.getText(), file1);
                        FILMUtility.parse(parseVideoInputFileDirField.getText(), file2);
                    
                        System.out.println("Attempting to ReMux files...");
                        parseButton.setEnabled(false);
                        FILMfile newFilm = FILMUtility.swapAudio(file1, file2);
                        
                        File f = new File(parseAudioInputFileDirField.getText());
                        
                        FILMUtility.reconstruct(newFilm, parseOutputFileDirField.getText() + "\\NEW_" + f.getName());
                        parseButton.setEnabled(true);
                    } else {

                        FILMUtility.parse(parseVideoInputFileDirField.getText(), file2);
                        System.out.println("Attempting to ReMux files...");
                        parseButton.setEnabled(false);
                        FILMfile newFilm = FILMUtility.swapAudioFromADXFile(parseAudioInputFileDirField.getText(), file2);
                        
                        File f = new File(parseVideoInputFileDirField.getText());
                        
                        FILMUtility.reconstruct(newFilm, parseOutputFileDirField.getText() + "\\NEW_" + f.getName());
                        parseButton.setEnabled(true);
                    }
                    
                   
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                            
              
            }
        });
        parseButton.setFont(new Font("Arial", Font.PLAIN, 20));
        parseButton.setBounds(10, 125, 514, 57);
        frame.getContentPane().add(parseButton);
    }

}

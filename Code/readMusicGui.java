import com.mpatric.mp3agic.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.LayerUI;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Frankline Sable on 18/12/2016. At Maseno university
 * Temporary log:
 * assign original names to music
 * assign custom name to music
 * fix corrupted mp3
 * Pack Artist
 */
public class readMusicGui {

    private final JFrame frame;
    private JPanel actionPanel, panelDisclaimer, durationPane, framePanel;
    private final JButton button[] = new JButton[4], sideButton[] = new JButton[4], chooseFileBat;
    private int countMp3 = 0, loadingEffectCount = 0, unknownFiles;
    private final JFileChooser fileChooser;
    private Boolean dirIntact = false, stopThread = false, redoCheck = false;
    private JToolTip toolTip;
    private File currentDirectory, totalFiles[];
    private final JTextArea infoPane;
    private final ImageIcon errorIcon;
    private final String[] loadingEffect = {".", "..", "..."}, unwantedStrings = {"\\Q/\\E", "\\Q|\\E", "\\Q:\\E", "\\Q\"\\E", "\\Q<\\E", "\\Q>\\E", "\\Q?\\E", "\\Q\\\\E", "\\Q*\\E"};
    private final Color[] tooltipColor = {Color.decode("#FF3855"), Color.decode("#FDFF00"), Color.decode("#02A4D3"), Color.decode("#0066CC")};
    private final String finalColorArray[] = {"#FF3855", "#0066CC", "#8FD400", "#FF6037"};
    private final Color colorArray2[] = {new Color(255, 56, 85, 100), new Color(0, 102, 204, 100), new Color(143, 212, 0, 100), new Color(255, 96, 55, 100), new Color(117, 117, 117, 100)};
    private final Color[] colorArray = {Color.decode("#332000"), Color.decode("#664000"), Color.decode("#805000"), Color.decode("#b36f00"), Color.decode("#cc7f00"), Color.decode("#ff9f00"), Color.decode("#ffa91a"), Color.decode("#ffaa1d"), Color.decode("#FF3855"), Color.decode("#111111")};
    private Color durColor = new Color(255, 56, 85, 100);
    private String mp3FileName[], unknownNoFileName[];
    private final loadingAnim loadingLayerUi = new loadingAnim();
    private final loadingBatAnim[] loadingLayerUiButton = new loadingBatAnim[button.length];
    private final TitledBorder currentDirIndicator;
    private final JScrollPane commandScrollPane;
    private final JLabel durationLabel;
    private long fileBytes = 0, totalBytesMP3 = 0, mp3Bytes = 0, totalBytes, initialTime = 0;
    private static final long maxFileSize = 1200000000;

    private readMusicGui() {
        initialSetUps();

        currentDirIndicator = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "No Working directory is chosen", TitledBorder.LEFT, TitledBorder.BELOW_TOP);
        currentDirIndicator.setTitleFont(new Font("Courier New", Font.PLAIN, 11));

        TitledBorder actionPanelTitle = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "<html>Fixing Your <span style=\"color: red;\">MP3 Music</span> Library Made Easier", TitledBorder.LEFT, TitledBorder.BELOW_TOP);
        actionPanelTitle.setTitleColor(new Color(255, 255, 255, 255).brighter());
        actionPanelTitle.setTitleFont(new Font("Comic Sans MS", Font.PLAIN, 16));

        errorIcon = Utils.createImageIcon("Graphics/error_48px.png");

        chooseFileBat = new JButton("Choose The Music Directory");
        chooseFileBat.setBounds(25, 140, 455, 50);
        chooseFileBat.setFont(new Font("Gotham Light", Font.PLAIN, 13));
        chooseFileBat.addActionListener(new buttonActionHandler());

        int buttonSpace = 25;
        int w;
        int count2 = 0;
        int count = 1;
        JLayer<JButton>[] jButtonJLayer = new JLayer[button.length];
        for (w = 0; w < button.length; w++) {
            button[w] = new JButton(new ImageIcon(getClass().getResource("Graphics/icon" + count2 + ".png"))) {

                @Override
                public JToolTip createToolTip() {
                    toolTip = super.createToolTip();
                    toolTip.setFont(new Font("Courier New", Font.PLAIN, 14));
                    toolTip.setForeground(tooltipColor[(new Random()).nextInt(4)]);
                    return toolTip;
                }
            };

            String[] buttonToolTips = {"Repair corrupted music files", "Group music files into folders", "Rename using outer names", "Rename using inner names"};
            button[w].setToolTipText(buttonToolTips[w]);
            count2 += 2;
            button[w].setRolloverIcon(new ImageIcon(getClass().getResource("Graphics/icon" + count + ".png")));
            count += 2;
            button[w].addActionListener(new buttonActionHandler());

            loadingLayerUiButton[w] = new loadingBatAnim();

            jButtonJLayer[w] = new JLayer<>(button[w], loadingLayerUiButton[w]);
            jButtonJLayer[w].setBounds(buttonSpace, 30, 110, 100);
            buttonSpace += 115;
        }

        durationLabel = new JLabel();
        durationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        durationLabel.setFont(new Font("Courier New", Font.PLAIN, 11));

        durationPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(Utils.createImageIcon("Graphics/white-LT_GrayscaleFrost_07.jpg").getImage(), 0, 0, durationPane.getWidth(), durationPane.getHeight(), null);
                g.setColor(durColor);
                g.fillRect(0, 0, durationPane.getWidth(), durationPane.getHeight());
            }
        };
        durationPane.setVisible(false);
        durationPane.setLayout(new BorderLayout());
        durationPane.setBounds(568, 270, 75, 60);
        durationPane.add(durationLabel);

        infoPane = new JTextArea();
        infoPane.setBorder(BorderFactory.createLoweredBevelBorder());
        infoPane.setFont(new Font("Gotham Light", Font.PLAIN, 14));
        infoPane.setEditable(false);

        JLayer<JTextArea> jTextAreaJLayer = new JLayer<>(infoPane, loadingLayerUi);

        commandScrollPane = new JScrollPane(jTextAreaJLayer);
        commandScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), currentDirIndicator));

        LayerUI<JComponent> layerUI = new operationBackground();
        JLayer<JComponent> commandLayer = new JLayer<>(commandScrollPane, layerUI);
        commandLayer.setBounds(5, 215, 560, 175);

        fileChooser = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setSize(520, 350);
                dialog.setIconImage(Utils.createImageIcon("Graphics/view_file_48px.png").getImage());
                return dialog;
            }

            @Override
            public Icon getIcon(File f) {
                ImageIcon mp3Icon = Utils.createImageIcon("Graphics/music_48px.png");
                ImageIcon docFile = Utils.createImageIcon("Graphics/document_48px.png");
                ImageIcon blankFile = Utils.createImageIcon("Graphics/file_52px.png");
                ImageIcon picFile = Utils.createImageIcon("Graphics/image_file_48px.png");
                ImageIcon videoFile = Utils.createImageIcon("Graphics/movie_52px.png");

                ImageIcon musicFolder = Utils.createImageIcon("Graphics/music_folder_48px.png");
                ImageIcon videoFolder = Utils.createImageIcon("Graphics/movies_folder_48px.png");
                ImageIcon docFolder = Utils.createImageIcon("Graphics/documents_folder_48px.png");
                ImageIcon blankFolder = Utils.createImageIcon("Graphics/folder_48px.png");
                ImageIcon picFolder = Utils.createImageIcon("Graphics/pictures_folder_48px.png");

                String extension = Utils.getExtension(f);
                Icon icon;
                if (!f.isDirectory()) {
                    icon = blankFile;
                    if (extension != null) {
                        if (extension.equalsIgnoreCase(Utils.mp3)) {
                            icon = mp3Icon;
                        } else if (extension.equalsIgnoreCase(Utils.mp4) || extension.equalsIgnoreCase(Utils.gp) || extension.equalsIgnoreCase(Utils.avi) || extension.equalsIgnoreCase(Utils.vob)) {
                            icon = videoFile;
                        } else if (extension.equalsIgnoreCase(Utils.doc) || extension.equalsIgnoreCase(Utils.txt) || extension.equalsIgnoreCase(Utils.docx) || extension.equalsIgnoreCase(Utils.pdf)) {
                            icon = docFile;
                        } else if (extension.equalsIgnoreCase(Utils.tiff) || extension.equalsIgnoreCase(Utils.tif) || extension.equalsIgnoreCase(Utils.gif) || extension.equalsIgnoreCase(Utils.jpeg) || extension.equalsIgnoreCase(Utils.jpg) || extension.equalsIgnoreCase(Utils.png)) {
                            icon = picFile;
                        }
                    }
                } else {
                    icon = blankFolder;
                    File checkFolderHasMusic[] = f.listFiles();
                    if (checkFolderHasMusic != null) {
                        for (File aCheckFolderHasMusic : checkFolderHasMusic) {
                            String extension2 = Utils.getExtension(aCheckFolderHasMusic);
                            if (extension2 != null) {
                                if (extension2.equalsIgnoreCase(Utils.mp3)) {
                                    icon = musicFolder;
                                    break;
                                } else if (extension2.equalsIgnoreCase(Utils.mp4) || extension2.equalsIgnoreCase(Utils.gp) || extension2.equalsIgnoreCase(Utils.avi) || extension2.equalsIgnoreCase(Utils.vob)) {
                                    icon = videoFolder;
                                    break;
                                } else if (extension2.equalsIgnoreCase(Utils.doc) || extension2.equalsIgnoreCase(Utils.txt) || extension2.equalsIgnoreCase(Utils.docx) || extension2.equalsIgnoreCase(Utils.pdf)) {
                                    icon = docFolder;
                                    break;
                                } else if (extension2.equalsIgnoreCase(Utils.tiff) || extension2.equalsIgnoreCase(Utils.tif) || extension2.equalsIgnoreCase(Utils.gif) || extension2.equalsIgnoreCase(Utils.jpeg) || extension2.equalsIgnoreCase(Utils.jpg) || extension2.equalsIgnoreCase(Utils.png)) {
                                    icon = picFolder;
                                    break;
                                }
                            }
                        }
                    }
                }
                return icon;
            }
        };
        fileChooser.setDialogTitle("Please Choose the Music Directory");

        actionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage((new ImageIcon(getClass().getResource("Graphics/thumb-1920-171916.jpg"))).getImage(), 0, 0, actionPanel.getWidth(), actionPanel.getHeight(), null);

            }
        };
        actionPanel.setBackground(Color.lightGray);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), actionPanelTitle));
        actionPanel.setBounds(5, 10, 500, 200);
        actionPanel.setLayout(null);
        actionPanel.add(chooseFileBat);

        for (w = 0; w < button.length; w++) {
            actionPanel.add(jButtonJLayer[w]);
        }


        framePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage((new ImageIcon(getClass().getResource("Graphics/thumb-1920-171916.jpg"))).getImage(), 0, 0, framePanel.getWidth(), framePanel.getHeight(), null);
                g.setColor(new Color(2, 164, 211, 25));
                g.fillRect(0, 0, framePanel.getWidth(), framePanel.getHeight());
            }
        };
        framePanel.setLayout(null);
        framePanel.add(actionPanel);
        framePanel.add(commandLayer);
        framePanel.add(durationPane);
        count2 = 0;
        count = 1;

        int sideButtonSpacing = 75;
        for (int i = 0; i < sideButton.length; i++) {
            sideButton[i] = new JButton(new ImageIcon(getClass().getResource("Graphics/sideIcon" + count2 + ".png"))) {
                @Override
                public JToolTip createToolTip() {
                    JToolTip toolTip = super.createToolTip();
                    toolTip.setFont(new Font("Courier New", Font.PLAIN, 12));
                    toolTip.setForeground(Color.WHITE.brighter());
                    toolTip.setBackground(Color.decode("#FF3855"));
                    return toolTip;
                }
            };
            String[] sideButtonTooltips = {"Preferences", "Help", "Disclaimer", "About"};
            sideButton[i].setToolTipText(sideButtonTooltips[i]);
            count2 += 2;
            sideButton[i].setRolloverIcon(new ImageIcon(getClass().getResource("Graphics/sideIcon" + count + ".png")));
            count += 2;
            sideButton[i].setBounds((actionPanel.getX() + actionPanel.getWidth()) + 5, sideButtonSpacing, 32, 32);
            sideButtonSpacing += 31;
            sideButton[i].addActionListener(new sideButtonHandler());
            framePanel.add(sideButton[i]);
        }

        frame = new JFrame("Mp3 Formatter");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(650, 425);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(framePanel, BorderLayout.CENTER);
        frame.setIconImage((new ImageIcon(getClass().getResource("Graphics//app_icon2.png"))).getImage());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                for (loadingBatAnim aLoadingLayerUiButton : loadingLayerUiButton) {
                    if (aLoadingLayerUiButton.isRunning()) {
                        JOptionPane.showMessageDialog(frame, "Please avoid exiting while some pending operation(s) \n" +
                                "are in progress else your data may get corrupted!", "Cannot Exit Now ", JOptionPane.WARNING_MESSAGE);
                        break;
                    } else if (!aLoadingLayerUiButton.isRunning()) {
                        System.exit(0);


                    }
                }
            }
        });
        new Thread(() -> {
            try {
                for (; ; ) {
                    if (!stopThread) {
                        infoPane.setText("\n\n\nWaiting for operation" + loadingEffect[loadingEffectCount]);
                        loadingEffectCount++;
                        Thread.sleep(500);
                        if (loadingEffectCount == 3) {
                            loadingEffectCount = 0;
                        }
                    } else {
                        infoPane.setFont(new Font("Gotham Light", Font.PLAIN, 12));
                        infoPane.setForeground(Color.decode("#02A4D3"));
                        break;
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    private class buttonActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            frame.setTitle("Mp3 Formatter");

            if (e.getSource() == button[0]) {

                if (dirIntact) {
                    loadingLayerUiButton[0].start();
                    new SwingWorker<Boolean, Boolean>() {
                        Boolean result = false;

                        @Override
                        protected Boolean doInBackground() throws Exception {
                            initialTime = System.currentTimeMillis();
                            try {
                                Thread.sleep(500);
                                if (fixMp3File())
                                    result = true;
                            } catch (InterruptedException ignored) {
                            }
                            return result;
                        }

                        @Override
                        protected void done() {
                            super.done();
                            if (result) {
                                loadingLayerUiButton[0].stop();
                                duration(initialTime, System.currentTimeMillis());
                            } else {
                                loadingLayerUiButton[0].stop();
                                addLog("Error 100: There were some errors in the operation");
                            }
                        }
                    }.execute();

                }
            } else if (e.getSource() == button[1]) {
                if (dirIntact) {

                    loadingLayerUiButton[1].start();
                    new SwingWorker<Boolean, Boolean>() {
                        Boolean result = false;

                        @Override
                        protected Boolean doInBackground() throws Exception {
                            initialTime = System.currentTimeMillis();
                            try {
                                Thread.sleep(500);
                                if (groupIntoFolders())
                                    result = true;
                            } catch (InterruptedException ignored) {
                            }
                            return result;
                        }

                        @Override
                        protected void done() {
                            super.done();
                            if (result) {
                                loadingLayerUiButton[1].stop();
                                duration(initialTime, System.currentTimeMillis());
                            } else {
                                loadingLayerUiButton[1].stop();
                                addLog("Error 101: There were some errors in the operation");
                            }
                        }
                    }.execute();

                }

            } else if (e.getSource() == button[2]) {

                if (dirIntact) {

                    JTextArea warningText = new JTextArea("This my significantly damage your music file's metadata\n" +
                            "only proceed if you are sure the custom name is a valid \n" +
                            "mp3 file name");
                    warningText.setBackground(new Color(240, 240, 240));
                    warningText.setFont(new Font("Calibri Light", Font.PLAIN, 14));

                    Object[] options = {"Proceed", "Cancel"};
                    int requestState = JOptionPane.showOptionDialog(frame, warningText, "Warning...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                    if (requestState == JOptionPane.YES_OPTION) {

                        loadingLayerUiButton[2].start();
                        new SwingWorker<Boolean, Boolean>() {
                            Boolean result = false;

                            @Override
                            protected Boolean doInBackground() throws Exception {
                                initialTime = System.currentTimeMillis();
                                try {
                                    Thread.sleep(500);
                                    if (renameUsingOuterNames())
                                        result = true;
                                } catch (InterruptedException ignored) {
                                }

                                return result;
                            }

                            @Override
                            protected void done() {
                                super.done();
                                if (result) {
                                    loadingLayerUiButton[2].stop();
                                    duration(initialTime, System.currentTimeMillis());
                                } else {
                                    loadingLayerUiButton[2].stop();
                                    addLog("Error 102: There were some errors in the operation");
                                }
                            }
                        }.execute();
                    }
                }

            } else if (e.getSource() == button[3]) {

                if (dirIntact) {

                    loadingLayerUiButton[3].start();
                    new SwingWorker<Boolean, Boolean>() {
                        Boolean result = false;

                        @Override
                        protected Boolean doInBackground() throws Exception {

                            try {
                                initialTime = System.currentTimeMillis();
                                Thread.sleep(500);
                                if (renameUsingInnerNames())
                                    result = true;
                            } catch (InterruptedException ignored) {
                            }
                            return result;
                        }

                        @Override
                        protected void done() {
                            super.done();
                            if (result) {
                                loadingLayerUiButton[3].stop();
                                duration(initialTime, System.currentTimeMillis());
                            } else {
                                loadingLayerUiButton[3].stop();
                                addLog("Error 103: There were some errors in the operation");
                            }
                        }
                    }.execute();
                }

            } else {
                int returnVal = fileChooser.showDialog(frame, "Work Here");

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    stopThread = true;
                    dirIntact = true;
                    currentDirectory = fileChooser.getCurrentDirectory();
                    //  ////System.out.println(fileChooser.getSelectedFile().length());

                    currentDirIndicator.setTitle("Currently working in: " + currentDirectory.getAbsolutePath());
                    infoPane.setText("\nAnalysing directory...\n");
                    commandScrollPane.repaint();
                    redoCheck = false;

                    loadingLayerUi.start();
                    new SwingWorker<Boolean, Boolean>() {
                        Boolean result = false;

                        @Override
                        protected Boolean doInBackground() throws Exception {

                            try {
                                Thread.sleep(1000);
                                if (directoryInformation())
                                    result = true;
                            } catch (InterruptedException ignored) {
                            }
                            return result;
                        }

                        @Override
                        protected void done() {
                            super.done();
                            if (result)
                                loadingLayerUi.stop();
                            else {
                                loadingLayerUi.stop();
                                addLog("Error 111: There were some errors in the operation");
                            }
                        }
                    }.execute();
                }
            }
            blinkHelp();
        }
    }

    private void blinkHelp() {
        if (!dirIntact) {
            chooseFileBat.setFont(new Font("Courier New", Font.BOLD, 13));
            chooseFileBat.setIcon(errorIcon);

            new Thread(() -> {
                String textBlink = "Choose The Music Directory";
                try {

                    for (int f = 0; f < 2; f++) {
                        for (Color aColorArray : colorArray) {
                            chooseFileBat.setForeground(aColorArray);
                            Thread.sleep(50);
                        }
                    }
                    chooseFileBat.setFont(new Font("Courier New", Font.PLAIN, 13));
                    chooseFileBat.setIcon(null);

                    for (int f = 0; f < 2; f++) {
                        for (int i = 0; i < 4; i++) {
                            if (i == 0)
                                textBlink = "<html><span style=color:" + finalColorArray[i] + ";>Choose</span> The Music Directory";
                            if (i == 1)
                                textBlink = "<html>Choose<span style=color:\"" + finalColorArray[i] + ";\"> The </span>Music Directory";
                            if (i == 2)
                                textBlink = "<html>Choose The<<span style=color:\"" + finalColorArray[i] + ";\"> Music </span>Directory";
                            if (i == 3)
                                textBlink = "<html>Choose The Music<span style=color:\"" + finalColorArray[i] + ";\"> Directory </span>";

                            chooseFileBat.setText(textBlink);
                            Thread.sleep(100);
                        }
                    }
                    textBlink = "Choose The Music Directory";
                    chooseFileBat.setText(textBlink);
                } catch (InterruptedException ignored) {
                }
            }).start();

        }
    }

    private Boolean directoryInformation() {

        totalFiles = null;
        int subDirectories = 0;
        unknownFiles = 0;
        int mp3Files = 0;
        Boolean operationStatus = false;
        totalFiles = currentDirectory.listFiles();

        assert totalFiles != null;
        for (File totalFile : totalFiles) {
            if (totalFile.isDirectory()) {
                subDirectories++;
            } else {
                String extension = Utils.getExtension(totalFile);
                if (extension != null) {
                    if ((extension).toLowerCase().equals(Utils.mp3)) {
                        mp3Files++;
                    } else {
                        unknownFiles++;
                    }
                } else {
                    unknownFiles++;
                }
            }
        }
        countMp3 = 0;
        int countUnknown = 0;
        mp3FileName = new String[mp3Files];
        unknownNoFileName = new String[unknownFiles];

        for (File totalFile : totalFiles) {
            if (!totalFile.isDirectory()) {
                String extension = Utils.getExtension(totalFile);
                if (extension != null) {
                    if ((extension).toLowerCase().equals(Utils.mp3)) {
                        mp3FileName[countMp3] = totalFile.getName();
                        countMp3++;
                    } else {
                        unknownNoFileName[countUnknown] = totalFile.getName();
                        countUnknown++;
                    }

                } else {
                    unknownNoFileName[countUnknown] = totalFile.getName();
                    countUnknown++;
                }
            }
            operationStatus = true;
        }
        if (!redoCheck) {
            addLog("Total Number of files " + totalFiles.length + " and size ");
            if (subDirectories != 0)
                addLog("SubDirectories found " + subDirectories);
            if (mp3Files != 0)
                addLog("Music Files found " + mp3Files);
            if (unknownFiles != 0)
                addLog("Unknown Files found " + unknownFiles);
        }

        return operationStatus;
    }

    private Boolean fixMp3File() throws UnsupportedTagException {

        if (redoCheck) {
            addLog("\nRe-analysing current directory");
            directoryInformation();
            addLog("done!");
        }
        Boolean operationStatus = false;
        int renamedFiles = 0;

        int checkInvalid = mp3FileName.length + unknownFiles;

        for (int i = 0; i < unknownFiles; i++) {
            File oldFile = new File(currentDirectory + "//" + unknownNoFileName[i]);
            File newFile = new File(currentDirectory + "//" + unknownNoFileName[i] + ".mp3");
            if (oldFile.renameTo(newFile)) {
                renamedFiles++;
            } else {
                addLog("There is a problem with " + unknownNoFileName[i]);
            }
        }
        if (renamedFiles != 0)
            addLog("\nAttempting to fix " + renamedFiles + " unknown files");

        refresh();
        mp3FileName = new String[countMp3];
        countMp3 = 0;
        assert totalFiles != null;
        for (File totalFile : totalFiles) {
            if (!totalFile.isDirectory()) {
                String extension = Utils.getExtension(totalFile);
                if (extension != null) {
                    if ((extension).toLowerCase().equals(Utils.mp3)) {
                        mp3FileName[countMp3] = totalFile.getName();
                        countMp3++;
                    }
                }
            }
        }
        int invalidMp3Files = 0;
        for (int i = 0; i < countMp3; i++) {

            File fileInspect = new File(currentDirectory + "//" + mp3FileName[i]);
            if (fileInspect.length() < maxFileSize)
                fileBytes -= fileInspect.length();
            status(false);
            try {

                if (fileInspect.length() > maxFileSize)
                    throw new IOException();
                Mp3File check = new Mp3File(currentDirectory + "//" + mp3FileName[i]);

                if (!check.hasId3v1Tag()) {
                    if (!check.hasId3v2Tag()) {
                        if (check.getLengthInSeconds() > 0 && check.getFrameCount() > 1000) {
                            try {
                                ID3v1 id3v1 = new ID3v1Tag();
                                check.setId3v1Tag(id3v1);
                                id3v1.setComment("Once corrupted and repaired by Mp3Fixer");
                                check.save(currentDirectory + "//Fixed_" + mp3FileName[i]);
                            } catch (NotSupportedException e) {
                                try {
                                    ID3v2 id3v2 = new ID3v24Tag();
                                    check.setId3v1Tag(id3v2);
                                    id3v2.setComment("Once corrupted and repaired by Mp3Fixer");
                                    check.save(currentDirectory + "//Fixed " + mp3FileName[i]);

                                } catch (NotSupportedException e2) {
                                    throw new InvalidDataException();
                                }
                            }
                            if (new File(check.getFilename()).delete())
                                addLog(mp3FileName[i] + " has been fixed");

                        } else
                            throw new InvalidDataException();
                    }

                }
                operationStatus = true;

            } catch (InvalidDataException | IOException e) {

                File oldFile = new File(currentDirectory + "//" + mp3FileName[i]);
                String restoreName = mp3FileName[i].replace(".mp3", "");
                restoreName = restoreName.replace(".MP3", "");
                restoreName = restoreName.replace(".mP3", "");
                restoreName = restoreName.replace(".Mp3", "");

                File newFile = new File(currentDirectory + "//" + restoreName);
                if (!oldFile.renameTo(newFile)) {
                    addLog("There is a problem with " + mp3FileName[i]);
                }
                invalidMp3Files++;
                operationStatus = true;
            }
        }


        if (invalidMp3Files != 0)
            addLog("Invalid mp3 Files " + invalidMp3Files);
        if ((checkInvalid - invalidMp3Files) != 0)//tot-inv
            addLog("Repaired mp3 Files " + (Math.abs(checkInvalid - invalidMp3Files)));
        redoCheck = true;

        return operationStatus;
    }

    private Boolean renameUsingOuterNames() throws IOException, UnsupportedTagException {

        Boolean operationStatus = false;
        String nameParts[];
        refresh();
        int countMe2 = 0;
        int renameSuccess = 0;
        int failureSuccess = 0;
        int alienFiles = 0;

        for (File totalFile : totalFiles) {
            if (!totalFile.isDirectory()) {
                String extension = Utils.getExtension(totalFile);
                if (extension != null) {
                    if ((extension).toLowerCase().equalsIgnoreCase(Utils.mp3)) {
                        mp3FileName[countMe2] = totalFile.getName();
                        try {
                            mp3Bytes -= new File(currentDirectory + "//" + mp3FileName[countMe2]).length();
                            status(true);

                            Mp3File renameMp3File = new Mp3File(currentDirectory + "//" + mp3FileName[countMe2]);

                            nameParts = mp3FileName[countMe2].split("-");
                            String musicTitle;
                            try {
                                musicTitle = nameParts[1];
                                musicTitle = musicTitle.replaceAll(".mp3", "");
                                musicTitle = musicTitle.replaceAll(".Mp3", "");
                                musicTitle = musicTitle.replaceAll(".MP3", "");
                            } catch (IndexOutOfBoundsException e) {
                                musicTitle = "Unknown Title";
                            }

                            File oldFile = new File(renameMp3File.getFilename());
                            File newFile = new File(currentDirectory + "//workingOn.mp3");

                            if (oldFile.renameTo(newFile)) {

                                renameMp3File = new Mp3File(newFile.getAbsoluteFile());
                                ID3v2 id3v2;
                                if (renameMp3File.hasId3v2Tag()) {
                                    id3v2 = renameMp3File.getId3v2Tag();
                                    id3v2.setArtist(nameParts[0]);
                                    id3v2.setTitle(musicTitle);
                                    renameMp3File.setId3v2Tag(id3v2);
                                } else {
                                    ////System.out.println("Save " + mp3FileName[countMe2] + " which " + renameMp3File.getFilename());
                                    nameParts[0] = nameParts[0].replaceAll(".mp3", "");
                                    nameParts[0] = nameParts[0].replaceAll(".Mp3", "");
                                    nameParts[0] = nameParts[0].replaceAll(".MP3", "");

                                    id3v2 = new ID3v24Tag();
                                    id3v2.setArtist(nameParts[0]);
                                    id3v2.setTitle(musicTitle);
                                    id3v2.setYear(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
                                    id3v2.setComment("Edited by Mp3 Formatter, developer Frankline Sable");
                                    id3v2.setAlbum("Unknown Album");
                                    id3v2.setComposer("Mp3 Formatter");
                                    id3v2.setPublisher("Frankline Sable");
                                    id3v2.setUrl("http://franklinesable.blogspot.co.ke/");
                                    renameMp3File.setId3v2Tag(id3v2);
                                }

                                renameMp3File.save(currentDirectory + "//" + mp3FileName[countMe2]);

                                if ((new File(renameMp3File.getFilename())).delete()) {
                                    renameSuccess++;
                                }
                            } else {
                                failureSuccess++;
                            }
                            countMe2++;
                            operationStatus = true;
                        } catch (InvalidDataException e) {
                            failureSuccess++;
                            operationStatus = true;
                        } catch (NotSupportedException e) {
                            if (new File(currentDirectory + "//" + mp3FileName[countMe2]).delete()) {
                                if (new File(currentDirectory + "//workingOn.mp3").renameTo(new File(currentDirectory + "//" + mp3FileName[countMe2]))) {
                                    failureSuccess++;
                                    operationStatus = true;
                                }
                            }
                        }
                    } else {
                        alienFiles++;
                        operationStatus = true;
                    }
                } else {
                    alienFiles++;
                    operationStatus = true;
                }
            }
        }
        addLog("\nFiles Formatted " + renameSuccess);
        if (failureSuccess != 0)
            addLog("There were " + failureSuccess + " error(s) encountered");
        if (alienFiles != 0)
            addLog(alienFiles + " unknown files found");
        return operationStatus;
    }

    private Boolean renameUsingInnerNames() throws IOException, UnsupportedTagException {

        Boolean operationStatus = false;
        int alienFiles = 0;
        refresh();

        int countMe2 = 0, renameSuccess = 0, renameFailure = 0;

        for (File totalFile : totalFiles) {
            if (!totalFile.isDirectory()) {
                String extension = Utils.getExtension(totalFile);
                if (extension != null) {
                    if ((extension).toLowerCase().equalsIgnoreCase(Utils.mp3)) {
                        mp3FileName[countMe2] = totalFile.getName();
                        try {
                            mp3Bytes -= new File(currentDirectory + "//" + mp3FileName[countMe2]).length();
                            status(true);

                            Mp3File mp3File = new Mp3File(currentDirectory + "//" + mp3FileName[countMe2]);
                            String newName = null;

                            if (mp3File.hasId3v1Tag()) {
                                ID3v1 id3v1 = mp3File.getId3v1Tag();
                                newName = id3v1.getArtist() + " - " + id3v1.getTitle() + ".mp3";
                            } else if (mp3File.hasId3v2Tag()) {
                                ID3v2 id3v2 = mp3File.getId3v2Tag();
                                newName = id3v2.getArtist() + " - " + id3v2.getTitle() + ".mp3";
                            }

                            if (newName == null) {
                                newName = mp3FileName[countMe2];
                                renameFailure++;
                            }
                            if (newName.contains("null")) {
                                if (newName.endsWith("- null.mp3")) {
                                    newName = newName.replaceAll("null", "Unknown Track");
                                }
                            } else if (newName.endsWith("- .mp3")) {
                                newName = newName.replaceAll(" - ", " - Unknown Track");
                            } else if (newName.startsWith(" -")) {
                                newName = newName.replace(" - ", "Unknown Artist - ");
                            }
                            addLog("\nAnalysing illegal characters: ");
                            for (int i = 0; i < unwantedStrings.length; i++) {
                                newName = newName.replaceAll(unwantedStrings[i], "");
                                infoPane.append(i + " " + unwantedStrings[i]);
                            }
                            infoPane.append("Illegal characters analysed");

                            File oldFile = new File(mp3File.getFilename());
                            File newFile = new File(currentDirectory + "//" + newName);

                            if (oldFile.renameTo(newFile)) {
                                renameSuccess++;
                            } else {

                            }

                            countMe2++;
                            operationStatus = true;
                        } catch (InvalidDataException e) {
                            infoPane.append("Failed to rename " + mp3FileName[countMe2]);
                            renameFailure++;
                            operationStatus = true;
                        }
                    } else {
                        alienFiles++;
                        operationStatus = true;
                    }
                } else {
                    alienFiles++;
                    operationStatus = true;
                }
            }
        }
        addLog("\nNumber of files renamed is " + renameSuccess);
        if (renameFailure != 0)
            addLog("\nFailed to rename " + renameFailure + " files");
        if (alienFiles != 0)
            addLog(alienFiles + " unknown files found");
        return operationStatus;
    }

    private Boolean groupIntoFolders() {

        Boolean operationStatus = false;
        refresh();
        int corruptFiles = 0;
        int countMe2 = 0, renameSuccess = 0;
        int alienFiles = 0;

        for (File totalFile : totalFiles) {
            if (!totalFile.isDirectory()) {
                String extension = Utils.getExtension(totalFile);
                if (extension != null) {
                    if ((extension).toLowerCase().equalsIgnoreCase(Utils.mp3)) {
                        mp3FileName[countMe2] = totalFile.getName();
                        try {
                            mp3Bytes -= new File(currentDirectory + "//" + mp3FileName[countMe2]).length();
                            status(true);

                            Mp3File mp3File = new Mp3File(currentDirectory + "//" + mp3FileName[countMe2]);
                            String folderName = null;
                            String newName = null;

                            if (mp3File.hasId3v1Tag()) {
                                ID3v1 id3v1 = mp3File.getId3v1Tag();
                                folderName = id3v1.getArtist();
                                newName = id3v1.getArtist() + " - " + id3v1.getTitle() + ".mp3";
                            } else if (mp3File.hasId3v2Tag()) {
                                ID3v2 id3v2 = mp3File.getId3v2Tag();
                                folderName = id3v2.getArtist();
                                newName = id3v2.getArtist() + " - " + id3v2.getTitle() + ".mp3";
                            }// | \/ : ? " <>
                            if (newName == null) {
                                folderName = "Unknown Tracks";
                                newName = mp3FileName[countMe2];
                            }
                            if (folderName == null || folderName.equals("")) {
                                folderName = "Unknown Tracks";
                            }
                            if (newName.contains("null")) {
                                if (newName.endsWith("- null.mp3")) {
                                    newName = newName.replaceAll("null", "Unknown Track");
                                }
                            } else if (newName.endsWith("- .mp3")) {
                                newName = newName.replaceAll(" - ", " - Unknown Track");
                            } else if (newName.startsWith(" -")) {
                                newName = newName.replace(" - ", "Unknown Artist - ");
                            } else if (folderName.startsWith(" -")) {
                                folderName = folderName.replace(" - ", "");
                            }
                            addLog("\nAnalysing illegal characters: ");
                            for (int i = 0; i < unwantedStrings.length; i++) {

                                folderName = folderName.replaceAll(unwantedStrings[i], "");
                                newName = newName.replaceAll(unwantedStrings[i], "");

                                infoPane.append(i + " " + unwantedStrings[i]);
                            }
                            infoPane.append("Illegal characters analysed");

                            File oldFile = new File(mp3File.getFilename());
                            File createFolder = new File(currentDirectory + "//" + folderName + "//");
                            createFolder.mkdir();
                            if (createFolder.exists()) {
                                File newFile = new File(createFolder.getAbsolutePath() + "//" + newName);

                                //    ////System.out.println("old Name " + oldFile.getAbsoluteFile());
                                //   ////System.out.println("New Name " + newFile.getAbsoluteFile());
                                if (oldFile.renameTo(newFile)) {
                                    renameSuccess++;
                                } else {
                                    File errorMp3s = new File(currentDirectory + "//" + folderName + "//" + mp3FileName[countMe2]);
                                    if (oldFile.renameTo(errorMp3s)) {
                                        renameSuccess++;
                                    } else {
                                        //          ////System.out.println("Error moving " + folderName);
                                        folderName = folderName.replaceAll("\\W", "");
                                        if (createFolder.delete()) {
                                            createFolder = new File(currentDirectory + "//" + folderName + "//");
                                            createFolder.mkdir();
                                            if (!new File(currentDirectory + "//" + mp3FileName[countMe2]).renameTo(new File(currentDirectory + "//" + folderName + "//" + mp3FileName[countMe2]))) {
                                                corruptFiles++;
                                            }
                                        }

                                    }
                                }
                                countMe2++;
                                operationStatus = true;
                            }
                        } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                            File errorDir = new File(currentDirectory + "//+++Corrupt MP3 Files");
                            errorDir.mkdir();
                            if (errorDir.exists()) {
                                File errorFile = new File(currentDirectory + "//" + mp3FileName[countMe2]);
                                if (errorFile.renameTo(new File(errorDir.getAbsolutePath() + "//" + mp3FileName[countMe2]))) {
                                    corruptFiles++;
                                    operationStatus = true;
                                } else
                                    e.printStackTrace();
                            }
                        }
                    } else {
                        alienFiles++;
                        operationStatus = true;
                    }
                } else {
                    alienFiles++;
                    operationStatus = true;
                }
            }
        }
        addLog("\n" + renameSuccess + "  Files grouped into folders");
        if (corruptFiles != 0)
            addLog(corruptFiles + " Files are corrupted!\nTry to fix them at " + currentDirectory + "//+++Corrupt MP3 Files");
        if (alienFiles != 0)
            addLog(alienFiles + " unknown files found");
        return operationStatus;
    }

    private class sideButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == sideButton[3]) {
                JLabel aboutText = new JLabel("<html>\n<span style=\"color:#FF3855; margin-left=100px;\">\tMp3 Formatter<br></span><span style=\"color:#9B7653;\">Version 19.12.16.</span> Developer <span style=\"color:#02A4D3;\">Frankline Sable</span>");
                aboutText.setBackground(new Color(240, 240, 240));
                aboutText.setFont(new Font("Calibri Light", Font.PLAIN, 14));

                JOptionPane.showMessageDialog(frame, aboutText, "About program", JOptionPane.INFORMATION_MESSAGE);

            } else if (e.getSource() == sideButton[2]) {

                JLabel textArea = new JLabel(new DisclaimerHolder().myDisclaimer(false));
                // textArea.setEditable(false);
                textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
                textArea.setBackground(new Color(240, 240, 240));

                Object[] options = {"View Licence", "Okay"};
                int requestState = JOptionPane.showOptionDialog(frame, textArea, "Disclaimer(Please Read Carefully)", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (requestState == JOptionPane.YES_OPTION) {

                    TitledBorder disclaimerBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Licence Agreement For Mp3 Formatter(Freeware)", TitledBorder.LEFT, TitledBorder.BELOW_TOP);
                    disclaimerBorder.setTitleFont(new Font("Courier New", Font.BOLD, 14));
                    disclaimerBorder.setTitleColor(Color.decode("#02A4D3"));

                    JLabel textAreaDisclaimer = new JLabel(new DisclaimerHolder().myDisclaimer(true));
                    //textAreaDisclaimer.setEditable(false);
                    textAreaDisclaimer.setFont(new Font("Courier New", Font.PLAIN, 11));
                    textAreaDisclaimer.setBackground(new Color(240, 240, 240));

                    panelDisclaimer = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            g.drawImage((new ImageIcon(getClass().getResource("Graphics/68408293-sign-wallpapers.jpg"))).getImage(), 0, 0, panelDisclaimer.getWidth(), panelDisclaimer.getHeight(), null);
                        }
                    };
                    JScrollPane scrollPane = new JScrollPane(textAreaDisclaimer);
                    scrollPane.setBorder(disclaimerBorder);
                    scrollPane.setAutoscrolls(true);
                    scrollPane.setBounds(10, 10, 430, 213);

                    panelDisclaimer.setLayout(null);
                    panelDisclaimer.add(scrollPane);

                    JDialog disclaimerDialog = new JDialog();
                    disclaimerDialog.setVisible(true);
                    disclaimerDialog.setModal(false);
                    disclaimerDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    disclaimerDialog.setTitle("Disclaimer");                                    //creating objects for the gui components
                    disclaimerDialog.setSize(450, 260);
                    disclaimerDialog.setLocation(frame.getX() + 55, frame.getY() + 50);
                    disclaimerDialog.setLayout(new BorderLayout());                                //creating objects for the gui components
                    disclaimerDialog.setAlwaysOnTop(true);                                //definition of several objects that we are using
                    disclaimerDialog.setResizable(false);
                    disclaimerDialog.add(panelDisclaimer, BorderLayout.CENTER);
                }
            } else if (e.getSource() == sideButton[1]) {
                JLabel textArea = new JLabel("<html>\nThis will launch the browser in<br> the <a href=\"http://www.franklinesable.blogspot.co.ke\">Developer's Help Website</a><br>Sure <b>proceed</b> to the help link?");
                //textArea.setEditable(false);
                textArea.setFont(new Font("Courier New", Font.PLAIN, 11));
                textArea.setBackground(new Color(240, 240, 240));

                Object[] options = {"Yes please", "No thank you"};
                int requestState = JOptionPane.showOptionDialog(frame, textArea, "Proceed to the internet?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, Utils.createImageIcon("Graphics/open_in_browser_48px.png"), options, options[1]);

                if (requestState == JOptionPane.YES_OPTION) {
                    Desktop desktop = Desktop.getDesktop();
                    // now enable buttons for actions that are supported.
                    URI uri;
                    try {
                        uri = new URI("http://www.franklinesable.blogspot.co.ke");
                        desktop.browse(uri);
                    } catch (IOException | URISyntaxException ignored) {
                    }
                }
            } else {

                File file = new File("%//Mp3 Fixer");
                if (!file.exists())
                    if (file.mkdirs())
                        infoPane.append("Program Log Stored in " + file.getAbsoluteFile());
                if (file.exists()) {
                    try {
                        String lookAndFeel[] = {"System", "Nimbus", "Metal"};
                        String fontStyle[] = {"Segoe Print", "Lucida Handwriting", "Segoe Script"};

                        int keyEvents[] = {KeyEvent.VK_S, KeyEvent.VK_N, KeyEvent.VK_M};
                        JRadioButton radioButton[] = new JRadioButton[keyEvents.length];
                        ButtonGroup group = new ButtonGroup();
                        JPanel panel = new JPanel(new GridLayout(0, 1));

                        String selectedRadioButton = "System theme";
                        Scanner scan = null;
                        try {
                            scan = new Scanner(new File("%//Mp3 Fixer" + "//Mp3 Fixer save.txt"));
                            while (scan.hasNextLine()) {
                                selectedRadioButton = scan.nextLine();
                            }
                        } catch (FileNotFoundException ignored) {
                        } finally {
                            if (scan != null)
                                scan.close();
                        }

                        PrintStream printStream = new PrintStream(file.getAbsolutePath() + "//Mp3 Fixer save.txt");

                        for (int i = 0; i < lookAndFeel.length; i++) {
                            radioButton[i] = new JRadioButton(lookAndFeel[i] + " theme");
                            radioButton[i].setActionCommand(lookAndFeel[i] + " theme");
                            radioButton[i].setFont(new Font(fontStyle[i], Font.PLAIN, 12));
                            radioButton[i].setMnemonic(keyEvents[i]);
                            radioButton[i].addActionListener(e13 -> printStream.println(e13.getActionCommand()));
                            group.add(radioButton[i]);
                            panel.add(radioButton[i]);
                            if (radioButton[i].getActionCommand().equalsIgnoreCase(selectedRadioButton)) {
                                radioButton[i].setSelected(true);
                                printStream.println(selectedRadioButton);
                            }
                        }

                        Object[] options = {"Apply", "Cancel"};
                        int requestState = JOptionPane.showOptionDialog(frame, panel, "Choose The App  Theme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, Utils.createImageIcon("Graphics/change_theme_48px.png"), options, options[1]);

                        if (requestState == JOptionPane.YES_OPTION) {
                            printStream.flush();
                            printStream.close();

                            initialSetUps();
                            SwingUtilities.updateComponentTreeUI(frame);
                            SwingUtilities.updateComponentTreeUI(fileChooser);
                        } else {
                            printStream.flush();
                            printStream.close();
                            printStream.println(selectedRadioButton);
                        }

                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JLabel label = new JLabel("<html>Unable, To <span style=\"color: #FF0000;\">Load</span> Theme directory");
                    label.setFont(new Font("Courier New", Font.PLAIN, 14));
                    JOptionPane.showMessageDialog(frame, label, "Look And Feel Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private void initialSetUps() {
        Scanner scan = null;
        try {
            scan = new Scanner(new File("%//Mp3 Fixer" + "//Mp3 Fixer save.txt"));
            String theme = "System theme";

            while (scan.hasNextLine()) {
                theme = scan.nextLine();
            }

            if (theme.equalsIgnoreCase("Metal theme")) {
// Set cross-platform Java L&F (also called "Metal")
                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName());
            } else if (theme.equalsIgnoreCase("System theme")) {
// Set System L&F
                MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName());
            } else if (theme.equalsIgnoreCase("Nimbus theme")) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            }
        } catch (FileNotFoundException | NoSuchElementException | ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException ignored) {
        } finally {
            if (scan != null)
                scan.close();
        }
    }

    private void refresh() {
        countMp3 = 0;

        totalFiles = null;
        totalFiles = currentDirectory.listFiles();
        fileBytes = 0;
        totalBytes = 0;
        mp3Bytes = 0;
        totalBytesMP3 = 0;

        if (totalFiles != null) {
            for (File totalFile : totalFiles) {
                if (!totalFile.isDirectory()) {
                    String extension = Utils.getExtension(totalFile);
                    if (extension != null) {
                        if ((extension).toLowerCase().equals(Utils.mp3)) {
                            mp3Bytes += totalFile.length();
                            countMp3++;
                        }
                    }
                    if (totalFile.length() < maxFileSize)
                        fileBytes += totalFile.length();
                } else {
                    //directorys
                }
            }
        } else {
            //if nothing found
        }
        totalBytes = fileBytes;
        totalBytesMP3 = mp3Bytes;
        mp3FileName = new String[countMp3];
        new checkMP3Files(totalFiles);
    }

    private void duration(long initialTime, long finalTime) {

        durationPaneAnim("<html>Duration:<br><strong>" + (float) (finalTime - initialTime) / 1000 + "</strong<br>Seconds");

    }

    private void durationPaneAnim(String duration) {
        final int x = 75;
        final int y = 60;
        durColor = colorArray2[new Random().nextInt(colorArray2.length)];

        durationPane.setVisible(true);

        durationPane.setSize(0, 0);
        new Thread(new Runnable() {
            int height = 0;

            @Override
            public void run() {
                try {
                    for (int i = 0; i <= x; i++) {
                        if (i < y) {
                            height++;
                        }
                        durationPane.setSize(i, height);
                        Thread.sleep(5);
                    }
                    durationLabel.setText(duration);
                    Thread.sleep(3000);
                    for (int i = x; i >= 0; i--) {//75,60
                        if (i <= y) {
                            height--;
                        }
                        durationPane.setSize(i, height);
                        Thread.sleep(5);
                    }
                } catch (InterruptedException ignored) {
                }
                //durationLabel.setText(duration);
            }
        }).start();

    }

    private void addLog(String task) {
        infoPane.append("\n" + task);
    }

    private void status(Boolean stat) {
        if (!stat) {
            String stats = "Mp3 Formatter, Operation Status: " + (int) (100 - (100 * (float) fileBytes / totalBytes));
            frame.setTitle(stats + "%");
        } else {
            String stats = "Mp3 Formatter, Operation Status: " + (int) (100 - (100 * (float) mp3Bytes / totalBytesMP3));
            frame.setTitle(stats + "%");
        }
    }

    public static void main(String[] args) {
        new readMusicGui();
    }
}

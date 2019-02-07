package MusicPlayer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import javax.swing.JSlider;
import javax.swing.JList;
import javax.swing.SwingConstants;
import AudioClass.AudioClass;
import AudioClass.PlayBack;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainBase {

	private final JFrame frame1;
	private final JPanel panel1, panel2, panel3, panel4;
	private final JButton bigButs[] = new JButton[10];
	private final JButton menuButs[] = new JButton[10];
	private final String bigNames[] = { "Love", "Desire", "Fear", "Error", "Friends" };
	private int a = 30, b = 5, c = 5, pCC = 0, playList;
	private final TitledBorder bdA, bdB, bdC, bdD, bdE;
	private final Color highLight[] = { Color.GRAY, Color.WHITE };
	private final JSlider vSlider;
	private final ImageIcon icb[] = new ImageIcon[10];
	private final String butTT[] = { "Skip backward", "play song", "stop track", "Skip forward",
			"Repeat/Loop through tracks", "Shuffle playlist", "pause playback", "end current operation" };
	private Thread pt;
	private Boolean expClick = false;
	private String path;
	private final JLabel label;
	protected ImageIcon ic1, ic2, listBackIc[] = new ImageIcon[5];
	protected Image img1, listBackImg[] = new Image[listBackIc.length];
	private JList list;
	private String savedPlays[] = new String[3];
	private Scanner scanFile;
	private int count = 0;
	private int playWhatA=0;
	
	JFrame frame;
	 Canvas canV;
	 String playThis;
	 MediaPlayerFactory mpf;
	 EmbeddedMediaPlayer emp;
	 String playWhich;
	
	String musiqContainer[]={"D:\\Rock Music\\iphone\\Angels & Airwaves - True Love.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - Young London.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - Saturday Love.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - The War.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - The Wolfpack.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - Valkyrie Missile.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - Breathe.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - Sirens.mp3",
			"D:\\Rock Music\\iphone\\Angels & Airwaves - The Flight of Apollo.mp3",
			};

	public MainBase() {

		imgClass();
		songList();
		bdA = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Browse Multiple songs",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		bdA.setTitleFont(new Font("Serif", Font.PLAIN, 17));
		bdA.setTitleColor(Color.GREEN);

		bdB = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Adjust The Volume Here",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		bdB.setTitleFont(new Font("Serif", Font.PLAIN, 15));
		bdB.setTitleColor(Color.GRAY);

		bdC = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Various Options Menu",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		bdC.setTitleFont(new Font("Serif", Font.PLAIN, 16));
		bdC.setTitleColor(Color.BLUE);

		bdD = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Swipe to drag the dockable panel",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		bdD.setTitleFont(new Font("Serif", Font.PLAIN, 20));
		bdD.setTitleColor(Color.BLACK);

		bdE = BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.BLACK), "Song List",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP);
		bdE.setTitleFont(new Font("Serif", Font.PLAIN, 20));
		bdE.setTitleColor(Color.BLACK);

		label = new JLabel("swipe left or right");
		label.setIcon(ic2);
		label.setBorder(bdD);
		label.setFont(new Font("Sitka Banner", Font.PLAIN, 23));
		label.setBounds(0, 390, 390, 130);

		list = new JList(savedPlays);
		list.setBounds(10, 50, 460, 500);
		
		list.setBackground(Color.LIGHT_GRAY);
		list.setFont(new Font("Sitka Banner", Font.PLAIN, 18));
		list.setVisible(false);
		list.addListSelectionListener((ListSelectionEvent e) -> {

			// playSelected();
		});

		vSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 125, 10);
		vSlider.setMajorTickSpacing(10);
		vSlider.setBackground(new Color(211, 211, 211));
		vSlider.setBorder(bdB);
		vSlider.setBackground(Color.DARK_GRAY.brighter());
		vSlider.setPaintTicks(true);
		vSlider.setBounds(5, 525, 390, 40);
		vSlider.addMouseListener(new mouseHandlerA());
		vSlider.addChangeListener((ChangeEvent e) -> {

			try{
			bdB.setTitle("Current Volume: " + vSlider.getValue() + "%");
			vSlider.repaint();
			new AudioClass("A4");
			emp.setVolume(vSlider.getValue());
			}
			
			catch(Exception ex){
				
			}
		});

		panel1 = new panelClassA();
		panel1.setBackground(Color.BLACK);
		panel1.setLayout(null);

		panel2 = new panelClassB();
		panel2.setBounds(5, 0, 400, 570);
		panel2.setBackground(Color.BLACK);
		panel2.setLayout(null);
		panel2.setBorder(bdA);

		panel3 = new panelClassC();
		panel3.setBounds(405, 0, 485, 570);
		panel3.setBorder(bdE);
		panel3.setLayout(new GridLayout());
		panel3.add(new JScrollPane(list));

		panel4 = new panelClassD();
		panel4.setBounds(-385, 390, 390, 130);
		panel4.setBorder(bdC);
		panel4.setBackground(Color.gray);
		panel4.setLayout(null);
		panel4.addMouseListener(new mouseHandlerB());
		panel4.addMouseMotionListener(new mouseHandlerC());

		for (int i = 0; i < 5; i++) {

			bigButs[i] = new buttonClassA(bigNames[i]);
			bigButs[i].setBounds(5, a, 390, 70);
			bigButs[i].addMouseListener(new mouseHandlerA());
			bigButs[i].addActionListener(new actionHandler());
			pCC = 0;
			a += 71;
			panel2.add(bigButs[i]);
		}
		for (int i = 1; i < 9; i++) {

			menuButs[i] = new JButton(icb[i]);
			menuButs[i].setBorder(BorderFactory.createEmptyBorder());
			menuButs[i].setBackground(panel4.getBackground());
			menuButs[i].addMouseListener(new mouseHandlerB());

			if (i < 5) {
				menuButs[i].setBounds(b, 25, 96, 50);
				b += 95;
			} else {
				menuButs[i].setBounds(c, 75, 96, 50);
				c += 95;
			}
			panel4.add(menuButs[i]);
			menuButs[i].setToolTipText(butTT[i - 1]);
			menuButs[i].setEnabled(false);
			menuButs[i].addActionListener(new actionHandlerB());
		}

		frame1 = new JFrame();
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setSize(950, 610);
		frame1.setVisible(true);
		frame1.setTitle("Only For Angels And Airwaves Fans(AVA), Please Do Enjoy! :-)");
		frame1.setLayout(new BorderLayout());
		frame1.setLocationRelativeTo(null);

		frame1.add(panel1, BorderLayout.CENTER);
		panel1.add(panel2);
		panel1.add(panel3);
		panel2.add(vSlider);
		panel2.add(panel4);
		panel2.add(label);

	}

	private class panelClassA extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			g.drawImage(img1, 0, 0, null);

		}
	}

	private class panelClassB extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			g.drawImage(img1, 0, 0, null);

		}
	}

	private class panelClassC extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			g.drawImage(listBackImg[playList], 0, 0, null);
			g.setColor(Color.RED);
			g.setFont(new Font("Serif", Font.BOLD, 50));
			g.drawString("I'm Afraid I CAN'T", 40, 200);
			g.drawString("   Continue! :'-{", 50, 270);

		}
	}

	private class panelClassD extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			// g.drawImage(img2, 0, 0, null);

		}
	}

	private class buttonClassA extends JButton {

		private String bN;

		public buttonClassA(String buttonName) {

			bN = buttonName;
		}

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			g.setColor(highLight[pCC]);
			g.drawImage(img1, 0, 0, null);
			g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			g.drawString(bN, 170, 50);

		}
	}

	private class mouseHandlerA extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent event) {

			if (event.getSource() == vSlider) {

				bdB.setTitleColor(Color.WHITE.brighter());
				vSlider.repaint();

			} else {

				pCC = 1;

				for (int i = 0; i < 5; i++) {

					bigButs[i].repaint();
				}
				new AudioClass("A2");

			}

		}

		public void mouseExited(MouseEvent event) {

			pCC = 0;

			for (int i = 0; i < 5; i++) {

				bigButs[i].repaint();
			}

			bdB.setTitle("Adjust The Volume Here");
			bdB.setTitleColor(Color.GRAY);
			vSlider.repaint();

		}

	}

	private class mouseHandlerB extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent e) {

			bdC.setTitleColor(new Color(153, 204, 255).brighter());
			panel4.repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {

			bdC.setTitleColor(new Color(0, 153, 255).darker());
			panel4.repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			expClick = false;
		}

		public void mousePressed(MouseEvent event) {

			if (!event.isMetaDown()) {

				expClick = true;
			} else {

				expClick = false;
			}
		}
	}

	private class mouseHandlerC implements MouseMotionListener {
		@Override
		public void mouseMoved(MouseEvent e) {

			expClick = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			if (expClick) {

				panel4.setEnabled(false);
				new AudioClass("A10");
				for (int i = 1; i < 9; i++) {

					menuButs[i].setEnabled(false);
				}

				try {

					pt.stop();
					pt = new panel4Thread();
					pt.start();
				} catch (Exception ex) {

					pt = new panel4Thread();
					pt.start();
				}
			}
		}
	}

	private class actionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			new AudioClass("A5");

			if (e.getSource() == bigButs[0]) {

				bdA.setTitleColor(Color.red);
				bdA.setTitle("Songs Of Love, Its All About Passion");
				panel2.repaint();
				playWhatA = 1;
				playList = 0;
			
				
			} else if (e.getSource() == bigButs[1]) {

				bdA.setTitleColor(Color.ORANGE);
				bdA.setTitle("Songs Of Rage, Its All About War");
				panel2.repaint();
				playList = 1;
				playWhatA = 2;
			} else if (e.getSource() == bigButs[2]) {

				bdA.setTitle("Songs Of Courage, A Heros Meal");
				bdA.setTitleColor(Color.BLUE);
				panel2.repaint();
				playList = 2;
				playWhatA = 3;
			} else if (e.getSource() == bigButs[3]) {

				bdA.setTitleColor(Color.PINK);
				bdA.setTitle("Songs Of Hope, Let Hope Not Die");
				panel2.repaint();
				playList = 3;
			} else {

				bdA.setTitleColor(Color.YELLOW);
				bdA.setTitle("Crazy Ones , For Crazy People");
				panel2.repaint();
				playList = 4;

			}
			panel3.repaint();
			list.setVisible(true);
			songList();
			// playSong();
			list.repaint();

		}

	}

	private class actionHandlerB implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			new AudioClass("A15");

			if (e.getSource() == menuButs[1]) {

				
			} else if (e.getSource() == menuButs[2]) {

				if(playWhatA==1){
					
					
					if(list.getSelectedIndex()<0){
						
						
						System.out.println("Playing All Love Songs by Ava");
						
					}
					else{
						
						System.out.println("Currently Playing "+savedPlays[list.getSelectedIndex()]);
					
					playBack();
					emp.start();
					
					}
				}
				else if(playWhatA==2){
					
					if(list.getSelectedIndex()<0){
						
						
						System.out.println("Playing All Love Songs by Ava");
						
					}
					else{
						
						System.out.println("Currently Playing "+savedPlays[list.getSelectedIndex()]);
					
					playBack();
					emp.start();
					}
				}
				else if(playWhatA==3){
					
					if(list.getSelectedIndex()<0){
						
						
						System.out.println("Playing All Love Songs by Ava");
						
					}
					else{
						
						System.out.println("Currently Playing "+savedPlays[list.getSelectedIndex()]);
					
					playBack();
					emp.start();
					}
				}
				else{
					
					System.out.println("No songs selceted!");			}
				
			} else if (e.getSource() == menuButs[3]) {

				emp.stop();
				
			} else if (e.getSource() == menuButs[4]) {

				emp.skipPosition(0.1f);
			} else if (e.getSource() == menuButs[5]) {

				
			} else if (e.getSource() == menuButs[6]) {

				
			} else if (e.getSource() == menuButs[7]) {

				emp.pause();
			} else if (e.getSource() == menuButs[8]) {

				System.exit(0);
				
			} else {
				
				System.err.println("Illegal button detected");
			}
		}
	}

	protected void imgClass() {

		ic1 = new ImageIcon(getClass().getResource("MainBase-img/baseImg.jpg"));
		img1 = ic1.getImage();

		ic2 = new ImageIcon(getClass().getResource("Label-img/drag.png"));

		for (int i = 1; i < 9; i++) {

			icb[i] = new ImageIcon(getClass().getResource("Button-img/media" + i + ".png"));
		}

		for (int i = 0; i < 4; i++) {

		}

		for (int i = 0; i < listBackImg.length; i++) {

			listBackIc[i] = new ImageIcon(getClass().getResource("SongList-img/songList" + i + ".jpg"));
			listBackImg[i] = listBackIc[i].getImage();
		}
		new AudioClass("A10");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame.setDefaultLookAndFeelDecorated(false);
		new MainBase();

	}

	private class panel4Thread extends Thread {

		private int myX;

		@Override
		public void run() {

			myX = panel4.getX();

			if (myX < -50) {

				path = "EAST";

			} else if (myX >= -50) {

				path = "WEST";

			} else {

				System.err.println("A critical Error has occurred");
			}
			try {

				switch (path) {

				case "EAST":

					for (;;) {

						panel4.setBounds(myX, 390, 390, 130);
						myX++;
						Thread.sleep(5);

						if (myX == 5) {

							break;
						}
					}
					break;

				case "WEST":

					for (;;) {

						panel4.setBounds(myX, 390, 390, 130);
						myX--;
						Thread.sleep(5);

						if (myX == -385) {

							break;
						}
					}

					break;

				default:

					System.err.println("There's a critical fault in the draggable panel i.e panel4");
				}

				panel4.setEnabled(true);
				for (int i = 1; i < 9; i++) {

					menuButs[i].setEnabled(true);
				}

			} catch (InterruptedException ex) {

				System.err.println(ex);
			}

		}
	}

	public void songList() {

		try {

			scanFile = new Scanner(new File("D:\\Java buzz\\MusicPlayer Database\\musiq[" + playList + "].txt"));

			while (scanFile.hasNextLine()) {

				savedPlays[count] = scanFile.nextLine();
				count++;

				if (count > 2) {

					count = 0;
					break;
				}

			}
		} catch (FileNotFoundException e) {

			System.err.println("Error in the playlist");
		}

	}
	
	public void playBack(){
		
		if(playWhatA==1){
			
			if(list.getSelectedIndex()>-1){
			playThis=musiqContainer[list.getSelectedIndex()];
		}}
		
		else if(playWhatA==2){
			
			if(list.getSelectedIndex()>-1){
				playThis=musiqContainer[list.getSelectedIndex()+3];
			}
		}
		else if(playWhatA==3){
			
			if(list.getSelectedIndex()>-1){
				playThis=musiqContainer[list.getSelectedIndex()+6];
			}
		}
		else{
			
			System.out.println("Playlist not found");
		}
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		mpf = new MediaPlayerFactory();
		
		emp= mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(frame1));
		emp.setEnableMouseInputHandling(false);
		emp.setEnableKeyInputHandling(false);
		emp.setVolume(vSlider.getValue());
		emp.prepareMedia(playThis);
		
	}
}

package AudioClass;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import MusicPlayer.MainBase;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class PlayBack {

	private JFrame frame;
	private Canvas canV;
	private JPanel panel;
	private String playThis;
	private MediaPlayerFactory mpf;
	private EmbeddedMediaPlayer emp;
	private int playList,playy;
	private String musiqContainer[]={"D:\\Rock Music\\iphone\\Angels & Airwaves - True Love.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - Young London.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - Saturday Love.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - The War.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - The Wolfpack.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - Valkyrie Missile.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - Breathe.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - Sirens.mp3",
								"D:\\Rock Music\\iphone\\Angels & Airwaves - The Flight of Apollo.mp3",
								};
	
	public PlayBack(int playListt,String playWhich, int playyy){
		
		playList=playListt;
		playy=playyy;
		
		songList();
		
		canV = new Canvas();
		canV.setBackground(Color.BLACK);

		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(canV);

		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setSize(50, 50);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		frame.add(panel);

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		mpf = new MediaPlayerFactory();

		emp= mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(frame));
		emp.setVideoSurface(mpf.newVideoSurface(canV));

		emp.setEnableMouseInputHandling(false);

		emp.setEnableKeyInputHandling(false);

		emp.prepareMedia(playThis);
		
		
		switch(playWhich){
		
		case "start":
			
			emp.stop();
			emp.play();
			break;
			
		case "stop":
			
			emp.stop();
			break;
			
		case "pause":
			
			emp.pause();
			break;
			
		case "A4":
			
		
			break;
			
		case "A5":
			break;
			
		case "A6":
			break;
			
			default:
				System.err.println("Music could not be loaded!");
		}
		playBackAll();
	}

	public void playBackAll() {

		
	}
	public void songList() {

		if(playList==1){
			
			playThis=musiqContainer[playy];
		}

	}

}

package AudioClass;
/**
 * 
 */


import javazoom.jl.decoder.JavaLayerException;
/**
 * @author Frankline Sable
 *
 */
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AudioClass {
	
	private FileInputStream audio;
	private Player execute;
	private soundThreadClass playSound;
	private final String thisAudio;
	
	public AudioClass(String playWhich){
		
		thisAudio=playWhich;
		
		try{
			  playSound.stop();
			  playSound=new soundThreadClass();
			  playSound.start();
			  
		  }
		  catch(Exception e){
			
			  playSound=new soundThreadClass();
			  playSound.start();
			}
	   }
	   private class soundThreadClass extends Thread{
		   
		  @Override
		   public void run(){
			   
			   try{
				   
				   switch(thisAudio){
				   
				   case "A1":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\class-SnailMania-Move.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
					   
				   case "A2":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\button_Clicked.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
					   
				   case "A3":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\threadDestroyed.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
					   
				   case "A4":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\thread_Stopped.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A5":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\terminal-sound.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A6":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\Class-kickedOut.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A7":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\programExit.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A8":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\feedbacksound.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A9":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\popUpsound.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
				   case "A10":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\closingsubwindows.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A11":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\happy.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A12":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\moviePlayer.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   case "A13":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\otherThreadActivities.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
			       case "A14":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\snailVerticalMovement.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
			       case "A15":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\pop.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
			       case "A16":
					   
					   audio = new FileInputStream("C:\\Users\\Frankline Sable\\workspace\\New And Improved Programs Maseno\\src\\Sounds\\FireWeapon.mp3");
			           execute = new Player(audio);
			           execute.play();
			           break;
			           
				   default:
					   
						   System.out.println("Invalid Arguments in the Audio Class");
						   break;
				   }
			} 
			   catch (FileNotFoundException e) {

	            System.out.println("Cannot Find Audio File");
	        } 
			   catch (JavaLayerException e) {

	            System.out.println("Cannot Read audio file");
	        }
		   }
	   }
}
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class control 
{
	/**
	 * Variables
	 */
	//Pr�fvar ob ein Programm geladen ist
	boolean isLoad = false;
	//Liste um Befehle einzulesen
	private ArrayList<String> arrayL = new ArrayList<String>();
	//Dieses Array bildet den Programmspeicher des Pic ab.
	
	public GUI gui;
	public storage sto;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		/**
		 * Erzeugen des controllers
		 */
		 final control ctrl = new control();
		/**
		 * Erzeugen des storage
		 */
		 storage createsto = new storage();
		 ctrl.sto = createsto;
		/**
		 * Erstellen der GUI 
		 */
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					GUI frame = new GUI();
					frame.setVisible(true);
					ctrl.gui = frame;
					frame.ctrl = ctrl; //GUI->controller Verbindung
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Control Constructor
	 */
	public control ()
	{
		
	}
	
	public void readFile()
	{
		int linecounter = 0; //Z�hlt Zeilen mit Programmcode
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		File file = fc.getSelectedFile();
		//System.out.println(file.getPath()); testweise pfad ausgeben
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String zeile = null;
			while ((zeile = in.readLine()) != null) 
			{
				//System.out.println("Gelesene Zeile: " + zeile); zeile ausgeben
				arrayL.add(zeile); 
				if(zeile.charAt(0) != ' ')//linecounter wird nur erh�ht, wenn die Zeile Code enth�lt.
					linecounter++;
			}
			in.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		if(linecounter <= (14 * 1))
		{
			/**
			 * Programmspeicher l�schen
			 */
			for(int i = 0; i < (14*1024); i++)
				sto.progStorage[i]=0;
			/**
			 * Einlesen des Programms
			 */
			int j = 0; //Z�hler nur f�r Codezeilen
			for(int i=0; i < arrayL.size(); i++)//Z�hler f�r alle Zeilen
			{
				
				String zeile = arrayL.get(i);
				if(zeile.charAt(0) != ' ') //Codezeile?
				{
					String comand = zeile.substring(5, 9); //Programmcode extrahieren
					sto.progStorage[j] = Integer.parseInt(comand, 16); //Hexzahl in Int parsen & Programmspeicher f�llen
					System.out.println(sto.progStorage[j]); //Test
					j++;//Codez�hler erh�hen
				}
				
			}
			isLoad = true; //Angeben, dass ein Programm geladen wurde.
		}
		else
		{
			gui.showError(1); //
		}
		
	}


}

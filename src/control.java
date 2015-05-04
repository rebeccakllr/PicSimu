import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.table.TableColumn;

public class control
{
	/**
	 * Variables
	 */
	//Pr�fvar ob ein Programm geladen ist
	boolean isLoad = false;
	//Liste um Befehle einzulesen
	public ArrayList<String> arrayL = new ArrayList<String>();
	//Dieses Array bildet den Programmspeicher des Pic ab.
	
	public MyThread myThread;
	public static MyThread startThread;
	public GUI gui;
	private storage sto = storage.getInstance();
	private logic log = logic.getInstance();
	private int linecounter;
	public int getLinecounter() {
		return linecounter;
	}

	public Object[][] data;
	public JTable table_source_code;
	public boolean[] isSourcecode;
	
	private int markierung;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{		
		/**
		 * Erzeugen des controllers
		 */
//		 final control ctrl = new control();
		/**
		 * Erzeugen des storage
		 */
//		 storage createsto = new storage();
//		 ctrl.sto = createsto;
		/**
		* Erzeugen der Pic Logik
		*/
//		logic createlog = new logic();
//		ctrl.log = createlog;
//		ctrl.log.setStorage(ctrl.sto);
		/**
		 * Erstellen der GUI 
		 */
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					/*
					GUI frame = new GUI();
					frame.setVisible(true);
					ctrl.gui = frame;
					frame.ctrl = ctrl; //GUI->controller Verbindung
					ctrl.log.gui = frame;
					frame.sto = sto;
					frame.log = log;
					frame.initializeStorage();
					frame.initializeSpecialRegister();
					*/
					
					final control ctrl = new control();
					storage sto = storage.getInstance(); 
					//logic log = logic.getInstance();
					GUI newgui = new GUI();
	
					newgui.setVisible(true);
					/**
					 * Spawn Connections between Objects
					 */

					startThread = new MyThread();
					
					ctrl.gui = newgui;
					//ctrl.gui.startThread = startThread;
					
					ctrl.log.setGUI(newgui);
					ctrl.log.setCTRL(ctrl);
					
					sto.setGUI(newgui);
					sto.setCTRL(ctrl);
					
					ctrl.gui.ctrl = ctrl;
				
					newgui.initializeStorage();
					newgui.initializeSpecialRegister();
					
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
		linecounter = 0; //Z�hlt Zeilen mit Programmcode
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
			isSourcecode = new boolean[arrayL.size()];
			for (int i = 0; i < arrayL.size();i++)
			{
				if (arrayL.get(i).charAt(0) != ' '){
					isSourcecode[i] = true;
				}
				else{
					isSourcecode[i] = false;
				}
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		/**
		 * Programmtext in Tabelle schreiben
		 */
		data = new Object[arrayL.size()][2];
		
		for (int i=0; i < arrayL.size(); i++)
		{
			data[i][1]= arrayL.get(i);
		}
		
		
		//neue Tabelle erstellen und damit die alte ersetzen
		table_source_code = new JTable(data, gui.columnNames){
			//Editierbarkeit f�r zweite Spalte ausschalten
			@Override	
			public boolean isCellEditable(int row, int column){
				switch (column){
				case 0: return true;
				default: return false;
				}
			}
			//Checkbox in erste Spalte
			@Override
			public Class getColumnClass(int column){
				switch (column){
				case 0: return Boolean.class;
				default: return String.class;
				}
			}
		};
		gui.scrollPane_source_code.setViewportView(table_source_code);
		
		//Spaltenbreite von table_sourcecode setzen
		table_source_code.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn col_bp = table_source_code.getColumnModel().getColumn(0);
		col_bp.setPreferredWidth(20);
		TableColumn col_prog = table_source_code.getColumnModel().getColumn(1);
		col_prog.setMinWidth(500);
		table_source_code.setBounds(0, 0, 100, 100);
		
		
		
		if(linecounter <= (1024))
		{
			/**
			 * Programmspeicher l�schen
			 */
			for(int i = 0; i < (1024); i++)
				sto.setProgStorage(i,0);
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
					sto.setProgStorage(j, Integer.parseInt(comand, 16));//Hexzahl in Int parsen & Programmspeicher f�llen
					//System.out.println(sto.progStorage[j]); //Test
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
	

	/**
	 * Zeile zum Markieren ausw�hlen
	 */
	public void selectRow(){
		int sc_pc = 0;
		markierung = 0;
		//durchl�uft isSourcecode
		for (int i = 1; i<arrayL.size();i++)
		{
			if(isSourcecode[i]==true)
				sc_pc++;
			if (sc_pc == sto.getPC())
			{
				markierung = i;
				break;
			}
		}
		table_source_code.changeSelection(markierung, 1, false, false);
	}
	

}

package konjicevSkok;

import java.util.LinkedList;

import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;

public class GameButtons {
	/**
	 * Pomocna klasa koja kreira dugmice za igru a koriscene cuva u LinkedList.
	 * Sadrzi nekoliko funkcija, opis je uz svaku.
	 */
	private LinkedList<Button> usedButtons;	
	private static int numberOfFields; //brojac polja
	
	public GameButtons() {
		// Konstruktor-inicijalizuje listu koja treba da sadrzi dugmadi
		usedButtons = new LinkedList<Button>();
	}
	
	public Button createButton(String data) {
		/**
		 * Pravi button sa zadatim tekstom.
		 * @param data: tekst koji ide u button
		 * @return: kreiran button zute boje
		 */	
		
		Button button = new Button();
		button.setMinSize(50, 40);
		if (data.trim().isEmpty()) {  // ako 'data' ne sadrzi nijedan karakter
			button.setVisible(false); // napravi se nevidljivo dugme
			button.setDisable(true); // koje je onemoguceo
		}
		else { 				   // ako ima neki tekst napravi se button
			numberOfFields++; // i povecava se brojac polja
			button.setText(data);
			button.setStyle("-fx-background-color: yellow"); // button je zut
			button.setBorder(Border.EMPTY);
		}
		return button;
	}
	
	public boolean checkMove(Button button1, Button button2) {
		/** 
		 * Proverava da li moze da se odigra potez izmedju dva polja.
		 * Da bi potez bio moguc, mora se igrac kretati kao konj u sahu.
		 * 
		 * @param button1: polje na kome se igrac nalazi
		 * @param button2: polje na kome igrac zeli da skoci
		 * @return: true ako je potez moguc, false ako nije
		 */
		
		if (usedButtons.contains(button2))       // ako je zeljeno polje vec
			return false;						 // korisceno potez nije moguc
		int row1 = GridPane.getRowIndex(button1); // red prvog polja
		int row2 = GridPane.getRowIndex(button2); // red drugog polja
		int col1 = GridPane.getColumnIndex(button1); // kolona prvog polja
		int col2 = GridPane.getColumnIndex(button2); // kolona drugog polja
		
		int row = Math.abs(row2 - row1); // udaljenost izmedju redova drugog i prvog polja
		int col = Math.abs(col2 - col1); // udaljenost izmedju njihovih kolona
		
		/* Konj u sahu se krece ili dva polja horizontalno pa jedan vertikalno,
		 * ili dva vertikalno pa jedan horizontalno, tako da od gornjih
		 * vrednosti jedna treba da bude jednaka 2 a druga 1, ili obrnuto.
		 * Sve druge vrednosti znace da potez nije moguc.
		 */
		
		return ((row == 2 && col == 1) || (row == 1 && col == 2));
	}
	
	public void addUsedButton(Button button1, Button button2) {
		/** 
		 * Nakon odigranog poteza predjeno polje ubacuje u LinkedListu 
		 * 'usedButtons' gde se cuvaju sva koriscena dugmad
		 * 
		 * @param button1: polje na kome se igrac nalazio
		 * @param button2: polje na koje je igrac skocio i trenutno se nalazi
		 **/
		
		usedButtons.addLast(button2); // ubacuje ga na kraj liste
		button2.setStyle("-fx-background-color: red");
		// polje na kome se igrac nalazi stavlja se da bude crvene boje
		if (button1!=null)
			button1.setStyle("-fx-background-color: green");
			// polja koja je igrac presao postanu zelene boje
	}
	
	public void changeButtons(Button button1, Button button2) {
		/** 
		 * Menja izgled dugmadi, prvo postavlja da bude zute boje
		 * a drugo da bude crvene.
		 * Koristi se kao pomocna funkcija za vracanje poteza. 
		 * 
		 * @param button1: polje na kome se igrac nalazio
		 * @param button2: prethodno polje korisnika, kome se vraca
		 **/
		button1.setStyle("-fx-background-color: yellow");
		button2.setStyle("-fx-background-color: red");
	}
	
	public Button findPreviousButton() {
		/**
		 * Vraca, ali ne uklanja, poslednji element liste ili 
		 * vraca null ako je lista prazna. 
		 */
		return usedButtons.peekLast();	
	}
	public Button removeLastButton() {
		/**
		 * Uklanja i vraca poslednji element liste ili 
		 * vraca null ako je lista prazna. 
		 */
		return usedButtons.pollLast();
	}
	
	@Override
	public String toString() {
		// String reprezentacija objekta klase GameButtons. 
		StringBuilder sb = new StringBuilder();
		for (Button button : usedButtons) {
			String text = button.getText();
			sb.append(text);
		}
		return sb.toString();
	}
	
	public static int getNumberOfFields() {
		// vraca broj napravljenih polja
		return numberOfFields;
	}
	public int totalUsedButtons() {
		// vraca broj dugmadi u listi
		return usedButtons.size();
	}
	
}

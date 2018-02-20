package konjicevSkok;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
	
	/* 
	 * Aplikacija za igranje "Konjicevog skoka".
	 * 
	 * Opis zadatka dat je u pdf fajlu tekuceg direktorijuma.
	 * 
	 * Po pokretanju aplikacije otvara se prozor i omogucuje se korisniku da
	 * izabere zeljenu datoteku. Treba izabrati text file 'konjicevSkok' iz 
	 * tekuceg direktorijuma, mada bi igrica mogla da radi i sa bilo kojim
	 * fajlom koji je ispravnog sadrzaja.
	 */
	
	private static Stage primaryStage;	
	private static File file;
	private static GridPane fields = new GridPane();
	
	// polje na kome se igrac trenutno nalazi
	private static Button currentButton = new Button();
	// polje na kome se igrac nalazio pre odigranog poteza
	private static Button previousButton = new Button();
	// objekat pomocne klase GameButtons
	private static GameButtons usedButtons = new GameButtons();	
	
	private static Button ucitajBtn, vratiBtn;
	private static Label porukaLb, greskaLb;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Konjicev skok");
		
		// koreni cvor i pomocna GUI fukncija
		VBox root = new VBox(5);
		createGUI(root);
		
		Scene scene = new Scene(root, 430, 320);		
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
		
	}
	
	public void createGUI(VBox root) {	
		/**
		 * Pravi GUI interfejs, sa odgovarajucim dugmadima i funkcijama.
		 * @param root: VBox, cvorovi deca se rasporedjuju u jednu kolonu.
		 */
		
		// dugme za ucitavanje fajla
		ucitajBtn = new Button("Ucitaj iz fajla");
		
		/* Srednji cvor u kome ce biti smestena polja.
		 * Polja se stvaraju tek posto korisnik ucita fajl tako da pri 
		 * otvaranju prozora ovaj cvor nije jos vidljiv.
		 */
		VBox middle = new VBox();	
		// funkcija za smestanje ucitanih polja u cvor
		middle = read(middle);
		middle.setPrefWidth(430);
		
		/* Text Label gde ce se pojaviti poruka crvene boje ako
		 * korisnik pokusa da napravi pogresan potez. 
		 * Inicijalno je prazna. 
		 */		
		greskaLb = new Label();
		greskaLb.setTextFill(Color.RED);
		
		// donji HBox cvor, cvorovi deca se rasporedjuju u jedan red
		HBox bottom = new HBox();
		
		/* Text Label gde ce se smestati tekst koji korisnik dobija
		 * kako napreduje u igrici. 
		 */
		porukaLb = new Label();
		porukaLb.setMinWidth(370);
		
		/* Dugme koje omogucuje korisniku da vrati potez.
		 * Inicijalno je onemoguceno
		 */
		vratiBtn = new Button("Vrati");
		vratiBtn.setDisable(true);
		// funkcija za vracanje poteza
		returnMove();
		
		// cvorovi deca donjeg HBox cvora
		bottom.getChildren().addAll(porukaLb, vratiBtn);
		
		// deca korenog cvora, pozicionirana na centar
		root.getChildren().addAll(ucitajBtn, middle, greskaLb, bottom);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(10));
		
		// srednji cvor sa poljima ima prioritet u odnosu na ostale
		VBox.setVgrow(middle, Priority.ALWAYS);	
	}
	
	
	public VBox read(VBox middle) {
		/** 
		 * Funkcija koja se aktivira kada korisnik pritisne dugme 'Ucitaj iz fajla'.
		 * Omogucuje korisniku da izabere ulazni fajl sa lokalnog hard-diska.
		 * Nakon uspesnog izbora fajla dugme se onemogucuje, u centralnom delu
		 * prozora prikazuju se polja koja odgovaraju sadrzaju izabranog fajla
		 * a omogucuje se dugme 'Vrati' na dnu prozora
		 * Pretpostavlja se da ce korisnik odabrati fajl ispravnog sadrzaja.
		 * 
		 * @param: VBox middle, srednji cvor u kome ce biti smestena polja 
		 * @return: vraca taj cvor
		 **/

		ucitajBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// funkcija za otvaranje fajla
				file = openFile();
				if (file != null) {
					ucitajBtn.setDisable(true);
					vratiBtn.setDisable(false);
					/* Funkcija koja kreira polja od sadrzaja ulaznog fajla
					 * i smesta ih u GridPane fields.
					 */
					fields = createFields(file);
					/* GridPane fields se postavlja kao dete cvor,
					 * srednjem VBox cvoru, pozicionirano na centar.
					 */
					middle.getChildren().add(fields);
					middle.setAlignment(Pos.CENTER);
				}
			}
		});
		return middle;
	}
	
	public File openFile() {
		/**
		 * Funkcija za biranje fajla iz tekuceg direktorijuma.
		 * @return: ucitan fajl ili null ako korisnik ne izabere fajl
		 */
		
		File file = null;
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Resourse File");
		chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text files", "*.txt"));
		file = chooser.showOpenDialog(primaryStage);
		
		return file;
	}
	
	public GridPane createFields(File file) {	
		/**
		 * Pomocna funkcija za pravljenje polja od sadrzaja fajla.
		 * GridPane rasporedjuje cvorove u redove i kolone.
		 * 
		 * @param: ucitan fajl od strane korisnika
		 * @return: GridPane sa dugmadima
		 */

		try {
			// cita sve linije fajla i smesta ih u listu Stringova
			List<String> lines = Files.readAllLines(Paths.get(file.getName()), StandardCharsets.UTF_8);
			int j = 0; // brojac redova ucitanog fajla
			boolean firstLetter = true;
			
			// citanje svake linije ponaosob
			for (String l : lines) {
				// deli liniju na delove sa znakom ; kao delimiterom
				String line[] = l.split(";");
				int columnNo = line.length; // broj kolona u redu
				
				// pravljenje dugmadi (buttons)
				for (int i=0; i<columnNo; i++) {
					Button button = usedButtons.createButton(line[i]);
					
					// postavlja dugme u odgovarajucu poziciju
				    GridPane.setRowIndex(button, j);
				    GridPane.setColumnIndex(button, i);
				    
				    /* Nalazi prvo slovo u prvom redu i postavlja ga kao
				     * prvo dugme i trenutno polje igraca.
				     */
				    if (!(line[i].trim().equals("")) && firstLetter) {
				    	currentButton = button;
				    	/* Smesta prvo dugme u listu koriscenih dugmadi.
				    	 * Funkcija prima dva argumenta, prethodno dugme
				    	 * na koje se nalazio korisnik, i trenutno dugme
				    	 * na koje je skocio.
				    	 * Kako je trenutno dugme ujedno i prvo ucitano, 
				    	 * prethodno ne postoji pa je postavljeno za null. 
				    	 */
				    	usedButtons.addUsedButton(null, button);
				    	// slovo prvog polja se postavlja kao tekst poruke
				    	porukaLb.setText(usedButtons.toString());
				    	firstLetter = false;
				    }
				    /* Funkcija koja se aktivira ako korisnik pritisne bilo
				     * koje od napravljenih dugmadi.
				     */
				    button.setOnAction(new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent arg0) {
							// proverava potez izmedju trenutnog polja i pritisnutog dugmeta
							if (!usedButtons.checkMove(currentButton, button))
								// ako je nije ispravan korisnik se obavestava o tome
								greskaLb.setText("Nije dopusteno skociti na to polje.");
							else {
								// ako jeste ispravan, potez se odigrava
								gameMove(currentButton, button);
								// pritisnuto dugme se postavlja kao trenutno
								currentButton = button;
								/* Ako korisnik predje sva polja tekst poruke se oboji
								 * u zeleno a dugme 'Vrati' se onemogucuje.
								 */
								if (GameButtons.getNumberOfFields() == usedButtons.totalUsedButtons()) {
									porukaLb.setTextFill(Color.GREEN);
									vratiBtn.setDisable(true);
								}
							}				
						}
				    });
				    /* Napravljeno dugme se doda u GridPane fields.
				     * Dugmad su pozicionirana u centar, sa razmakom od 
				     * 3 piskela izmedju njih.
				     */
				    fields.getChildren().add(button);
				    fields.setAlignment(Pos.CENTER);
				    fields.setHgap(3);
				    fields.setVgap(3);
				}
				j++;
			}
		} catch (IOException e) {
			System.out.println("Greska pri radu sa datotekom.");
			System.exit(1); // prekid daljeg izvrsavanja programa
		}
		return fields;
	}
	
	public void gameMove(Button firstButton, Button secondButton){
		/**
		 * Funkcija za odigravanje poteza. 
		 * Smesta novo dugme u listu koriscenih dugmadi,
		 * a njegovo slovo dodaje u tekst poruke.
		 * 
		 * @param firstButton: trenutno polje korisnika
		 * @param secondButton: polje na koje je korisnik skocio
		 */
		usedButtons.addUsedButton(firstButton, secondButton);
		greskaLb.setText("");
		porukaLb.setText(usedButtons.toString());
	}
	
	public void returnMove() {
		/**
		 * Funkcija koja omogucuje korisniku da vrati potez. 
		 * Aktivira se kad se priticne dugme 'Vrati'.
		 *
		 **/
		vratiBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// vracanje poteza ima smisla ako je odigran bar jedan potez
				if (usedButtons.totalUsedButtons()>1) {
					// uklanja poslednje polje iz liste koriscenih dugmadi
					currentButton = usedButtons.removeLastButton();
					// nalazi prethodno polje korisnika
					previousButton = usedButtons.findPreviousButton();
					// menja im boje i zamenjuje ih
					usedButtons.changeButtons(currentButton, previousButton);
					currentButton = previousButton;
					// stampa ponovo tekst poruke
					porukaLb.setText(usedButtons.toString());
				}		
			}		
		});
	}
}

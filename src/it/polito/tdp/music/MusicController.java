package it.polito.tdp.music;

import java.net.URL;
import java.time.Month;
import java.util.*;
import java.util.ResourceBundle;

import it.polito.tdp.music.model.Artist;
import it.polito.tdp.music.model.Model;
import it.polito.tdp.music.model.Stat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class MusicController {
	Model model;
	public void setModel(Model model) {
		this.model=model;
		
		boxMese.getItems().addAll(model.getMesi());
	}
	
	private List<Artist> artisti=new ArrayList<Artist>();
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Month> boxMese;

    @FXML
    private Button btnArtisti;

    @FXML
    private Button btnNazioni;

    @FXML
    private TextArea txtResult;
    
    @FXML
    void doElencoArtisti(ActionEvent event) {
    	txtResult.clear();
    	artisti.clear();
    	Month m=boxMese.getValue();
    	if(m==null){
    		txtResult.appendText("Selezionare un mese.");
    		return;
    	}	
    	model.getTuttiArtisti();
    	model.getTuttiPaesi();
    	Map<Artist,Integer> stat=model.getStat(m);
    	
    	txtResult.appendText("Artisti di moda in "+m.toString()+":\n");
    	for(Artist a: stat.keySet()){
    		txtResult.appendText(a.getArtist()+" --> "+stat.get(a)+"\n");
    		artisti.add(a);
    	}
    }
 
    @FXML
    void doDistanzaMax(ActionEvent event) {
    	Month m=boxMese.getValue();
    	if(m==null){
    		txtResult.appendText("Selezionare un mese.");
    		return;
    	}
    	Stat stat=model.creaGrafo(artisti, m);
    	
    	txtResult.appendText("\nNumero massimo di artisti distinti in coumune:\n"+stat.getS()+" - "+stat.getD()+" --> "+stat.getPeso());
    }

    @FXML
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'MusicA.fxml'.";
        assert btnArtisti != null : "fx:id=\"btnArtisti\" was not injected: check your FXML file 'MusicA.fxml'.";
        assert btnNazioni != null : "fx:id=\"btnNazioni\" was not injected: check your FXML file 'MusicA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'MusicA.fxml'.";

    }
}


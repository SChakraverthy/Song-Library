package songlib.view;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @author Swapna Chakraverthy
 * @author Neel Patel
 */
public class SongLibController {
	
	@FXML
	private ListView<Song> listView;
	@FXML
	private TextArea songDetails;
	@FXML
	private TextField songName;
	@FXML
	private TextField songArtist;
	@FXML
	private TextField songAlbum;
	@FXML
	private TextField songYear;
	@FXML
	private Button apply;
	@FXML
	private Button cancel;
	
	private ObservableList<Song> obsList;
	
	private Document songData;
	
	public void start(Stage stage) {
		obsList = FXCollections.observableArrayList();
		
		//XML DOM
		songData = loadSongData();
		
		listView.setItems(obsList);
		
		listView.setOnMouseClicked((event) -> {
			showSongDetails();
		});
		
		//select first song and show its details
		listView.getSelectionModel().select(0);
		
		if(!obsList.isEmpty())
			showSongDetails();
	}
	
	private void showSongDetails() {
		Song song = listView.getSelectionModel().getSelectedItem();
		String s = "Name: " + song.getName() + "\nArtist: " + song.getArtist();
		if(song.getAlbum() != null && !song.getAlbum().isEmpty()) {
			s = s + "\nAlbum: " + song.getAlbum();
		}
		if(song.getYear() != 0) {
			s = s + "\nYear: " + song.getYear();
		}
		songDetails.setText(s);
	}
	
	private void clearText() {
		songName.clear();
		songArtist.clear();
		songAlbum.clear();
		songYear.clear();
		songName.setDisable(true);
		songArtist.setDisable(true);
		songAlbum.setDisable(true);
		songYear.setDisable(true);
		apply.setVisible(false);
		cancel.setVisible(false);
	}
	
	private void sortList() {
		Comparator<Song> comparator = Comparator.comparing(Song::getName);
		obsList.sort(comparator);
	}
	
	@FXML
	public void add() {
		songName.setDisable(false);
		songArtist.setDisable(false);
		songAlbum.setDisable(false);
		songYear.setDisable(false);
		apply.setVisible(true);
		cancel.setVisible(true);
		
		apply.setOnAction((event) -> {
			if((songName.getText() != null && !songName.getText().isEmpty()) 
					&& (songArtist.getText() != null && !songArtist.getText().isEmpty())) {
				Song song = new Song(songName.getText(), songArtist.getText());
				if(songAlbum.getText() != null && !songAlbum.getText().isEmpty())
					song.setAlbum(songAlbum.getText());
				else song.setAlbum("");
				if(songYear.getText() != null && !songYear.getText().isEmpty())
					song.setYear(Integer.valueOf(songYear.getText()));
				else song.setYear(0);
				if(!obsList.contains(song)) {
					
					obsList.add(song);
					
					//save to XML DOM
					Element songXML = songData.createElement("song");
					Element songNameXML = songData.createElement("Name");
					Element songArtistXML = songData.createElement("Artist");
					Element songAlbumXML = songData.createElement("Album");
					Element songYearXML = songData.createElement("Year");
					songNameXML.appendChild(songData.createTextNode(songName.getText()));
					songArtistXML.appendChild(songData.createTextNode(songArtist.getText()));
					songAlbumXML.appendChild(songData.createTextNode(songAlbum.getText()));
					songYearXML.appendChild(songData.createTextNode(songYear.getText()));
					songXML.appendChild(songNameXML);
					songXML.appendChild(songArtistXML);
					songXML.appendChild(songAlbumXML);
					songXML.appendChild(songYearXML);
					songData.getFirstChild().appendChild(songXML);
					saveSongData();
				}
				sortList();
				clearText();
			}
		});
		
		cancel.setOnAction((event) -> {
			clearText();
		});
	}
	
	@FXML
	public void delete() {
		
	}
	
	@FXML
	public void edit() {
		
	}
	
	/*
	 * Writes the data in songData to songlist.xml
	 */
	private void saveSongData() {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(new File("src/songlib/resources/songlist.xml"));
			Source input = new DOMSource(songData);
			transformer.transform(input, output);
			System.out.println("saving");
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Populates the obsList with songs from an XML file, and sorts it.
	 * 
	 * @return Document object containing song data.
	 */
	private Document loadSongData() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File("src/songlib/resources/songlist.xml"));
			document.getDocumentElement().normalize();

			NodeList nList = document.getElementsByTagName("song");
			
			for(int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) node;
					Song song = new Song();
					song.setName(e.getElementsByTagName("Name").item(0).getTextContent());
					song.setArtist(e.getElementsByTagName("Artist").item(0).getTextContent());
					song.setAlbum(e.getElementsByTagName("Album").item(0).getTextContent());
					if(e.getElementsByTagName("Year").item(0).getTextContent() != "")
						song.setYear(Integer.valueOf(e.getElementsByTagName("Year").item(0).getTextContent()));
					else song.setYear(0);
					obsList.add(song);				
				}
			}
			sortList();
			return document;
			
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}

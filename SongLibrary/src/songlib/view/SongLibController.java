
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
		
		//Load the XML file into songData and populate obsList with that data
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
	
	private void clearInput() {
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
					saveSong(song);
					//save to XML DOM
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("Song already exists. It will not be added.");
					alert.showAndWait();
				}
				sortList();
				listView.requestFocus();
				listView.getSelectionModel().select(song);
				showSongDetails();
				clearInput();
			}
		});
		
		cancel.setOnAction((event) -> {
			clearInput();
		});
	}
	
	@FXML
	public void delete() {
		
		apply.setVisible(true);
		cancel.setVisible(true);
		
		cancel.setOnAction((event)->{
			clearInput();
		});
		
		apply.setOnAction((event) -> {
			
			// Get the current song selection's details.
			Song song = listView.getSelectionModel().getSelectedItem();
			
			String s_name = song.getName();
			String s_artist = song.getArtist();
				
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
						
						Element e_name = (Element) e.getElementsByTagName("Name").item(0);
						Element e_artist = (Element) e.getElementsByTagName("Artist").item(0);
						Element e_album = (Element) e.getElementsByTagName("Album").item(0);
						Element e_year = (Element) e.getElementsByTagName("Year").item(0);
						
						if(s_name.equals(e_name.getTextContent()) && s_artist.equals(e_artist.getTextContent())) {
							
							// Found the Element want to delete. Delete the child nodes first, then the parent node.
							Node parent = e.getParentNode();
							e.removeChild(e_name);
							e.removeChild(e_artist);
							e.removeChild(e_album);
							e.removeChild(e_year);
							parent.removeChild(e);
						}
						
					}
				}
				
				// Save the changes to the xml file.
				Transformer transformer;
				try {
					transformer = TransformerFactory.newInstance().newTransformer();
					DOMSource source = new DOMSource(document);
					StreamResult output = new StreamResult("src/songlib/resources/songlist.xml");
					transformer.transform(source, output);
				} catch (TransformerFactoryConfigurationError | TransformerException e) {
					e.printStackTrace();
				}
					
				obsList.remove(song);
				showSongDetails();
				sortList();
				clearInput();
			
			} catch (ParserConfigurationException | SAXException | IOException e1) {
				e1.printStackTrace();
			}
			
			
			
		});
		
		return;
	}
	
	@FXML
	public void edit() {
		
		songName.setDisable(false);
		songArtist.setDisable(false);
		songAlbum.setDisable(false);
		songYear.setDisable(false);
		apply.setVisible(true);
		cancel.setVisible(true);
		
		// Get the current song selection's details and place the details in the text field in the editor. 
		Song song = listView.getSelectionModel().getSelectedItem();
		
		String s_name = song.getName();
		String s_artist = song.getArtist();
		
		songName.setText(s_name);
		songArtist.setText(s_artist);			
		
		if(song.getAlbum() != null) {
			String s_album = song.getAlbum();
			songAlbum.setText(s_album);
		}
		
		if(song.getYear() != 0) {
			String s_year = String.valueOf(song.getYear());
			songYear.setText(s_year);
		}
		
		cancel.setOnAction((event) -> {
			clearInput();
		});
		
		apply.setOnAction((event) -> {
			
			// Grab the changes made by the user and update the xml file with the changed details.
			String song_name;
			String song_artist;
			
			if(songName.getText() != null && !songName.getText().isEmpty()) {
				song_name = songName.getText();
			} else {
				song_name = s_name;
			}
			
			if(songArtist.getText() != null && !songArtist.getText().isEmpty()) {
				song_artist = songArtist.getText();
			} else {
				song_artist = s_artist;
			}			
			
			Song newSongInfo = new Song(song_name, song_artist);
			
			if(songAlbum.getText() != null && !songAlbum.getText().isEmpty()) {
				newSongInfo.setAlbum(songAlbum.getText());
			} else {
				newSongInfo.setAlbum(song.getAlbum());;
			}
			
			if(songYear.getText() != null && !songYear.getText().isEmpty()) {
				newSongInfo.setYear(Integer.valueOf(songYear.getText()));
			} else {
				newSongInfo.setYear(song.getYear());;
			}
			
			
			if(newSongInfo.equals(song)) {
								
				if((newSongInfo.getAlbum() != null && song.getAlbum() != null) && (newSongInfo.getYear() != 0 && song.getYear() != 0)) {
					
					// Check to see if the information is the same.
					if((newSongInfo.getAlbum()).equals(song.getAlbum()) && newSongInfo.getYear() == song.getYear()) {
						
						sortList();
						showSongDetails();
						clearInput();
						return;
						
					}
					
				}
			}
			
			if(!newSongInfo.equals(song) && obsList.contains(newSongInfo)) {
			
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Song already exists. Unable to edit.");
				alert.showAndWait();

				sortList();
				showSongDetails();
				clearInput();
				return;
			}
			
			updateSong(song, newSongInfo);
			
			// Change the view on the observable list.
			obsList.remove(song);
			obsList.add(newSongInfo);
			
			listView.requestFocus();
			listView.getSelectionModel().select(newSongInfo);		
			
			sortList();
			showSongDetails();
			clearInput();
		});
		
	}
		
	/*
	 * Saves the song to songData (XML DOM object). Then writes it to the xml file.
	 */
	private void saveSong(Song song) {
		Element songXML = songData.createElement("song");
		Element songNameXML = songData.createElement("Name");
		Element songArtistXML = songData.createElement("Artist");
		Element songAlbumXML = songData.createElement("Album");
		Element songYearXML = songData.createElement("Year");
		songNameXML.appendChild(songData.createTextNode(song.getName()));
		songArtistXML.appendChild(songData.createTextNode(song.getArtist()));
		songAlbumXML.appendChild(songData.createTextNode(song.getAlbum()));
		songYearXML.appendChild(songData.createTextNode(String.valueOf(song.getYear())));
		songXML.appendChild(songNameXML);
		songXML.appendChild(songArtistXML);
		songXML.appendChild(songAlbumXML);
		songXML.appendChild(songYearXML);
		songData.getFirstChild().appendChild(songXML);
		
		saveSongData();
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
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Populates obsList with songs from an XML file, and sorts it.
	 * 
	 * @return XML Document object containing song data.
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

	/**
	 * Parses the XML file to update the song data and save changes.
	 **/
private void updateSong(Song oldSongInfo, Song newSongInfo) {
		
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
					
					Element e_name = (Element) e.getElementsByTagName("Name").item(0);
					Element e_artist = (Element) e.getElementsByTagName("Artist").item(0);
					Element e_album = (Element) e.getElementsByTagName("Album").item(0);
					Element e_year = (Element) e.getElementsByTagName("Year").item(0);
					
					if((oldSongInfo.getName()).equals(e_name.getTextContent())){
						e_name.setTextContent(newSongInfo.getName());						
					}
					
					if((oldSongInfo.getArtist()).equals(e_artist.getTextContent())) {
						e_artist.setTextContent(newSongInfo.getArtist());
					}
					
					if((oldSongInfo.getAlbum()).equals(e_album.getTextContent())) {
						e_album.setTextContent(newSongInfo.getAlbum());
					}
					
					if((String.valueOf(oldSongInfo.getYear())).equals(e_year.getTextContent())) {
						e_year.setTextContent(String.valueOf(newSongInfo.getYear()));
					}					
					
				}
			}
			
			// Save the changes to the xml file.
			Transformer transformer;
			try {
				transformer = TransformerFactory.newInstance().newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult output = new StreamResult("src/songlib/resources/songlist.xml");
				transformer.transform(source, output);
			} catch (TransformerFactoryConfigurationError | TransformerException e) {
				e.printStackTrace();
			}
			
			return;
			
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		}
		return;
	}

	
}
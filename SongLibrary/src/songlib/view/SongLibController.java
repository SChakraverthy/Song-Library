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
import javafx.stage.Stage;

/**
 * @author Swapna Chakraverthy
 * @author Neel Patel
 */
public class SongLibController {
	
	@FXML ListView<Song> listView;
	@FXML Button add;
	@FXML Button delete;
	@FXML Button edit;
	
	private ObservableList<Song> obsList;
	
	private Document songData;
	
	public void start(Stage stage) {
		obsList = FXCollections.observableArrayList();
		
		songData = loadSongData();
		
		listView.setItems(obsList);
		
		// select the first item
		listView.getSelectionModel().select(0);
		
	}
	
	public void add() {
		
	}
	
	public void delete() {
		
	}
	
	public void edit() {
		
	}
	
	/*
	 * Writes the data in songData to songlist.xml
	 */
	public void saveSongData() {
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

			NodeList nList = document.getElementsByTagName("record");
			
			for(int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) node;
					Song song = new Song();
					song.setName(e.getElementsByTagName("Name").item(0).getTextContent());
					song.setArtist(e.getElementsByTagName("Artist").item(0).getTextContent());
					obsList.add(song);				
				}
			}
			//Sort the list of songs
			Comparator<Song> comparator = Comparator.comparing(Song::getName);
			obsList.sort(comparator);
			
			return document;
			
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}

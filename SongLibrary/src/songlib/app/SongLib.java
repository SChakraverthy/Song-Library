package songlib.app;
	
import javafx.application.Application;
import javafx.stage.Stage;
import songlib.view.SongLibController;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;

/**
 * @author Swapna Chakraverthy
 * @author Neel Patel
 */
public class SongLib extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/songlib/view/SongLib.fxml"));
			GridPane root = (GridPane)loader.load();
			
			SongLibController controller = loader.getController();
			
			controller.start(stage);
			
			Scene scene = new Scene(root,800,600);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setTitle("Song Library");
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

package quickencrypt;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("EncryptView.fxml"));

			Parent root = loader.load();

			stage.initStyle(StageStyle.DECORATED);
			stage.setTitle("Quick File Encryption");
			//stage.setResizable(false);

			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/encrypt.png"))); 

			//properly close application (if this doesn't happen automatically)
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
			
			stage.show();
			root.requestFocus();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}

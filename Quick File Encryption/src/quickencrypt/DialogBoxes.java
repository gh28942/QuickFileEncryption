package quickencrypt;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DialogBoxes {

	public static void showMessageBox(String Title, String HeaderText, String ContextText, String imgStr) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(Title);
				alert.setHeaderText(HeaderText);
				alert.setContentText(ContextText);
				if(!imgStr.equalsIgnoreCase("none"))
					alert.setGraphic(new ImageView(this.getClass().getResource(imgStr).toString()));
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(this.getClass().getResource("/encrypt.png").toString()));
				alert.show();
			}
		});
	}

	public static void showErrorBox(String Title, String HeaderText, String ContextText) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle(Title);
				alert.setHeaderText(HeaderText);
				alert.setContentText(ContextText);
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image(this.getClass().getResource("/encrypt.png").toString()));
				alert.show();
			}
		});
	}
}

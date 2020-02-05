package quickencrypt;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

public class Controller {

	@FXML
	TextField textfield;
	@FXML
	TextArea textarea;
	@FXML 
	private AnchorPane ap;
	
	int processedFiles = 0;
	String currentFolder = "user.home";
	
	@FXML
	public void encryptText() {
		try {
			final String secretKey = textfield.getText();
		    String originalString = textarea.getText();
		    String encryptedString = AES.encrypt(originalString, secretKey);
		    textarea.setText(encryptedString);
		} catch (Exception e) {
			DialogBoxes.showErrorBox("Encryption error", "Could not encrypt text", "Please check if your key is valid and correct.");
		}
	}
	
	@FXML
	public void decryptText() {
		try {
			final String secretKey = textfield.getText();
		    String encryptedString = textarea.getText();
		    String decryptedString = AES.decrypt(encryptedString, secretKey);
		    textarea.setText(decryptedString);
		} catch (Exception e) {
			DialogBoxes.showErrorBox("Decryption error", "Could not decrypt text", "Please check if your key is valid and correct.");
		}
	}

	@FXML
	public void openFile() {
		
		File fileToRead=chooseFile("Open File");
				
		//load text
		String contentString = readFileContent(fileToRead);
		
	    textarea.setText(contentString);
	}
	
	public File chooseFile(String titleText) {
		
		//open filechooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(titleText);
		
		//remember last folder & show it to the user
		if(currentFolder.contentEquals("user.home"))
			fileChooser.setInitialDirectory(new File(System.getProperty(currentFolder)));
		else
			fileChooser.setInitialDirectory(new File(currentFolder));
			
		//let the user choose a file
		Path openfile = fileChooser.showOpenDialog(new Stage()).toPath();
		String currentFile = openfile.toString();
		currentFolder = openfile.toFile().getParent().toString(); //show the last used folder to the user next time
		
		return new File(currentFile);
	}
	
	public String readFileContent(File fileToRead) {
		StringBuilder content = new StringBuilder();
		try( FileReader fileStream = new FileReader( fileToRead ); 
		    BufferedReader bufferedReader = new BufferedReader( fileStream ) ) {

		    String line = null;
		    while( (line = bufferedReader.readLine()) != null ) {
		    	content.append(line + "\n");
		    }
		    String contentString = content.toString();
		    contentString = contentString.substring(0, contentString.length() - 1); //removes last linebreak (to output the accurate file content)
		    
			return contentString;
		    
		} catch ( FileNotFoundException ex ) {
		    ex.printStackTrace();
		} catch ( IOException ex ) {
			ex.printStackTrace();
		}
		return "Could not read file content!";
	}

	@FXML
	public void saveFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		
		//remember last folder & show it to the user
		if(currentFolder.contentEquals("user.home"))
			fileChooser.setInitialDirectory(new File(System.getProperty(currentFolder)));
		else
			fileChooser.setInitialDirectory(new File(currentFolder));
				
		File file = fileChooser.showSaveDialog(new Stage());
		currentFolder = file.getParent().toString();
		
		saveFileContent(file, textarea.getText() ,true);
	}
	
	public void saveFileContent(File file, String content, boolean showMessagebox) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file, false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(content);  //New line
			printWriter.close();
	    
			if(showMessagebox)
				DialogBoxes.showMessageBox("File saved!", "\"" + file + "\" was stored successfully!", "", "none");
		} catch (IOException e) {
			DialogBoxes.showErrorBox("Error (IOException)", e.toString(), "Could not save file.");
			e.printStackTrace();
		} 
	}
	
	@FXML
	public void clearText(){
		textarea.setText("");
	}
	
	@FXML
	public void about(){
		String MITlicense = "Copyright 2020 GerH.\n\nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation\n files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, \nmerge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom\n the Software is furnished to do so, subject to the following conditions:\nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT\n LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. \nIN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, \nDAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR \nIN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.";

		DialogBoxes.showMessageBox("About", MITlicense, "Copyright by GerH, Developer Github: gh28942\nIcon made by Smashicons from www.flaticon.com", "/encrypt_small.png");
	}
	
	@FXML
	public void EncryptFolder(){
		File selectedDirectory = chooseFolder(true);
		initiateSubfolderSearch(selectedDirectory.getAbsolutePath(), true);
	}
	
	@FXML
	public void DecryptFolder(){
		File selectedDirectory = chooseFolder(false);
		initiateSubfolderSearch(selectedDirectory.getAbsolutePath(), false);
	}
	
	public File chooseFolder(boolean encrypt) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		if(encrypt)
			dirChooser.setTitle("Choose a folder to encrypt (includes subfolders)");
		else
			dirChooser.setTitle("Choose a folder to decrypt (includes subfolders)");

		//remember last folder & show it to the user
		if(currentFolder.contentEquals("user.home"))
			dirChooser.setInitialDirectory(new File(System.getProperty(currentFolder)));
		else
			dirChooser.setInitialDirectory(new File(currentFolder));

		File chosenFolder = dirChooser.showDialog(new Stage());
		currentFolder = chosenFolder.toString();
		return chosenFolder;
	}
	
	public void initiateSubfolderSearch(String folderToLookInto, boolean encrypt) {
		processedFiles = 0;
		textarea.setText("> "+folderToLookInto);
	    File[] files = new File(folderToLookInto).listFiles();
	    lookupFiles(files, encrypt);
	    if(encrypt)
	    	updateWall(processedFiles + " files were encrypted successfully!");
	    else
	    	updateWall(processedFiles + " files were decrypted successfully!");
	}

	public void lookupFiles(File[] files, boolean encrypt) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	        	updateWall(">> \\" + file.getName() + ": ");
	            lookupFiles(file.listFiles(), encrypt); // recursive
	        } else {
	            //encrypt/decrypt
	            if(encrypt)
	            	encryptFile(file, true);
	            else
	            	decryptFile(file, true);
	        }
	    }
	}
	
	public void encryptFile(File file, boolean updateWall) {
		final String secretKey = textfield.getText();
	    String originalString = readFileContent(file);
	    if(!originalString.contentEquals("Could not read file content!")) {
	    	boolean isBinary = isBinaryFile(file);
			if(!isBinary) {
			    String encryptedString = AES.encrypt(originalString, secretKey);
			    saveFileContent(file, encryptedString, false);
			    if (updateWall)
			    	updateWall("File encrypted: " + file.getName());
	            processedFiles++;
			}
	    }
	    else {
	    	if (updateWall)
	    		updateWall("Could not read file "+file.getAbsolutePath());
	    }
	}
	
	public void decryptFile(File file, boolean updateWall) {
		final String secretKey = textfield.getText();
	    String encryptedString = readFileContent(file);
	    if(!encryptedString.contentEquals("Could not read file content!")) {
	    	boolean isBinary = isBinaryFile(file);
			if(!isBinary) {
		    	String decryptedString = AES.decrypt(encryptedString, secretKey);
		    	saveFileContent(file, decryptedString, false);
		    	if (updateWall)
		    		updateWall("File decrypted: " + file.getName());
	            processedFiles++;
			}
	    }
	    else {
	    	if (updateWall)
	    		updateWall("Could not read file "+file.getAbsolutePath());
	    }
	}
	
	public void updateWall(String toAdd) {
		String wallText = textarea.getText();
		wallText+="\n"+toAdd;
		textarea.setText(wallText);
	}
	 
	boolean isBinaryFile(File f) {
		try {
	        String type = Files.probeContentType(f.toPath());
	        if (type == null) {
	            //type couldn't be determined, assume binary
	            return true;
	        } else if (type.startsWith("text")) {
	            return false;
	        } else {
	            //type isn't text
	            return true;
	        }
		} catch (IOException e) {
			e.printStackTrace();
			//error occurred, return true to avoid mistakes
			return true;
		}
    }
	
	public void encryptFile() {
		File file = chooseFile("Encrypt a file");
		encryptFile(file, false);
		textarea.setText("Encryption finished for the file " + file.getAbsolutePath());
	}
	
	public void decryptFile() {
		File file = chooseFile("Decrypt a file");
		decryptFile(file, false);
		textarea.setText("Decryption finished for the file " + file.getAbsolutePath());
	}
}

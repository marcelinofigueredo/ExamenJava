
package com.mycompany.appinvestigacion;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author marcelino
 */
public class MainController implements Initializable {
    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    @FXML
    private TabPane tabpane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

   
    private void cargarPestania(String nombre){
        try{
            FXMLLoader loader = new FXMLLoader();
            Parent vistaMarca = loader.load(MainController.class.getResourceAsStream(nombre+".fxml"));
            Tab t=new Tab();
            t.setText(nombre);
            t.setContent(vistaMarca);
            this.tabpane.getTabs().add(t);
            this.tabpane.getSelectionModel().select(t);
        }catch(IOException e){
            LOG.log(Level.SEVERE, "Error al cargar FXML de "+nombre, e);
        }
    }

    @FXML
    private void OnActionMarca(ActionEvent event) {
        this.cargarPestania("primary");
    }

    @FXML
    private void OnActionProducto(ActionEvent event) {
        this.cargarPestania("producto");
    }
    
    
}

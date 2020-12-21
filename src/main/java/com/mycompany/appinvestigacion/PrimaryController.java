package com.mycompany.appinvestigacion;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;



public class PrimaryController implements Initializable{
    
    private static final Logger LOG = Logger.getLogger(PrimaryController.class.getName());

    @FXML
    private TextField txfId;
    @FXML
    private TextField txfDescripcion;
    

    private Connection con=null;
    @FXML
    private TableColumn<Marca, Integer> colCodigo;
    @FXML
    private TableColumn<Marca, String> colDescripcion;
    @FXML
    private TableView<Marca> tblDatos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        this.colCodigo.setCellValueFactory(new PropertyValueFactory<>("Codigo"));
        this.colDescripcion.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));
        
        try{
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_investigacion", "admin", "12345");
            }catch(SQLException ex){
                LOG.log(Level.SEVERE, "Error al conectar a la base de datos");
                Alert al=new Alert(AlertType.ERROR);
                al.setTitle("Error de conexion");
                al.setHeaderText("No se puede conectar a la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
                System.exit(1);
            }
        
        this.cargarDatos();
        
        this.tblDatos.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Marca> obs, Marca valorAnterior, Marca valorNuevo) -> {
        if (valorNuevo != null){
            this.txfId.setText(valorNuevo.getCodigo().toString());
            this.txfDescripcion.setText(valorNuevo.getDescripcion());
        }
    });
}
    
    private void cargarDatos(){
        this.tblDatos.getItems().clear();
        try{
            String sql="SELECT * FROM marca";
            Statement stm=this.con.createStatement();
            ResultSet resultado=stm.executeQuery(sql);
            while(resultado.next()){
                Integer cod = resultado.getInt("idmarca");
                String desc = resultado.getString("descripcion");
                Marca m=new Marca(cod, desc);
                this.tblDatos.getItems().add(m);
            }
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al cargar datos de la BD", ex);
        }
    }
    
    @FXML
    private void onActionRegistrar(ActionEvent event) {
        
        String descripcion = this.txfDescripcion.getText();
        
     
        try{
            
            String sql = "INSERT INTO marca(descripcion) VALUES(?)";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.execute();
            Alert al=new Alert(AlertType.INFORMATION);
            al.setTitle("Exito");
            al.setHeaderText("Marca guardada correctamente");
            al.show();
            this.txfId.clear();
            this.txfDescripcion.clear();
            this.cargarDatos();
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al insertar", ex);
               Alert al=new Alert(AlertType.ERROR);
                al.setTitle("Error de conexion");
                al.setHeaderText("No se puede insertar registro a la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        String id = this.txfId.getText();
        String descripcion = this.txfDescripcion.getText();
        String sql = "UPDATE marca SET descripcion = ? WHERE idmarca = ?";
        
        try{
            PreparedStatement stm = this.con.prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.setInt(2, Integer.parseInt(id));
            stm.execute();
            Alert al=new Alert(AlertType.INFORMATION);
            al.setTitle("Exito");
            al.setHeaderText("Marca editada correctamente");
            al.show();
            this.txfId.clear();
            this.txfDescripcion.clear();
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al editar", ex);
               Alert al=new Alert(AlertType.ERROR);
                al.setTitle("Error de conexion");
                al.setHeaderText("No se puede editar registro a la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
        this.txfId.setText("");
        this.txfDescripcion.setText("");
     
    }

    @FXML
    private void onActionEliminar(ActionEvent event) {
        String strCodigo = this.txfId.getText();
        String strDescripcion = this.txfDescripcion.getText();
        
        if (strCodigo.isEmpty()){
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error al eliminar");
            a.setHeaderText("Ingrese un codigo");
            a.show();
        }else {
            Alert alConfirm = new Alert(AlertType.CONFIRMATION);
            alConfirm.setTitle("Confirmar");
            alConfirm.setHeaderText("¿Desear Eliminar la marca?");
            alConfirm.setContentText(strCodigo + " - " + strDescripcion);
            Optional<ButtonType> accion = alConfirm.showAndWait();
            
            if(accion.get().equals(ButtonType.OK)){
            try{
                String sql = "DELETE FROM marca WHERE idmarca = ?";
                PreparedStatement stm = con.prepareStatement(sql);
                Integer cod=Integer.parseInt(strCodigo);
                stm.setInt(1, cod);
                stm.execute();
                int cantidad = stm.getUpdateCount();
                if(cantidad == 0){
                    Alert a=new Alert(AlertType.ERROR);
                     a.setTitle("Error al eliminar");
                    a.setHeaderText("No existe la marca con codigo"+strCodigo);
                     a.show();
                }else{
                     Alert a=new Alert(AlertType.INFORMATION);
                     a.setTitle("Eliminado");
                    a.setHeaderText("Marca eliminada correctamente.");
                     a.show();
                     this.cargarDatos();
                }
            }catch (SQLException ex){
                LOG.log(Level.SEVERE, "Error al eliminar", ex);
            }
        }
    }
}
}

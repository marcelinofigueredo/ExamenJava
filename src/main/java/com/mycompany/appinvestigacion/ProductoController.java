/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author marcelino
 */
public class ProductoController implements Initializable {
    private static final Logger LOG = Logger.getLogger(PrimaryController.class.getName());

    @FXML
    private TextField txfCodigo;
    @FXML
    private TextField txfDesc;
    @FXML
    private TextField txfCantidad;
    
    @FXML
    private TextField txfPrecio;
    @FXML
    private ComboBox<String> cmbIva;
    @FXML
    private ComboBox<Marca> cmbMarca;
    
    private ConexionBD conex;
    @FXML
    private TableView<Producto> tblDatos2;
    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colDesc;
    @FXML
    private TableColumn<Producto, Float> colCantidad;
    @FXML
    private TableColumn<Producto, Float> colIVA;
    @FXML
    private TableColumn<Producto, Float> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colIdmarca;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.colId.setCellValueFactory(new PropertyValueFactory<>("Codigo"));
        this.colDesc.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));
        this.colCantidad.setCellValueFactory(new PropertyValueFactory<>("Cantidad"));
        this.colIVA.setCellValueFactory(new PropertyValueFactory<>("Iva"));
        this.colPrecio.setCellValueFactory(new PropertyValueFactory<>("Precio"));  
        this.colIdmarca.setCellValueFactory(new PropertyValueFactory<>("Marca"));
        
        this.cmbMarca.setCellFactory((ListView<Marca> l)->{
            
            return new ListCell<Marca>(){
                @Override
                protected void updateItem(Marca m, boolean empty){
                    if(!empty){
                        this.setText("("+m.getCodigo()+") "+m.getDescripcion());
                    }else{
                        this.setText("");
                    }
                    super.updateItem(m, empty);
                }
            };
        });
        
        //Establecer la forma en que el combo va a mostrar la marca seleccioinada
        this.cmbMarca.setButtonCell(new ListCell<Marca>(){
                @Override
                protected void updateItem(Marca m, boolean empty){
                    if(!empty){
                        this.setText("("+m.getCodigo()+") "+m.getDescripcion());
                    }else{
                        this.setText("");
                    }
                    super.updateItem(m, empty);
                }
            }
        );
                    
        //Cargar los posibles valores en el combo de IVA
        this.cmbIva.getItems().add("10%");
        this.cmbIva.getItems().add("5%");
        this.cmbIva.getItems().add("Excento");
        this.cmbIva.getSelectionModel().selectFirst();

        //Se crea la conexion a la base de datos con la clase creada para el efecto
        this.conex = new ConexionBD();
        //Invocamos al metodo que trae los registros de la tabla marca para cargar en el combo
        this.cargarMarcas();
        this.cargarDatos();
        
        this.tblDatos2.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Producto> obs, Producto valorAnterior, Producto valorNuevo) -> {
        if (valorNuevo != null){
            this.txfCodigo.setText(valorNuevo.getCodigo().toString());
            this.txfDesc.setText(valorNuevo.getDescripcion());
            this.txfCantidad.setText(valorNuevo.getCodigo().toString());
            this.txfPrecio.setText(valorNuevo.getCodigo().toString());
            for(Marca mc : this.cmbMarca.getItems()){
                if (mc.getCodigo().equals(this.tblDatos2.getSelectionModel().getSelectedItem().getMarca())){
                    this.cmbMarca.getSelectionModel().select(mc);
                    break;
                }
            }
        }
    });
    }
        //Metodo que consulta a la base de datos y carga las marcas en el combo
    private void cargarMarcas(){
        try{
            String sql = "SELECT * FROM marca";
            Statement stm = this.conex.getConexion().createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while(resultado.next()){
                this.cmbMarca.getItems().add(new Marca(resultado.getInt("idmarca"), resultado.getString("descripcion")));
            }
        }catch(Exception ex){
            LOG.log(Level.SEVERE, "Error al cargar Marcas", ex);
        }
    }
    
    

    @FXML
    private void onActionRegistraR(ActionEvent event) {
        String codigo = this.txfCodigo.getText();
        String descripcion = this.txfDesc.getText();
        String cantidad = this.txfCantidad.getText();
        String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        Integer iv;
        switch (iva){
            case"10%":
                iv = 10;
                break;
            case"5%":
                iv = 5;
                break;
            default:
                iv = 0;
                break;
        }
        String precio = this.txfPrecio.getText();
        Integer marca = this.cmbMarca.getSelectionModel().getSelectedItem().getCodigo();
 
        if(codigo.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty() || iva.isEmpty() || precio.isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Complete todos los campos.");
            a.show();
        }else{
        
        try{
            
            String sql = "INSERT INTO producto(descripcion, cantidad, iva, precio, idmarca) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stm = conex.getConexion().prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.setString(2, cantidad);
            stm.setInt(3, iv);
            stm.setString(4, precio);
            stm.setInt(5, marca);
            
            stm.execute();
            Alert al=new Alert(Alert.AlertType.INFORMATION);
            al.setTitle("Exito");
            al.setHeaderText("Producto guardada correctamente");
            al.show();
            this.txfCodigo.clear();
            this.txfDesc.clear();
            this.cargarMarcas();
            this.cargarDatos();
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al insertar", ex);
               Alert al=new Alert(Alert.AlertType.ERROR);
                al.setTitle("Error de conexion");
                al.setHeaderText("No se puede insertar registro a la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
        }
    }
    }
        private void cargarDatos(){
        this.tblDatos2.getItems().clear();
        try{
            String sql="SELECT * FROM producto";
            Statement stm=this.conex.getConexion().createStatement();
            ResultSet resultado=stm.executeQuery(sql);
            while(resultado.next()){
                Integer cod = resultado.getInt("idproducto");
                String desc = resultado.getString("descripcion");
                Float cant = resultado.getFloat("cantidad");
                String iva = resultado.getString("iva");
                Float prec = resultado.getFloat("precio");
                Integer marc = resultado.getInt("idmarca");
                Producto p=new Producto(cod, desc, cant, iva, prec, marc);
                this.tblDatos2.getItems().add(p);
            }
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al cargar datos de la BD", ex);
        }
    }

    @FXML
    private void onActionEditaR(ActionEvent event) {
        String id = this.txfCodigo.getText();
        String descripcion = this.txfDesc.getText();
        String cantidad = this.txfCantidad.getText();
              String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        Integer iv;
        switch (iva){
            case"10%":
                iv = 10;
                break;
            case"5%":
                iv = 5;
                break;
            default:
                iv = 0;
                break;
        }
        String precio = this.txfPrecio.getText();
        Integer marca = this.cmbMarca.getSelectionModel().getSelectedItem().getCodigo();
        
        String sql = "UPDATE producto SET descripcion = ?, cantidad = ?, iva = ?, precio = ?, idmarca = ? WHERE idproducto = ?";
        
        try{
            PreparedStatement stm = this.conex.getConexion().prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.setString(2, cantidad);
            stm.setInt(3, iv);
            stm.setString(4, precio);
            stm.setInt(5, marca);
            stm.setInt(6, Integer.parseInt(id));
            stm.execute();
            Alert al=new Alert(Alert.AlertType.INFORMATION);
            al.setTitle("Exito");
            al.setHeaderText("Marca editada correctamente");
            al.show();
            this.txfCodigo.clear();
            this.txfDesc.clear();
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al editar", ex);
               Alert al=new Alert(Alert.AlertType.ERROR);
                al.setTitle("Error de conexion");
                al.setHeaderText("No se puede editar registro a la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
        }
              this.cargarMarcas();
            this.cargarDatos();
    }

    @FXML
    private void onActionEliminaR(ActionEvent event) {
     String strCodigo = this.txfCodigo.getText();
        String strDescripcion = this.txfDesc.getText();
        String strCantidad = this.txfCantidad.getText();
        String strIva = this.cmbIva.getSelectionModel().getSelectedItem();
        String strPrecio = this.txfPrecio.getText();
     
        
        if (strCodigo.isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error al eliminar");
            a.setHeaderText("Ingrese un codigo");
            a.show();
        }else {
            Alert alConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            alConfirm.setTitle("Confirmar");
            alConfirm.setHeaderText("¿Desear Eliminar el producto?");
            alConfirm.setContentText(strCodigo + " - " + strDescripcion+ " - "+ strCantidad+ " - "+strIva+ " - "+strPrecio);
            Optional<ButtonType> accion = alConfirm.showAndWait();
            
            if(accion.get().equals(ButtonType.OK)){
            try{
                String sql = "DELETE FROM producto WHERE idproducto = ?";
                PreparedStatement stm = conex.getConexion().prepareStatement(sql);
                Integer cod=Integer.parseInt(strCodigo);
                stm.setInt(1, cod);
                stm.execute();
                int cantidad = stm.getUpdateCount();
                if(cantidad == 0){
                    Alert a=new Alert(Alert.AlertType.ERROR);
                     a.setTitle("Error al eliminar");
                    a.setHeaderText("No existe el producto con codigo"+strCodigo);
                     a.show();
                }else{
                     Alert a=new Alert(Alert.AlertType.INFORMATION);
                     a.setTitle("Eliminado");
                    a.setHeaderText("Producto eliminada correctamente.");
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
    





   


    

package parquimetros.modelo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.utils.Conexion;

public class ModeloImpl implements Modelo {
	
	private static Logger logger = LoggerFactory.getLogger(ModeloImpl.class);	

	protected Connection conexion = null;

	@Override
	public boolean conectar(String username, String password) {
		logger.info("Se establece la conexión con la BD.");
        this.conexion = Conexion.getConnection(username, password);        
    	return (this.conexion != null);	
	}

	@Override
	public void desconectar() {
		logger.info("Se desconecta la conexión a la BD.");
		Conexion.closeConnection(this.conexion);		
	}

	@Override
	public ResultSet consulta(String sql)
	{
		logger.info("Se intenta realizar la siguiente consulta {}",sql);
		
		/** TODO: ejecutar la consulta sql recibida como parámetro utilizando 
		*         la propiedad conexion y devolver el resultado en un ResulSet
		*/

		/*
		NOTA: estos métodos no fueron usados ya que para ser efectivos debíamos mantener
		el statement y el result set abiertos, lo cual generaría basura en nuestro programa.
		Preferimos ir accediendo a la base de datos desde los distintos métodos según sea necesario.
		 */

		ResultSet rs= null;
		try
		{ 
			Statement stmt = this.conexion.createStatement();
			rs = stmt.executeQuery(sql);

		}
		catch (SQLException ex){
		   logger.error("SQLException: " + ex.getMessage());
		   logger.error("SQLState: " + ex.getSQLState());
		   logger.error("VendorError: " + ex.getErrorCode());
		}

		return rs;
	}	
	
	@Override
	public void actualizacion (String sql)
	{  /** TODO: ejecutar la consulta de actualizacion sql recibida como 
 		*       parámetro utilizando la propiedad conexion 
		*/  

		try {
			Statement stmt = this.conexion.createStatement();
			stmt.executeUpdate(sql);
		}
		catch(SQLException ex){
			System.out.println("Mensaje: " + ex.getMessage()); // Mensaje retornado por MySQL
			System.out.println("Código: " + ex.getErrorCode()); // Código de error de MySQL
			System.out.println("SQLState: " + ex.getSQLState()); // Código de error del SQL standart
		}
	}	
}

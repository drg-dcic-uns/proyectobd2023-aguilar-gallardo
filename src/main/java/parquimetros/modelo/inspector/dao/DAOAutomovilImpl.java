package parquimetros.modelo.inspector.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTO;
import parquimetros.modelo.inspector.exception.AutomovilNoEncontradoException;
import parquimetros.utils.Mensajes;

public class DAOAutomovilImpl implements DAOAutomovil {

	private static Logger logger = LoggerFactory.getLogger(DAOAutomovilImpl.class);
	
	private Connection conexion;
	
	public DAOAutomovilImpl(Connection conexion) {
		this.conexion = conexion;
	}

	@Override
	public void verificarPatente(String patente) throws AutomovilNoEncontradoException, Exception {
		/** 
		 * TODO Debe verificar que exista la patente en la tabla automoviles. 
		 * 		Deberá generar una excepción AutomovilNoEncontradoException en caso de no encontrarlo. 
		 *      Si hay algún error en la consulta o en la conexión deberá propagar la excepción.    
		 *       
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se inicializa en el constructor.     
		 */

		String sql = "SELECT patente FROM automoviles WHERE patente= ?;";
		ResultSet rs = null;
		PreparedStatement stmt = this.conexion.prepareStatement(sql);
		stmt.setString(1, patente);
		rs = stmt.executeQuery();

		if (!rs.next()) {
			throw new AutomovilNoEncontradoException(Mensajes.getMessage("DAOAutomovilImpl.recuperarAutomovilPorPatente.AutomovilNoEncontradoException"));
		}

		rs.close();
		stmt.close();

	}

}

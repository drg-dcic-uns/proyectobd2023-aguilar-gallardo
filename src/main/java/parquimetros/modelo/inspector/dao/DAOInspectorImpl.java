package parquimetros.modelo.inspector.dao;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parquimetros.modelo.Modelo;
import parquimetros.modelo.ModeloImpl;

import parquimetros.modelo.beans.InspectorBean;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.utils.Mensajes;

public class DAOInspectorImpl implements DAOInspector {

	private static Logger logger = LoggerFactory.getLogger(DAOInspectorImpl.class);
	
	private Connection conexion;
	
	public DAOInspectorImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public InspectorBean autenticar(String legajo, String password) throws InspectorNoAutenticadoException, Exception {
		/** 
		 * TODO Código que autentica que exista en la B.D. un legajo de inspector y que el password corresponda a ese legajo
		 *      (recuerde que el password guardado en la BD está encriptado con MD5) 
		 *      En caso exitoso deberá retornar el inspectorBean.
		 *      Si la autenticación no es exitosa porque el legajo no es válido o el password es incorrecto
		 *      deberá generar una excepción InspectorNoAutenticadoException 
		 *      y si hubo algún otro error deberá producir y propagar una Exception.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se inicializa en el constructor.      
		 */

		String sql= "SELECT * FROM inspectores WHERE legajo =? AND password = md5(?);";
		InspectorBean retornar = null;
		PreparedStatement stmt = null;
		ResultSet rs=null;

		stmt = this.conexion.prepareStatement(sql);
		stmt.setString(1, legajo);
		stmt.setString(2, password);
		rs = stmt.executeQuery();
		if (rs.next()) {
			retornar.setLegajo(rs.getInt("legajo"));
			retornar.setPassword(rs.getString("password"));
			retornar.setApellido(rs.getString("apellido"));
			retornar.setNombre(rs.getString("nombre"));
			retornar.setDNI(rs.getInt("DNI"));

		}
		else{
			throw new InspectorNoAutenticadoException(Mensajes.getMessage("DAOAutomovilImpl.recuperarAutomovilPorPatente.AutomovilNoEncontradoException"));
		}
		rs.close();
		stmt.close();
		return retornar;
	}






}

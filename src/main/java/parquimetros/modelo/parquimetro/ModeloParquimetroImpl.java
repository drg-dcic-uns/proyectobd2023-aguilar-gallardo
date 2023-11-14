package parquimetros.modelo.parquimetro;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.ModeloImpl;
import parquimetros.modelo.beans.*;
import parquimetros.modelo.inspector.dao.datosprueba.DAOParquimetrosDatosPrueba;
import parquimetros.modelo.inspector.dao.datosprueba.DAOUbicacionesDatosPrueba;
import parquimetros.modelo.parquimetro.dao.datosprueba.DAOTarjetasDatosPrueba;
import parquimetros.modelo.parquimetro.dto.EntradaEstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.dto.EstacionamientoDTO;
import parquimetros.modelo.parquimetro.dto.EstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.dto.SalidaEstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.exception.ParquimetroNoExisteException;
import parquimetros.modelo.parquimetro.exception.SinSaldoSuficienteException;
import parquimetros.modelo.parquimetro.exception.TarjetaNoExisteException;
import parquimetros.utils.Mensajes;

public class ModeloParquimetroImpl extends ModeloImpl implements ModeloParquimetro {

	private static Logger logger = LoggerFactory.getLogger(ModeloParquimetroImpl.class);
	
	@Override
	public ArrayList<TarjetaBean> recuperarTarjetas() throws Exception {
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarTarjetas.logger"));
		/** 
		 * TODO Debe retornar una lista de UbicacionesBean con todas las tarjetas almacenadas en la B.D. 
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */

		// Datos estáticos de prueba. Quitar y reemplazar por código que recupera las ubicaciones de la B.D. en una lista de UbicacionesBean		 
		/*DAOTarjetasDatosPrueba.poblar();
		
		for (TarjetaBean ubicacion : DAOTarjetasDatosPrueba.datos.values()) {
			tarjetas.add(ubicacion);	
		}*/
		// Fin datos estáticos de prueba.

		ArrayList<TarjetaBean> retorno = new ArrayList<>();

		//el statement principal, que recupera las tarjetas
		Statement stmt = null;
		ResultSet res = null;
		String sql = null;

		//el statement que recupera el tipo de tarjeta (para guardar el tipoTarjetaBean dentro del BeanTarjeta)
		Statement stmtTipo = null;
		ResultSet resTipo = null;
		String sqlTipo = null;
		TipoTarjetaBean tipoTarjeta = null;

		//el statement que recupera el automovil (para guardar el automovilBean dentro del BeanTarjeta)
		Statement stmtAutomovil = null;
		ResultSet resAutomovil = null;
		String sqlAutomovil = null;
		AutomovilBean automovil = null;

		//el statement que recupera el conductor (para guardar el conductorBean dentro del BeanAutomovil)
		Statement stmtConductor = null;
		ResultSet resConductor = null;
		String sqlConductor = null;
		ConductorBean conductor = null;

		sql = "SELECT * FROM tarjetas";
		stmt = this.conexion.createStatement();
		res = stmt.executeQuery(sql);

		while(res.next()) {
			TarjetaBean tarjeta = new TarjetaBeanImpl();
			tarjeta.setId(res.getInt("id_tarjeta"));
			tarjeta.setSaldo(res.getDouble("saldo"));

			//se asigna el tipo de tarjetaBean
			stmtTipo = this.conexion.createStatement();
			sqlTipo = "SELECT * FROM tipos_tarjeta WHERE tipo='" + res.getString("tipo") + "';";
			resTipo = stmtTipo.executeQuery(sqlTipo);
			resTipo.next();
			tipoTarjeta = new TipoTarjetaBeanImpl();
			tipoTarjeta.setTipo(resTipo.getString("tipo"));
			tipoTarjeta.setDescuento(resTipo.getDouble("descuento"));
			tarjeta.setTipoTarjeta(tipoTarjeta);

			//se asigna el automovilBean
			stmtAutomovil = this.conexion.createStatement();
			sqlAutomovil = "SELECT * FROM automoviles WHERE patente='" + res.getString("patente") + "';";
			resAutomovil = stmtAutomovil.executeQuery(sqlAutomovil);
			resAutomovil.next();
			automovil = new AutomovilBeanImpl();
			automovil.setPatente(resAutomovil.getString("patente"));
			automovil.setMarca(resAutomovil.getString("marca"));
			automovil.setModelo(resAutomovil.getString("modelo"));
			automovil.setColor(resAutomovil.getString("color"));

			//se asigna el conductorBean al automovilBean
			stmtConductor = this.conexion.createStatement();
			sqlConductor = "SELECT * FROM conductores WHERE dni=" + resAutomovil.getInt("dni") + ";";
			resConductor = stmtConductor.executeQuery(sqlConductor);
			resConductor.next();
			conductor = new ConductorBeanImpl();
			conductor.setNroDocumento(resConductor.getInt("dni"));
			conductor.setRegistro(resConductor.getInt("registro"));
			conductor.setApellido(resConductor.getString("apellido"));
			conductor.setNombre(resConductor.getString("nombre"));
			conductor.setDireccion(resConductor.getString("direccion"));
			conductor.setTelefono(resConductor.getString("telefono"));
			automovil.setConductor(conductor);

			//se asigna el automovil a la tarjeta
			tarjeta.setAutomovil(automovil);

			retorno.add(tarjeta);
		}
		System.out.println("se recuperaron bien las tarjetas");

		if (res != null) {
			res.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		if (resTipo != null) {
			resTipo.close();
		}
		if (stmtTipo != null) {
			stmtTipo.close();
		}
		if (resAutomovil != null) {
			resAutomovil.close();
		}
		if (stmtAutomovil != null) {
			stmtAutomovil.close();
		}

		return retorno;
	}
	
	/*
	 * Atención: Este codigo de recuperarUbicaciones (como el de recuperarParquimetros) es igual en el modeloParquimetro 
	 *           y en modeloInspector. Se podría haber unificado en un DAO compartido. Pero se optó por dejarlo duplicado
	 *           porque tienen diferentes permisos ambos usuarios y quizas uno estaría tentado a seguir agregando metodos
	 *           que van a resultar disponibles para ambos cuando los permisos de la BD no lo permiten.
	 */	
	@Override
	public ArrayList<UbicacionBean> recuperarUbicaciones() throws Exception {
		
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarUbicaciones.logger"));
		
		/** 
		 * TODO Debe retornar una lista de UbicacionesBean con todas las ubicaciones almacenadas en la B.D. 
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */



		ArrayList<UbicacionBean> retorno = new ArrayList<UbicacionBean>();

		Statement stmt = null;
		ResultSet res = null;
		String sql = "SELECT * FROM ubicaciones";
		stmt = this.conexion.createStatement();
		res = stmt.executeQuery(sql);

		while(res.next()) {
			UbicacionBean ub = new UbicacionBeanImpl();
			ub.setCalle(res.getString("calle"));
			ub.setAltura(res.getInt("altura"));
			ub.setTarifa(res.getDouble("tarifa"));
			retorno.add(ub);
		}


		if (res != null) {
			res.close();
		}
		if (stmt != null) {
			stmt.close();
		}

		return retorno;
	}

	@Override
	public ArrayList<ParquimetroBean> recuperarParquimetros(UbicacionBean ubicacion) throws Exception {
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarParquimetros.logger"));
		
		/** 
		 * TODO Debe retornar una lista de ParquimetroBean con todos los parquimetros que corresponden a una ubicación.
		 * 		 
		 *      Debería propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */

		/* datos de prueba
		DAOParquimetrosDatosPrueba.poblar(ubicacion);
		
		for (ParquimetroBean parquimetro : DAOParquimetrosDatosPrueba.datos.values()) {
			parquimetros.add(parquimetro);	
		}
		 Fin datos estáticos de prueba.*/

		ArrayList<ParquimetroBean> parquimetros = new ArrayList<ParquimetroBean>();
		Statement stmt = null;
		ResultSet res = null;
		String sql = "SELECT * FROM parquimetros WHERE calle='" + ubicacion.getCalle() + "' AND altura=" + ubicacion.getAltura() + ";";
		stmt = this.conexion.createStatement();
		res = stmt.executeQuery(sql);

		while(res.next()) {
			ParquimetroBean parquimetro = new ParquimetroBeanImpl();
			parquimetro.setId(res.getInt("id_parq"));
			parquimetro.setNumero(res.getInt("numero"));
			parquimetro.setUbicacion(ubicacion);
			parquimetros.add(parquimetro);
		}

		if(res != null){
			res.close();
		}
		if(stmt != null){
			stmt.close();
		}
	
		return parquimetros;
	}

	@Override
	public EstacionamientoDTO conectarParquimetro(ParquimetroBean parquimetro, TarjetaBean tarjeta)
			throws SinSaldoSuficienteException, ParquimetroNoExisteException, TarjetaNoExisteException, Exception {

		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.conectarParquimetro.logger"),parquimetro.getId(),tarjeta.getId());
		
		/**
		 * TODO Invoca al stored procedure conectar(...) que se encarga de realizar en una transacción la apertura o cierre 
		 *      de estacionamiento segun corresponda.
		 *      
		 *      Segun la infromacion devuelta por el stored procedure se retorna un objeto EstacionamientoDTO o
		 *      dependiendo del error se produce la excepción correspondiente:
		 *       SinSaldoSuficienteException, ParquimetroNoExisteException, TarjetaNoExisteException     
		 *  
		 */

		//Primero vemos si existen la tarjeta y el parquimetro.


		EstacionamientoDTO retorno = null;
		PreparedStatement stmt = this.conexion.prepareStatement("CALL conectar(?,?)");

		stmt.setInt(1, tarjeta.getId());
		stmt.setInt(2, parquimetro.getId());
		ResultSet res = stmt.executeQuery();


		if(res.next()) {
			if (res.getString("Tipo_Operacion").equals("SQLEXCEPTION, transaccion abortada")) {
				throw new SQLException(" Transaccion abortada");
			}
			if (res.getString("Tipo_Operacion").equals("No existe la tarjeta")) {
				throw new TarjetaNoExisteException();
			}
			if (res.getString("Tipo_Operacion").equals("No existe el parquimetro")){
				throw new ParquimetroNoExisteException();
			}
			if(res.getString("Tipo_Operacion").equals("Apertura")){
				if(res.getInt("Exito_Operacion")==0)
					throw new SinSaldoSuficienteException();
				int tiempoDisponible = res.getInt("Tiempo_Disponible_Estacionamiento");
				stmt = this.conexion.prepareStatement("SELECT * FROM estacionamientos WHERE id_tarjeta=? AND id_parq=? AND estacionamientos.fecha_sal IS NULL");
				stmt.setInt(1, tarjeta.getId());
				stmt.setInt(2, parquimetro.getId());
				res = stmt.executeQuery();
				if(res.next()){

					retorno = new EntradaEstacionamientoDTOImpl(Integer.toString(tiempoDisponible),res.getDate("fecha_ent").toString(),res.getTime("hora_ent").toString());

				}
			}
			else {
				retorno = new SalidaEstacionamientoDTOImpl(Integer.toString(res.getInt("Tiempo_Transcurrido_Estacionamiento")),Double.toString(res.getDouble("Saldo_Actualizado")),res.getDate("Fecha_Entrada").toString(),res.getTime("Hora_Entrada").toString(),res.getDate("Fecha_Salida").toString(),res.getTime("Hora_Salida").toString());
			}
		}


		res.close();
		stmt.close();

		return retorno;
	}

}

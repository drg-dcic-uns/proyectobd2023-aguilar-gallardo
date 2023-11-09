package parquimetros.modelo.parquimetro;

import java.sql.ResultSet;
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
		String sql = "SELECT * FROM tarjetas";
		stmt = this.conexion.createStatement();
		res = stmt.executeQuery(sql);

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

		/*// Datos estáticos de prueba. Quitar y reemplazar por código que recupera las ubicaciones de la B.D. en una lista de UbicacionesBean
		DAOUbicacionesDatosPrueba.poblar();
		
		for (UbicacionBean ubicacion : DAOUbicacionesDatosPrueba.datos.values()) {
			ubicaciones.add(ubicacion);	
		}*/
		// Fin datos estáticos de prueba.

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

		System.out.println("se recuperaron bien las ubicaciones");
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
		
		//Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reales.
		if ((tarjeta.getSaldo() < 0) && (tarjeta.getTipoTarjeta().getDescuento() < 1)) {  // tarjeta k1
			throw new SinSaldoSuficienteException();
		}
		EstacionamientoDTO estacionamiento;

		LocalDateTime currentDateTime = LocalDateTime.now();
        // Definir formatos para la fecha y la hora
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Formatear la fecha y la hora como cadenas separadas
        String fechaAhora = currentDateTime.format(dateFormatter);
        String horaAhora = currentDateTime.format(timeFormatter);
		
		if (tarjeta.getId() == 2) { 		//EntradaEstacionamientoDTO(String tiempoDisponible, String fechaEntrada, String horaEntrada)			
			estacionamiento = new EntradaEstacionamientoDTOImpl("01:40:00",
																fechaAhora,
																horaAhora);
		} else if (tarjeta.getId() == 3) {  		//SalidaEstacionamientoDTO(String tiempoTranscurrido, String saldoTarjeta, String fechaEntrada,	String horaEntrada, String fechaSalida, String horaSalida)
			
			LocalDateTime antes = currentDateTime.minusMinutes(45); // hora actual menos 45 minutos
			
			estacionamiento = new SalidaEstacionamientoDTOImpl("00:45:00", // tiempoTranscurrido
																"10.20", // saldoTarjeta
																fechaAhora, // fechaEntrada
																antes.format(timeFormatter), // horaEntrada
																fechaAhora, // fechaSalida
																horaAhora); // horaSalida
		} else if (tarjeta.getId() == 4) { 

			LocalDateTime antes = currentDateTime.minusMinutes(90); // hora actual menos 45 minutos
			
			estacionamiento = new SalidaEstacionamientoDTOImpl("01:30:00", // tiempoTranscurrido
																"-85", // saldoTarjeta
																fechaAhora, // fechaEntrada
																antes.format(timeFormatter), // horaEntrada
																fechaAhora, // fechaSalida
																horaAhora); // horaSalida
			
		} else {
			throw new Exception();
		}
	
		return estacionamiento;
		//Fin datos estáticos de prueba
		
	}

}

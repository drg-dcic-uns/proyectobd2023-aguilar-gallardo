package parquimetros.modelo.inspector;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.ModeloImpl;
import parquimetros.modelo.beans.*;
import parquimetros.modelo.inspector.dao.DAOParquimetro;
import parquimetros.modelo.inspector.dao.DAOParquimetroImpl;
import parquimetros.modelo.inspector.dao.DAOInspector;
import parquimetros.modelo.inspector.dao.DAOInspectorImpl;
import parquimetros.modelo.inspector.dao.DAOAutomovil;
import parquimetros.modelo.inspector.dao.DAOAutomovilImpl;
import parquimetros.modelo.inspector.dao.datosprueba.DAOParquimetrosDatosPrueba;
import parquimetros.modelo.inspector.dao.datosprueba.DAOUbicacionesDatosPrueba;
import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTO;
import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTOImpl;
import parquimetros.modelo.inspector.dto.MultaPatenteDTO;
import parquimetros.modelo.inspector.dto.MultaPatenteDTOImpl;
import parquimetros.modelo.inspector.exception.AutomovilNoEncontradoException;
import parquimetros.modelo.inspector.exception.ConexionParquimetroException;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.modelo.inspector.exception.InspectorNoHabilitadoEnUbicacionException;
import parquimetros.utils.Mensajes;

public class ModeloInspectorImpl extends ModeloImpl implements ModeloInspector {

	private static Logger logger = LoggerFactory.getLogger(ModeloInspectorImpl.class);	
	
	public ModeloInspectorImpl() {
		logger.debug(Mensajes.getMessage("ModeloInspectorImpl.constructor.logger"));
	}

	@Override
	public InspectorBean autenticar(String legajo, String password) throws InspectorNoAutenticadoException, Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.autenticar.logger"), legajo, password);

		if (legajo==null || legajo.isEmpty() || password==null || password.isEmpty()) {
			throw new InspectorNoAutenticadoException(Mensajes.getMessage("ModeloInspectorImpl.autenticar.parametrosVacios"));
		}
		DAOInspector dao = new DAOInspectorImpl(this.conexion);
		return dao.autenticar(legajo, password);		
	}
	
	@Override
	public ArrayList<UbicacionBean> recuperarUbicaciones() throws Exception {
		
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarUbicaciones.logger"));
		/** 
		 * TODO Debe retornar una lista de UbicacionesBean con todas las ubicaciones almacenadas en la B.D. 
		 *      Debería propagar una excepción si hay algún error en la consulta. 
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.       
		 *      
		 */
		ArrayList<UbicacionBean> ubicaciones = new ArrayList<UbicacionBean>();

		String sql= "SELECT calle,altura FROM parquimetros GROUP BY calle,altura ;";
		Statement stmt = this.conexion.createStatement();
		ResultSet rs= stmt.executeQuery(sql);
		while(rs.next()){
			UbicacionBean ubicacion= new UbicacionBeanImpl();
			ubicacion.setCalle(rs.getString("calle"));
			ubicacion.setAltura(Integer.parseInt(rs.getString("altura")));
			ubicaciones.add(ubicacion);
		}
		rs.close();
		stmt.close();

		return ubicaciones;
	}

	@Override
	public ArrayList<ParquimetroBean> recuperarParquimetros(UbicacionBean ubicacion) throws Exception {
		
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarParquimetros.logger"),ubicacion.toString());
		
		/** 
		 * TODO Debe retornar una lista de ParquimetroBean con todos los parquimetros que corresponden a una ubicación.
		 * 		Debería propagar una excepción si hay algún error en la consulta.
		 *            
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.      
		 *      
		 */

		ArrayList<ParquimetroBean> parquimetros = new ArrayList<ParquimetroBean>();

		String sql= "SELECT * FROM parquimetros WHERE calle =? AND altura =?;";
		PreparedStatement stmt = this.conexion.prepareStatement(sql);
		stmt.setString(1, ubicacion.getCalle());
		stmt.setInt(2, ubicacion.getAltura());
		ResultSet rs= stmt.executeQuery();
		while(rs.next()){
			ParquimetroBean parq= new ParquimetroBeanImpl();
			parq.setId(rs.getInt("id_parq"));
			parq.setNumero(rs.getInt("numero"));
			parq.setUbicacion(ubicacion);
			parquimetros.add(parq);
		}
		rs.close();
		stmt.close();

		return parquimetros;
	}

	@Override
	public void conectarParquimetro(ParquimetroBean parquimetro, InspectorBean inspectorLogueado) throws ConexionParquimetroException, Exception {
		// es llamado desde Controlador.conectarParquimetro
  
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.conectarParquimetro.logger"),parquimetro.toString());
		
		/** TODO Simula la conexión al parquímetro con el inspector que se encuentra logueado en el momento 
		 *       en que se ejecuta la acción. 
		 *       
		 *       Debe verificar si el inspector está habilitado a acceder a la ubicación del parquímetro 
		 *       en el dia y hora actual, segun la tabla asociado_con. 
		 *       Sino puede deberá producir una excepción ConexionParquimetroException.     
		 *       En caso exitoso se registra su acceso en la tabla ACCEDE y retorna exitosamente.		         
		 *     
		 *       Si hay un error no controlado se produce una Exception genérica.
		 *       
		 *       Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *       que se hereda al extender la clase ModeloImpl.
		 *  
		 * @param parquimetro
		 * @throws ConexionParquimetroException
		 * @throws Exception
		 */

		//conectar("inspector", "inspector");
		String consultaHora=" SELECT CURTIME();";
		String consultaFecha= "SELECT CURDATE();";
		String consultaDia="";

		String dia = null;
		String horaTotal = null;
		String fechaAcceso= null;
		String[] separacionHora= new String[3];
		String turno = null;

		int hora= 0;
		int diaBaseDeDatos = 0;

		ResultSet horaRes=null;
		ResultSet FechaRes=null;
		ResultSet diaRes=null;
		ResultSet autorizado=null;

		try {
			FechaRes = consulta(consultaFecha);
			horaRes = consulta(consultaHora);

			while (horaRes.next()) {
				horaTotal = horaRes.getString("CURTIME()");
			}
			while (FechaRes.next()){
				fechaAcceso=FechaRes.getString("CURDATE()");
			}
			consultaDia="SELECT DAYOFWEEK('"+fechaAcceso+"');";
			diaRes = consulta(consultaDia);

			while (diaRes.next()) {
				diaBaseDeDatos = diaRes.getInt("DAYOFWEEK('"+fechaAcceso+"')");
			}

		}catch (SQLException e) {
			System.out.println("Mensaje: " + e.getMessage()); // Mensaje retornado por MySQL
			System.out.println("Código: " + e.getErrorCode()); // Código de error de MySQL
			System.out.println("SQLState: " + e.getSQLState()); // Código de error del SQL standart
		}
		switch(diaBaseDeDatos){
			case 1: dia="do"; break;

			case 2: dia="lu"; break;

			case 3: dia="ma"; break;

			case 4: dia="mi"; break;

			case 5: dia="ju"; break;

			case 6: dia="vi"; break;

			case 7: dia="sa"; break;
		}
		separacionHora=horaTotal.split(":");
		hora = Integer.parseInt(separacionHora[0]);

		if (hora>=8 && hora<=13){
			turno="m";
		} else if(hora>=14 && hora<=23){
			turno="t";
		}

		String sql="SELECT calle,altura FROM asociado_con WHERE legajo="+inspectorLogueado.getLegajo()+" AND dia='"+dia+"' AND turno='"+turno+"';";

		boolean autorizadoEnParquimetro = false;
		try {
			autorizado = consulta(sql);
			while (autorizado.next() && !autorizadoEnParquimetro) {
				if (autorizado.getString("calle").equals(parquimetro.getUbicacion().getCalle()) && autorizado.getInt("altura") == parquimetro.getUbicacion().getAltura()) {
					String sql2 = "INSERT INTO accede (fecha, hora, id_parq, legajo) VALUES ('" + fechaAcceso + "','" + horaTotal + "'," + parquimetro.getId()+ "," +inspectorLogueado.getLegajo() + ");" ;
					actualizacion(sql2);
					autorizadoEnParquimetro = true;
				}
			}
		}catch(SQLException e)
		{
			System.out.println("Mensaje: " + e.getMessage()); // Mensaje retornado por MySQL
			System.out.println("Código: " + e.getErrorCode()); // Código de error de MySQL
			System.out.println("SQLState: " + e.getSQLState()); // Código de error del SQL standart
		}


		if(!autorizadoEnParquimetro){
			throw new ConexionParquimetroException("Inspector no autorizado en la ubicación del parquímetro");
		}

	}

	@Override
	public UbicacionBean recuperarUbicacion(ParquimetroBean parquimetro) throws Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarUbicacion.logger"),parquimetro.getId());
		UbicacionBean ubicacion = parquimetro.getUbicacion();
		if (Objects.isNull(ubicacion)) {
			DAOParquimetro dao = new DAOParquimetroImpl(this.conexion);
			ubicacion = dao.recuperarUbicacion(parquimetro);
		}			
		return ubicacion; 
	}

	@Override
	public void verificarPatente(String patente) throws AutomovilNoEncontradoException, Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.verificarPatente.logger"),patente);
		DAOAutomovil dao = new DAOAutomovilImpl(this.conexion);
		dao.verificarPatente(patente); 
	}	
	
	@Override
	public EstacionamientoPatenteDTO recuperarEstacionamiento(String patente, UbicacionBean ubicacion) throws Exception {

		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarEstacionamiento.logger"),patente,ubicacion.getCalle(),ubicacion.getAltura());
		/**
		 * TODO Verifica si existe un estacionamiento abierto registrado la patente en la ubicación, y
		 *	    de ser asi retorna un EstacionamientoPatenteDTO con estado Registrado (EstacionamientoPatenteDTO.ESTADO_REGISTRADO), 
		 * 		y caso contrario sale con estado No Registrado (EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO).
		 * 
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.
		 */
		//
		// Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reale de la BD.
		//
		// Diseño de datos de prueba: Las patentes que terminan en 1 al 8 fueron verificados como existentes en la tabla automovil,
		//                            las terminadas en 9 y 0 produjeron una excepción de AutomovilNoEncontradoException y Exception.
		//                            entonces solo consideramos los casos terminados de 1 a 8
 		// 
		// Utilizaremos el criterio que si es par el último digito de patente entonces está registrado correctamente el estacionamiento.
		//
		/*
		String fechaEntrada, horaEntrada, estado;
		
		if (Integer.parseInt(patente.substring(patente.length()-1)) % 2 == 0) {
			estado = EstacionamientoPatenteDTO.ESTADO_REGISTRADO;

			LocalDateTime currentDateTime = LocalDateTime.now();
	        // Definir formatos para la fecha y la hora
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	        // Formatear la fecha y la hora como cadenas separadas
	        fechaEntrada = currentDateTime.format(dateFormatter);
	        horaEntrada = currentDateTime.format(timeFormatter);
			
		} else {
			estado = EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO;
	        fechaEntrada = "";
	        horaEntrada = "";
		}

		return new EstacionamientoPatenteDTOImpl(patente, ubicacion.getCalle(), String.valueOf(ubicacion.getAltura()), fechaEntrada, horaEntrada, estado);
		// Fin de datos de prueba */


		EstacionamientoPatenteDTO retornar = null;
		String estado = null;
		String fechaEntrada = null;
		String horaEntrada = null;

		String sql = "SELECT fecha_ent, hora_ent FROM estacionados WHERE patente = ? AND calle = ? AND altura = ?;";
		ResultSet rs = null;
		PreparedStatement stmt = null;
		SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");

		stmt = this.conexion.prepareStatement(sql);
		stmt.setString(1, patente);
		stmt.setString(2, ubicacion.getCalle());
		stmt.setInt(3, ubicacion.getAltura());
		rs = stmt.executeQuery();
		if (rs.next()){
			estado = EstacionamientoPatenteDTO.ESTADO_REGISTRADO;
			fechaEntrada = sdfFecha.format(rs.getDate("fecha_ent"));
			horaEntrada = sdfHora.format(rs.getTimestamp("hora_ent"));
		}
		else{
			estado = EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO;
			fechaEntrada = "";
			horaEntrada = "";
		}
		retornar = new EstacionamientoPatenteDTOImpl(patente, ubicacion.getCalle(), String.valueOf(ubicacion.getAltura()), fechaEntrada, horaEntrada, estado);

		rs.close();
		stmt.close();

		return retornar;

	}
	

	@Override
	public ArrayList<MultaPatenteDTO> generarMultas(ArrayList<String> listaPatentes, 
													UbicacionBean ubicacion, 
													InspectorBean inspectorLogueado) 
									throws InspectorNoHabilitadoEnUbicacionException, Exception {

		logger.info(Mensajes.getMessage("ModeloInspectorImpl.generarMultas.logger"),listaPatentes.size());		
		
		/** 
		 * TODO Primero verificar si el inspector puede realizar una multa en esa ubicacion el dia y hora actual 
		 *      segun la tabla asociado_con. Sino puede deberá producir una excepción de 
		 *      InspectorNoHabilitadoEnUbicacionException. 
		 *            
		 * 		Luego para cada una de las patentes suministradas, si no tiene un estacionamiento abierto en dicha 
		 *      ubicación, se deberá cargar una multa en la B.D. 
		 *      
		 *      Debe retornar una lista de las multas realizadas (lista de objetos MultaPatenteDTO).
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.      

		*/
		String consultaHora=" SELECT CURTIME();";
		String consultaDia= null;
		String consultaFecha = "SELECT CURDATE();";

		String fecha = null;
		String dia = null;
		String horaTotal = null;
		String[] separacionHora= new String[3];
		String turno = null;

		int hora= 0;
		int diaBaseDeDatos = 0;

		Statement stmtHora = null;
		Statement stmtDia = null;
		Statement stmtFecha = null;

		ResultSet horaRes=null;
		ResultSet diaRes=null;
		ResultSet fechaRes=null;

		stmtHora = this.conexion.createStatement();
		horaRes = stmtHora.executeQuery(consultaHora);
		stmtFecha = this.conexion.createStatement();
		fechaRes = stmtFecha.executeQuery(consultaFecha);

		if (fechaRes.next()) {
			fecha = fechaRes.getString("CURDATE()");
		}
		if (horaRes.next()) {
			horaTotal = horaRes.getString("CURTIME()");
		}

		stmtDia = this.conexion.createStatement();
		consultaDia = "SELECT DAYOFWEEK('"+fecha+"');";
		diaRes = stmtDia.executeQuery(consultaDia);

		if (diaRes.next()){
			diaBaseDeDatos= diaRes.getInt("DAYOFWEEK('"+fecha+"')");
		}

		switch(diaBaseDeDatos){
			case 1: dia="do"; break;

			case 2: dia="lu"; break;

			case 3: dia="ma"; break;

			case 4: dia="mi"; break;

			case 5: dia="ju"; break;

			case 6: dia="vi"; break;

			case 7: dia="sa"; break;
		}
		separacionHora=horaTotal.split(":");
		hora = Integer.parseInt(separacionHora[0]);

		if (hora>=8 && hora<=13){
			turno="m";
		} else{
			if(hora>=14 && hora<=20){
				turno="t";
			}
			else{
				throw new InspectorNoHabilitadoEnUbicacionException();
			}
		}
		stmtHora.close();
		stmtDia.close();
		stmtFecha.close();
		horaRes.close();
		diaRes.close();
		fechaRes.close();

		String sql="SELECT * FROM asociado_con WHERE legajo=? AND dia=? AND turno=? AND calle=? AND altura=?;";
		PreparedStatement stmtInspector = null;
		ResultSet autorizado=null;

		stmtInspector = this.conexion.prepareStatement(sql);
		stmtInspector.setInt(1, inspectorLogueado.getLegajo());
		stmtInspector.setString(2, dia);
		stmtInspector.setString(3, turno);
		stmtInspector.setString(4, ubicacion.getCalle());
		stmtInspector.setInt(5, ubicacion.getAltura());
		autorizado = stmtInspector.executeQuery();
		if (!autorizado.next()){
			throw new InspectorNoHabilitadoEnUbicacionException();
		}
		stmtInspector.close();
		autorizado.close();



		ArrayList<MultaPatenteDTO> multas = new ArrayList<>();
		int numeroMulta = 0;
		for (String patente : listaPatentes) {
				EstacionamientoPatenteDTO estacionamiento = this.recuperarEstacionamiento(patente, ubicacion);
				if (estacionamiento.getEstado() == EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO) {

					try {
						verificarPatente(patente);
					}catch(AutomovilNoEncontradoException e){
						System.out.println(Mensajes.getMessage("DAOAutomovilImpl.recuperarAutomovilPorPatente.AutomovilNoEncontradoException"));
					}



					MultaPatenteDTO multa = new MultaPatenteDTOImpl(String.valueOf(numeroMulta), patente, ubicacion.getCalle(), String.valueOf(ubicacion.getAltura()),
							fecha,
							horaTotal,
							String.valueOf(inspectorLogueado.getLegajo()));
					multas.add(multa);
					numeroMulta++;
				}
		}

		return multas;


	}
}

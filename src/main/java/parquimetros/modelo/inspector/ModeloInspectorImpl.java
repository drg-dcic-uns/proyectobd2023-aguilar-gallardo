package parquimetros.modelo.inspector;
import java.text.SimpleDateFormat;
import java.sql.*;
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


		String consultaHora=" SELECT CURTIME();";
		String consultaFecha= "SELECT CURDATE();";
		String consultaDia="";

		String dia = "";
		String horaTotal = "";
		String[] separacionHora;
		String turno = "";


		Date fechaAcceso = null;

		int hora= 0;
		int diaBaseDeDatos = 0;

		ResultSet horaRes=null;
		ResultSet FechaRes=null;
		ResultSet diaRes=null;
		ResultSet autorizado=null;

		Statement stmtHora,stmtFecha;
		PreparedStatement stmtAutorizado,stmtDia;





		stmtHora = this.conexion.createStatement();
		horaRes = stmtHora.executeQuery(consultaHora);
		stmtFecha = this.conexion.createStatement();
		FechaRes = stmtFecha.executeQuery(consultaFecha);

		while (horaRes.next()) {
			horaTotal = horaRes.getString("CURTIME()");
		}
		while (FechaRes.next()){
			fechaAcceso=FechaRes.getDate("CURDATE()");
		}

		consultaDia = "SELECT DAYOFWEEK(?);";
		stmtDia = this.conexion.prepareStatement(consultaDia);
		stmtDia.setDate(1,fechaAcceso);
		diaRes = stmtDia.executeQuery();


		while (diaRes.next()) {

			diaBaseDeDatos = diaRes.getInt("DAYOFWEEK('"+fechaAcceso+"')");
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
		} else if(hora>=14 && hora<=20){
			turno="t";
		}
		/*si el horario esta fuera de rango, el turno quedará establecido en null
		y la consulta sobre asociado con no nos devolverá un result set con contenido
		por lo tanto se lanza excepcion de ConexionParquimetro  */

		String sql="SELECT calle,altura FROM asociado_con WHERE legajo=? AND dia=? AND turno=?;";



		boolean autorizadoEnParquimetro = false;

		stmtAutorizado= this.conexion.prepareStatement(sql);
		stmtAutorizado.setInt(1,inspectorLogueado.getLegajo());
		stmtAutorizado.setString(2,dia);
		stmtAutorizado.setString(3,turno);
		autorizado = stmtAutorizado.executeQuery();

		while (autorizado.next() && !autorizadoEnParquimetro) {
			if (autorizado.getString("calle").equals(parquimetro.getUbicacion().getCalle()) && autorizado.getInt("altura") == parquimetro.getUbicacion().getAltura()) {
				String sql2 = "INSERT INTO accede (fecha, hora, id_parq, legajo) VALUES (?,?,?,?);" ;
				PreparedStatement stmt = this.conexion.prepareStatement(sql2);
				stmt.setDate(1, fechaAcceso);
				stmt.setString(2, horaTotal);
				stmt.setInt(3, parquimetro.getId());
				stmt.setInt(4, inspectorLogueado.getLegajo());
				stmt.executeUpdate();
				autorizadoEnParquimetro = true;
			}
		}



		if(!autorizadoEnParquimetro){
			throw new ConexionParquimetroException("Inspector no autorizado en la ubicación del parquímetro");
		}

		autorizado.close();
		stmtAutorizado.close();
		horaRes.close();
		FechaRes.close();
		diaRes.close();
		stmtHora.close();
		stmtFecha.close();
		stmtDia.close();

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

		Date fecha = null;
		String dia = "";
		String horaTotal = "";
		String[] separacionHora;
		String turno = null;

		int hora= 0;
		int diaBaseDeDatos = 0;
		int idAsociado = 0;

		Statement stmtHora = null;
		Statement stmtDia = null;
		Statement stmtFecha = null;
		PreparedStatement stmtMulta;

		ResultSet horaRes=null;
		ResultSet diaRes=null;
		ResultSet fechaRes=null;

		stmtHora = this.conexion.createStatement();
		horaRes = stmtHora.executeQuery(consultaHora);
		stmtFecha = this.conexion.createStatement();
		fechaRes = stmtFecha.executeQuery(consultaFecha);

		if (fechaRes.next()) {
			fecha = fechaRes.getDate("CURDATE()");
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
		}

		/*si el horario esta fuera de rango, el turno quedará establecido en null
		y la consulta sobre asociado con no nos devolverá un result set con contenido
		por lo tanto se lanza excepcion de InspectorNoHabilitado  */

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

		idAsociado = autorizado.getInt("id_asociado_con");

		stmtDia.close();
		stmtHora.close();
		stmtFecha.close();
		stmtInspector.close();
		autorizado.close();
		horaRes.close();
		diaRes.close();
		fechaRes.close();


		ArrayList<MultaPatenteDTO> multas = new ArrayList<>();

		for (String patente : listaPatentes) {
				EstacionamientoPatenteDTO estacionamiento = recuperarEstacionamiento(patente, ubicacion);
				if (Objects.equals(estacionamiento.getEstado(), EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO)) {
					logger.info("Intento verificar la patenten en la base de datos");
					try {
						verificarPatente(patente);
					}catch(AutomovilNoEncontradoException e){
						System.out.println(Mensajes.getMessage("DAOAutomovilImpl.recuperarAutomovilPorPatente.AutomovilNoEncontradoException"));
					}

					String ingresarMulta = "INSERT INTO multa VALUES (?,?,?,?,?);";
					stmtMulta = this.conexion.prepareStatement(ingresarMulta);
					stmtMulta.setInt(1, 0);
					stmtMulta.setDate(2, fecha);
					stmtMulta.setString(3, horaTotal);
					stmtMulta.setInt(4,idAsociado );
					stmtMulta.setString(5, patente);
					stmtMulta.executeUpdate();

					stmtMulta.close();

					//HACEMOS SELECT DE LA MULTA NUEVAMENTE PARA RECUPERAR EL VALOR DE AUTOINCREMENT PUESTO
					String recuperarMulta = "SELECT * FROM multa WHERE patente=? AND fecha=? AND hora=? AND id_asociado_con=?;";
					PreparedStatement stmtRecuperarMulta = this.conexion.prepareStatement(recuperarMulta);
					stmtRecuperarMulta.setString(1, patente);
					stmtRecuperarMulta.setDate(2, fecha);
					stmtRecuperarMulta.setString(3, horaTotal);
					stmtRecuperarMulta.setInt(4, idAsociado);
					ResultSet rs = stmtRecuperarMulta.executeQuery();
					if (rs.next()) {
						MultaPatenteDTO multa = new MultaPatenteDTOImpl(rs.getString("numero"), patente, ubicacion.getCalle(), String.valueOf(ubicacion.getAltura()),
								rs.getDate("fecha").toString(), rs.getString("hora"),
								String.valueOf(inspectorLogueado.getLegajo()));
						multas.add(multa);
					}
					rs.close();
					stmtRecuperarMulta.close();
				}
		}
		return multas;
	}

}

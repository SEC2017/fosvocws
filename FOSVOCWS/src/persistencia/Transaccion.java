package persistencia;

import excepciones.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Clase que encapsula una transaccion a la base de datos. 
// La misma maneja una conexion a la base de datos para efectuar la transacci�n.
public class Transaccion {
	// Conexion a la base de datos, la cual sera usada para manejar la
	// transaccion.
	private Connection conexion;

	// Constructor de la clase transaccion. El mismo se encarga de crear la
	// transaccion seteando apropidadamente el nivel de transaccionalidad.
	public Transaccion(Connection conexion, int nivelTransaccionalidad) throws PersistenciaException {
		this.conexion = conexion;
		try {
			this.conexion.setAutoCommit(false);
			this.conexion.setTransactionIsolation(nivelTransaccionalidad);
		} catch (SQLException e) {
			throw new PersistenciaException("No se pudo crear la transacci�n . Se ha ca�do la conexi�n debido a \n"
					+ "que se ha superado el tiempo de espera. Reinicie el programa, por favor.");
		}
	}

	// Selectora de la conexion utilizada para la transaccion.
	public Connection getConexion() {
		return conexion;
	}

	// Metodo encargado de finalizar la transaccion. La forma de terminarla
	// depende de el parametro ok. Si el mismo esta en true los cambios hechos
	// en
	// la transaccion tendran validez, si esta en false todos cambios de la
	// transaccion seran deshechos.
	public void finalizarTransaccion(boolean ok) throws PersistenciaException {
		try {
			if (ok) {
				conexion.commit();
			} else {
				conexion.rollback();
			}
		} catch (SQLException e) {
			throw new PersistenciaException("No se pudo finalizar la transaccion");
		}
	}

	// Metodo encargado de generar un PreparedStatement para esta transaccion.
	public PreparedStatement prepareStatement(String sql) throws PersistenciaException {
		try {
			return conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			throw new PersistenciaException("No se pudo preparar la consulta");
		}
	}

}

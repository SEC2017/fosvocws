package logica.productos.refaccion;

import excepciones.PersistenciaException;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Calendar;

import java.util.Vector;

import logica.viviendas.Domicilio;

import logica.viviendas.Domicilios;

import logica.viviendas.ViviendaAReparar;
import persistencia.Transaccion;

public class Cotizaciones {
	public void ingresarCotizacion(Transaccion t, Cotizacion cotizacion, int idProveedor) throws PersistenciaException {
		/* 20 */java.sql.Date fechaCotizacion = new java.sql.Date(cotizacion.getFecha().getTime().getTime());

		/* 22 */PreparedStatement prep = t.prepareStatement("INSERT INTO Cotizaciones (fecha, montoPresupuestado, detalleMateriales, idProveedor) VALUES (?,?,?,?);");
		try {
			/* 26 */prep.setDate(1, fechaCotizacion);
			/* 27 */prep.setDouble(2, cotizacion.getMontoPresupuesto());
			/* 28 */prep.setString(3, cotizacion.getDetalleMateriales());
			/* 29 */prep.setInt(4, idProveedor);

			/* 31 */if (prep.executeUpdate() != 1)
				/* 32 */throw new PersistenciaException(
				/* 33 */"No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			/* 36 */throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			/* 37 */throw new PersistenciaException(e.getMessage());
		}
	}

	public int ingresarCotizacionInt(Transaccion t, Cotizacion cotizacion, int idProveedor) throws PersistenciaException {
		/* 43 */int key = 0;
		/* 44 */ResultSet rs = null;

		/* 46 */java.sql.Date fechaCotizacion = new java.sql.Date(cotizacion.getFecha()
		/* 47 */.getTime().getTime());

		/* 49 */PreparedStatement prep = t.prepareStatement("INSERT INTO Cotizaciones (fecha, montoPresupuestado, detalleMateriales, idProveedor) VALUES (?,?,?,?);");
		try {
			/* 53 */prep.setDate(1, fechaCotizacion);
			/* 54 */prep.setDouble(2, cotizacion.getMontoPresupuesto());
			/* 55 */prep.setString(3, cotizacion.getDetalleMateriales());
			/* 56 */prep.setInt(4, idProveedor);

			/* 58 */if (prep.executeUpdate() != 1) {
				/* 59 */throw new PersistenciaException(
				/* 60 */"No ha sido posible realizar la operacion");
			}
			/* 62 */rs = prep.getGeneratedKeys();

			/* 64 */if (rs.next()) {
				/* 65 */key = rs.getInt(1);
			}
			/* 67 */rs.close();
			/* 68 */prep.close();
		} catch (SQLException e) {
			/* 71 */throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			/* 73 */throw new PersistenciaException(e.getMessage());
		}

		/* 76 */return key;
	}

	public void modificarCotizacion(Transaccion t, Cotizacion cotizacion, int provId, int idVivienda, int cedulaAportante) throws PersistenciaException {
		java.sql.Date fechaCotizacion = new java.sql.Date(cotizacion.getFecha().getTime().getTime());
		Domicilios dom = new Domicilios();
		try {
			ViviendaAReparar vivienda = dom.buscarViviendaAReparar(t, cedulaAportante);
			Domicilio domi = vivienda.getDomicilio();
			int idDomicilio = dom.buscarDomicilioId(t, domi, cedulaAportante);

			Refacciones refacciones = new Refacciones();
			int idCotizacion = refacciones.getCotizacionId(t, idDomicilio);
			PreparedStatement prep = t.prepareStatement("UPDATE Cotizaciones SET fecha=?,montoPresupuestado=?,detalleMateriales=?,idProveedor=? WHERE id = ?;");

			prep.setDate(1, fechaCotizacion);
			prep.setDouble(2, cotizacion.getMontoPresupuesto());
			prep.setString(3, cotizacion.getDetalleMateriales());
			prep.setInt(4, provId);
			prep.setInt(5, idCotizacion);

			if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMessage());
		}
	}

	public Vector<DataCotizacion> buscarCotizacionesId(Transaccion t, Cotizacion cotizacion) throws PersistenciaException {
		/* 93 */Vector<DataCotizacion> lista = new Vector<DataCotizacion>();

		/* 95 */java.sql.Date fecha = new java.sql.Date(cotizacion.getFecha().getTime()
		/* 96 */.getTime());

		/* 98 */Proveedores proveedores = new Proveedores();

		Proveedor pro = cotizacion.getProveedor();
		String nombreProveedor = pro.getNombreBarraca();
		int idProveedor = proveedores.getId(t, nombreProveedor);
		System.out.println(" el proveedor es " + nombreProveedor);
		Calendar calendar = Calendar.getInstance();
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Cotizaciones WHERE fecha = ? AND montoPresupuestado = ? AND detalleMateriales = ?, and idProveedor =?;");

			prep.setDate(1, fecha);
			prep.setDouble(2, cotizacion.getMontoPresupuesto());
			prep.setString(3, cotizacion.getDetalleMateriales());
			prep.setInt(4, idProveedor);

			ResultSet rs = prep.executeQuery();

			while (rs.next()) {
				calendar.setTime(rs.getDate(2));
				Cotizacion cot = new Cotizacion(calendar, rs.getDouble(3), rs.getString(4), pro);
				DataCotizacion d = new DataCotizacion(cot, rs.getInt(1));
				lista.add(d);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}

		return lista;
	}

	public Cotizacion buscarCotizacion(Transaccion t, ViviendaAReparar vivienda, int cedulaAportante) throws PersistenciaException {
		Cotizacion cot = null;
		Domicilio dom = vivienda.getDomicilio();

		Domicilios domicilios = new Domicilios();
		int idDomicilio = domicilios.buscarDomicilioId(t, dom, cedulaAportante);

		Refacciones refacciones = new Refacciones();
		int idCotizacion = refacciones.getCotizacionId(t, idDomicilio);
		Proveedores proveedores = new Proveedores();

		Calendar calendar = Calendar.getInstance();
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Cotizaciones WHERE id = ?;");

			prep.setInt(1, idCotizacion);

			ResultSet rs = prep.executeQuery();

			if (rs.next()) {
				calendar.setTime(rs.getDate(2));
				cot = new Cotizacion(calendar, rs.getDouble(3), rs.getString(4), proveedores.buscarPorId(t, rs.getInt(5)));
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}

		return cot;
	}
	public Cotizacion buscarCotizacionVersion2016(Transaccion t, ViviendaAReparar vivienda, int cedulaAportante, int idprestamo) throws PersistenciaException {
		Cotizacion cot = null;
		//Domicilio dom = vivienda.getDomicilio();

		//Domicilios domicilios = new Domicilios();
		//int idDomicilio = domicilios.buscarDomicilioId(t, dom, cedulaAportante);
        
		Refacciones refacciones = new Refacciones();
		int idCotizacion = refacciones.getCotizacionIdPorPrestamo(t, idprestamo);
		Proveedores proveedores = new Proveedores();

		Calendar calendar = Calendar.getInstance();
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Cotizaciones WHERE id = ?;");

			prep.setInt(1, idCotizacion);

			ResultSet rs = prep.executeQuery();

			if (rs.next()) {
				calendar.setTime(rs.getDate(2));
				cot = new Cotizacion(calendar, rs.getDouble(3), rs.getString(4), proveedores.buscarPorId(t, rs.getInt(5)));
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}

		return cot;
	}

	public DataCotizacion buscarDataCotizacion(Transaccion t, ViviendaAReparar vivienda, int cedulaAportante) throws PersistenciaException {
		Cotizacion cot = null;
		DataCotizacion dataCot = null;

		Domicilio dom = vivienda.getDomicilio();

		Domicilios domicilios = new Domicilios();
		int idDomicilio = domicilios.buscarDomicilioId(t, dom, cedulaAportante);
		// System.out.println("EL ID DE DOMICILIO ES " + idDomicilio);
		Refacciones refacciones = new Refacciones();
		int idCotizacion = refacciones.getCotizacionId(t, idDomicilio);
		// System.out.println("EL ID DE COTIZACION ES: " +
		// idCotizacion);
		Proveedores proveedores = new Proveedores();

		Calendar calendar = Calendar.getInstance();
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Cotizaciones WHERE id = ?;");

			prep.setInt(1, idCotizacion);

			ResultSet rs = prep.executeQuery();

			if (rs.next()) {
				calendar.setTime(rs.getDate(2));
				cot = new Cotizacion(calendar, rs.getDouble(3), rs.getString(4), proveedores.buscarPorId(t, rs.getInt(5)));
				dataCot = new DataCotizacion(cot, idCotizacion);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}

		return dataCot;
	}

	public void eliminarCotizacion(Transaccion t, DataCotizacion cotizacion) throws PersistenciaException {
		int idCotizacion = cotizacion.getId();
		PreparedStatement prep = t.prepareStatement("DELETE FROM Cotizaciones WHERE id = ?;");
		try {
			prep.setInt(1, idCotizacion);
			if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
	}

	public void eliminarCotizacion(Transaccion t, int idcot) throws PersistenciaException {
		// TODO Auto-generated method stub
		PreparedStatement prep = t.prepareStatement("DELETE FROM Cotizaciones WHERE id = ?;");
		try {
			prep.setInt(1, idcot);
			if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
	}

	public void modificarProveedor(Transaccion t, int idsolicitud, int idProveedor) throws PersistenciaException {
		// TODO Auto-generated method stub
		String sql = "UPDATE Cotizaciones SET idproveedor = ? WHERE id = (SELECT idcotizacion FROM refacciones WHERE idprestamo = (SELECT idprestamo FROM solicitudes WHERE id = ?))";
		try {
			PreparedStatement ps = t.prepareStatement(sql);
			ps.setInt(1, idProveedor);
			ps.setInt(2, idsolicitud);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		}
	}

}

/*
 * Location: C:\Users\Sole\Desktop\servidor\ Qualified Name: logica.productos.refaccion.Cotizaciones JD-Core Version: 0.6.0
 */
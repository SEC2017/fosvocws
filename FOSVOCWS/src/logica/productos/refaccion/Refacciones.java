package logica.productos.refaccion;

import excepciones.PersistenciaException;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Vector;

import logica.productos.Prestamo;

import logica.productos.Prestamos;

import logica.viviendas.Domicilio;

import logica.viviendas.Domicilios;

import logica.viviendas.ViviendaAReparar;
import persistencia.Transaccion;

public class Refacciones {
	public void ingresarPrestamoRefaccion(Transaccion t, Refaccion refaccion, int idCotizacion, int cedulaAportante) throws PersistenciaException {
		/* 23 */ViviendaAReparar viviendaAReparar = refaccion.getVivienda();
		/* 24 */Domicilio dom = viviendaAReparar.getDomicilio();
		/* 25 */Domicilios domicilios = new Domicilios();
		/* 26 */int idDomicilio = domicilios.buscarDomicilioId(t, dom, cedulaAportante);

		/* 28 */Prestamo prestamo = refaccion.getPrestamo();
		/* 29 */Prestamos prestamos = new Prestamos();

		/* 31 */int idPrestamo = prestamos.ingresarPrestamoInt(t, prestamo);

		/* 33 */PreparedStatement prep = t
				.prepareStatement("INSERT INTO Refacciones (idPrestamo, totalSolicitado, idDomicilioReparar, idCotizacion, detalle, cantCuotas) VALUES (?,?,?,?,?,?);");
		try {
			/* 36 */prep.setInt(1, idPrestamo);
			/* 37 */prep.setDouble(2, refaccion.getTotalSolicitado());
			/* 38 */prep.setInt(3, idDomicilio);
			/* 39 */prep.setInt(4, idCotizacion);
			/* 40 */prep.setString(5, refaccion.getDetalle().toString());
			/* 41 */prep.setInt(6, refaccion.getCantCuotasSolicitadas());

			/* 43 */if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			/* 47 */throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			/* 48 */throw new PersistenciaException(e.getMessage());
		}
	}

	public int ingresarPrestamoRefaccionInt(Transaccion t, Refaccion refaccion, int idCotizacion, int cedulaAportante) throws PersistenciaException {
		/* 55 */int key = 0;
		/* 56 */ResultSet rs = null;

		/* 58 */ViviendaAReparar viviendaAReparar = refaccion.getVivienda();
		/* 59 */Domicilio dom = viviendaAReparar.getDomicilio();
		/* 60 */Domicilios domicilios = new Domicilios();
		/* 61 */int idDomicilio = domicilios.buscarDomicilioId(t, dom, cedulaAportante);

		/* 63 */Prestamo prestamo = refaccion.getPrestamo();
		/* 64 */Prestamos prestamos = new Prestamos();

		/* 66 */int idPrestamo = prestamos.ingresarPrestamoInt(t, prestamo);

		/* 68 */PreparedStatement prep = t
				.prepareStatement("INSERT INTO Refacciones (idPrestamo, totalSolicitado, idDomicilioReparar, idCotizacion, detalle, cantCuotas) VALUES (?,?,?,?,?,?);");
		try {
			/* 71 */prep.setInt(1, idPrestamo);
			/* 72 */prep.setDouble(2, refaccion.getTotalSolicitado());
			/* 73 */prep.setInt(3, idDomicilio);
			/* 74 */prep.setInt(4, idCotizacion);
			/* 75 */prep.setString(5, refaccion.getDetalle().toString());
			/* 76 */prep.setInt(6, refaccion.getCantCuotasSolicitadas());

			/* 78 */if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
			/* 79 */rs = prep.getGeneratedKeys();

			/* 81 */if (rs.next()) {
				/* 82 */key = rs.getInt(1);
			}
			/* 84 */rs.close();
			/* 85 */prep.close();
		} catch (SQLException e) {
			/* 86 */throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			/* 87 */throw new PersistenciaException(e.getMessage());
			/* 88 */}
		return key;
	}

	public void modificarPrestamoRefaccion(Transaccion t, Refaccion refaccion, int idPrestamo, int cedulaAportante) throws PersistenciaException {
		/* 94 */ViviendaAReparar viviendaAReparar = refaccion.getVivienda();
		/* 95 */Domicilio dom = viviendaAReparar.getDomicilio();
		// System.out.println(dom.getCalle()+" "+dom.getNumero());
		/* 96 */Domicilios domicilios = new Domicilios();
		/* 97 */int idDomicilio = domicilios.buscarDomicilioId(t, dom, cedulaAportante);

		/* 99 */Cotizaciones cotizaciones = new Cotizaciones();
		DataCotizacion datacotizacion = cotizaciones.buscarDataCotizacion(t, viviendaAReparar, cedulaAportante);
		int idCotizacion = datacotizacion.getId();

		PreparedStatement prep = t
				.prepareStatement("UPDATE Refacciones SET totalSolicitado = ?, idDomicilioReparar =?, idCotizacion = ?, detalle = ?, cantCuotas = ? WHERE idPrestamo = ?;");
		try {
			prep.setDouble(1, refaccion.getTotalSolicitado());
			prep.setInt(2, idDomicilio);
			prep.setInt(3, idCotizacion);
			prep.setString(4, refaccion.getDetalle().toString());
			prep.setInt(5, refaccion.getCantCuotasSolicitadas());
			prep.setInt(6, idPrestamo);

			if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
	}

	public int getCotizacionId(Transaccion t, int idDomicilio) throws PersistenciaException {
		int idCotizacion = 0;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Refacciones WHERE idDomicilioReparar = ? ORDER BY id DESC;");
			prep.setInt(1, idDomicilio);
			ResultSet rs = prep.executeQuery();

			if (rs.next()) {
				idCotizacion = rs.getInt(5);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMessage());
		}

		return idCotizacion;
	}
	public int getCotizacionIdPorPrestamo(Transaccion t, int idPrestamo) throws PersistenciaException {
		int idCotizacion = 0;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Refacciones WHERE idPrestamo = ? ORDER BY id DESC;");
			prep.setInt(1, idPrestamo);
			ResultSet rs = prep.executeQuery();

			if (rs.next()) {
				idCotizacion = rs.getInt(5);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMessage());
		}

		return idCotizacion;
	}
	public int buscarIdPrestamo(Transaccion t, int idRefaccion) throws PersistenciaException {
		int idPrestamo = 0;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT idPrestamo FROM Refacciones WHERE id = ?;");

			prep.setInt(1, idRefaccion);
			ResultSet rs = prep.executeQuery();
			if (rs.next()) {
				idPrestamo = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return idPrestamo;
	}

	public Refaccion buscarRefaccion(Transaccion t, int idPrestamo, int cedulaAportante) throws PersistenciaException {
		Prestamos prestamos = new Prestamos();
		Domicilios domicilios = new Domicilios();
		Cotizaciones cotizaciones = new Cotizaciones();

		Refaccion refaccion = null;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Refacciones WHERE idPrestamo = ?;");

			prep.setInt(1, idPrestamo);
			ResultSet rs = prep.executeQuery();

			Vector<String> detalle = new Vector<String>();

			if (rs.next()) {
				Prestamo prestamo = prestamos.buscarPrestamo(t, rs.getInt(3));
				ViviendaAReparar vivienda = domicilios.buscarViviendaARepararPorIdDomicilio(t, rs.getInt(4));
				Cotizacion cotizacion = cotizaciones.buscarCotizacion(t, vivienda, cedulaAportante);
				int cantCuotas = rs.getInt(7);
				String s = rs.getString(6);

				int fin = 0;
				int inicio = 0;
				while (fin < s.length()) {
					if ((s.charAt(fin) == ' ') || (fin == s.length() - 1)) {
						if (fin != s.length() - 1)
							detalle.add(s.substring(inicio, fin));
						else
							detalle.add(s.substring(inicio, fin + 1));
						inicio = fin + 1;
					}
					fin++;
				}

				refaccion = new Refaccion(prestamo, rs.getDouble(2), vivienda, cotizacion, detalle, cantCuotas);
			}
			rs.close();
			prep.close();
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return refaccion;
	}
	public Refaccion buscarRefaccionVersion2016(Transaccion t, int idPrestamo, int cedulaAportante, int idsolicitud) throws PersistenciaException {
		Prestamos prestamos = new Prestamos();
		Domicilios domicilios = new Domicilios();
		Cotizaciones cotizaciones = new Cotizaciones();

		Refaccion refaccion = null;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT * FROM Refacciones WHERE idPrestamo = ?;");

			prep.setInt(1, idPrestamo);
			ResultSet rs = prep.executeQuery();

			Vector<String> detalle = new Vector<String>();

			if (rs.next()) {
				Prestamo prestamo = prestamos.buscarPrestamo(t, rs.getInt(3));
				ViviendaAReparar vivienda = domicilios.buscarViviendaARepararPorIdDomicilio(t, rs.getInt(4));
				Cotizacion cotizacion = cotizaciones.buscarCotizacionVersion2016(t, vivienda, cedulaAportante, idPrestamo);
				int cantCuotas = rs.getInt(7);
				String s = rs.getString(6);

				int fin = 0;
				int inicio = 0;
				while (fin < s.length()) {
					if ((s.charAt(fin) == ' ') || (fin == s.length() - 1)) {
						if (fin != s.length() - 1)
							detalle.add(s.substring(inicio, fin));
						else
							detalle.add(s.substring(inicio, fin + 1));
						inicio = fin + 1;
					}
					fin++;
				}

				refaccion = new Refaccion(prestamo, rs.getDouble(2), vivienda, cotizacion, detalle, cantCuotas);
			}
			rs.close();
			prep.close();
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return refaccion;
	}

	public int buscarIdPrestamoRefaccion(Transaccion t, int cedulaAportante) throws PersistenciaException {
		int idPrestamo = 0;
		try {
			PreparedStatement prep = t
					.prepareStatement("select idPrestamo from Refacciones, Domicilios where idDomicilioReparar = Domicilios.id AND Domicilios.cedulaAportante = ?;");
			prep.setInt(1, cedulaAportante);

			ResultSet rs = prep.executeQuery();
			if (rs.next()) {
				idPrestamo = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return idPrestamo;
	}

	public int buscarIdPrestamoRefaccion(Transaccion t, int cedulaAportante, int idSolicitud) throws PersistenciaException {
		int idPrestamo = 0;
		try {
			PreparedStatement prep = t
					.prepareStatement("select refacciones.idPrestamo from Refacciones, Prestamos, Solicitudes where solicitudes.idprestamo = Prestamos.id AND prestamos.id = refacciones.idprestamo AND solicitudes.id = ?;");
			prep.setInt(1, idSolicitud);

			ResultSet rs = prep.executeQuery();
			if (rs.next()) {
				idPrestamo = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return idPrestamo;
	}

	public void eliminarRefaccion(Transaccion t, Refaccion refaccion) throws PersistenciaException {
		int idPrestamo = refaccion.getCodigo();
		PreparedStatement prep = t.prepareStatement("DELETE FROM Refacciones WHERE idPrestamo = ?;");

		try {
			prep.setInt(1, idPrestamo);
			if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
	}

	public void eliminarRefaccion(Transaccion t, int idrefaccion) throws PersistenciaException {
		PreparedStatement prep = t.prepareStatement("DELETE FROM Refacciones WHERE id = ?;");

		try {
			prep.setInt(1, idrefaccion);
			if (prep.executeUpdate() != 1)
				throw new PersistenciaException("No ha sido posible realizar la operacion");
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
	}

	public int buscarIdPrestamoRefaccionPorIdPrestamo(Transaccion t, int idPrestamo) throws PersistenciaException {
		// TODO Auto-generated method stub
		int resu = 0;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT id FROM Refacciones WHERE idPrestamo = ?;");

			prep.setInt(1, idPrestamo);
			ResultSet rs = prep.executeQuery();
			if (rs.next()) {
				resu = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return resu;
	}

	public int buscarIdCotizacion(Transaccion t, int idRefaccion) throws PersistenciaException {
		int idcotizacion = 0;
		try {
			PreparedStatement prep = t.prepareStatement("SELECT idCotizacion FROM Refacciones WHERE id = ?;");

			prep.setInt(1, idRefaccion);
			ResultSet rs = prep.executeQuery();
			if (rs.next()) {
				idcotizacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenciaException(e.getMessage());
		} catch (PersistenciaException e) {
			throw new PersistenciaException(e.getMensaje());
		}
		return idcotizacion;
	}
}

/*
 * Location: C:\Users\Sole\Desktop\servidor\ Qualified Name: logica.productos.refaccion.Refacciones JD-Core Version: 0.6.0
 */
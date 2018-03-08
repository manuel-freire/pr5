package es.ucm.fdi.simobject;

import java.util.ArrayList;
import java.util.Map;

public class Vehicle extends SimulationObject {
	private int localizacion;
	private int velMaxima;
	private int velActual;
	private int tiempoAveria;
	private ArrayList<Junction> itinerario; // no sería más comodo llevar
											// carreteras?
	private int kilometrage;// creo que también hace falta
	private Road actual;
	private int proxCruce;
	private boolean haLlegado;

	public Vehicle(int velM, ArrayList<Junction> cruc, String ide) {
		super(ide);
		localizacion = 0;
		velMaxima = velM;
		velActual = 0;
		tiempoAveria = 0;
		itinerario = cruc;
		kilometrage = 0;
		proxCruce = 0;
		haLlegado = false;
	}

	public int getMaxSpeed() {
		return velMaxima;
	}

	public int getLocation() {
		return localizacion;
	}

	public void setTiempoAveria(int n) {
		tiempoAveria += n;
	}

	public Road getRoad() {
		return actual;
	}

	public int getTiempoAveria() {
		return tiempoAveria;
	}

	public void setVelocidadActual(int vel) {
		if (vel > velMaxima)
			velActual = velMaxima;
		else
			velActual = vel;
		velActual=0;
	}

	public int getVelocidadActual() {
		return velActual;

	}

	public Junction nextJunction() {
		return itinerario.get(proxCruce);
	}

	public void avanza() {
		if (tiempoAveria > 0) {
			tiempoAveria--;
		} else if (!haLlegado) {
			if (localizacion + velActual >= actual.getLongitud()) {
				kilometrage += actual.getLongitud() - localizacion;
				localizacion = actual.getLongitud();
				velActual = 0; // hay que hacer una comprobacion si ya se llego
								// al final
				actual.getFinal().entraVehiculo(this); 
				actual.saleVehiculo(this); // esta la usamos si queremos que lo elimine de la carretera
			} else {
				kilometrage += velActual;
				localizacion += velActual;
			}
		}
	}

	void moverASiguienteCarretera(Road c) {
		proxCruce++;
		actual = c;
		localizacion = 0;
		if (proxCruce == itinerario.size())
			haLlegado = true;
	}

	protected void fillReportDetails(Map<String, String> out) {
		out.put("speed", "" + velActual);
		out.put("kilometrage", "" + kilometrage);
		out.put("faulty", "" + tiempoAveria);
		if (!haLlegado)
			out.put("location", "(" + actual.getId() + ", " + localizacion
					+ ")");
		else
			out.put("location", "arrived");
	}

	// metodo auxiliar para que cada coche escriba la última linea, util para el
	// de road.
	protected String getFillVehiculo() {
		return ("(" + id + ", " + localizacion + ")");

	}

	protected String getReportHeader() {
		return "[vehicle_report]";
	}
}

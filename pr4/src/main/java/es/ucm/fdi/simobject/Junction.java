package es.ucm.fdi.simobject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.exceptions.SimulatorError;

public class Junction extends SimObject {
	protected Map<Road, IncomingRoad> saberInc;
	protected List<IncomingRoad> entrantes;
	protected int semaforo;
	protected Map<Junction, Road> saberSaliente;

	public Junction(String id) {
		super(id);
		saberInc = new HashMap<>();
		entrantes = new ArrayList<>();
		semaforo = 0;
		saberSaliente = new HashMap<>();
	}

	public void newVehicle(Vehicle c) {
		saberInc.get(c.getRoad()).cola.add(c);
	}

	public void newOutgoing(Road r) {
		saberSaliente.put(r.getFinal(), r);
	}

	public void newIncoming(Road r) {
		IncomingRoad ir = new IncomingRoad(r.getId());
		if (entrantes.isEmpty())
			ir.semaforoVerde = true;
		saberInc.put(r, ir);
		entrantes.add(ir);
	}

	public void moveToNextRoad(Vehicle v) {
		Junction nextJunction = v.getProxCruce();
		if (nextJunction != null) {
			Road r = saberSaliente.get(nextJunction);
			if (r == null)
				throw new SimulatorError("A vehicle goes over ghost roads");
			r.newVehicle(v);
		} else
			v.arrived();
	}

	public void avanza() {
		if (!entrantes.isEmpty()) {
			IncomingRoad roadGreen = entrantes.get(semaforo);
			if (!roadGreen.cola.isEmpty()) {
				Vehicle lucky = roadGreen.cola.getFirst();
				lucky.getRoad().removeVehicle(lucky);
				roadGreen.cola.pop();
				moveToNextRoad(lucky);
			}
			avanzaSemaforo();
		}
	}
	
	public void avanzaSemaforo(){
		IncomingRoad roadGreen = entrantes.get(semaforo);
		roadGreen.semaforoVerde = false;
		semaforo++;
		if (semaforo == entrantes.size())
			semaforo = 0;
		entrantes.get(semaforo).semaforoVerde = true;
	}
	
	public void preparaSemaforo(){

	}

	protected void fillReportDetails(Map<String, String> out) {
		StringBuilder reportJunct = new StringBuilder();
		entrantes.forEach(r -> reportJunct.append(r.GeneraReport() + ","));

		if (entrantes.size() != 0)
			reportJunct.delete(reportJunct.length() - 1, reportJunct.length());

		out.put("queues", reportJunct.toString());
	}

	protected String getReportHeader() {
		return "junction_report";
	}

	protected class IncomingRoad {
		protected ArrayDeque<Vehicle> cola;
		protected String id;
		protected boolean semaforoVerde;

		public IncomingRoad(String r) {
			cola = new ArrayDeque<>();
			id = r;
			semaforoVerde = false;
		}

		protected String GeneraReport() {
			StringBuilder vehiculosCola = new StringBuilder();
			cola.forEach(v -> vehiculosCola.append(v.getId() + ","));
			if (cola.size() != 0)
				vehiculosCola.delete(vehiculosCola.length() - 1,
						vehiculosCola.length());

			StringBuilder r = new StringBuilder();
			r.append("(" + id + ",");
			r.append(semaforoReport());
			r.append(",[" + vehiculosCola + "])");

			return r.toString();
		}
		
		protected String semaforoReport(){
			StringBuilder r = new StringBuilder();
			if (semaforoVerde)
				r.append("green");
			else
				r.append("red");
			return r.toString();
		}
	}
}

package it.polito.tdp.music.model;

public class Stat {
	private Country s;
	private Country d;
	private double peso;
	
	public Stat(Country s, Country d, double peso) {
		super();
		this.s = s;
		this.d = d;
		this.peso = peso;
	}
	public Country getS() {
		return s;
	}
	public void setS(Country s) {
		this.s = s;
	}
	public Country getD() {
		return d;
	}
	public void setD(Country d) {
		this.d = d;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
}

package it.polito.tdp.music.model;

import java.time.Month;
import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.music.db.MusicDAO;

public class Model {

	MusicDAO dao;
	private Map<Integer,Artist> artisti;
	private Set<Country> paesi; //paesi in cui sono stati ascoltati
	private Map<Integer,Country> mappaPaesi;
	private SimpleWeightedGraph<Country,DefaultWeightedEdge> grafo; 
	
	public Model(){
		dao=new MusicDAO();
		artisti=new HashMap<Integer,Artist>();
		mappaPaesi=new HashMap<Integer, Country>();
	}
	
	public List<Month> getMesi() {
		return dao.getMesi();
	}
	
	public void getTuttiArtisti(){
		for(Artist a: dao.getAllArtists()){
			artisti.put(a.getId(), a);
		}
	}
	
	public void getTuttiPaesi(){
		for(Country c: dao.getAllCountries()){
			mappaPaesi.put(c.getId(), c);
		}
	}

	public Map<Artist, Integer> getStat(Month m) {
		paesi=new HashSet<>();
		return dao.getStat(m,artisti);
	}
	
	public Set<Country> getCountry(List<Artist> selezionati,Month m){
		
		for(Artist a: selezionati){
			paesi.addAll(dao.getNazioniPerArtista(a,m,mappaPaesi));
		}
		return paesi;
	}
	
	public Stat creaGrafo(List<Artist> a,Month m){
		grafo=new SimpleWeightedGraph<Country,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//aggiungo gli archi
		Set<Country> paesi=this.getCountry(a, m);
		Graphs.addAllVertices(grafo, paesi);
		
		//inserisco gli archi
		for(Country c1: paesi){
			for(Country c2: paesi){
				if(!c1.equals(c2)){
					int peso=dao.getPeso(c1,c2,m);
					if(peso!=0){
						DefaultWeightedEdge arco=grafo.addEdge(c1, c2);
						if(arco!=null){
							grafo.setEdgeWeight(arco, peso);
						}
					}
				}
			}
		}
		
		double pesoMax=0;
		Stat stat=null;
		for(DefaultWeightedEdge d: grafo.edgeSet()){
			if(grafo.getEdgeWeight(d)>pesoMax){
				pesoMax=grafo.getEdgeWeight(d);
				Country source=grafo.getEdgeSource(d);
				Country dest=grafo.getEdgeTarget(d);
				
				stat=new Stat(source,dest,pesoMax);
			}
		}
		return stat;
	}

}

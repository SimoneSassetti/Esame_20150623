package it.polito.tdp.music.db;

import it.polito.tdp.music.model.Artist;
import it.polito.tdp.music.model.City;
import it.polito.tdp.music.model.Country;
import it.polito.tdp.music.model.Listening;
import it.polito.tdp.music.model.Track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.*;

public class MusicDAO {
	
	public static void main(String[] args) {
		MusicDAO dao = new MusicDAO() ;
		
		List<Country> countries = dao.getAllCountries() ;
		//System.out.println(countries) ;
		
		List<City> cities = dao.getAllCities() ;
		//System.out.println(cities) ;
		
		List<Artist> artists = dao.getAllArtists() ;
		
		List<Track> tracks = dao.getAllTracks() ;
		
		List<Listening> listenings = dao.getAllListenings() ;

		System.out.format("Loaded %d countries, %d cities, %d artists, %d tracks, %d listenings\n", 
				countries.size(), cities.size(), artists.size(), tracks.size(), listenings.size()) ;
	}
	
	public List<Country> getAllCountries() {
		final String sql = "SELECT id, country FROM country" ;
		
		List<Country> countries = new ArrayList<Country>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while( res.next() ) {
				countries.add( new Country(res.getInt("id"), res.getString("country"))) ;
			}
			conn.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		return countries ;
	}
	
	public List<City> getAllCities() {
		final String sql = "SELECT id, city FROM city" ;
		
		List<City> cities = new ArrayList<City>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while( res.next() ) {
				cities.add( new City(res.getInt("id"), res.getString("city"))) ;
			}
			
			conn.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
		return cities ;
		
	}

	
	public List<Artist> getAllArtists() {
		final String sql = "SELECT id, artist FROM artist" ;
		
		List<Artist> artists = new ArrayList<Artist>() ;
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			
			while( res.next() ) {
				artists.add( new Artist(res.getInt("id"), res.getString("artist"))) ;
			}
			conn.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		return artists ;
	}

	public List<Track> getAllTracks() {
		final String sql = "SELECT id, track FROM track" ;
		
		List<Track> tracks = new ArrayList<Track>() ;
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while( res.next() ) {
				tracks.add( new Track(res.getInt("id"), res.getString("track"))) ;
			}
			conn.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		return tracks ;
		
	}
	
	public List<Listening> getAllListenings() {
		final String sql = "SELECT id, userid, month, weekday, longitude, latitude, countryid, cityid, artistid, trackid FROM listening" ;
		
		List<Listening> listenings = new ArrayList<Listening>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while( res.next() ) {
				listenings.add( new Listening(res.getLong("id"), res.getLong("userid"), res.getInt("month"), res.getInt("weekday"),
						res.getDouble("longitude"), res.getDouble("latitude"), res.getInt("countryid"), res.getInt("cityid"),
						res.getInt("artistid"), res.getInt("trackid"))) ;
			}
			conn.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		return listenings ;
	}

	public List<Month> getMesi() {
		
		String sql="SELECT distinct month FROM listening order by month";
		
		List<Month> mesi=new ArrayList<Month>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				mesi.add(Month.of(res.getInt("month"))) ;
			}
			conn.close() ;
			return mesi;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public Map<Artist, Integer> getStat(Month m, Map<Integer, Artist> artisti) {
		
		String sql="SELECT l.artistid,a.artist,count(*) as conteggio from artist as a, listening as l "+
					"where l.month=? and a.id=l.artistid "+
					"group by l.artistid "+
					"order by conteggio desc";
		
		Map<Artist,Integer> mappa=new LinkedHashMap<>();
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getValue());
			ResultSet rs = st.executeQuery() ;
			
			int i=0;
			while(rs.next() && i<20) {
				Artist a=artisti.get(rs.getInt("artistid"));
				if(!artisti.containsKey(a)){
					Artist nuovo=new Artist(rs.getInt("artistid"),rs.getString("artist"));
					mappa.put(nuovo, rs.getInt("conteggio"));
					artisti.put(nuovo.getId(), nuovo);
					i++;
				}else{
					mappa.put(a, rs.getInt("conteggio"));
					i++;
				}
			}
			conn.close() ;
			return mappa;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Country> getNazioniPerArtista(Artist a, Month m, Map<Integer, Country> mappaPaesi) {
		
		String sql="select distinct c.id, c.country from country as c, listening as l,artist as a "+
					"where a.id=l.artistid and c.id=l.countryid and l.month=? and l.artistid=? order by c.id";
		
		List<Country> paesi=new ArrayList<Country>();
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getValue());
			st.setInt(2, a.getId());
			ResultSet rs = st.executeQuery() ;
			
			while(rs.next()) {
				Country c=mappaPaesi.get(rs.getInt("id"));
				if(c!=null)
					paesi.add(c);
			}
			conn.close() ;
			return paesi;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public int getPeso(Country c1, Country c2, Month m) {
		
		String sql="select t1.countryid,t2.countryid,count(*) as peso "+
					"from (select distinct a1.id,l1.countryid "+
					"from artist as a1,listening as l1 "+
					"where a1.id=l1.artistid and l1.month=? and l1.countryid=?) as t1, "+
					"(select distinct a2.id,l2.countryid "+
					"from artist as a2,listening as l2 "+
					"where a2.id=l2.artistid and l2.month=? and l2.countryid=?) as t2 "+
					"where t1.id=t2.id "+
					"group by t1.countryid";
		
		int peso=0;
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getValue());
			st.setInt(2, c1.getId());
			st.setInt(3, m.getValue());
			st.setInt(4, c2.getId());
			ResultSet rs = st.executeQuery() ;
			
			if(rs.next()){
				peso=rs.getInt("peso");
			}else{
				peso=0;
			}
			conn.close() ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return peso;
	}

}

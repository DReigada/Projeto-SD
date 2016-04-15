package pt.upa.transporter.core;

public class Regions {
	
	public final static String[] northRegion = 
		{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"};
	public final static String[] centreRegion = 
		{"Lisboa", "Leiria", "Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"};
	public final static String[] southRegion = 
		{"Setúbal", "Évora", "Portalegre", "Beja", "Faro"};
	
	private Boolean _north, _centre, _south;
	
	
	public Regions(Boolean north, Boolean centre, Boolean south) {
		_north = north;
		_centre = centre;
		_south = south;
	}

	public static Boolean validateCity(String city){
		for (String c : northRegion) if(city.equals(c)) return true;
		for (String c : centreRegion) if(city.equals(c)) return true;
		for (String c : southRegion) if(city.equals(c)) return true;
		return false;
	}
	
	public Boolean hasCity(String city){
		if(_north) for (String c : northRegion) if(city.equals(c)) return true;
		if(_centre) for (String c : centreRegion) if(city.equals(c)) return true;
		if(_south) for (String c : southRegion) if(city.equals(c)) return true;
		return false;
	}
}

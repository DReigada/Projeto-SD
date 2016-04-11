package pt.upa.transporter.core;

public class Regions {
	public enum NorthRegion{
		Porto, Braga, VianaCastelo, VilaReal, Braganca;
	}
	public enum CentreRegion{
		Lisboa, Leiria, Santarem, CasteloBranco, Coimbra, Aveiro, Viseu, Guarda;
	}
	public enum SouthRegion{
		Setubal, Evora, Portalegre, Beja, Faro;
	}
	
	private Boolean _north, _centre, _south;
	
	
	public Regions(Boolean north, Boolean centre, Boolean south) {
		_north = north;
		_centre = centre;
		_south = south;
	}

	public static Boolean validateCity(String city){
		for (Enum<NorthRegion> c : NorthRegion.values()) if(city.equals(c.name())) return true;
		for (Enum<CentreRegion> c : CentreRegion.values()) if(city.equals(c.name())) return true;
		for (Enum<SouthRegion> c : SouthRegion.values()) if(city.equals(c.name())) return true;
		return false;
	}
	
	public Boolean hasCity(String city){
		if(_north) for (Enum<NorthRegion> c : NorthRegion.values()) if(city.equals(c.name())) return true;
		if(_centre) for (Enum<CentreRegion> c : CentreRegion.values()) if(city.equals(c.name())) return true;
		if(_south) for (Enum<SouthRegion> c : SouthRegion.values()) if(city.equals(c.name())) return true;
		return false;
	}
}

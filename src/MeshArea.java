

import java.awt.Polygon;
import java.util.ArrayList;

public class MeshArea {
	String id;
	public ArrayList <Vertex> connections = new ArrayList <Vertex>();
	
	public Polygon polygon;
	public boolean visited = false;

	public MeshArea(int nPoints){
		polygon = new Polygon(new int[nPoints], new int[nPoints], nPoints);
		polygon.npoints = nPoints;
	}
	
	public void setNPoints(int nPoints){
		polygon.npoints = nPoints;
	}
	
	public int getNPoints(){
		return polygon.npoints;
	}
	
	
	
}

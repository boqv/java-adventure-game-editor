import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javax.imageio.ImageIO;


public class Screen {

	private Player player;
	private ArrayList <MeshArea> polygons = new ArrayList<MeshArea>();
	private Point startingPoint, endPoint;
	private Image image;
	private MeshArea selectedPolygon;
	private MeshArea targetPolygon;
	private MeshArea sourcePolygon;
	
	private Vertex destinationVertex;
	private ArrayList <Vertex> path = new ArrayList <Vertex>();
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	private int corner;
	
	private Sprite selectedSprite;
	
	public boolean showPolygons = true;
	private int nPoints;
	
	public String imagePath;
	
	public Screen(){
		
		try {
			image = ImageIO.read(new File("C:\\Users\\Johan\\Documents\\Kod\\AdvEditor\\res\\goblins.png"));
			imagePath = "C:\\Users\\Johan\\Documents\\Kod\\AdvEditor\\res\\goblins.png";
		} catch (IOException e) {}
		
		nPoints = 4;
	}
	
	public ArrayList <MeshArea> getPolygons(){
		return this.polygons;
	}
	
	public boolean startConnecting(int x, int y){
		
		for(MeshArea P : polygons){
			
			if(P.polygon.contains(x, y)){
				startingPoint = new Point(x, y);
				endPoint = startingPoint;
				selectedPolygon = P;
				return true;
			}
		}
		
		return false;
	}
	
	public void createConnections(int x, int y){
		
		endPoint = new Point(x, y);
		
		Point point = new Point(startingPoint.x + (endPoint.x - startingPoint.x)/2 , 
				                startingPoint.y + (endPoint.y - startingPoint.y)/2);

		MeshArea connectedPolygon = null;
		for(MeshArea P : polygons){
						
				if(P.polygon.contains(x, y)){
					connectedPolygon = P;
					break;
				}
		 }
	
		Vertex v1 = new Vertex();
		v1.position = point;
		v1.targetMesh = connectedPolygon;
		v1.destMesh = selectedPolygon;
		
		Vertex v2 = new Vertex();
		v2.position = point;
		v2.targetMesh = selectedPolygon;
		v2.destMesh = connectedPolygon;
		
		selectedPolygon.connections.add(v1);
		connectedPolygon.connections.add(v2);
		
		startingPoint = endPoint = new Point(0, 0);
	}
	
	public void placeCharacter(int x, int y){
		for(MeshArea P : polygons){
			
			if(P.polygon.contains(x, y)){
				
				//starting point for character.
				player.setPosition(x, y);
				
				//find all nodes connected to this poly and add to list.
				sourcePolygon = P;
				break;
			}
			
		}
	}
	
	public void modifyPolygon(int x, int y){
		selectedPolygon.polygon.invalidate();
		selectedPolygon.polygon.xpoints[corner] = x;
		selectedPolygon.polygon.ypoints[corner] = y;
	}
	
	public void addPolygon(int x, int y){
		
		Polygon poly;
		MeshArea m = new MeshArea(nPoints);
		
		if(nPoints == 8){
			int[] xPoints = {x, x + 50, x + 100, x + 100, x + 100, x + 50,  x,       x};
			int[] yPoints = {y, y,      y,       y + 50,  y + 100, y + 100, y + 100, y+50};
			poly = new Polygon(xPoints, yPoints, nPoints);
		}
		else {
			int[] xPoints = {x, x + 100, x + 100, x};
			int[] yPoints = {y, y, y + 100, y + 100};
			poly = new Polygon(xPoints, yPoints, nPoints);
		}
		
		m.polygon = poly;
		m.id = ""+m;
		polygons.add(m);
		selectedPolygon = m;
	}
	

	
	public void clearScreen(){
		
		polygons.clear();
		
		startingPoint = new Point(0, 0);
		endPoint = new Point(0, 0);
		
		player = new Player();
		
	    
	}
	

	public boolean insidePolygonCorner(int x, int y) {
		
			if(selectedPolygon == null) return false;
			
			for(int i = 0; i < selectedPolygon.getNPoints(); i++){
				Rectangle rect = new Rectangle(selectedPolygon.polygon.xpoints[i] - 10, selectedPolygon.polygon.ypoints[i] - 10, 20, 20);
				
				if(rect.contains(x, y)){
					corner = i;
					
					return true;
				}
			}
		
			return false;
	}

	public boolean insidePolygon(int x, int y) {
		for(MeshArea P : polygons){
						
			if(P.polygon.contains(x, y)){
				selectedPolygon = P;
				return true;
			}
		}
		return false;
	}

	public void setConnectionEndPoint(int x, int y){
		endPoint = new Point(x, y);
	}

	public void movePolygon(int x, int y, int lastX, int lastY) {
		selectedPolygon.polygon.invalidate();
		selectedPolygon.polygon.translate(x - lastX, y - lastY);
	}

	public void findPath(int x, int y) {
		boolean isFound = false;
		//reset stuff
		
		LinkedList<Vertex> vertices = null;
		
		sourcePolygon = player.getPolygon(polygons);
		
		if(sourcePolygon == null) return;
		sourcePolygon.visited = false;
		targetPolygon = null;
		
		for(MeshArea P : polygons){
						
			if(P.polygon.contains(x, y)){
				vertices = new LinkedList<Vertex>();
				//create end point vertex where the user clicked
				destinationVertex = new Vertex();
				destinationVertex.position = new Point(x, y);
				destinationVertex.destMesh = P;
				
				//find nodes that connects to this poly.
				targetPolygon = P;
				
				if(targetPolygon == sourcePolygon){
					isFound = true;
					break;
				}
				
				
			}
			P.visited = false;
		}
		
		if(targetPolygon == null) return;
		
		path.clear();
		Vertex temp = null;
		
		if(!isFound){
	        //shit starts happening here
			sourcePolygon.visited = true;
			//add of the vertexes leading out of source
			for(Vertex v : sourcePolygon.connections){
				
				if(v.targetMesh == targetPolygon){
					path.add(v);
					vertices.clear();
					isFound = true;
					break;
				}
				v.prev = null;
				vertices.add(v);
				
			}
			
			//go through the vertices until the fucking targetMesh is found or the fucking vertices queue is empty.
			while(!vertices.isEmpty()){
				
				temp = vertices.poll();  //pick and remove first one in queue
				
				//this is where you want to start moving towards the end point instead of looking for more vertices.
				if(temp.targetMesh == targetPolygon){ 
					isFound = true;
					break;
				}
				
				for(Vertex v : temp.targetMesh.connections){    //the targetMesh of the added vertex is the polygon u want to add from.
						if(!v.targetMesh.visited && !v.destMesh.visited)
						{
							v.prev = temp;
							vertices.add(v);
						}
				}
			
				
				temp.targetMesh.visited = true;
			
			}
		}
	
		Vertex j = temp;
		vertices.clear();
	
		
		while(j != null){
			path.add(j);
			j = j.prev;
		}
		
		path.add(0, destinationVertex);
	
		
		
		
		if(isFound) player.move(path);
		
	}

	public void removePolygon(int x, int y) {
		
		for(MeshArea P : polygons){
			if(P.polygon.contains(x, y)){
				
				for(Vertex v : P.connections){
					
					for(Vertex w : v.targetMesh.connections){
						
						if(w.targetMesh == P){
							v.targetMesh.connections.remove(w);
							break;
						}
					}
				}
				P.connections.clear();
				
				polygons.remove(P);
				break;
			}	
		}
		
	}

	//create xml for saving the screen
	public void createXMLfile(File file) {
		FileManager.createXMLfile(file, this);
	}
	
	public boolean loadXMLFile(File file){
		return FileManager.loadXMLFile(file, this);
	}

	public void setSelectedPolygon(MeshArea p) {
		selectedPolygon = p;
	}

	public void setNPoint(int nPoints) {
		this.nPoints = nPoints;
	}
	
	public int getNPoint(){
		return this.nPoints;
	}

	public Image getImage() {
		return image;
	}

	public boolean showPolygons() {
		return showPolygons;
	}

	public MeshArea getSelectedPolygon() {
		return selectedPolygon;
	}

	public Point getStartingPoint() {
		return startingPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}
	
	public Player getPlayer(){
		return player;
	}

	public void loadBackgroundImage(File file) {

		try {
			image = ImageIO.read(file);
			imagePath = file.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadImage(File file){
		
		Sprite sprite = new Sprite();
		sprite.loadImage(file);
		
		selectedSprite = sprite;
		
		
	}

	public String getImagePath() {
		return imagePath;
	}

	public Sprite getSelectedSprite() {
		return selectedSprite;
	}

	public void setSelectedSprite(Sprite selectedSprite) {
		this.selectedSprite = selectedSprite;
	}

	public void addSprite(int x, int y) {
		selectedSprite.setPos(x - selectedSprite.getWidth()/2, y - selectedSprite.getHeight()/2);
		
		this.sprites.add(selectedSprite);
		
		Collections.sort(this.sprites);
			
	}

	public ArrayList<Sprite> getSprites() {
		return this.sprites;
	}	
	
}

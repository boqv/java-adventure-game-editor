

import java.awt.Point;

public class Vertex  implements Comparable<Vertex>{
	
	Point position;
    MeshArea targetMesh;
    MeshArea destMesh;
    String targetId; //used when loading

	public Vertex prev;
	
	public Vertex(){
		position = new Point();
	}

	@Override
	public int compareTo(Vertex o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setPosition(int x, int y){
		position.x = x;
		position.y = y;
	}


}

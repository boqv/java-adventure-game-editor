import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;


public class Player {

	private volatile Point2D.Float playerPoint;
	private float speed = 1.0f;
	private MeshArea cachedPolygon;
	private Thread thread;
	private boolean isVisible = false;
	
	public BufferedImage sprite;
	private BufferedImage[] leftSprites;
	private BufferedImage[] rightSprites;
	
	private int frame = 0;
	private int frameCounter = 0;
	
	private boolean isWalking = false;
	
	private int direction = 1;
	
	public Player(){
		this.playerPoint = new Point2D.Float(-20, -20);
		try {
			sprite = ImageIO.read(this.getClass().getResource("maincharLEFT.png"));
		} catch (IOException e) {}
		
		
		leftSprites = new BufferedImage[4];
		rightSprites = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++){
			leftSprites[i] = sprite.getSubimage(i * 48, 0, 48, 48);
		}
		
		try {
			sprite = ImageIO.read(this.getClass().getResource("maincharRIGHT.png"));
		} catch (IOException e) {}
		
		
		rightSprites = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++){
			rightSprites[i] = sprite.getSubimage(i * 48, 0, 48, 48);
		}
	}
	
	public boolean isWalking(){
		return isWalking;
	}
	public boolean isVisible(){
		return isVisible;
	}
	
	public void setVisibility(boolean visibility){
		isVisible = visibility;
	}
	
	public float getX(){
		return playerPoint.x;
	}
	
	public float getY(){
		return playerPoint.y;
	}
	
	public void setPosition(float x, float y){
		playerPoint = new Point2D.Float(x, y);
	}
	
	//Get the polygon the player is standing on, if for some reason the player is not standing on a polygon, 
	//send the previously reached polygon.
	public MeshArea getPolygon(ArrayList <MeshArea> polygons){
		for(MeshArea P : polygons){
			
			if(P.polygon.contains(playerPoint.x, playerPoint.y)){
				this.cachedPolygon = P;
				return P;
			}
		}
		
		return this.cachedPolygon;	
	}
	
	public synchronized void move(ArrayList <Vertex> path){
			
			if(thread != null){
				if(thread.isAlive()) thread.stop();
			}
		
			thread = new Thread()
			{
				double now = System.currentTimeMillis();
				double last = now;
				
			    public void run() {
			     
			    	Collections.reverse(path);
			    	isWalking = true;	
			    	Vertex v = path.get(0);
					while(!path.isEmpty()){
						now = System.currentTimeMillis();
						
						if(now - last > 4){
							
							
							
							float dX = v.position.x - playerPoint.x;
							float dY = v.position.y - playerPoint.y;
							
							float len = vectorLength(dX, dY);
							
							dX = dX / len;
							dY = dY / len;
							
							playerPoint.x += dX * speed;
							if(dX >= 0) direction = 1;
							else direction = 4;
							
							playerPoint.y += dY * speed;
							
							if(len < 1){
								cachedPolygon = v.targetMesh;   //update the cached polygon to the newly reached polygon.
								path.remove(0);
								if(!path.isEmpty()) v = path.get(0);
							}
							
							last = now;
						}
					
					}
					isWalking = false;
			    }
			};
			
			thread.start();
	
		}
	
	float vectorLength(float x, float y) {
	    return (float)Math.sqrt(x*x + y*y);
	}

	public Image getSprite() {
		
		if(isWalking){
			frameCounter++;
			if(frameCounter % 20 == 0) frame++;
			if(frame >= 4) frame = 0;
			if(direction == 4) return leftSprites[frame];
			if(direction == 1) return rightSprites[frame];
		}
		else {
			if(direction == 4) return leftSprites[0];
			if(direction == 1) return rightSprites[0];
		}
		
		return null;
	}
}

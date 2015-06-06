import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import javax.imageio.ImageIO;


public class Sprite implements Comparable <Sprite> {

	BufferedImage[] sprite;
	int frames;
	Point position = new Point();
	
	public void loadImage(File file){
		BufferedImage image;
		
		//frames = numberOfSprites;
		
		sprite = new BufferedImage[1];
		
		try {
			image = ImageIO.read(file);
			
			
			for(int i = 0; i < 1; i++){
				sprite[i] = image.getSubimage(i * 64, 0, 64, 64);
			}
		} catch (IOException e) {}
	}
	
	//kommentaren #2
	public int getHeight(){
		return sprite[0].getHeight();
	}
	public int getWidth(){
		return sprite[0].getWidth();
	}

	public void setPos(int x, int y) {
		position.x = x;
		position.y = y;
		
	}
	
	public Point getPos(){
		return position;
	}

	public Image getSprite() {
		return this.sprite[0];
	}


	@Override
	public int compareTo(Sprite o) {
		return this.getPos().y - o.getPos().y;
	}
   
	
}






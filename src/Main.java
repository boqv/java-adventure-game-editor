import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private boolean dragging = false;
	private boolean moving = false;
	private boolean connecting = false;
	private boolean searchMode = false;
	private boolean spriteMode = false;
	private boolean placeCharacter = false;
	private int lastX, lastY;
	private Screen screen;
	private Point mousePos = new Point();
	
	public JFrame jFrame;
	
	Main(){
		addMouseListener(this);
		addMouseMotionListener(this);
		
		screen = new Screen();
		newFile();
		
		gameLoop();
	}
	
	public void gameLoop(){
		new Thread(){
					
			public void run(){
				
				double now = System.currentTimeMillis();
				double last = now;
				
				while(true){
					now = System.currentTimeMillis();
					
					if(now - last > 8){
						repaint();
						last = now;
					}
				}
			
			}
				
		}.start();
	}
	
	public static void main(String[] args) {
		
		Main m = new Main();
		
		m.jFrame = GUI.createJFrame(m);
	}
	
	public boolean loadFile(File file){
		screen.loadXMLFile(file);
		return true;
	}
	
	public boolean saveFile(File file){
		screen.createXMLfile(file);
		return true;
	}
	
	public void newFile(){
		screen.clearScreen();
	}
	
	public void paint(Graphics g){
		
		g.drawImage(screen.getImage(), 0, 0, 640, 400, null);
		
		
	    
	    if(screen.showPolygons()){
			for(MeshArea P : screen.getPolygons()){
			
				
				g.setColor(Color.RED);
			    g.drawPolygon(P.polygon);
			 
			    if(P == screen.getSelectedPolygon()){
				    for(int i = 0; i < P.getNPoints(); i++){
					    g.setColor(Color.BLUE);
					    g.drawOval(P.polygon.xpoints[i] - 8, P.polygon.ypoints[i] - 8, 16, 16);
				    }
			    }
			    
			    for(Vertex v : P.connections){
			    	g.setColor(Color.GREEN);
				    g.fillOval(v.position.x - 5, v.position.y - 5, 10, 10);
			    }
			}
			
			g.drawLine(screen.getStartingPoint().x, screen.getStartingPoint().y, screen.getEndPoint().x, screen.getEndPoint().y);
	    }
		
		 Player player = screen.getPlayer();
		 g.drawImage(player.getSprite(), (int)player.getX() - 44, (int)player.getY() - 86, (int)player.getX() + 52, (int)player.getY() + 10, 
		    		0, 0, 48, 48, null);
		 
		 
		 
		 if(spriteMode){
				BufferedImage b = screen.getSelectedSprite().sprite[0];
				g.drawImage(b, mousePos.x - b.getWidth()/2, mousePos.y - b.getHeight()/2, 64, 64, null);
			}
			
		   
			for(Sprite s : screen.getSprites()){
				
			
					g.drawImage(s.getSprite(), s.getPos().x, s.getPos().y, 64, 64, null);
			}
			
		   
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		//remove a polygon (right-click)
		if((e.getModifiers() == InputEvent.BUTTON3_MASK)){
			
			if(searchMode)
				return;
			
			screen.removePolygon(x, y);
		}
		else {
			screen.insidePolygon(x, y);
		}
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		
		if(spriteMode){
			
			screen.addSprite(x, y);
			spriteMode = false;
		}
		//add new polygon
		if(e.isControlDown()){
			screen.addPolygon(x, y);
			return;	
		}
		
		//connect polygons
		if(e.isShiftDown()){
			
			if(screen.startConnecting(x, y)){
				connecting = true;
			}
			return;
		}
							
		if(screen.insidePolygonCorner(x, y)){
			dragging = true;
			return;
		}
				
		if(screen.insidePolygon(x, y)){
			if(!dragging) moving = true;
			lastX = x;
			lastY = y;
		}
		else {
			screen.setSelectedPolygon(null);
		}
			
	}

	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();;
		
		dragging = false;
		moving = false;
		
		if(placeCharacter){
			screen.placeCharacter(x, y);
			
			placeCharacter = false;
			return;	
		}
		
		if(searchMode){	
			screen.findPath(x, y);
		    return;
		}
		
		if(connecting){
			screen.createConnections(x, y);
			connecting = false;
		}
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();;
		
		if(searchMode)
			return;
		
		if(dragging){
			screen.modifyPolygon(x, y);
		}
		
		if(connecting){
			screen.setConnectionEndPoint(x, y);
		}
		
		else if(moving){
			screen.movePolygon(x, y, lastX, lastY);
		}
		
		lastX = x;
		lastY = y;
		
	}

	public void mouseMoved(MouseEvent e) {
		mousePos.x = e.getX();
		mousePos.y = e.getY();
	}

	public void keyPressed(KeyEvent e) {
	
		
		if(e.getKeyCode() == KeyEvent.VK_J){
			if(!placeCharacter) placeCharacter = true;
			else placeCharacter = false;
			searchMode = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_O){
			toggleShowPolygons();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_4){
			screen.setNPoint(4);
		}
		
		if(e.getKeyCode() == KeyEvent.VK_8){
			screen.setNPoint(8);
		}
		
		
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void changeImage(File file) {
		screen.loadBackgroundImage(file);
		
	}

	public void toggleShowPolygons() {
		screen.showPolygons = !screen.showPolygons;
	}

	public void setMode(int mode) {
		if(mode == Utils.EDITOR) searchMode = false;
		if(mode == Utils.GAME_SIM) searchMode = true;
		
		placeCharacter = false;
	}

	public boolean isSimulating() {
		return searchMode;
	}

	public void addSprite(File file) {
		spriteMode = true;
		
		screen.loadImage(file);
		
	}
}

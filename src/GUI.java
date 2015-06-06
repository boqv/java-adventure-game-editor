import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GUI {
	
	public static JFrame createJFrame(Main context){
		
		JFrame jFrame = new JFrame();
		jFrame.add(context);
		
		jFrame.setSize(640, 480);
		
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        jFrame.setFocusable(true);
		jFrame.requestFocusInWindow();
		 
		jFrame.addKeyListener(context);
		jFrame.setLocationRelativeTo(null);

	    jFrame.setJMenuBar(GUI.createMenu(context));
	    
	    JToolBar toolBar = new JToolBar();
	    JButton togglePolygons = new JButton(new ImageIcon("res/showPolys.png"));
	    
	    togglePolygons.setToolTipText("Show polygons");
	    togglePolygons.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				context.toggleShowPolygons();
				
			}
	    	
	    });
	    
	    JButton toggleEditorMode = new JButton(new ImageIcon("res/play.png"));
	    
	    toggleEditorMode.setToolTipText("Editor/Game Mode");
	    toggleEditorMode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				
				if(context.isSimulating()){
					toggleEditorMode.setIcon(new ImageIcon("res/play.png"));
					context.setMode(Utils.EDITOR);
				}
				else {
					toggleEditorMode.setIcon(new ImageIcon("res/stop.png"));
					context.setMode(Utils.GAME_SIM);
				}
				
			}
	    	
	    });
	    
	
	    JButton addSpriteButton = new JButton(new ImageIcon("res/treeIcon.png"));
	    addSpriteButton.setToolTipText("Add sprite");
	    addSpriteButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Files", "png"));
				if (fileChooser.showOpenDialog(context) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  
				  context.addSprite(file);
				  
				}
			}
	    	
	    });
  
	    toolBar.setVisible(true);
	    toolBar.setPreferredSize(new Dimension(320, 34));
	
	    toolBar.add(togglePolygons);
	    toolBar.add(toggleEditorMode);
	    toolBar.add(addSpriteButton);
	    jFrame.add(toolBar, BorderLayout.NORTH);

		jFrame.setVisible(true);	
		
		return jFrame;
	}
	
	public static JMenuBar createMenu(Main context){
		
		
		JMenuBar menuBar = new JMenuBar();
		
	    JMenu fileMenu = new JMenu("File");
	    
	    JMenuItem newMenuItem = new JMenuItem("New");
	    newMenuItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				
				context.newFile();
				
			}
	    	
	    });
	    
	    JMenuItem saveMenuItem = new JMenuItem("Save");
	    
	    saveMenuItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
				if (fileChooser.showSaveDialog(context) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  
				  //add .xml-extension
				  String filePath = file.getAbsolutePath();
				  if(!filePath.toLowerCase().endsWith(".xml")) {
				      file = new File(filePath + ".xml");
				  }
				  
				  context.saveFile(file);
				}
			}
	    	
	    });
	    JMenuItem loadMenuItem = new JMenuItem("Open");
	    
	    loadMenuItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
				if (fileChooser.showOpenDialog(context) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  
				  context.loadFile(file);
				  
				}
				
			}
	    	
	    });
	    
	    JMenuItem quitMenuItem = new JMenuItem("Quit");
	    
	    quitMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
	    	
	    });
	    
	    fileMenu.add(newMenuItem);
	    fileMenu.add(loadMenuItem);
	    fileMenu.add(saveMenuItem);
	    fileMenu.add(quitMenuItem);
	    
	    JMenu modeMenu = new JMenu("Mode");
	    JMenuItem editorMenuItem = new JMenuItem("Editor");
	    editorMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				context.setMode(Utils.EDITOR);
			}
	    });
	    JMenuItem gameSimMenuItem = new JMenuItem("Game sim");
	    gameSimMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				context.setMode(Utils.GAME_SIM);
			}
	    });
	    
	    modeMenu.add(editorMenuItem);
	    modeMenu.add(gameSimMenuItem);
	    
	    JMenu optionsMenu = new JMenu("Options");
	    
	    JMenuItem changeImageMenuItem = new JMenuItem("Change image");
	    
	    changeImageMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = selectFile("png", context);
				context.changeImage(file);
			}
	    	
	    });
	    
	    optionsMenu.add(changeImageMenuItem);
	    JMenu aboutMenu = new JMenu("About");
	    
	   
	    
	    menuBar.add(fileMenu);
	    menuBar.add(modeMenu);
	    menuBar.add(optionsMenu);
	    menuBar.add(aboutMenu);
	    //menuBar.add(toolBar, BorderLayout.);
	    
	    return menuBar;
	}
	
	public static File selectFile(String filetype, Main context){
		File file = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(filetype+" Files", filetype));
		if (fileChooser.showOpenDialog(context) == JFileChooser.APPROVE_OPTION) {
		  file = fileChooser.getSelectedFile(); 
		}
		return file;
	}
}

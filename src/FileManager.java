import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class FileManager {

public static boolean loadXMLFile(File file, Screen screen){
		
		HashMap<String, MeshArea> loadedPolygons = new HashMap<String, MeshArea>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String imagePath = null;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			screen.getPolygons().clear();
			
			//load image
			NodeList imageNode = doc.getElementsByTagName("image");
			Element imageElement = (Element) imageNode.item(0);
			imagePath = imageElement.getTextContent();
			
			
			
			NodeList nodeList = doc.getElementsByTagName("polygon");
			
			for(int i = 0; i < nodeList.getLength(); i++){
				
				Node polyNode = (Node) nodeList.item(i);
				
				MeshArea newPolygon = null;
				
				if (polyNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element polyElement = (Element) polyNode;
					
					int nPoints = Integer.parseInt(polyElement.getElementsByTagName("nPoints").item(0).getTextContent());
					
					System.out.println(nPoints);
					newPolygon = new MeshArea(nPoints);
					//get id and add to hashmap for the connections.
					String id = polyElement.getElementsByTagName("id").item(0).getTextContent();
					newPolygon.id = id;
			
					//add point positions
					NodeList pointList = polyElement.getElementsByTagName("point");
					
					for(int j = 0; j < nPoints; j++){
						Node pointNode = (Node) pointList.item(j);
						Element pointElement = (Element) pointNode;
						int x = Integer.parseInt(pointElement.getElementsByTagName("x").item(0).getTextContent());
						int y = Integer.parseInt(pointElement.getElementsByTagName("y").item(0).getTextContent());
						newPolygon.polygon.xpoints[j] = x;
						newPolygon.polygon.ypoints[j] = y;
						
					}
					
					
					//add connections
					NodeList connectionList = polyElement.getElementsByTagName("connection");
					
					for(int k = 0; k < connectionList.getLength(); k++){
						Node connectionNode = (Node) connectionList.item(k);
						Element connectionElement = (Element) connectionNode;
						
						int x = Integer.parseInt(connectionElement.getElementsByTagName("x").item(0).getTextContent());
						int y = Integer.parseInt(connectionElement.getElementsByTagName("y").item(0).getTextContent());
						String target = connectionElement.getElementsByTagName("target").item(0).getTextContent();
						
						Vertex newVertex = new Vertex();
						newVertex.setPosition(x, y);
						newVertex.destMesh = newPolygon;
						newVertex.targetId = target;
						
						newPolygon.connections.add(newVertex);
					}
					
				}
				loadedPolygons.put(newPolygon.id, newPolygon);
				screen.getPolygons().add(newPolygon);
			}
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
		
		//connect the dots!!!!!
		for(MeshArea p : screen.getPolygons()){
			for(Vertex v : p.connections){
				v.targetMesh = loadedPolygons.get(v.targetId);
			}
		}
		
		System.out.println(imagePath);
		//change screen image
		screen.loadBackgroundImage(new File(imagePath));
		return true;
	}

//create xml for saving the screen
	public static void createXMLfile(File file, Screen screen) {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("screen");
			
			Element imageElement = doc.createElement("image");
			imageElement.setTextContent(screen.getImagePath());
			
			rootElement.appendChild(imageElement);
			
			for(MeshArea p : screen.getPolygons()){
				
				Element polygonElement = doc.createElement("polygon");
				
				Element idElement = doc.createElement("id");
				idElement.setTextContent(p.id);
				
				polygonElement.appendChild(idElement);
				
				Element nPointsElement = doc.createElement("nPoints");
				nPointsElement.setTextContent(Integer.toString(p.getNPoints()));
				
				polygonElement.appendChild(nPointsElement);
				
				for(int i = 0; i < p.getNPoints(); i++){
					Element positionElement = doc.createElement("point");
						
						Element xPositionElement = doc.createElement("x");
						Element yPositionElement = doc.createElement("y");
						
						xPositionElement.setTextContent(Integer.toString(p.polygon.xpoints[i]));
						yPositionElement.setTextContent(Integer.toString(p.polygon.ypoints[i]));
				
					positionElement.appendChild(xPositionElement);
					positionElement.appendChild(yPositionElement);
						
					polygonElement.appendChild(positionElement);
				}
				
				for(Vertex v : p.connections){
					Element connectionElement = doc.createElement("connection");
							
						Element cPositionElement = doc.createElement("position");
							Element xPositionElement = doc.createElement("x");
							Element yPositionElement = doc.createElement("y");
							
							xPositionElement.setTextContent(Integer.toString(v.position.x));
							yPositionElement.setTextContent(Integer.toString(v.position.y));
									
						cPositionElement.appendChild(xPositionElement);
						cPositionElement.appendChild(yPositionElement);
								
						Element targetElement = doc.createElement("target");
						targetElement.setTextContent(v.targetMesh.id);
								
						connectionElement.appendChild(cPositionElement);
						connectionElement.appendChild(targetElement);
					
					polygonElement.appendChild(connectionElement);
				}
				
				rootElement.appendChild(polygonElement);
			}
			doc.appendChild(rootElement);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
				
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
		
	
	}
}

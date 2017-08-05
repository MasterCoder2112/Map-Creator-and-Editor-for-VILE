import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Title: Display
 * @author Alexander Byrd
 * Date Created: November 3, 2016
 * 
 * Description:
 * Creates the map creator and editor display, and also displays all the
 * buttons and options on the screen. Allows you to create a new map
 * for a raycasting game, edit an existing map, and save or load maps
 * after creating them or wanting to open a new one. It also allows the
 * user to traverse the map, and add whatever the user wants to the map.
 *
 */
public class Display extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private static final double versionNum = 1.5;
	
	//For the 4 key bindings
	private static Action leftAction;
	private static Action rightAction;
	private static Action upAction;
	private static Action downAction;
	
	//Creates a layeredPane which allows for layering JComponents in the 
	//Pane
	private JLayeredPane mapCreation;
	
	//Menu Stuff /////////////////////
	private JMenuBar options;
	private JMenu mapOptions;
	private JMenuItem newMap;
	private JMenuItem openMap;
	private JMenuItem saveMap;
	private JMenuItem chooseGame;
	//////////////////////////////////
	
	//Textfields /////////////////////
	private JTextField height;
	private JTextField mapDimensions;
	private JTextField shownMapSize;
	private JTextField mapName;
	private JTextField itemRotation;
	private JTextField mapNum;
	private JTextField mapAudio;
	private JTextField mapHeight;
	private JTextField mapBrightness;
	private JTextField itemActID;
	//////////////////////////////////
	
	//JLabels ////////////////////////
	private JLabel heightTitle;
	private JLabel mapDimensionTitle;
	private JLabel currentWallTitle;
	private JLabel shownMapSizeTitle;
	private JLabel background;
	private JLabel mapNameTitle;
	private JLabel mapNumTitle;
	private JLabel mapAudioTitle;
	private JLabel itemRotationTitle;
	private JLabel itemActIDTitle;
	private JLabel mapHeightTitle;
	private JLabel mapBrightnessTitle;
	private JLabel mapFloorTitle;
	private JLabel mapCeilingTitle;
	//////////////////////////////////
	
	private MapBlock[][] map;
	private long amountOfWalls = 0;
	private long amountOfEntities = 0;
	private long amountOfFloors = 0;
	
	//JButtons ///////////////////////
	private JButton[] wallTypes;
	private JButton[] entityTypes;
	private JButton[] floorTypes;
	private JButton currentWall;
	private JButton currentEntity;
	private JButton wallMode;
	private JButton entityMode;
	private JButton mapOptionMode;
	private JButton nextPage;
	private JButton previousPage;
	private JButton refreshMap;
	private JButton undo;
	private JButton mapFloor;
	private JButton mapCeiling;
	private JButton floorModeButton;
	//////////////////////////////////
	
	//Currently selected wall or entity ID
	private int currentWallType = 0;
	private int currentEntityType = 0;
	
	//How many pages of each will there be?
	//Depends on number of walls or entities
	private int wallPages = 0;
	private int entityPages = 0;
	private int floorPages = 0;
	
	//What page you are on of each
	private int pageW = 0;
	private int pageE = 0;
	private int pageF = 0;
	
	//0 = walls, 1 = Entities, 2 = Map Options
	private int mapMode = 2;
	
	public int column = 0;
	public int row = 0;
	
	//How much of the map is shown on screen
	private int shownMapHeight;
	private int shownMapWidth;
	
	//Map Ceiling and Floor ID's
	private int mapFloorID = 0;
	private int mapCeilingID = 0;
	
	private int buttonSize = 50;
	
	private boolean floorMode = true;
	
	//Holds the past 100 actions in it so that the user can undo if
	//needed
	private ArrayList<MapBlock> actions = new ArrayList<MapBlock>();
	
	//Filename all images are loaded from
	public static String filePath = "Vile";
	
   /**
    * Creates a new Map Creator basically. This displays everything
    * and starts the program.
    */
	public Display()
	{
		//Gets the computers Screen size in width and height to be used
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		//Gets the files that can be used to display the elements of the
		//map if the files are avaialbe.
		try
		{
			amountOfWalls = 
					Files.list(Paths.get(filePath+"/walls")).count();
			amountOfEntities = 
					Files.list(Paths.get(filePath+"/entities")).count();
			amountOfFloors = 
					Files.list(Paths.get(filePath+"/floors")).count();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	   /*
	    * Based on the amount of files found, the list on the side of
	    * the screen displays the number of images/types avaiable for
	    * both walls and entitys on the side. No more, and no less.
	    * Also figures the amount of pages that you can scroll through
	    * for walls or entities depending on how many there are of each.
	    * Each page holds 36 walls or entities.
	    */
		wallTypes = new JButton[(int)amountOfWalls];
		entityTypes = new JButton[(int)amountOfEntities];
		floorTypes = new JButton[(int)amountOfFloors];
		wallPages = (int)amountOfWalls / 36;
		entityPages = (int)amountOfEntities / 36;
		floorPages = (int)amountOfFloors / 36;
		
		//Known JComponent shtuff
		this.setTitle("Map Creator/Editor Version "+versionNum);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize((int)width, (int)height);
		
		//Start frame from coordinates 0,0 on screen
		this.setLocation(0, 0);
		this.setResizable(false);
		this.setVisible(true);
		
		upAction =  new UpAction();
		downAction = new DownAction();
		leftAction = new LeftAction();
		rightAction = new RightAction();
			
		mapCreation = new JLayeredPane();
		mapCreation.setLayout(null);
		
		mapCreation.setBackground(new Color(0,0,0));
		mapCreation.setOpaque(true);
		
		this.setContentPane(mapCreation);
		
		mapCreation.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put
		(KeyStroke.getKeyStroke("UP"), "upAction");
		mapCreation.getActionMap().put("upAction", upAction);
		
		mapCreation.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put
				(KeyStroke.getKeyStroke("DOWN"), "downAction");
		mapCreation.getActionMap().put("downAction", downAction);
		
		mapCreation.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put
				(KeyStroke.getKeyStroke("RIGHT"), "rightAction");
		mapCreation.getActionMap().put("rightAction", rightAction);
		
		mapCreation.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put
				(KeyStroke.getKeyStroke("LEFT"), "leftAction");
		mapCreation.getActionMap().put("leftAction", leftAction);
		
		options = new JMenuBar();
		mapOptions = new JMenu("Options");
		setUpMenuItems();
		
		//Adds menu and its options
		mapOptions.add(newMap);
		mapOptions.add(openMap);
		mapOptions.add(saveMap);
		mapOptions.add(chooseGame);
		
		options.add(mapOptions);
		options.setVisible(true);
		this.setJMenuBar(options);
		
		//Adds text fields
		setUpTextfields();
		mapCreation.repaint();
		
		//Sets up JLabels
		setUpLabels();
		mapCreation.repaint();
		
		//Sets up Buttons then sufficiently updates the screen.
		setUpButtons();
		mapCreation.repaint();
		mapCreation.repaint();
		mapCreation.repaint();	
		
		//Sets shownMapHeight and shownMapWidth to their default values
		String[] mapShown = shownMapSize.getText().split("x");
		shownMapHeight = Integer.parseInt(mapShown[0]);
		shownMapWidth = Integer.parseInt(mapShown[1]);
	}

	//Starts the display
	public static void main(String[] args) 
	{
		new Display();
	}

	@Override
   /**
    * Takes any actions performed, and activates the corresponding
    * events to each.
    */
	public void actionPerformed(ActionEvent e) 
	{
	   /*
	    * If newMap is pressed, create a new map of dimensions that are
	    * defined in the mapDimensions textfield. If there was a map
	    * drawn before, erase it before drawing this one.
	    */
		if(e.getSource() == newMap)
		{
			String dimensions = mapDimensions.getText();
			String[] dimensionsActual = dimensions.split("x");
			
			row = 0;
			column = 0;
			
			int width = Integer.parseInt(dimensionsActual[0]);
			int height = Integer.parseInt(dimensionsActual[1]);
			
			if(map != null)
			{
				eraseMap();
			}
			
			//Instantiates size of new map
			map = new MapBlock[height][width];
			
			//Create new map of MapBlocks
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					map[i][j] = new MapBlock(i,j);
				}
			}
			
			//Gets new dimensions of the map
			String[] mapDim = mapDimensions.getText().split("x");
			int mapHeight = Integer.parseInt(mapDim[0]);
			int mapWidth = Integer.parseInt(mapDim[1]);
			
			//Gets how much of map is shown
			String[] mapShown = shownMapSize.getText().split("x");
			int shownMapHeight = Integer.parseInt(mapShown[0]);
			int shownMapWidth = Integer.parseInt(mapShown[1]);
			
		   /*
		    * Determines if what you want to show is more than how much
		    * of the map is actually available, and if it is less, then
		    * just show what you can.
		    */
			if(mapHeight >= shownMapHeight)
			{
				mapHeight = shownMapHeight;
			}
			
			if(mapWidth >= shownMapWidth)
			{
				mapWidth = shownMapWidth;
			}
			
			int buttonSize;
			
		   /*
		    * Determines button size based on the size of the map being
		    * displayed. If a lot of the map is being shown at once, then
		    * the buttons will have to be smaller to fit in the same area
		    * that fewer buttons with a larger size would fit in.
		    */
			if(mapWidth > mapHeight)
			{
				if(mapWidth / 10 != 0)
				{
					buttonSize = 50 / (mapWidth / 10);
				}
				else
				{
					buttonSize = 50;
				}
			}
			else
			{
				if(mapHeight / 10 != 0)
				{
					buttonSize = 50 / (mapHeight / 10);
				}
				else
				{
					buttonSize = 50;
				}
			}
			
		   /*
		    * Display given map on the screen with the given width and
		    * height of buttons shown, and a calculated button size
		    * to set them within the same space in the frame.
		    */
			for(int i = 0; i < mapHeight; i++)
			{
				for(int j = 0; j < mapWidth; j++)
				{
					map[i][j].setBounds(250 + (j * buttonSize), 75 + (i * buttonSize),
							buttonSize, buttonSize);
					
					map[i][j].itemButton.setBounds
					(250 + ((j - column) * buttonSize) + (buttonSize / 4), 
							75 + ((i - row) * buttonSize) + (buttonSize / 4),
							buttonSize / 2, buttonSize / 2);

					map[i][j].addActionListener(this);
					
					//Items are shown above the type of block itself
					mapCreation.add(map[i][j].itemButton, new Integer(2));
					mapCreation.add(map[i][j], new Integer(1));
					
					mapCreation.repaint();
				}
			}
			
			refreshMap();
		}
		
	   /*
	    * If in wallMode, display all the wall types available to place
	    * on the screen, and remove all the items from the side so only
	    * the wall types will be seen.
	    * 
	    * Also removes all floor and map option buttons from the screen
	    * if they are there as well.
	    */
		if(e.getSource() == wallMode)
		{
			mapMode = 0;
			pageW = 0;
			
			currentWallTitle.setText("Current Wall Type:");
			currentWallTitle.setVisible(true);
			
			if(wallPages == 0)
			{
				nextPage.setVisible(false);
				previousPage.setVisible(false);
			}
			else
			{
				nextPage.setVisible(true);
				previousPage.setVisible(true);
			}
			
			currentWall.setVisible(true);
			currentEntity.setVisible(false);
			itemRotation.setVisible(false);
			itemRotationTitle.setVisible(false);
			itemActID.setVisible(false);
			itemActIDTitle.setVisible(false);
			mapDimensions.setVisible(false);
			mapName.setVisible(false);
			mapAudio.setVisible(false);
			mapNum.setVisible(false);
			mapHeight.setVisible(false);
			mapBrightness.setVisible(false);
			mapDimensionTitle.setVisible(false);
			mapNameTitle.setVisible(false);
			mapAudioTitle.setVisible(false);
			mapNumTitle.setVisible(false);
			mapHeightTitle.setVisible(false);
			mapBrightnessTitle.setVisible(false);
			mapFloor.setVisible(false);
			mapCeiling.setVisible(false);
			floorModeButton.setVisible(false);
			mapFloorTitle.setVisible(false);
			mapCeilingTitle.setVisible(false);
			heightTitle.setVisible(true);
			height.setVisible(true);
			
			//Remove all floor buttons
			for(int i = 0; i < floorTypes.length; i++)
			{
				floorTypes[i].setVisible(false);
				mapCreation.repaint();
			}
			
			//Remove all entity buttons
			for(int i = 0; i < entityTypes.length; i++)
			{
				entityTypes[i].setVisible(false);
				mapCreation.repaint();
			}
			
			int wallsDrawn = wallTypes.length;
			
			if(wallTypes.length > 36)
			{
				wallsDrawn = 36;
			}
			
			for(int i = 0; i < wallsDrawn; i++)
			{
				wallTypes[i].setVisible(true);
				mapCreation.repaint();	
			}
		}
		
	   /*
	    * Basically same as the wallMode did except for it removes the
	    * wall icons, and adds the entity ones to the screen to be used.
	    * 
	    * Also removes all floor and map option buttons from the screen
	    * if they are there as well.
	    */
		if(e.getSource() == entityMode)
		{
			mapMode = 1;
			pageE = 0;
			
			currentWallTitle.setText("Current Entity Type:");
			currentWallTitle.setVisible(true);
			
			if(entityPages == 0)
			{
				nextPage.setVisible(false);
				previousPage.setVisible(false);
			}
			else
			{
				nextPage.setVisible(true);
				previousPage.setVisible(true);
			}
			
			currentWall.setVisible(false);
			currentEntity.setVisible(true);
			itemRotation.setVisible(true);
			itemRotationTitle.setVisible(true);
			itemActID.setVisible(true);
			itemActIDTitle.setVisible(true);
			mapDimensions.setVisible(false);
			mapName.setVisible(false);
			mapAudio.setVisible(false);
			mapNum.setVisible(false);
			mapHeight.setVisible(false);
			mapBrightness.setVisible(false);
			mapDimensionTitle.setVisible(false);
			mapNameTitle.setVisible(false);
			mapAudioTitle.setVisible(false);
			mapNumTitle.setVisible(false);
			mapHeightTitle.setVisible(false);
			mapBrightnessTitle.setVisible(false);
			mapFloor.setVisible(false);
			mapCeiling.setVisible(false);
			floorModeButton.setVisible(false);
			mapFloorTitle.setVisible(false);
			mapCeilingTitle.setVisible(false);
			heightTitle.setVisible(false);
			height.setVisible(false);
					
			//Remove all floor buttons
			for(int i = 0; i < floorTypes.length; i++)
			{
				floorTypes[i].setVisible(false);
				mapCreation.repaint();
			}
			
			for(int i = 0; i < wallTypes.length; i++)
			{
				wallTypes[i].setVisible(false);
				mapCreation.repaint();	
			}
			
			int entitiesDrawn = entityTypes.length;
			
			if(entityTypes.length > 36)
			{
				entitiesDrawn = 36;
			}
			
			for(int i = 0; i < entitiesDrawn; i++)
			{
				entityTypes[i].setVisible(true);
				mapCreation.repaint();
			}
		}
		
		//The map option and floor/ceiling mode
		if(e.getSource() == mapOptionMode)
		{
			//Set up this mode
			setUpFloorMode();
		}
		
	   /*
	    * Whenever you want to display the next page, it removes the
	    * elements from the current page, and displays the next page
	    * of walls or entities depending on the mode you're in.
	    */
		if(e.getSource() == nextPage)
		{
			//If walls
			if(mapMode == 0 && pageW < wallPages)
			{
				pageW++;
				
				int wallsDrawn = (pageW * 36) + 36;
				
				if(wallsDrawn > wallTypes.length)
				{
					wallsDrawn = wallTypes.length;
				}
				
				//Reset them all first
				for(int i = 0; i < wallTypes.length; i++)
				{
					wallTypes[i].setVisible(false);
					mapCreation.repaint();
				}
				
				for(int i = (pageW * 36); i < wallsDrawn; i++)
				{
					wallTypes[i].setVisible(true);
					mapCreation.repaint();
				}
			}
			//If Entities
			else if(mapMode == 1 && pageE < entityPages)
			{
				pageE++;
				
				int entitiesDrawn = (pageE * 36) + 36;
				
				if(entitiesDrawn > entityTypes.length)
				{
					entitiesDrawn = entityTypes.length;
				}
				
				//Reset them all first
				for(int i = 0; i < entityTypes.length; i++)
				{
					entityTypes[i].setVisible(false);
					mapCreation.repaint();
				}
				
				for(int i = (pageE * 36); i < entitiesDrawn; i++)
				{
					entityTypes[i].setVisible(true);
					mapCreation.repaint();
				}
			}
			//If Floors
			else if(mapMode == 2 && pageF < floorPages)
			{
				pageF++;
				
				int floorsDrawn = (pageF * 36) + 36;
				
				if(floorsDrawn > floorTypes.length)
				{
					floorsDrawn = floorTypes.length;
				}
				
				//Reset them all first
				for(int i = 0; i < floorTypes.length; i++)
				{
					floorTypes[i].setVisible(false);
					mapCreation.repaint();
				}
				
				for(int i = (pageF * 36); i < floorsDrawn; i++)
				{
					floorTypes[i].setVisible(true);
					mapCreation.repaint();
				}
			}
		}
		
	   /*
	    * Does the same thing as nextPage, but basically just the
	    * opposite way in terms of pages.
	    */
		if(e.getSource() == previousPage)
		{
			//If walls
			if(mapMode == 0 && pageW > 0)
			{
				pageW--;
				
				int wallsDrawn = (pageW * 36) + 36;
				
				if(wallsDrawn > wallTypes.length)
				{
					wallsDrawn = wallTypes.length;
				}
				
				//Reset them all first
				for(int i = 0; i < wallTypes.length; i++)
				{
					wallTypes[i].setVisible(false);
					mapCreation.repaint();
				}
				
				for(int i = (pageW * 36); i < wallsDrawn; i++)
				{
					wallTypes[i].setVisible(true);
					mapCreation.repaint();
				}
			}
			//If Entities
			else if(mapMode == 1 && pageE > 0)
			{
				pageE--;
				
				int entitiesDrawn = (pageE * 36) + 36;
				
				if(entitiesDrawn > entityTypes.length)
				{
					entitiesDrawn = entityTypes.length;
				}
				
				//Reset them all first
				for(int i = 0; i < entityTypes.length; i++)
				{
					entityTypes[i].setVisible(false);
					mapCreation.repaint();
				}
				
				for(int i = (pageE * 36); i < entitiesDrawn; i++)
				{
					entityTypes[i].setVisible(true);
					mapCreation.repaint();
				}
			}
		}
	
	   /*
	    * If the map is refreshed on screen, erase the current map
	    * image and redraw the map on screen.
	    */
		if(e.getSource() == refreshMap && map != null)
		{
			eraseMap();
			
			refreshMap();
		}
		
		//If Floor Mode Button
		if(e.getSource() == floorModeButton)
		{
			if(floorMode)
			{
				floorMode = false;
				floorModeButton.setText("Ceiling Mode On");
			}
			else
			{
				floorMode = true;
				floorModeButton.setText("Floor Mode On");
			}
		}
		
	   /*
	    * If there has been changes made to the map, each version of a
	    * block is recorded in the actions arraylist and every time undo
	    * is pressed, and there are actions that have been made, this
	    * basically takes the previous version of the block changed and
	    * sets it to the block that was changed to make it reverse the
	    * changes that were made. Then take that change out of the
	    * arraylist as it has been undone.
	    */
		if(e.getSource() == undo && actions.size() > 0)
		{
			MapBlock temp = actions.get(actions.size() - 1);
			
			int x = temp.x;
			int y = temp.y;
			
			mapCreation.remove(map[x][y]);
			mapCreation.remove(map[x][y].itemButton);
			mapCreation.remove(map[x][y].heightLabel);
			
			map[x][y] = temp;
			map[x][y].updateImage(buttonSize);
			map[x][y].updateItemImage(buttonSize);
			
			//Set location of button above or below other frame elements
			mapCreation.add(map[x][y], new Integer(1));
			mapCreation.add(map[x][y].itemButton, new Integer(2));
			mapCreation.add(map[x][y].heightLabel, new Integer(3));
			
			refreshMap();
			
			mapCreation.repaint();
			
			actions.remove(actions.size() - 1);
		}
	
	   /*
	    * Set the currentWallType or currentEntityType, and set the image
	    * that corresponds to that type, depending on which wall or entity
	    * is picked, and depending on which map mode (walls or entities)
	    * that you are in.
	    */
		if(mapMode == 0)
		{
			for(int i = 0; i < wallTypes.length; i++)
			{
				if(e.getSource() == wallTypes[i])
				{
					currentWallType = i;
					
					ImageIcon tempIcon = new ImageIcon
					(filePath+"/walls/wall"+i+".png");
					
					//Resizes the Image to meet the size of the button
					Image img = tempIcon.getImage() ;  
					Image newimg = img.getScaledInstance
							(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
					tempIcon = new ImageIcon(newimg);
					
					currentWall.setIcon(tempIcon);
					mapCreation.repaint();
				}
			}
		}
		else if(mapMode == 1)
		{
			for(int i = 0; i < entityTypes.length; i++)
			{
				if(e.getSource() == entityTypes[i])
				{
					currentEntityType = i;
					
					ImageIcon tempIcon = new ImageIcon
							(filePath+"/entities/item"+i+".png");
					
					//Resizes the Image to meet the size of the button
					Image img = tempIcon.getImage() ;  
					Image newimg = img.getScaledInstance
							(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
					tempIcon = new ImageIcon(newimg);
					
					currentEntity.setIcon(tempIcon);
					mapCreation.repaint();
				}
			}
		}
		else
		{
			for(int i = 0; i < floorTypes.length; i++)
			{
				if(e.getSource() == floorTypes[i])
				{		
					ImageIcon tempIcon = new ImageIcon
							(filePath+"/floors/floor"+(i + 1)+".png");
					
					//Resizes the Image to meet the size of the button
					Image img = tempIcon.getImage() ;  
					Image newimg = img.getScaledInstance
							(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
					tempIcon = new ImageIcon(newimg);
					
					//Which mode is it on for setting floor and
					//ceiling
					if(floorMode)
					{
						mapFloorID = i;
						mapFloor.setIcon(tempIcon);
					}
					else
					{
						mapCeilingID = i;
						mapCeiling.setIcon(tempIcon);
					}

					mapCreation.repaint();
				}
			}
		}
		
	   /*
	    * Goes through all the buttons of the map that are shown, and
	    * whichever one is clicked, change it in the corresponding
	    * manner.
	    */
		for(int i = row; i < shownMapHeight + row; i++)
		{
			for(int j = column; j < shownMapWidth + column; j++)
			{
				if(e.getSource() == map[i][j])
				{
				   /*
				    * Create a new MapBlock that has no references to a
				    * already existing object, then add it to the actions
				    * arraylist as a previous version of the block.
				    */
					MapBlock temp = new MapBlock(i, j);
					temp.blockHeight = map[i][j].blockHeight;
					temp.blockID = map[i][j].blockID;
					temp.itemID = map[i][j].itemID;
					temp.itemButton = map[i][j].itemButton;
					temp.heightLabel = map[i][j].heightLabel;
					temp.wallTexture = map[i][j].wallTexture;
					temp.entityTexture = map[i][j].entityTexture;
					
				   /*
				    * See if the text is null, and if it is not, use the
				    * value in it to set the rotation of the enemy. If it
				    * is, then use the default value of 0 as the enemies
				    * rotation.
				    */
					try
					{					
						//Which way enemy/item is looking
						temp.itemRotation = 
								Integer.parseInt(itemRotation.getText());
					}
					catch (Exception ex)
					{
						temp.setToolTipText
						("Rotation: 0, Item Activation ID: "
								+temp.itemActivationID);
						
						//Which way enemy/item is looking
						temp.itemRotation = 0;
					}
					
				   /*
				    * See if the text is null, and if it is not, use the
				    * value in it to set the activation ID of the item. If it
				    * is, then use the default value of 0 as the items
				    * activation ID.
				    */
					try
					{					
						temp.itemActivationID = 
								Integer.parseInt(itemActID.getText());
					}
					catch (Exception ex)
					{
						temp.setToolTipText
						("Rotation: "+temp.itemRotation+
								", Item Activation ID: 0");

						temp.itemActivationID = 0;
					}
					
					//Make sure itemRotation is between 0 and 360
					while(temp.itemRotation > 359)
					{
						temp.itemRotation -= 360;
					}
					
					if(temp.itemRotation <= 0)
					{
						temp.itemRotation = 0;
					}
					
					if(temp.itemActivationID <= 0)
					{
						temp.itemActivationID = 0;
					}
					
					//If blank block, there is no rotation
					if(temp.itemID == 0)
					{
						temp.itemRotation = 0;
						temp.itemActivationID = 0;
					}
					
					//Reset tool tip after checks
					temp.setToolTipText
					("Rotation: "+temp.itemRotation+
							", Item Activation ID: "
							+temp.itemActivationID);
					
					actions.add(temp);
					
					//If past 100 actions, start removing the first action
					//each time a new action is committed.
					if(actions.size() > 100)
					{
						actions.remove(0);
					}
					
					//If wall mode, change wall type and hieght
					if(mapMode == 0)
					{
						map[i][j].blockID = currentWallType;
						map[i][j].blockHeight = 
								Integer.parseInt(height.getText());
						
						if(map[i][j].blockID == 0)
						{
							map[i][j].blockHeight = 0;
						}
						
						map[i][j].size = 0;
						map[i][j].updateImage(buttonSize);
						mapCreation.repaint();
					}
					//If entity mode, change blocks entitytype
					else if(mapMode == 1)
					{
						map[i][j].itemID = currentEntityType;
						map[i][j].size2 = 0;
						map[i][j].updateItemImage(buttonSize);
						
					   /*
					    * See if the text is null, and if it is not, use the
					    * value in it to set the rotation of the enemy. If it
					    * is, then use the default value of 0 as the enemies
					    * rotation.
					    */
						try
						{					
							//Which way enemy/item is looking
							map[i][j].itemRotation = 
									Integer.parseInt(itemRotation.getText());
						}
						catch (Exception ex)
						{
							map[i][j].setToolTipText
							("Rotation: 0, Item Activation ID: "
									+map[i][j].itemActivationID);
							
							//Which way enemy/item is looking
							map[i][j].itemRotation = 0;
						}
						
					   /*
					    * See if the text is null, and if it is not, use the
					    * value in it to set the activation ID of the item. If it
					    * is, then use the default value of 0 as the items
					    * activation ID.
					    */
						try
						{					
							map[i][j].itemActivationID = 
									Integer.parseInt(itemActID.getText());
						}
						catch (Exception ex)
						{
							map[i][j].setToolTipText
							("Rotation: "+map[i][j].itemRotation+
									", Item Activation ID: 0");

							map[i][j].itemActivationID = 0;
						}
						
						//Make sure itemRotation is between 0 and 360
						while(map[i][j].itemRotation > 359)
						{
							map[i][j].itemRotation -= 360;
						}
						
						if(map[i][j].itemRotation <= 0)
						{
							map[i][j].itemRotation = 0;
						}
						
						if(map[i][j].itemActivationID <= 0)
						{
							map[i][j].itemActivationID = 0;
						}
						
						//If blank block, there is no rotation
						if(map[i][j].itemID == 0)
						{
							map[i][j].itemRotation = 0;
							map[i][j].itemActivationID = 0;
						}
						
						//Reset tool tip after checks
						map[i][j].setToolTipText
						("Rotation: "+map[i][j].itemRotation+
								", Item Activation ID: "
								+map[i][j].itemActivationID);
					}
					
					mapCreation.repaint();
				}
			}
		}
	}
	
   /**
    * Sets up the menu items shown in the frame.
    */
	public void setUpMenuItems()
	{
		newMap = new JMenuItem("Create New Map");
		newMap.addActionListener(this);
		
	   /*
	    * A new thing I have found out about, the FileDialog object allows
	    * you to navigate the computer using a GUI to find the file
	    * and in its location that you want to open (having to be a text
	    * file) and then opens it when you double click it. It opens it
	    * be using the loadLevel method and the fileLocation that is
	    * sent back by the file you chose.
	    */
		openMap = new JMenuItem(new AbstractAction("Load Map") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(new JFrame(), "Load", FileDialog.LOAD);
                fd.setFile("*.txt");
                fd.setVisible(true);
                String fileLocation = fd.getFiles()[0].getParent();
                String fileName = fd.getFile();
                loadLevel(fileLocation, fileName);
            }
        });
		
		//Does same thing as openMap, but saves the file instead at
		//that location.
		saveMap = new JMenuItem(new AbstractAction("Save Map") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(new JFrame(), "Save", FileDialog.SAVE);
                fd.setFile(".txt");
                fd.setVisible(true);
                String fileLocation = fd.getFiles()[0].getParent();
                String fileName = fd.getFile();
                saveLevel(fileLocation, fileName);
            }
        });
		
		chooseGame = new JMenuItem(new AbstractAction("Choose Texture File") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(new JFrame(), "Choose File", FileDialog.LOAD);
                fd.setVisible(true);
                String fileLocation = fd.getFiles()[0].getParent();
                
                //Convert string to a char array
                char[] fileLocationArray = fileLocation.toCharArray();
                
                //Used to keep creating sections of the original strings
                String sections = fileLocation;
                
                //Keeps tracks of indexs to change the \ around
                ArrayList<Integer> indexsToChange 
                		= new ArrayList<Integer>();
                
                //Default start index
                Integer actualIndex = -1;
                
                while(true)
                {
                	//Each index that \ char is found on
                	Integer temp = sections.indexOf("\\");
                	
                	//If found
                	if(temp != -1)
                	{
                		//Creates new section of string after the last 
                		//index found that was found with the \ char
                		sections = sections.substring(temp.intValue()+1);
                		
                		//Actual index that the \ was found at in the
                		//original string, not in the section
                		actualIndex += temp.intValue() + 1;
                		
                		//Add it to the indexs to change
                		indexsToChange.add(actualIndex);
                	}
                	//If not found
                	else		
                	{
                		break;
                	}
                }
                
                //Change all the indexs that were to be changed
                for(Integer temp: indexsToChange)
                {
                	fileLocationArray[temp.intValue()] = '/';
                }
                
                fileLocation = "";
                
                //Make new string out of char array
                for(char temp: fileLocationArray)
                {
                	fileLocation += temp;
                }
                
                //This is the new filePath
                filePath = fileLocation;
                
                //Reset the buttons and refresh the map so that all
                //the images are reset.
                resetButtons();
                refreshMap();
            }
        });
	}
	
   /**
    * Sets up the textfields and their positions. Pretty
    * typical stuff
    */
	public void setUpTextfields()
	{
		try
		{
			mapDimensions = new JTextField
					(""+map.length+"x"+map[0].length);
		}
		catch(Exception e)
		{
			try
			{
				mapDimensions = new JTextField
					(""+map.length+"x"+map.length);
			}
			catch(Exception ex)
			{
				mapDimensions = new JTextField
						("0x0");
			}
		}
		
		mapName = new JTextField("DefaultName");
		mapName.setEditable(true);
		mapName.setVisible(true);
		mapName.setBounds(this.getWidth() - 250, 
				100, 175, 25);
		mapCreation.add(mapName);
		
		mapDimensions.setEditable(true);
		mapDimensions.setVisible(true);
		mapDimensions.setBounds(this.getWidth() - 250, 
				200, 100, 25);
		mapCreation.add(mapDimensions);
		
		mapNum = new JTextField("4");
		mapNum.setEditable(true);
		mapNum.setVisible(true);
		mapNum.setBounds(this.getWidth() - 250, 
				300, 100, 25);
		mapCreation.add(mapNum);
		
		mapHeight = new JTextField("200");
		mapHeight.setEditable(true);
		mapHeight.setVisible(true);
		mapHeight.setBounds(this.getWidth() - 250, 
				500, 100, 25);
		mapCreation.add(mapHeight);
		
		mapBrightness = new JTextField("10000");
		mapBrightness.setEditable(true);
		mapBrightness.setVisible(true);
		mapBrightness.setBounds(this.getWidth() - 250, 
				600, 100, 25);
		mapCreation.add(mapBrightness);
		
		mapAudio = new JTextField("AudioFileName");
		mapAudio.setEditable(true);
		mapAudio.setVisible(true);
		mapAudio.setBounds(this.getWidth() - 250, 
				400, 100, 25);
		mapCreation.add(mapAudio);
		
		height = new JTextField("0");
		height.setEditable(true);
		height.setVisible(false);
		height.setBounds((this.getWidth() / 2) + 100, 
				175, 100, 25);
		mapCreation.add(height);
		
		shownMapSize = new JTextField("10x10");
		shownMapSize.setEditable(true);
		shownMapSize.setVisible(true);
		shownMapSize.setBounds((this.getWidth() / 2) + 100, 
				450, 100, 25);
		mapCreation.add(shownMapSize);
		
		itemRotation = new JTextField("");
		itemRotation.setEditable(true);
		itemRotation.setVisible(false);
		itemRotation.setBounds((this.getWidth() / 2) + 100, 
				175, 100, 25);
		mapCreation.add(itemRotation);
		
		itemActID = new JTextField("");
		itemActID.setEditable(true);
		itemActID.setVisible(false);
		itemActID.setBounds((this.getWidth() / 2) + 300, 
				335, 100, 25);
		mapCreation.add(itemActID);
	}
	
   /**
    * Sets up the JLabels and their positions.
    */
	public void setUpLabels()
	{
		background = new JLabel();
		background.setBounds(0, 
				0, this.getWidth(), this.getHeight());
		background.setVisible(true);
		mapCreation.add(background, new Integer(3));
		
		mapNameTitle = new JLabel("Map Name:");
		mapNameTitle.setBounds(this.getWidth() - 250, 
				75, 100, 25);
		mapNameTitle.setVisible(true);
		mapNameTitle.setForeground(Color.GREEN);
		mapCreation.add(mapNameTitle);
		
		heightTitle = new JLabel("Wall Height:");
		heightTitle.setBounds((this.getWidth() / 2) + 100, 
				150, 100, 25);
		heightTitle.setVisible(false);
		heightTitle.setForeground(Color.GREEN);
		mapCreation.add(heightTitle);
		
		mapDimensionTitle = new JLabel("Map Dimensions:");
		mapDimensionTitle.setBounds(this.getWidth() - 250, 
				175, 100, 25);
		mapDimensionTitle.setVisible(true);
		mapDimensionTitle.setForeground(Color.GREEN);
		mapCreation.add(mapDimensionTitle);
		
		mapNumTitle = new JLabel("Map Number:");
		mapNumTitle.setBounds(this.getWidth() - 250, 
				275, 100, 25);
		mapNumTitle.setVisible(true);
		mapNumTitle.setForeground(Color.GREEN);
		mapCreation.add(mapNumTitle);
		
		mapAudioTitle = new JLabel("Map Audio (Just name. Not .wav):");
		mapAudioTitle.setBounds(this.getWidth() - 250, 
				375, 200, 25);
		mapAudioTitle.setVisible(true);
		mapAudioTitle.setForeground(Color.GREEN);
		mapCreation.add(mapAudioTitle);
		
		mapHeightTitle = new JLabel("Map Height:");
		mapHeightTitle.setBounds(this.getWidth() - 250, 
				475, 100, 25);
		mapHeightTitle.setVisible(true);
		mapHeightTitle.setForeground(Color.GREEN);
		mapCreation.add(mapHeightTitle);
		
		mapBrightnessTitle = new JLabel("Map Brightness (10000 is typical):");
		mapBrightnessTitle.setBounds(this.getWidth() - 250, 
				575, 200, 25);
		mapBrightnessTitle.setVisible(true);
		mapBrightnessTitle.setForeground(Color.GREEN);
		mapCreation.add(mapBrightnessTitle);
		
		currentWallTitle = new JLabel("Currently Selected Wall:");
		currentWallTitle.setBounds((this.getWidth() / 2) + 100, 
				260, 200, 25);
		currentWallTitle.setVisible(false);
		currentWallTitle.setForeground(Color.GREEN);
		mapCreation.add(currentWallTitle);
		
		shownMapSizeTitle = new JLabel("Displayed map size:");
		shownMapSizeTitle.setBounds((this.getWidth() / 2) + 100, 
				425, 200, 25);
		shownMapSizeTitle.setVisible(true);
		shownMapSizeTitle.setForeground(Color.GREEN);
		mapCreation.add(shownMapSizeTitle);
		
		itemRotationTitle = new JLabel("Rotation (Degrees, 0 is positive z):");
		itemRotationTitle.setBounds((this.getWidth() / 2) + 100, 
				150, 200, 25);
		itemRotationTitle.setVisible(false);
		itemRotationTitle.setForeground(Color.GREEN);
		mapCreation.add(itemRotationTitle);
		
		itemActIDTitle = new JLabel("Item Activation ID:");
		itemActIDTitle.setBounds((this.getWidth() / 2) + 300, 
				300, 200, 25);
		itemActIDTitle.setVisible(false);
		itemActIDTitle.setForeground(Color.GREEN);
		mapCreation.add(itemActIDTitle);
		
		mapFloorTitle = new JLabel("Map Floor:");
		mapFloorTitle.setBounds((this.getWidth() / 2) + 100, 
				170, 100, 50);
		mapFloorTitle.setVisible(true);
		mapFloorTitle.setForeground(Color.GREEN);
		mapCreation.add(mapFloorTitle);
		
		mapCeilingTitle = new JLabel("Map Ceiling:");
		mapCeilingTitle.setBounds((this.getWidth() / 2) + 100, 
				270, 100, 50);
		mapCeilingTitle.setVisible(true);
		mapCeilingTitle.setForeground(Color.GREEN);
		mapCreation.add(mapCeilingTitle);
	}
	
   /**
    * Sets up the buttons and their positions
    */
	public void setUpButtons()
	{
		//Keep track of every 4 buttons
		int j = 0;
		
		//Keep track of every 36 buttons
		int k = 0;
		
		ImageIcon tempIcon;
		
		wallMode = new JButton("Walls");
		wallMode.setBounds(0, 
				0, 100, 50);
		wallMode.addActionListener(this);
		mapCreation.add(wallMode);
		
		entityMode = new JButton("Entities");
		entityMode.setBounds(100, 
				0, 100, 50);
		entityMode.addActionListener(this);
		mapCreation.add(entityMode);
		
		mapOptionMode = new JButton("Map Options");
		mapOptionMode.setBounds(200, 
				0, 150, 50);
		mapOptionMode.addActionListener(this);
		mapCreation.add(mapOptionMode);
		
		nextPage = new JButton("Next Page");
		nextPage.setBounds(25, 
				50, 150, 50);
		nextPage.addActionListener(this);
		mapCreation.add(nextPage);
		
		previousPage = new JButton("Previous Page");
		previousPage.setBounds(25, 
				100, 150, 50);
		previousPage.addActionListener(this);
		mapCreation.add(previousPage);
		
		undo = new JButton("Undo");
		undo.setBounds(this.getWidth() / 2, 
				15, 150, 50);
		undo.addActionListener(this);
		mapCreation.add(undo);
		
		refreshMap = new JButton("Refresh View");
		refreshMap.setBounds(this.getWidth() / 2, 
				this.getHeight() - 125, 150, 50);
		refreshMap.addActionListener(this);
		mapCreation.add(refreshMap);
		
		if(wallPages == 0)
		{
			nextPage.setVisible(false);
			previousPage.setVisible(false);
		}
		
		for(int i = 0; i < wallTypes.length; i++)
		{
			tempIcon = new ImageIcon
					(filePath+"/walls/wall"+i+".png");
			
			//Resizes the Image to meet the size of the button
			Image img = tempIcon.getImage() ;  
			Image newimg = img.getScaledInstance
					(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
			tempIcon = new ImageIcon(newimg);
			
			if(j % 4 == 0)
			{
				j = 0;
			}
			
			if(k % 36 == 0)
			{
				k = 0;
			}
			
			wallTypes[i] = new JButton();
			wallTypes[i].setBounds(j + (50 * j),
					(50 * ((int)(k/4)+1)) + 100, 50, 50);
			wallTypes[i].setIcon(tempIcon);
			wallTypes[i].requestFocus();
			wallTypes[i].addActionListener(this);
			wallTypes[i].setVisible(false);
			mapCreation.add(wallTypes[i]);
			mapCreation.repaint();
			
			j++;
			k++;
		}
		
		tempIcon = new ImageIcon
				(filePath+"/floors/floor"+currentWallType+".png");
		
		//Resizes the Image to meet the size of the button
		Image img = tempIcon.getImage() ;  
		Image newimg = img.getScaledInstance
				(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
		tempIcon = new ImageIcon(newimg);
		
		currentWall = new JButton();
		currentWall.setBounds((this.getWidth() / 2) + 100, 
				310, 50, 50);
		currentWall.setIcon(tempIcon);
		currentWall.addActionListener(this);
		currentWall.setVisible(false);
		mapCreation.add(currentWall);
		
		mapFloor = new JButton();
		mapFloor.setBounds((this.getWidth() / 2) + 100, 
				210, 50, 50);
		mapFloor.setIcon(tempIcon);
		mapFloor.setVisible(true);
		mapCreation.add(mapFloor);
		
		mapCeiling = new JButton();
		mapCeiling.setBounds((this.getWidth() / 2) + 100, 
				310, 50, 50);
		mapCeiling.setIcon(tempIcon);
		mapCeiling.setVisible(true);
		mapCreation.add(mapCeiling);
		
		floorModeButton = new JButton("Floor Mode On");
		floorModeButton.setBounds(25, 
				450, 150, 50);
		floorModeButton.addActionListener(this);
		mapCreation.add(floorModeButton);
		
		j = 0;
		k = 0;
		
		for(int i = 0; i < floorTypes.length; i++)
		{
			tempIcon = new ImageIcon
					(filePath+"/floors/floor"+(i + 1)+".png");
			
			//Resizes the Image to meet the size of the button
			img = tempIcon.getImage() ;  
			newimg = img.getScaledInstance
					(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
			tempIcon = new ImageIcon(newimg);
			
			if(j % 4 == 0)
			{
				j = 0;
			}
			
			if(k % 36 == 0)
			{
				k = 0;
			}
			
			floorTypes[i] = new JButton();
			floorTypes[i].setBounds(j + (50 * j),
					(50 * ((int)(k/4)+1)) + 100, 50, 50);
			floorTypes[i].setIcon(tempIcon);
			floorTypes[i].addActionListener(this);
			floorTypes[i].setVisible(true);
			mapCreation.add(floorTypes[i]);
			mapCreation.repaint();
			
			j++;
			k++;
		}
		
		j = 0;
		k = 0;
		
		for(int i = 0; i < entityTypes.length; i++)
		{
			tempIcon = new ImageIcon
					(filePath+"/entities/item"+i+".png");
			
			//Resizes the Image to meet the size of the button
			img = tempIcon.getImage() ;  
			newimg = img.getScaledInstance
					(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
			tempIcon = new ImageIcon(newimg);
			
			if(j % 4 == 0)
			{
				j = 0;
			}
			
			if(k % 36 == 0)
			{
				k = 0;
			}
			
			entityTypes[i] = new JButton();
			entityTypes[i].setBounds(j + (50 * j),
					(50 * ((int)(k/4)+1)) + 100, 50, 50);
			entityTypes[i].setIcon(tempIcon);
			entityTypes[i].addActionListener(this);
			entityTypes[i].setVisible(false);
			mapCreation.add(entityTypes[i]);
			mapCreation.repaint();
			
			j++;
			k++;
		}
		
		tempIcon = new ImageIcon
				(filePath+"/entities/item"+currentEntityType+".png");
		
		//Resizes the Image to meet the size of the button
		img = tempIcon.getImage() ;  
		newimg = img.getScaledInstance
				(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
		tempIcon = new ImageIcon(newimg);
		
		currentEntity = new JButton();
		currentEntity.setBounds((this.getWidth() / 2) + 100, 
				310, 50, 50);
		currentEntity.setIcon(tempIcon);
		currentEntity.addActionListener(this);
		currentEntity.setVisible(false);
		currentEntity.setFocusable(false);
		mapCreation.add(currentEntity);
	}
	
   /**
    * Erases the visual map from the screen so a new map can be seen
    */
	public void eraseMap()
	{
		int mapWidth = 0;
		int mapHeight = 0;
		
		try
		{
			mapHeight = map.length;
		}
		catch(Exception e)
		{
		}
		
		try
		{
			mapWidth = map[0].length;
		}
		catch(Exception e)
		{
		}
		
		//Removes all the visual map components on the screen
		for(int i = 0; i < mapHeight; i++)
		{
			for(int j = 0; j < mapWidth; j++)
			{
				map[i][j].removeActionListener(this);
				mapCreation.remove(map[i][j]);
				mapCreation.remove(map[i][j].itemButton);
				mapCreation.remove(map[i][j].heightLabel);
			}
		}
	}
	
   /**
    * Refreshes the map that is shown each time the user refreshes the
    * map, or decides to scroll to the sides, or up and down.
    */
	public void refreshMap()
	{
		//Erase current map to display the refreshed map
		eraseMap();
		
		//Gets mapDimensions you want to reset
		String[] mapDim = mapDimensions.getText().split("x");
		int mapHeight = Integer.parseInt(mapDim[0]);
		int mapWidth = Integer.parseInt(mapDim[1]);
		
	   /*
	    * If the map is a different size now, then use the map already
	    * created and factor it in to the new map of the new size whether
	    * it is larger or smaller. Bascially cut the extended part of the
	    * map off if the map is made smaller, or extend it if it is 
	    * made bigger.
	    */
		if(mapHeight != map.length || mapWidth != map[0].length)
		{
			MapBlock[][] temp = new MapBlock[mapHeight][mapWidth];
			
			for(int i = 0; i < mapHeight; i++)
			{
				for(int j = 0; j < mapWidth; j++)
				{
					try
					{
						temp[i][j] = map[i][j];
					}
					catch(Exception ex)
					{
						temp[i][j] = new MapBlock(i,j);
					}
				}
			}
			
			map = temp;
		}
		
		//Gets how much of map is shown
		String[] mapShown = shownMapSize.getText().split("x");
		shownMapHeight = Integer.parseInt(mapShown[0]);
		shownMapWidth = Integer.parseInt(mapShown[1]);
		
		if(mapHeight >= shownMapHeight)
		{
			mapHeight = shownMapHeight;
		}
		
		if(mapWidth >= shownMapWidth)
		{
			mapWidth = shownMapWidth;
		}
		
		if(mapWidth > mapHeight)
		{
			if(mapWidth % 10 != 0
					&& mapWidth / 10 != 0)
			{
				buttonSize = 50 / ((mapWidth / 10) + 1);
			}
			else if(mapWidth % 10 == 0
					&& mapWidth / 10 != 0)
			{
				buttonSize = 50 / ((mapWidth / 10));
			}
			else
			{
				buttonSize = 50;
			}
		}
		else
		{
			if(mapHeight % 10 != 0
					&& mapHeight / 10 != 0)
			{
				buttonSize = 50 / ((mapHeight / 10) + 1);
			}
			else if(mapHeight % 10 == 0
					&& mapHeight / 10 != 0)
			{
				buttonSize = 50 / ((mapHeight / 10));
			}
			else
			{
				buttonSize = 50;
			}
		}
		
		mapHeight = row + mapHeight;
		mapWidth = column + mapWidth;
		
		if(mapHeight > map.length)
		{
			mapHeight = map.length;
		}
		
		if(mapWidth > map[0].length)
		{
			mapWidth = map[0].length;
		}
		
	   /*
	    * Display given map on the screen with the given width and
	    * height of buttons shown, and a calculated button size
	    * to set them within the same space in the frame.
	    */
		for(int i = row; i < mapHeight; i++)
		{
			for(int j = column; j < mapWidth; j++)
			{
				map[i][j].setBounds(250 + ((j - column) * buttonSize), 
						75 + ((i - row) * buttonSize),
						buttonSize, buttonSize);
				
				map[i][j].itemButton.setBounds
				(250 + ((j - column) * buttonSize) + (buttonSize / 4), 
						75 + ((i - row) * buttonSize) + (buttonSize / 4),
						buttonSize / 2, buttonSize / 2);
				
				map[i][j].heightLabel.setBounds
				(250 + ((j - column) * buttonSize) + (buttonSize / 4), 
						75 + ((i - row) * buttonSize) + (buttonSize / 4),
						buttonSize / 2, buttonSize / 2);
				
				map[i][j].addActionListener(this);
				map[i][j].updateImage(buttonSize);
				map[i][j].updateItemImage(buttonSize);
				mapCreation.add(map[i][j].heightLabel, new Integer(3));
				mapCreation.add(map[i][j].itemButton, new Integer(2));
				mapCreation.add(map[i][j], new Integer(1));
				mapCreation.repaint();
			}
		}
	}
	
   /**
    * Saves the level in the given file location sent in. Saves the file
    * in a way that any game using my game engine can read and make into
    * levels.
    * 
    * @param fileLocation
    * @param fileName
    */
	public void saveLevel(String fileLocation, String fileName)
	{
		//Printing output to a file. Prevents memory leaks
		BufferedWriter save;
		
	   /*
	    * Tries to write the integer to the file, and if it can't it
	    * catches the exception.
	    */
		try 
		{
		   /*
		    * Writes a file called data.txt, writes an integer in String
		    * format into it, and then closes the file.
		    */
			save = new BufferedWriter(new FileWriter(fileLocation+"\\"+fileName));
			
			//Write the maps name to the file, then go to the next line.
			save.write(mapName.getText());
			save.newLine();
			
			String mapNumtemp = mapNum.getText();
			String mapHeighttemp =  mapHeight.getText();
			String mapBrighttemp = mapBrightness.getText();
			String mapAudiotemp = mapAudio.getText();
			String temp = "";
			
			if(mapNumtemp == "")
			{
				mapNumtemp = "1001001";
			}
			
			if(mapHeighttemp == "")
			{
				mapHeighttemp = "200";
			}
			
			if(mapBrighttemp == "")
			{
				mapBrighttemp = "10000";
			}
			
			if(mapAudiotemp == "")
			{
				mapAudiotemp = "level11";
			}
			
			temp = mapNumtemp+":"+mapHeighttemp+":"+mapBrighttemp+
					":"+mapAudiotemp+":"+mapFloorID+":"+mapCeilingID;
			
			//Writes in map number
			save.write(temp);
			save.newLine();
			
        	for(int i = 0; i < map.length; i++)
        	{
        		for(int j = 0; j < map[0].length; j++)
        		{
        			MapBlock m = map[i][j];
        			
        			int tempRotation = 0;
	        		tempRotation = m.itemRotation; 		
        			
        			save.write(m.blockHeight+":"+m.blockID+":"
        			+m.itemID+":"+tempRotation+":"
        					+m.itemActivationID+",");
        		}
        		
        		//End of row of blocks
        		save.write("0:100:0:0:0,");
        		save.newLine();
        	}
        	
        	save.close();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
	}
	
   /**
    * Loads the file at the given file location sent in to the program
    * and sets up the map based on the map loaded.
    * 
    * @param fileLocation
    * @param fileName
    */
	public void loadLevel(String fileLocation, String fileName)
	{
		//Erase the previous map loaded if there is one
		if(map != null)
		{
			eraseMap();
		}
		
		//A new scanner object that is defaultly set to null
		Scanner sc = null;  
		
	   /*
	    * Try to read the file and set the integer read to target,
	    * and catch the exception if it can't. Then, if the file is 
	    * able to be read and opened, after reading the integer go
	    * to the "finally" and close the scanner.
	    */
		try 
		{
			//Creates a Scanner that can read the file
			sc = new Scanner(new BufferedReader
					(new FileReader(fileLocation+"\\"+fileName)));
			
			int r = 0;
			int c = 0;
			int colNum = 0;
			
			ArrayList<MapBlock> temp = new ArrayList<MapBlock>();
			
			//First Line of file should be name of map
			mapName.setText(sc.nextLine());
			
			try
			{
				String mapSettings = sc.nextLine();
				String[] temp2 = mapSettings.split(":");
				mapNum.setText(temp2[0]);
				mapHeight.setText(temp2[1]);
				mapBrightness.setText(temp2[2]);
				mapAudio.setText(temp2[3]);	
				mapFloorID = (Integer.parseInt(temp2[4]));
				mapCeilingID = (Integer.parseInt(temp2[5]));
			}
			catch(Exception e)
			{
				
			}
			
		   /*
		    * While the entire file has not been read yet, keep reading
		    * each line and spliting it up into its corresponding blocks
		    * then spliting up those blocks into their parts to add their
		    * values to the map in this editor.
		    */
			while (sc.hasNextLine())
			{          
				String line = sc.nextLine();
				String[] blocks = line.split(",");
				
				for(String block: blocks)
				{
					String[] values = block.split(":");
					
					//If not end of line signal, create block
					if(Integer.parseInt(values[1]) != 100)
					{
						MapBlock thisBlock = new MapBlock(r, c);
						
						thisBlock.blockHeight = Integer.parseInt(values[0]);
						thisBlock.blockID = Integer.parseInt(values[1]);
						thisBlock.itemID = Integer.parseInt(values[2]);
						
					   /*
					    * Try to set itemRotation from the map file, but
					    * if it is non-exsistant because it is an older
					    * map file, then just set it to a default of 0.
					    */
						try
						{
							thisBlock.itemRotation 
								= Integer.parseInt(values[3]);
						}
						catch (Exception e)
						{
							thisBlock.itemRotation = 0;
						}
						
					   /*
					    * Try to set itemActID from the map file, but
					    * if it is non-exsistant because it is an older
					    * map file, then just set it to a default of 0.
					    */
						try
						{
							thisBlock.itemActivationID 
								= Integer.parseInt(values[4]);
						}
						catch (Exception e)
						{
							thisBlock.itemActivationID = 0;
						}
						
						//Set tooltip
						thisBlock.setToolTipText
						("Rotation: "+thisBlock.itemRotation+
								", Item Activation ID: "+
								thisBlock.itemActivationID);
						
						temp.add(thisBlock);
					}
					
					c++;
				}
				
				colNum = c - 1;
				r++;
				c = 0;
			}
			
			//Size should of been 
			map = new MapBlock[r][colNum];
			
			//Sets new map size text based on new map size
			mapDimensions.setText(map.length+"x"+map[0].length);
			
		   /*
		    * Sets each block of the map to each block loaded in from
		    * the file.
		    */
			for(MapBlock block: temp)
			{
				int blockRow = block.x;
				int blockCol = block.y;
				
				map[blockRow][blockCol] = block;
				mapCreation.add(map[blockRow][blockCol]);
			}
			
			column = 0;
			row = 0;
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
		   /*
		    * Always close the file
		    */
			if (sc != null)
			{
				sc.close();
			}
			
			//Always refresh the map
			refreshMap();
			
			setUpFloorMode();
			
			resetButtons();
		}
	}
	
   /**
    * Resets textures of buttons on screen to match the new Game
    * textures that are chosen.
    */
	public void resetButtons()
	{
		  
        //Gets the files that can be used to display the elements of the
  		//map if the files are available.
  		try
  		{
  			amountOfWalls = 
  					Files.list(Paths.get(filePath+"/walls")).count();
  			amountOfEntities = 
  					Files.list(Paths.get(filePath+"/entities")).count();
  			amountOfFloors = 
  					Files.list(Paths.get(filePath+"/floors")).count();
  		}
  		catch(Exception ex)
  		{
  			System.out.println(ex);
  		}
  		
  		//Remove current wall types
  		for(int i = 0; i < wallTypes.length; i++)
  		{
  			mapCreation.remove(wallTypes[i]);
  		}
  		
  		//Remove current entity types
  		for(int i = 0; i < entityTypes.length; i++)
  		{
  			mapCreation.remove(entityTypes[i]);
  		}
  		
  		//Remove current floor types
  		for(int i = 0; i < floorTypes.length; i++)
  		{
  			mapCreation.remove(floorTypes[i]);
  		}
  		
  	   /*
  	    * Based on the amount of files found, the list on the side of
  	    * the screen displays the number of images/types avaiable for
  	    * both walls and entitys on the side. No more, and no less.
  	    * Also figures the amount of pages that you can scroll through
  	    * for walls or entities depending on how many there are of each.
  	    * Each page holds 36 walls or entities.
  	    */
  		wallTypes = new JButton[(int)amountOfWalls];
  		entityTypes = new JButton[(int)amountOfEntities];
  		floorTypes = new JButton[(int)amountOfFloors];
  		wallPages = (int)amountOfWalls / 36;
  		entityPages = (int)amountOfEntities / 36;
  		floorPages = (int)amountOfFloors / 36;
  		
		//Keep track of every 4 buttons
		int j = 0;
				
		//Keep track of every 36 buttons
		int k = 0;
		
		if(wallPages == 0)
		{
			nextPage.setVisible(false);
			previousPage.setVisible(false);
		}
		
		for(int i = 0; i < wallTypes.length; i++)
		{
			ImageIcon tempIcon = new ImageIcon
					(filePath+"/walls/wall"+i+".png");
			
			//Resizes the Image to meet the size of the button
			Image img = tempIcon.getImage() ;  
			Image newimg = img.getScaledInstance
					(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
			tempIcon = new ImageIcon(newimg);
			
			if(j % 4 == 0)
			{
				j = 0;
			}
			
			if(k % 36 == 0)
			{
				k = 0;
			}
			
			wallTypes[i] = new JButton();
			wallTypes[i].setBounds(j + (50 * j),
					(50 * ((int)(k/4)+1)) + 100, 50, 50);
			wallTypes[i].setIcon(tempIcon);
			wallTypes[i].requestFocus();
			wallTypes[i].setVisible(false);
			wallTypes[i].addActionListener(this);
			mapCreation.add(wallTypes[i]);
			mapCreation.repaint();
			
			j++;
			k++;
		}
		
		//Reset current wall Type
		currentWallType = 0;
		
		ImageIcon tempIcon = new ImageIcon
				(filePath+"/walls/wall"+currentWallType+".png");
		
		//Resizes the Image to meet the size of the button
		Image img = tempIcon.getImage() ;  
		Image newimg = img.getScaledInstance
				(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
		tempIcon = new ImageIcon(newimg);
		
		mapCreation.remove(currentWall);
		currentWall = new JButton();
		currentWall.setBounds((this.getWidth() / 2) + 100, 
				350, 50, 50);
		currentWall.setIcon(tempIcon);
		currentWall.addActionListener(this);
		currentWall.setVisible(false);
		mapCreation.add(currentWall);
		
		j = 0;
		k = 0;
		
		for(int i = 0; i < floorTypes.length; i++)
		{
			tempIcon = new ImageIcon
					(filePath+"/floors/floor"+(i + 1)+".png");
			
			//Resizes the Image to meet the size of the button
			img = tempIcon.getImage() ;  
			newimg = img.getScaledInstance
					(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
			tempIcon = new ImageIcon(newimg);
			
			if(j % 4 == 0)
			{
				j = 0;
			}
			
			if(k % 36 == 0)
			{
				k = 0;
			}
			
			floorTypes[i] = new JButton();
			floorTypes[i].setBounds(j + (50 * j),
					(50 * ((int)(k/4)+1)) + 100, 50, 50);
			floorTypes[i].setIcon(tempIcon);
			floorTypes[i].addActionListener(this);
			floorTypes[i].setVisible(true);
			mapCreation.add(floorTypes[i]);
			mapCreation.repaint();
			
			j++;
			k++;
		}
		
		tempIcon = new ImageIcon
				(filePath+"/floors/floor"+(mapFloorID + 1)+".png");
		
		//Resizes the Image to meet the size of the button
		img = tempIcon.getImage() ;  
		newimg = img.getScaledInstance
				(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
		tempIcon = new ImageIcon(newimg);
		
		mapCreation.remove(mapFloor);
		mapFloor = new JButton();
		mapFloor.setBounds((this.getWidth() / 2) + 100, 
				210, 50, 50);
		mapFloor.setIcon(tempIcon);
		mapFloor.addActionListener(this);
		mapFloor.setVisible(true);
		mapCreation.add(mapFloor);
		
		tempIcon = new ImageIcon
				(filePath+"/floors/floor"+(mapCeilingID + 1)+".png");
		
		//Resizes the Image to meet the size of the button
		img = tempIcon.getImage() ;  
		newimg = img.getScaledInstance
				(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
		tempIcon = new ImageIcon(newimg);
		
		mapCreation.remove(mapCeiling);
		mapCeiling = new JButton();
		mapCeiling.setBounds((this.getWidth() / 2) + 100, 
				310, 50, 50);
		mapCeiling.setIcon(tempIcon);
		mapCeiling.addActionListener(this);
		mapCeiling.setVisible(true);
		mapCreation.add(mapCeiling);
		
		j = 0;
		k = 0;
		
		for(int i = 0; i < entityTypes.length; i++)
		{
			tempIcon = new ImageIcon
					(filePath+"/entities/item"+i+".png");
			
			//Resizes the Image to meet the size of the button
			img = tempIcon.getImage() ;  
			newimg = img.getScaledInstance
					(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
			tempIcon = new ImageIcon(newimg);
			
			if(j % 4 == 0)
			{
				j = 0;
			}
			
			if(k % 36 == 0)
			{
				k = 0;
			}
			
			entityTypes[i] = new JButton();
			entityTypes[i].setBounds(j + (50 * j),
					(50 * ((int)(k/4)+1)) + 100, 50, 50);
			entityTypes[i].setIcon(tempIcon);
			entityTypes[i].addActionListener(this);
			entityTypes[i].setVisible(false);
			mapCreation.add(entityTypes[i]);
			mapCreation.repaint();
			
			j++;
			k++;
		}
		
		tempIcon = new ImageIcon
				(filePath+"/entities/item"+currentEntityType+".png");
		
		//Resizes the Image to meet the size of the button
		img = tempIcon.getImage() ;  
		newimg = img.getScaledInstance
				(50, 50, java.awt.Image.SCALE_SMOOTH) ;  
		tempIcon = new ImageIcon(newimg);
		
		mapCreation.remove(currentEntity);
		currentEntity = new JButton();
		currentEntity.setBounds((this.getWidth() / 2) + 100, 
				350, 50, 50);
		currentEntity.setIcon(tempIcon);
		currentEntity.addActionListener(this);
		currentEntity.setVisible(false);
		mapCreation.add(currentEntity);
		
		int mapWidth = 0;
		int mapHeight = 0;
		
		try
		{
			mapHeight = map.length;
		}
		catch(Exception e)
		{
		}
		
		try
		{
			mapWidth = map[0].length;
		}
		catch(Exception e)
		{
		}
		
	   /*
	    * Resets button size variables so that they will reset their
	    * images.
	    */
		for(int i = 0; i < mapHeight; i++)
		{
			for(int z = 0; z < mapWidth; z++)
			{
				map[i][z].size = 0;
				map[i][z].size2 = 0;
				
				//If block id is out of the range of the new Game textures
				//then reset everything about the old wall type
				if(map[i][z].blockID > amountOfWalls - 1)
				{
					map[i][z].blockID = 0;
					map[i][z].blockHeight = 0;
				}
				
				//Same with entities
				if(map[i][z].itemID > amountOfEntities - 1)
				{
					map[i][z].itemID = 0;
				}
			}
		}
	}
	
   /**
    * Sets up map creator for being on floor and ceiling mode. Since this
    * is needed to be called in more than one spot in the code, this
    * method is made to free up code space.
    */
	public void setUpFloorMode()
	{
		mapMode = 2;
		pageE = 0;
		
		currentWallTitle.setVisible(false);
		
		if(floorPages == 0)
		{
			nextPage.setVisible(false);
			previousPage.setVisible(false);
		}
		else
		{
			nextPage.setVisible(true);
			previousPage.setVisible(true);
		}
		
		currentWall.setVisible(false);
		currentEntity.setVisible(false);
		itemRotation.setVisible(false);
		itemRotationTitle.setVisible(false);
		itemActID.setVisible(false);
		itemActIDTitle.setVisible(false);
		mapDimensions.setVisible(true);
		mapName.setVisible(true);
		mapAudio.setVisible(true);
		mapNum.setVisible(true);
		mapHeight.setVisible(true);
		mapBrightness.setVisible(true);
		mapDimensionTitle.setVisible(true);
		mapNameTitle.setVisible(true);
		mapAudioTitle.setVisible(true);
		mapNumTitle.setVisible(true);
		mapHeightTitle.setVisible(true);
		mapBrightnessTitle.setVisible(true);
		mapFloor.setVisible(true);
		mapCeiling.setVisible(true);
		floorModeButton.setVisible(true);
		mapFloorTitle.setVisible(true);
		mapCeilingTitle.setVisible(true);
		heightTitle.setVisible(false);
		height.setVisible(false);
		
		
		for(int i = 0; i < wallTypes.length; i++)
		{
			wallTypes[i].setVisible(false);
			mapCreation.repaint();	
		}
		
		for(int i = 0; i < entityTypes.length; i++)
		{
			entityTypes[i].setVisible(false);
			mapCreation.repaint();
		}
		
		int floorsDrawn = floorTypes.length;
		
		if(floorTypes.length > 36)
		{
			floorsDrawn = 36;
		}
		
		for(int i = 0; i < floorsDrawn; i++)
		{
			floorTypes[i].setVisible(true);
			mapCreation.repaint();
		}
	}
	
   /**
    * Literally just for key bindings. This is for the upKey.
    * This is the action that it performs when the key is pressed
    * @author socce
    *
    */
	class UpAction extends AbstractAction
	{
		//Default thing
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			//If there is still more to scroll up on
			if(row > 0)
			{
				row--;
				refreshMap();
			}	
		}	
	}
	
   /**
    * Literally just for key bindings. This is for the down Key.
    * This is the action that it performs when the key is pressed
    * @author socce
    *
    */
	class DownAction extends AbstractAction
	{
		//Default thing
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			//Scrolls down if there is still more to scroll down on
			if(row + shownMapHeight < map.length)
			{
				row++;
				refreshMap();
			}
		}	
	}
	
   /**
    * Literally just for key bindings. This is for the right Key.
    * This is the action that it performs when the key is pressed
    * @author socce
    *
    */
	class RightAction extends AbstractAction
	{
		//Default thing
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			//Scrolls right if there is still more to scroll right on
			if(column + shownMapWidth < map[0].length)
			{
				column++;
				refreshMap();
			}
		}	
	}
	
   /**
    * Literally just for key bindings. This is for the left Key.
    * This is the action that it performs when the key is pressed
    * @author socce
    *
    */
	class LeftAction extends AbstractAction
	{
		//Default thing
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			//Scrolls left if there is still more to scroll left on
			if(column > 0)
			{
				column--;
				refreshMap();
			}
		}	
	}
}

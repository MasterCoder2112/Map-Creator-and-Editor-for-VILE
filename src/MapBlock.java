import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;


/**
 * Title: MapBlock
 * @author Alexander Byrd
 * Date Created: 10/23/2016
 *
 * Description:
 * Creates a new MapBlock element which represents a given tile of the map
 * file being created. This MapBlock's elements can be changed at will so
 * that when creating the map using the main program these values can be
 * changed with one click of a space instead of having to go through a 
 * whole process. These are used to store the values that will be saved
 * into the file.
 * 
 * Extends JButton so it can be displayed correctly, and can change the
 * values of it directly from clicking on a map tile.
 */
public class MapBlock extends JButton
{
	//Don't worry about 
	private static final long serialVersionUID = 1L;
	
	public int x;
	public int y;
	
	public int blockID = 0;
	public int itemID = 0;
	public int itemActivationID = 0;
	public int blockHeight = 0;
	
	//Which direction item/enemy is rotated
	public int itemRotation = 0;
	
	public ImageIcon wallTexture;
	public ImageIcon entityTexture;
	
	public JLabel itemButton;
	public JLabel heightLabel;
	
	private Image img;
	private Image newimg;
	private Image img2;
	private Image newimg2;
	
	public int size = 0;
	public int size2 = 0;
	
   /**
    * Creates a new MapBlock with a given x and y position in the map
    * and instantiates the JLabels.
    * @param x
    * @param y
    */
	public MapBlock(int x, int y) 
	{
		this.x = x;
		this.y = y;
		
		itemButton = new JLabel();
		this.setToolTipText("Rotation: 0, Item Activation ID: 0");
		heightLabel = new JLabel();
		heightLabel.setForeground(new Color(0,255,0));
		itemRotation = 0;
		itemActivationID = 0;
	}
	
   /**
    * Update the buttons image based on parameters sent in for the button
    * size, and based on the new ID of the block. Also changes the height
    * label to display the blocks new height.
    * 
    * @param buttonSize
    */
	public void updateImage(int buttonSize)
	{
		heightLabel.setText(""+blockHeight);
		
		if(size != buttonSize)
		{
			wallTexture = new ImageIcon(Display.filePath+"/walls/wall"+blockID+".png");
			img2 = wallTexture.getImage() ;  
			newimg2 = img2.getScaledInstance
					(buttonSize, buttonSize, java.awt.Image.SCALE_SMOOTH) ;  
			wallTexture = new ImageIcon(newimg2);
			size = buttonSize;
		}
		
		this.setIcon(wallTexture);
	}
	
   /**
    * Update the item image that is shown above the button depending
    * on its new ID and or new button size that it is in.
    * 
    * @param buttonSize
    */
	public void updateItemImage(int buttonSize)
	{	
		if(size2 != buttonSize)
		{
			entityTexture = new ImageIcon(Display.filePath+"/entities/item"+itemID+".png");
			img = entityTexture.getImage() ;  
			newimg = img.getScaledInstance
					(buttonSize / 2, buttonSize / 2, java.awt.Image.SCALE_SMOOTH);
			entityTexture = new ImageIcon(newimg);
			size2 = buttonSize;
		}
		
		itemButton.setIcon(entityTexture);
		
		if(itemID == 0)
		{
			itemButton.setVisible(false);
		}
		else
		{
			itemButton.setVisible(true);
		}
	}
}

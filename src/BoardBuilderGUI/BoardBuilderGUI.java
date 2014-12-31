package BoardBuilderGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

/**
 * GUI for making board files
 * Click on a tile to cycle through the different gadget options.
 * Type a name in the text field to set the name of the board and file
 * Click on "Generate New Board" to write out the file.
 * Outputs file to /pingball-phase3
 * If no name is provided, board name defaults to "defaultName"
 * Supports balls, squares, circle bumpers, square bumpers, triangle bumpers, and portals.
 * Portals require you to go in the .pb file and change otherBoard and otherPortal to desired values
 * Portals are swirls.
 * 
 *
 */

public class BoardBuilderGUI extends JFrame{
    
    //two dimensional array of buttons in the editor
    ArrayList<ArrayList<JButton>> boardButtons;
    
    //list of strings representing board file syntax of every cell in the editor
    ArrayList<ArrayList<String>> boardGadgets;
    
    JButton generateButton; //button clicked to generate the file
    JTextField nameTextField; //field where user types name of the board
    
    //name of board file and name of board
    String boardName = "deafultName";
    
    private static final ImageIcon blankTileIcon = new ImageIcon("blankIcon.png");
    private static final ImageIcon ballIcon = new ImageIcon("ballIcon.png");
    private static final ImageIcon squareBumperIcon = new ImageIcon("squareBumperIcon.png");
    private static final ImageIcon circleBumperIcon = new ImageIcon("circleBumperIcon.png");
    private static final ImageIcon topLeftTriangleIcon = new ImageIcon("topLeftTriangleIcon.png");
    private static final ImageIcon topRightTriangleIcon = new ImageIcon("topRightTriangleIcon.png");
    private static final ImageIcon bottomRightTriangleIcon = new ImageIcon("bottomRightTriangleIcon.png");
    private static final ImageIcon bottomLeftTriangleIcon = new ImageIcon("bottomLeftTriangleIcon.png");
    private static final ImageIcon portalIcon = new ImageIcon("portalIcon.png");
    
    //list of all possible cell icons
    ArrayList<ImageIcon> iconsList = new ArrayList<ImageIcon>(Arrays.asList(
            blankTileIcon, ballIcon, squareBumperIcon, circleBumperIcon, bottomLeftTriangleIcon, bottomRightTriangleIcon, topLeftTriangleIcon, topRightTriangleIcon, portalIcon));
    
    
    /**
     * Creates a new BoardBuilderGUI
     */
    public BoardBuilderGUI() {
        
        super("Board Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel= new JPanel();
        JPanel panel = new JPanel(new GridLayout(20,20,1,1));
        
        GroupLayout layout= new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        generateButton= new JButton ("Generate New Board");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!nameTextField.getText().trim().isEmpty()){
                    boardName = nameTextField.getText().trim();
                }
                createBoardFile();
            }
        });
        
        nameTextField = new JTextField();
        ParallelGroup parGroup= layout.createParallelGroup();
        parGroup.addComponent(generateButton).addComponent(nameTextField).addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE);
        SequentialGroup seqGroup=layout.createSequentialGroup();
        seqGroup.addComponent(generateButton).addComponent(nameTextField).addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE);
        layout.setHorizontalGroup(parGroup);
        layout.setVerticalGroup(seqGroup);


        
        boardButtons = new ArrayList<ArrayList<JButton>>();
        boardGadgets = new ArrayList<ArrayList<String>>();
        for (int yRange = 0; yRange < 20; yRange++){
            ArrayList<JButton> currentButtonRow = new ArrayList<JButton>();
            boardButtons.add(currentButtonRow);
            ArrayList<String> currentGadgetRow = new ArrayList<String>();
            boardGadgets.add(currentGadgetRow);
            for (int xRange = 0; xRange < 20; xRange++){
                JButton button = new JButton(blankTileIcon);
                button.setName(Integer.toString(xRange) +","+Integer.toString(yRange));
                button.setPreferredSize(new Dimension(30, 30));
                button.addActionListener(new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonClicked((JButton) e.getSource());
                        
                    }
                });
                panel.add(button);
                currentButtonRow.add(button);
                currentGadgetRow.add("");
            }
        }
        
        this.add(mainPanel);
        
    }
    
    /**
     * updates the icon of the button that was clicked and updates boardGadgets with the correct string
     * @param button clicked by user
     */
    private void buttonClicked(JButton button){
        int currentIconIndex = iconsList.indexOf(button.getIcon());
        if(currentIconIndex == iconsList.size()-1){
            currentIconIndex = -1;
        }
        button.setIcon(iconsList.get(currentIconIndex+1));
        int xPos = Integer.parseInt(button.getName().split(",")[0]);
        int yPos = Integer.parseInt(button.getName().split(",")[1]);
        boardGadgets.get(yPos).set(xPos, getGadgetFileText(button.getIcon(), xPos, yPos));
        createBoardFile();
        
    }
    
    /**
     * get board file syntax text from input parameters
     * @param gadgetIcon describes which gadget you desire
     * @param xPos of the gadget
     * @param yPos of the gadget
     * @return board file syntax text representing this gadget
     */
    private String getGadgetFileText(Icon gadgetIcon, int xPos, int yPos){
        if (gadgetIcon.equals(ballIcon)){
            return("ball name=ballAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + " xVelocity=0.0 yVelocity=0.0\n");
        } else if (gadgetIcon.equals(squareBumperIcon)){
            return("squareBumper name=squareBumperAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + "\n");
        } else if (gadgetIcon.equals(circleBumperIcon)){
            return("circleBumper name=circleBumperAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + "\n");
        } else if (gadgetIcon.equals(bottomLeftTriangleIcon)){
            return("triangleBumper name=triangleBumperAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + " orientation=270\n");
        } else if (gadgetIcon.equals(bottomRightTriangleIcon)){
            return("triangleBumper name=triangleBumperAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + " orientation=180\n");
        } else if (gadgetIcon.equals(topLeftTriangleIcon)){
            return("triangleBumper name=triangleBumperAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + " orientation=0\n");
        } else if (gadgetIcon.equals(topRightTriangleIcon)){
            return("triangleBumper name=triangleBumperAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + " orientation=90\n");
        } else if (gadgetIcon.equals(portalIcon)){
            return("portal name=portalAt" + xPos + "_" + yPos + " x=" + xPos + " y=" + yPos + "  otherBoard=NAME otherPortal=NAME\n");
        }
        else {
            return "";
        }   
    }
    
    /**
     * prints the board file to boardName.pb
     * if it exists already, then overwrite it
     */
    private void createBoardFile(){
        PrintWriter writer;
        try {
            writer = new PrintWriter(boardName+".pb", "UTF-8");
            writer.print("board name="+boardName+"\n");
            for (ArrayList<String> currentRow : boardGadgets){
                for (String currentTile : currentRow){
                    writer.print(currentTile);
                }
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BoardBuilderGUI main = new BoardBuilderGUI();
                
                main.pack();
                main.setVisible(true);
            }
        });
    }

}

package net.buddat.wgenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData.GrowthTreeStage;
import com.wurmonline.mesh.Tiles.Tile;
import com.wurmonline.wurmapi.api.MapData;
import com.wurmonline.wurmapi.api.WurmAPI;

import net.buddat.wgenerator.ui.MapPanel;
import net.buddat.wgenerator.util.Constants;

public class WGenerator extends JFrame implements ActionListener, FocusListener {

	private static final long serialVersionUID = 3099528642510249703L;
	
	private static final Logger logger = Logger.getLogger(WGenerator.class.getName());
	
	private WurmAPI api;
	
	private HeightMap heightMap;
	private String heightMapFilePath;
	
	private TileMap tileMap;
	
	@SuppressWarnings("rawtypes")
	
	//Top Level Containers
	private JPanel ahiriSwing_PnlMapAndStatus;
	private JTabbedPane ahiriSwing_TabControls;
		//Second Level Containers
		private JPanel ahiriSwing_PnlMapAndControls;
		private JPanel ahiriSwing_PnlConsoleAndStatus;
		private JPanel ahiriSwing_PnlHeightMapControls, ahiriSwing_PnlErosionControls, ahiriSwing_PnlDirtControls, ahiriSwing_PnlBiomeControls, ahiriSwing_PnlOreControls;
			//Third Level Containers
			private JPanel ahiriSwing_PnlMapControls;
			private JPanel ahiriSwing_PnlStatus, ahiriSwing_PnlConsole;
				//Fourth Level Containers
				private JPanel ahiriSwing_PnlHeightMapOptions, ahiriSwing_PnlHeightMapButtons;
				private JPanel ahiriSwing_PnlErosionOptions, ahiriSwing_PnlErosionButtons;
				private JPanel ahiriSwing_PnlDirtOptions, ahiriSwing_PnlDirtButtons;
				private JPanel ahiriSwing_PnlBiomeOptions, ahiriSwing_PnlBiomeButtons;
				private JPanel ahiriSwing_PnlOreOptions, ahiriSwing_PnlOreButtons;
					//Fifth Level Containers
					private JPanel ahiriSwing_PnlHeightMap_MapSize, ahiriSwing_PnlHeightMap_Seed, ahiriSwing_PnlHeightMap_ResIter, ahiriSwing_PnlHeightMap_EdgeBorder, ahiriSwing_PnlHeightMap_HeightLand;
					private JPanel ahiriSwing_PnlHeightMap_Gen, ahiriSwing_PnlHeightMap_Load;
					private JPanel ahiriSwing_PnlErosion_Iter, ahiriSwing_PnlErosion_Slope, ahiriSwing_PnlErosion_Sediment;
					private JPanel ahiriSwing_PnlErosion_Erode;
					private JPanel ahiriSwing_PnlDirt_Seed, ahiriSwing_PnlDirt_Amnt, ahiriSwing_PnlDirtSlope, ahiriSwing_PnlDirt_DiagHeight, ahiriSwing_PnlDirt_Water;
					private JPanel ahiriSwing_PnlDirt_Drop;
					private JPanel ahiriSwing_PnlBiome_Type, ahiriSwing_PnlBiome_SeedSize, ahiriSwing_PnlBiome_Slope, ahiriSwing_PnlBiome_HeightMin, ahiriSwing_PnlBiome_HeightMax, ahiriSwing_PnlBiome_Rate, ahiriSwing_PnlBiome_RateNS, ahiriSwing_PnlBiome_RateEW;
					private JPanel ahiriSwing_PnlBiome_Add, ahiriSwing_PnlBiome_ResetUndo;
					private JPanel ahiriSwing_PnlOre_RockIron, ahiriSwing_PnlOre_GoldSilver, ahiriSwing_PnlOre_ZincCopper, ahiriSwing_PnlOre_LeadTin, ahiriSwing_PnlOre_AddyGlimmer, ahiriSwing_PnlOre_MarbleSlate;
					private JPanel ahiriSwing_PnlOre_Gen;
					
	//Actual Elements
		//Status Elements
		private JProgressBar ahiriSwing_ProgressBarStatus;
		//Console Elements
		private JTextArea ahiriSwing_Console;
		private JScrollPane ahiriSwing_ConsoleScrollPane;
		//Map Elements
		private MapPanel pnlMap;
		//Map Controls Elements
		private JTextField txtName;
		private JButton btnSaveActions, btnLoadActions, btnSaveImages, btnSaveMap, btnShowDump, btnShowTopo, btnShowCave, btnShowHeightMap;
		//Tab Elements
			//HeightMap Elements
			private JLabel lblMapSize, lblSeed, lblRes, lblIterations, lblMinEdge, lblBorderWeight, lblMaxHeight;
			private JTextField txtSeed, txtRes, txtIterations, txtMinEdge, txtBorderWeight, txtMaxHeight;
			private JCheckBox chkLand;
			private JComboBox cmbMapSize;
			private JButton btnGenHeightMap, btnLoadHeightMap, btnReloadHeightMap, btnResetHeightSeed;
			//Erosion Elements
			private JLabel lblErodeIterations, lblErodeMinSlope, lblErodeMaxSediment;
			private JTextField txtErodeIterations, txtErodeMinSlope, txtErodeMaxSediment;
			private JButton btnErodeHeightMap;
			//Dirt Elements
			private JLabel lblBiomeSeed, lblDirtAmnt, lblDirtSlope, lblDirtDiagSlope, lblMaxDirtHeight, lblWaterHeight;
			private JTextField txtBiomeSeed, txtDirtAmnt, txtDirtSlope, txtDirtDiagSlope, txtMaxDirtHeight, txtWaterHeight;
			private JButton btnSeedBiome, btnDropDirt, btnDropWater;
			//Biome Elements
			private JLabel lblBiomeSeedCount, lblBiomeSize, lblBiomeMaxSlope, lblBiomeRate, lblBiomeRateN, lblBiomeRateS, lblBiomeRateE, lblBiomeRateW, lblBiomeMinHeight, lblBiomeMaxHeight;
			private JTextField txtBiomeSeedCount, txtBiomeSize, txtBiomeMaxSlope, txtBiomeRateN, txtBiomeRateS, txtBiomeRateE, txtBiomeRateW, txtBiomeMinHeight, txtBiomeMaxHeight;
			private JComboBox cmbBiomeType;
			private JButton btnUndoBiome, btnResetBiomes, btnResetBiomeSeed;
			//Ore Elements
			private JLabel lblRock, lblIron, lblGold, lblSilver, lblZinc, lblCopper, lblLead, lblTin, lblAddy, lblGlimmer, lblMarble, lblSlate;
			private JTextField txtRock, txtIron, txtGold, txtSilver, txtZinc, txtCopper, txtLead, txtTin, txtAddy, txtGlimmer, txtMarble, txtSlate;
			private JButton btnGenOres;
	
	private ArrayList<String> genHistory;
	
	private boolean apiClosed = true;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	
	public WGenerator(String title, int width, int height) {
		//Configures Main Window
		super(title);
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        //Create panel map
        pnlMap = new MapPanel(1, 1);
        
        //Configure HeightMap Elements
        lblMapSize = new JLabel("Map Size:");
		cmbMapSize = new JComboBox(new Integer[] {1024, 2048, 4096, 8192, 16384});
		cmbMapSize.setSelectedIndex(1);
		cmbMapSize.setEditable(false);
		
		lblSeed = new JLabel("Seed:");
		txtSeed = new JTextField("" + System.currentTimeMillis(), 10);
		lblRes = new JLabel("Res:");
		txtRes = new JTextField("" + (int) Constants.RESOLUTION, 3);
		lblIterations = new JLabel("Iterations:");
		txtIterations = new JTextField("" + (int) Constants.HEIGHTMAP_ITERATIONS, 2);
		lblMinEdge = new JLabel("Min Edge:");
		txtMinEdge = new JTextField("" + (int) Constants.MIN_EDGE, 3);
		lblBorderWeight = new JLabel("Border Weight:");
		txtBorderWeight = new JTextField("" + (int) Constants.BORDER_WEIGHT, 2);
		lblMaxHeight = new JLabel("Max Height:");
		txtMaxHeight = new JTextField("" + (int) Constants.MAP_HEIGHT, 3);
		chkLand = new JCheckBox("More Land", Constants.MORE_LAND);
		
		btnResetHeightSeed = new JButton("#");
		btnResetHeightSeed.addActionListener(this);
		btnGenHeightMap = new JButton("Gen Heightmap");
		btnLoadHeightMap = new JButton("Load File...");
		btnReloadHeightMap = new JButton("Reload");
		btnGenHeightMap.addActionListener(this);
		btnLoadHeightMap.addActionListener(this);
		btnReloadHeightMap.addActionListener(this);
		
        
        //Configure HeightMap Containers
		ahiriSwing_PnlHeightMapOptions = new JPanel();
		ahiriSwing_PnlHeightMapButtons = new JPanel();
		ahiriSwing_PnlHeightMapOptions.setLayout(new BoxLayout(ahiriSwing_PnlHeightMapOptions, BoxLayout.PAGE_AXIS));
		ahiriSwing_PnlHeightMapButtons.setLayout(new BoxLayout(ahiriSwing_PnlHeightMapButtons, BoxLayout.PAGE_AXIS));
		
		ahiriSwing_PnlHeightMapButtons.setPreferredSize(new Dimension(300,60));
		
		ahiriSwing_PnlHeightMap_MapSize = new JPanel();
		ahiriSwing_PnlHeightMap_Seed = new JPanel();
		ahiriSwing_PnlHeightMap_ResIter = new JPanel();
		ahiriSwing_PnlHeightMap_EdgeBorder = new JPanel();
		ahiriSwing_PnlHeightMap_HeightLand = new JPanel();
		ahiriSwing_PnlHeightMap_Gen = new JPanel();
		ahiriSwing_PnlHeightMap_Load = new JPanel();
		
		ahiriSwing_PnlHeightMap_MapSize.setLayout(new FlowLayout());
		ahiriSwing_PnlHeightMap_Seed.setLayout(new FlowLayout());
		ahiriSwing_PnlHeightMap_ResIter.setLayout(new FlowLayout());
		ahiriSwing_PnlHeightMap_EdgeBorder.setLayout(new FlowLayout());
		ahiriSwing_PnlHeightMap_HeightLand.setLayout(new FlowLayout());
		ahiriSwing_PnlHeightMap_Gen.setLayout(new FlowLayout());
		ahiriSwing_PnlHeightMap_Load.setLayout(new FlowLayout());
		
		ahiriSwing_PnlHeightMap_MapSize.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlHeightMap_Seed.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlHeightMap_ResIter.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlHeightMap_EdgeBorder.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlHeightMap_HeightLand.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlHeightMap_Gen.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlHeightMap_Load.setPreferredSize(new Dimension(300,30));
		
		ahiriSwing_PnlHeightMap_MapSize.setMaximumSize(ahiriSwing_PnlHeightMap_MapSize.getPreferredSize());
		ahiriSwing_PnlHeightMap_Seed.setMaximumSize(ahiriSwing_PnlHeightMap_Seed.getPreferredSize());
		ahiriSwing_PnlHeightMap_ResIter.setMaximumSize(ahiriSwing_PnlHeightMap_ResIter.getPreferredSize());
		ahiriSwing_PnlHeightMap_EdgeBorder.setMaximumSize(ahiriSwing_PnlHeightMap_EdgeBorder.getPreferredSize());
		ahiriSwing_PnlHeightMap_HeightLand.setMaximumSize(ahiriSwing_PnlHeightMap_HeightLand.getPreferredSize());
		ahiriSwing_PnlHeightMap_Gen.setMaximumSize(ahiriSwing_PnlHeightMap_EdgeBorder.getPreferredSize());
		ahiriSwing_PnlHeightMap_Load.setMaximumSize(ahiriSwing_PnlHeightMap_HeightLand.getPreferredSize());

		ahiriSwing_PnlHeightMap_MapSize.add(lblMapSize);
		ahiriSwing_PnlHeightMap_MapSize.add(cmbMapSize);
		ahiriSwing_PnlHeightMap_Seed.add(lblSeed);
		ahiriSwing_PnlHeightMap_Seed.add(txtSeed);
		ahiriSwing_PnlHeightMap_Seed.add(btnResetHeightSeed);
		ahiriSwing_PnlHeightMap_ResIter.add(lblRes);
		ahiriSwing_PnlHeightMap_ResIter.add(txtRes);
		ahiriSwing_PnlHeightMap_ResIter.add(lblIterations);
		ahiriSwing_PnlHeightMap_ResIter.add(txtIterations);
		ahiriSwing_PnlHeightMap_EdgeBorder.add(lblMinEdge);
		ahiriSwing_PnlHeightMap_EdgeBorder.add(txtMinEdge);
		ahiriSwing_PnlHeightMap_EdgeBorder.add(lblBorderWeight);
		ahiriSwing_PnlHeightMap_EdgeBorder.add(txtBorderWeight);
		ahiriSwing_PnlHeightMap_HeightLand.add(lblMaxHeight);
		ahiriSwing_PnlHeightMap_HeightLand.add(txtMaxHeight);
		ahiriSwing_PnlHeightMap_HeightLand.add(chkLand);
		ahiriSwing_PnlHeightMap_Gen.add(btnGenHeightMap);
		ahiriSwing_PnlHeightMap_Load.add(btnLoadHeightMap);
		ahiriSwing_PnlHeightMap_Load.add(btnReloadHeightMap);
		
		ahiriSwing_PnlHeightMapOptions.add(ahiriSwing_PnlHeightMap_MapSize);
		ahiriSwing_PnlHeightMapOptions.add(ahiriSwing_PnlHeightMap_Seed);
		ahiriSwing_PnlHeightMapOptions.add(ahiriSwing_PnlHeightMap_ResIter);
		ahiriSwing_PnlHeightMapOptions.add(ahiriSwing_PnlHeightMap_EdgeBorder);
		ahiriSwing_PnlHeightMapOptions.add(ahiriSwing_PnlHeightMap_MapSize);
		
		ahiriSwing_PnlHeightMapButtons.add(ahiriSwing_PnlHeightMap_Gen);
		ahiriSwing_PnlHeightMapButtons.add(ahiriSwing_PnlHeightMap_Load);
		
		//Configure Erosion Elements
		lblErodeIterations = new JLabel("Erosion Iterations:");
		txtErodeIterations = new JTextField("" + Constants.EROSION_ITERATIONS, 3);
		lblErodeMinSlope = new JLabel("Erosion Min Slope:");
		txtErodeMinSlope = new JTextField("" + Constants.MIN_SLOPE, 3);
		lblErodeMaxSediment = new JLabel("Max Sediment Per Iteration:");
		txtErodeMaxSediment = new JTextField("" + Constants.MAX_SEDIMENT, 3);
		
		btnErodeHeightMap = new JButton("Erode HeightMap");
		btnErodeHeightMap.addActionListener(this);
		
		//Configure Erosion Containers
		ahiriSwing_PnlErosionOptions = new JPanel();
		ahiriSwing_PnlErosionButtons = new JPanel();
		
		ahiriSwing_PnlErosionOptions.setLayout(new BoxLayout(ahiriSwing_PnlErosionOptions, BoxLayout.PAGE_AXIS));
		ahiriSwing_PnlErosionButtons.setLayout(new BoxLayout(ahiriSwing_PnlErosionButtons, BoxLayout.PAGE_AXIS));

		
		ahiriSwing_PnlErosion_Iter = new JPanel();
		ahiriSwing_PnlErosion_Slope = new JPanel();
		ahiriSwing_PnlErosion_Sediment = new JPanel();
		ahiriSwing_PnlErosion_Erode = new JPanel();
		
		ahiriSwing_PnlErosion_Iter.setLayout(new FlowLayout());
		ahiriSwing_PnlErosion_Slope.setLayout(new FlowLayout());
		ahiriSwing_PnlErosion_Sediment.setLayout(new FlowLayout());
		ahiriSwing_PnlErosion_Erode.setLayout(new FlowLayout());
		
		ahiriSwing_PnlErosion_Iter.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlErosion_Slope.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlErosion_Sediment.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlErosion_Erode.setPreferredSize(new Dimension(300,30));
		
		ahiriSwing_PnlErosion_Iter.setMaximumSize(ahiriSwing_PnlErosion_Iter.getPreferredSize());
		ahiriSwing_PnlErosion_Slope.setMaximumSize(ahiriSwing_PnlErosion_Slope.getPreferredSize());
		ahiriSwing_PnlErosion_Sediment.setMaximumSize(ahiriSwing_PnlErosion_Sediment.getPreferredSize());
		ahiriSwing_PnlErosion_Erode.setMaximumSize(ahiriSwing_PnlErosion_Erode.getPreferredSize());
		
		ahiriSwing_PnlErosion_Iter.add(lblErodeIterations);
		ahiriSwing_PnlErosion_Iter.add(txtErodeIterations);
		ahiriSwing_PnlErosion_Slope.add(lblErodeMinSlope);
		ahiriSwing_PnlErosion_Slope.add(txtErodeMinSlope);
		ahiriSwing_PnlErosion_Sediment.add(lblErodeMaxSediment);
		ahiriSwing_PnlErosion_Sediment.add(txtErodeMaxSediment);
		ahiriSwing_PnlErosion_Erode.add(btnErodeHeightMap);
		
		ahiriSwing_PnlErosionOptions.add(ahiriSwing_PnlErosion_Iter);
		ahiriSwing_PnlErosionOptions.add(ahiriSwing_PnlErosion_Slope);
		ahiriSwing_PnlErosionOptions.add(ahiriSwing_PnlErosion_Sediment);
		
		ahiriSwing_PnlErosionButtons.add(ahiriSwing_PnlErosion_Erode);
		
		//Configure Dirt Elements
		lblBiomeSeed = new JLabel("Seed:");
		txtBiomeSeed = new JTextField("" + System.currentTimeMillis(), 10);
		lblDirtAmnt = new JLabel("Dirt Per Tile:");
		txtDirtAmnt = new JTextField("" + Constants.DIRT_DROP_COUNT, 3);
		lblDirtSlope = new JLabel("Max Dirt Slope:");
		txtDirtSlope = new JTextField("" + Constants.MAX_DIRT_SLOPE, 3);
		lblDirtDiagSlope = new JLabel("Max Dirt Slope (Diagonal):");
		txtDirtDiagSlope = new JTextField("" + Constants.MAX_DIRT_DIAG_SLOPE, 3);
		lblMaxDirtHeight = new JLabel("Max Dirt Height:");
		txtMaxDirtHeight = new JTextField("" + Constants.ROCK_WEIGHT, 4);
		lblWaterHeight = new JLabel("Water Height:");
		txtWaterHeight = new JTextField("" + Constants.WATER_HEIGHT, 4);
		
		btnResetBiomeSeed = new JButton("#");
		btnResetBiomeSeed.addActionListener(this);
		btnDropDirt = new JButton("Drop Dirt");
		btnDropDirt.addActionListener(this);
		btnDropWater = new JButton("Reload Water");
		btnDropWater.addActionListener(this);
		
		//Configure Dirt Containers
		ahiriSwing_PnlDirtOptions = new JPanel();
		ahiriSwing_PnlDirtButtons = new JPanel();
		
		ahiriSwing_PnlDirtOptions.setLayout(new BoxLayout(ahiriSwing_PnlDirtOptions, BoxLayout.PAGE_AXIS));
		ahiriSwing_PnlDirtButtons.setLayout(new BoxLayout(ahiriSwing_PnlDirtButtons, BoxLayout.PAGE_AXIS));

		ahiriSwing_PnlDirt_Seed = new JPanel();
		ahiriSwing_PnlDirt_Amnt = new JPanel();
		ahiriSwing_PnlDirtSlope = new JPanel();
		ahiriSwing_PnlDirt_DiagHeight = new JPanel();
		ahiriSwing_PnlDirt_Water = new JPanel();
		ahiriSwing_PnlDirt_Drop = new JPanel();
		
		ahiriSwing_PnlDirt_Seed.setLayout(new FlowLayout());
		ahiriSwing_PnlDirt_Amnt.setLayout(new FlowLayout());
		ahiriSwing_PnlDirtSlope.setLayout(new FlowLayout());
		ahiriSwing_PnlDirt_DiagHeight.setLayout(new FlowLayout());
		ahiriSwing_PnlDirt_Water.setLayout(new FlowLayout());
		ahiriSwing_PnlDirt_Drop.setLayout(new FlowLayout());
		
		ahiriSwing_PnlDirt_Seed.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlDirt_Amnt.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlDirtSlope.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlDirt_DiagHeight.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlDirt_Water.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlDirt_Drop.setPreferredSize(new Dimension(300,30));
		
		ahiriSwing_PnlDirt_Seed.setMaximumSize(ahiriSwing_PnlDirt_Seed.getPreferredSize());
		ahiriSwing_PnlDirt_Amnt.setMaximumSize(ahiriSwing_PnlDirt_Amnt.getPreferredSize());
		ahiriSwing_PnlDirtSlope.setMaximumSize(ahiriSwing_PnlDirt_Amnt.getPreferredSize());
		ahiriSwing_PnlDirt_DiagHeight.setMaximumSize(ahiriSwing_PnlDirt_DiagHeight.getPreferredSize());
		ahiriSwing_PnlDirt_Water.setMaximumSize(ahiriSwing_PnlDirt_Water.getPreferredSize());
		ahiriSwing_PnlDirt_Drop.setMaximumSize(ahiriSwing_PnlDirt_Drop.getPreferredSize());
		
		ahiriSwing_PnlDirt_Seed.add(lblBiomeSeed);
		ahiriSwing_PnlDirt_Seed.add(txtBiomeSeed);
		ahiriSwing_PnlDirt_Seed.add(btnResetBiomeSeed);
		ahiriSwing_PnlDirt_Amnt.add(lblDirtAmnt);
		ahiriSwing_PnlDirt_Amnt.add(txtDirtAmnt);
		ahiriSwing_PnlDirtSlope.add(lblDirtSlope);
		ahiriSwing_PnlDirtSlope.add(txtDirtSlope);
		ahiriSwing_PnlDirt_DiagHeight.add(lblDirtDiagSlope);
		ahiriSwing_PnlDirt_DiagHeight.add(txtDirtDiagSlope);
		ahiriSwing_PnlDirt_DiagHeight.add(lblMaxDirtHeight);
		ahiriSwing_PnlDirt_DiagHeight.add(txtMaxDirtHeight);
		ahiriSwing_PnlDirt_Water.add(lblWaterHeight);
		ahiriSwing_PnlDirt_Water.add(txtWaterHeight);
		ahiriSwing_PnlDirt_Drop.add(btnDropDirt);
		ahiriSwing_PnlDirt_Drop.add(btnDropWater);

		ahiriSwing_PnlDirtOptions.add(ahiriSwing_PnlDirt_Seed);
		ahiriSwing_PnlDirtOptions.add(ahiriSwing_PnlDirt_Amnt);
		ahiriSwing_PnlDirtOptions.add(ahiriSwing_PnlDirtSlope);
		ahiriSwing_PnlDirtOptions.add(ahiriSwing_PnlDirt_DiagHeight);
		ahiriSwing_PnlDirtOptions.add(ahiriSwing_PnlDirt_Water);
		
		ahiriSwing_PnlDirtButtons.add(ahiriSwing_PnlDirt_Drop);
		
		//Configure Biome Elements
		btnSeedBiome = new JButton("Add Biome");
		btnSeedBiome.addActionListener(this);
		btnUndoBiome = new JButton("Undo Last Biome");
		btnUndoBiome.addActionListener(this);
		btnResetBiomes = new JButton("Reset Biomes");
		btnResetBiomes.addActionListener(this);
		
		cmbBiomeType = new JComboBox(new Tile[] { Tile.TILE_CLAY, Tile.TILE_DIRT, Tile.TILE_DIRT_PACKED, Tile.TILE_GRASS, Tile.TILE_GRAVEL, Tile.TILE_KELP,
				Tile.TILE_LAVA, Tile.TILE_MARSH, Tile.TILE_MOSS, Tile.TILE_MYCELIUM, Tile.TILE_PEAT, Tile.TILE_REED, Tile.TILE_SAND, Tile.TILE_STEPPE, 
				Tile.TILE_TAR, Tile.TILE_TUNDRA, Tile.TILE_TREE_APPLE, Tile.TILE_TREE_BIRCH, Tile.TILE_TREE_CEDAR, Tile.TILE_TREE_CHERRY, Tile.TILE_TREE_CHESTNUT, 
				Tile.TILE_TREE_FIR, Tile.TILE_TREE_LEMON, Tile.TILE_TREE_LINDEN, Tile.TILE_TREE_MAPLE, Tile.TILE_TREE_OAK, Tile.TILE_TREE_OLIVE, Tile.TILE_TREE_PINE,
				Tile.TILE_TREE_WALNUT, Tile.TILE_TREE_WILLOW, Tile.TILE_BUSH_CAMELLIA, Tile.TILE_BUSH_GRAPE, Tile.TILE_BUSH_LAVENDER, Tile.TILE_BUSH_OLEANDER,
				Tile.TILE_BUSH_ROSE, Tile.TILE_BUSH_THORN
		});
		cmbBiomeType.setSelectedIndex(12); // 12 = SAND
		cmbBiomeType.setEditable(false);
		
		lblBiomeSeedCount = new JLabel("Seed Count:");
		txtBiomeSeedCount = new JTextField("" + Constants.BIOME_SEEDS, 3);
		lblBiomeSize = new JLabel("Size:");
		txtBiomeSize = new JTextField("" + Constants.BIOME_SIZE, 3);
		lblBiomeMaxSlope = new JLabel("Max Slope:");
		txtBiomeMaxSlope = new JTextField("" + Constants.BIOME_MAX_SLOPE, 3);
		lblBiomeRate = new JLabel("Growth %:");
		lblBiomeRateN = new JLabel("N:");
		lblBiomeRateS = new JLabel("S:");
		lblBiomeRateE = new JLabel("E:");
		lblBiomeRateW = new JLabel("W:");
		txtBiomeRateN = new JTextField("" + Constants.BIOME_RATE / 2, 2);
		txtBiomeRateS = new JTextField("" + (int) (Constants.BIOME_RATE * 1.3), 2);
		txtBiomeRateE = new JTextField("" + (int) (Constants.BIOME_RATE * 0.6), 2);
		txtBiomeRateW = new JTextField("" + Constants.BIOME_RATE, 2);
		lblBiomeMinHeight = new JLabel("Min Height:");
		txtBiomeMinHeight = new JTextField("" + Constants.BIOME_MIN_HEIGHT, 4);
		lblBiomeMaxHeight = new JLabel("Max Height:");
		txtBiomeMaxHeight = new JTextField("" + Constants.BIOME_MAX_HEIGHT, 4);
		
		//Configure Biome Containers
		ahiriSwing_PnlBiomeOptions = new JPanel();
		ahiriSwing_PnlBiomeButtons = new JPanel();
				
		ahiriSwing_PnlBiomeOptions.setLayout(new BoxLayout(ahiriSwing_PnlBiomeOptions, BoxLayout.PAGE_AXIS));
		ahiriSwing_PnlBiomeButtons.setLayout(new BoxLayout(ahiriSwing_PnlBiomeButtons, BoxLayout.PAGE_AXIS));
		
		ahiriSwing_PnlBiome_Type = new JPanel();
		ahiriSwing_PnlBiome_SeedSize = new JPanel();
		ahiriSwing_PnlBiome_Slope = new JPanel();
		ahiriSwing_PnlBiome_HeightMin = new JPanel();
		ahiriSwing_PnlBiome_HeightMax = new JPanel();
		ahiriSwing_PnlBiome_Rate = new JPanel();
		ahiriSwing_PnlBiome_RateNS = new JPanel();
		ahiriSwing_PnlBiome_RateEW = new JPanel();
		ahiriSwing_PnlBiome_Add = new JPanel();
		ahiriSwing_PnlBiome_ResetUndo = new JPanel();
		
		ahiriSwing_PnlBiome_Type.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_SeedSize.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_Slope.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_HeightMin.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_HeightMax.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_Rate.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_RateNS.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_RateEW.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_Add.setLayout(new FlowLayout());
		ahiriSwing_PnlBiome_ResetUndo.setLayout(new FlowLayout());
		
		ahiriSwing_PnlBiome_Type.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_SeedSize.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_Slope.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_HeightMin.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_HeightMax.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_Rate.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_RateNS.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_RateEW.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_Add.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlBiome_ResetUndo.setPreferredSize(new Dimension(300,30));
		
		ahiriSwing_PnlBiome_Type.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlBiome_SeedSize.setMaximumSize(ahiriSwing_PnlBiome_SeedSize.getPreferredSize());
		ahiriSwing_PnlBiome_Slope.setMaximumSize(ahiriSwing_PnlBiome_Slope.getPreferredSize());
		ahiriSwing_PnlBiome_HeightMin.setMaximumSize(ahiriSwing_PnlBiome_HeightMin.getPreferredSize());
		ahiriSwing_PnlBiome_HeightMax.setMaximumSize(ahiriSwing_PnlBiome_HeightMax.getPreferredSize());
		ahiriSwing_PnlBiome_Rate.setMaximumSize(ahiriSwing_PnlBiome_Rate.getPreferredSize());
		ahiriSwing_PnlBiome_RateNS.setMaximumSize(ahiriSwing_PnlBiome_RateNS.getPreferredSize());
		ahiriSwing_PnlBiome_RateEW.setMaximumSize(ahiriSwing_PnlBiome_RateEW.getPreferredSize());
		ahiriSwing_PnlBiome_Add.setMaximumSize(ahiriSwing_PnlBiome_Add.getPreferredSize());
		ahiriSwing_PnlBiome_ResetUndo.setMaximumSize(ahiriSwing_PnlBiome_ResetUndo.getPreferredSize());
		

		ahiriSwing_PnlBiome_Type.add(cmbBiomeType);
		ahiriSwing_PnlBiome_SeedSize.add(lblBiomeSeedCount);
		ahiriSwing_PnlBiome_SeedSize.add(txtBiomeSeedCount);
		ahiriSwing_PnlBiome_SeedSize.add(lblBiomeSize);
		ahiriSwing_PnlBiome_SeedSize.add(txtBiomeSize);
		ahiriSwing_PnlBiome_Slope.add(lblBiomeMaxSlope);
		ahiriSwing_PnlBiome_Slope.add(txtBiomeMaxSlope);
		ahiriSwing_PnlBiome_HeightMin.add(lblBiomeMinHeight);
		ahiriSwing_PnlBiome_HeightMin.add(txtBiomeMinHeight);
		ahiriSwing_PnlBiome_HeightMax.add(lblBiomeMaxHeight);
		ahiriSwing_PnlBiome_HeightMax.add(txtBiomeMaxHeight);
		ahiriSwing_PnlBiome_Rate.add(lblBiomeRate);
		ahiriSwing_PnlBiome_RateNS.add(lblBiomeRateN);
		ahiriSwing_PnlBiome_RateNS.add(txtBiomeRateN);
		ahiriSwing_PnlBiome_RateNS.add(lblBiomeRateS);
		ahiriSwing_PnlBiome_RateNS.add(txtBiomeRateS);
		ahiriSwing_PnlBiome_RateEW.add(lblBiomeRateE);
		ahiriSwing_PnlBiome_RateEW.add(txtBiomeRateE);
		ahiriSwing_PnlBiome_RateEW.add(lblBiomeRateW);
		ahiriSwing_PnlBiome_RateEW.add(txtBiomeRateW);
		ahiriSwing_PnlBiome_Add.add(btnSeedBiome);
		ahiriSwing_PnlBiome_ResetUndo.add(btnUndoBiome);
		ahiriSwing_PnlBiome_ResetUndo.add(btnResetBiomes);
		
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_Type);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_SeedSize);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_Slope);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_HeightMin);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_HeightMax);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_Rate);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_RateNS);
		ahiriSwing_PnlBiomeOptions.add(ahiriSwing_PnlBiome_RateEW);
		
		ahiriSwing_PnlBiomeButtons.add(ahiriSwing_PnlBiome_Add);
		ahiriSwing_PnlBiomeButtons.add(ahiriSwing_PnlBiome_ResetUndo);
		
		//Configure Ore Elements
		btnGenOres = new JButton("Generate Ores");
		btnGenOres.addActionListener(this);
		
		lblIron = new JLabel("Iron %:");
		lblGold = new JLabel("Gold %:");
		lblSilver = new JLabel("Silver %:");
		lblZinc = new JLabel("Zinc %:");
		lblCopper = new JLabel("Copper %:");
		lblLead = new JLabel("Lead %:");
		lblTin = new JLabel("Tin %:");
		lblAddy = new JLabel("Addy %:");
		lblGlimmer = new JLabel("Glimmer %:");
		lblMarble = new JLabel("Marble %:");
		lblSlate = new JLabel("Slate %:");
		lblRock = new JLabel("Rock %:");
		
		txtIron = new JTextField("" + Constants.ORE_IRON, 3);
		txtIron.addFocusListener(this);
		txtGold = new JTextField("" + Constants.ORE_GOLD, 3);
		txtGold.addFocusListener(this);
		txtSilver = new JTextField("" + Constants.ORE_SILVER, 3);
		txtSilver.addFocusListener(this);
		txtZinc = new JTextField("" + Constants.ORE_ZINC, 3);
		txtZinc.addFocusListener(this);
		txtCopper = new JTextField("" + Constants.ORE_COPPER, 3);
		txtCopper.addFocusListener(this);
		txtLead = new JTextField("" + Constants.ORE_LEAD, 3);
		txtLead.addFocusListener(this);
		txtTin = new JTextField("" + Constants.ORE_TIN, 3);
		txtTin.addFocusListener(this);
		txtAddy = new JTextField("" + Constants.ORE_ADDY, 3);
		txtAddy.addFocusListener(this);
		txtGlimmer = new JTextField("" + Constants.ORE_GLIMMER, 3);
		txtGlimmer.addFocusListener(this);
		txtMarble = new JTextField("" + Constants.ORE_MARBLE, 3);
		txtMarble.addFocusListener(this);
		txtSlate = new JTextField("" + Constants.ORE_SLATE, 3);
		txtSlate.addFocusListener(this);
		txtRock = new JTextField("" + Constants.ORE_ROCK, 3);
		txtRock.setEditable(false);
		
		//Configure Ore Containers
		ahiriSwing_PnlOreOptions = new JPanel();
		ahiriSwing_PnlOreButtons = new JPanel();
		
		ahiriSwing_PnlOreOptions.setLayout(new BoxLayout(ahiriSwing_PnlOreOptions, BoxLayout.PAGE_AXIS));
		ahiriSwing_PnlOreButtons.setLayout(new BoxLayout(ahiriSwing_PnlOreButtons, BoxLayout.PAGE_AXIS));
		
		ahiriSwing_PnlOre_RockIron = new JPanel();
		ahiriSwing_PnlOre_GoldSilver = new JPanel();
		ahiriSwing_PnlOre_ZincCopper = new JPanel();
		ahiriSwing_PnlOre_LeadTin = new JPanel();
		ahiriSwing_PnlOre_AddyGlimmer = new JPanel();
		ahiriSwing_PnlOre_MarbleSlate = new JPanel();
		ahiriSwing_PnlOre_Gen = new JPanel();
		
		ahiriSwing_PnlOre_RockIron.setLayout(new FlowLayout());
		ahiriSwing_PnlOre_GoldSilver.setLayout(new FlowLayout());
		ahiriSwing_PnlOre_ZincCopper.setLayout(new FlowLayout());
		ahiriSwing_PnlOre_LeadTin.setLayout(new FlowLayout());
		ahiriSwing_PnlOre_AddyGlimmer.setLayout(new FlowLayout());
		ahiriSwing_PnlOre_MarbleSlate.setLayout(new FlowLayout());
		ahiriSwing_PnlOre_Gen.setLayout(new FlowLayout());
		
		ahiriSwing_PnlOre_RockIron.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlOre_GoldSilver.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlOre_ZincCopper.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlOre_LeadTin.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlOre_AddyGlimmer.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlOre_MarbleSlate.setPreferredSize(new Dimension(300,30));
		ahiriSwing_PnlOre_Gen.setPreferredSize(new Dimension(300,30));
		
		ahiriSwing_PnlOre_RockIron.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlOre_GoldSilver.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlOre_ZincCopper.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlOre_LeadTin.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlOre_AddyGlimmer.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlOre_MarbleSlate.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		ahiriSwing_PnlOre_Gen.setMaximumSize(ahiriSwing_PnlBiome_Type.getPreferredSize());
		
		ahiriSwing_PnlOre_RockIron.add(lblIron);
		ahiriSwing_PnlOre_RockIron.add(txtIron);
		ahiriSwing_PnlOre_GoldSilver.add(lblGold);
		ahiriSwing_PnlOre_GoldSilver.add(txtGold);
		ahiriSwing_PnlOre_GoldSilver.add(lblSilver);
		ahiriSwing_PnlOre_GoldSilver.add(txtSilver);
		ahiriSwing_PnlOre_ZincCopper.add(lblZinc);
		ahiriSwing_PnlOre_ZincCopper.add(txtZinc);
		ahiriSwing_PnlOre_ZincCopper.add(lblCopper);
		ahiriSwing_PnlOre_ZincCopper.add(txtCopper);
		ahiriSwing_PnlOre_LeadTin.add(lblLead);
		ahiriSwing_PnlOre_LeadTin.add(txtLead);
		ahiriSwing_PnlOre_LeadTin.add(lblTin);
		ahiriSwing_PnlOre_LeadTin.add(txtTin);
		ahiriSwing_PnlOre_AddyGlimmer.add(lblAddy);
		ahiriSwing_PnlOre_AddyGlimmer.add(txtAddy);
		ahiriSwing_PnlOre_AddyGlimmer.add(lblGlimmer);
		ahiriSwing_PnlOre_AddyGlimmer.add(txtGlimmer);
		ahiriSwing_PnlOre_MarbleSlate.add(lblMarble);
		ahiriSwing_PnlOre_MarbleSlate.add(txtMarble);
		ahiriSwing_PnlOre_MarbleSlate.add(lblSlate);
		ahiriSwing_PnlOre_MarbleSlate.add(txtSlate);
		ahiriSwing_PnlOre_RockIron.add(lblRock);
		ahiriSwing_PnlOre_RockIron.add(txtRock);
		ahiriSwing_PnlOre_Gen.add(btnGenOres);
		
		ahiriSwing_PnlOreOptions.add(ahiriSwing_PnlOre_RockIron);
		ahiriSwing_PnlOreOptions.add(ahiriSwing_PnlOre_GoldSilver);
		ahiriSwing_PnlOreOptions.add(ahiriSwing_PnlOre_ZincCopper);
		ahiriSwing_PnlOreOptions.add(ahiriSwing_PnlOre_LeadTin);
		ahiriSwing_PnlOreOptions.add(ahiriSwing_PnlOre_AddyGlimmer);
		ahiriSwing_PnlOreOptions.add(ahiriSwing_PnlOre_MarbleSlate);
		
		ahiriSwing_PnlOreButtons.add(ahiriSwing_PnlOre_Gen);
		
        //Configure Console Element
		ahiriSwing_Console = new JTextArea(50,41);
		ahiriSwing_ConsoleScrollPane = new JScrollPane(ahiriSwing_Console); 
		ahiriSwing_Console.setEditable(false);
		ahiriSwing_Console.setLineWrap(true);
		ahiriSwing_Console.setWrapStyleWord(true);
		ahiriSwing_Console.setText("Someday I'm going to be a console that shows logger messages\n\nBut that day is not today\n\nMy programmer needs to go to bed, it's 3AM in the morning.");
		
        //Configure Console Panel
        ahiriSwing_PnlConsole = new JPanel();
        ahiriSwing_PnlConsole.setPreferredSize(new Dimension(500, 100));
        ahiriSwing_PnlConsole.add(ahiriSwing_ConsoleScrollPane);
        
        //Configure Progress Bar for Status Panel
        ahiriSwing_ProgressBarStatus = new JProgressBar(0, 100);
        ahiriSwing_ProgressBarStatus.setValue(0);
                
        //Configure Status Panel
        ahiriSwing_PnlStatus = new JPanel();
        ahiriSwing_PnlStatus.setPreferredSize(new Dimension(500, 30));
        ahiriSwing_ProgressBarStatus.setPreferredSize(ahiriSwing_PnlStatus.getPreferredSize());
        ahiriSwing_PnlStatus.add(ahiriSwing_ProgressBarStatus);
        
        //Configure Console and Status Panel Container
    	ahiriSwing_PnlConsoleAndStatus = new JPanel();
    	ahiriSwing_PnlConsoleAndStatus.setPreferredSize(new Dimension(500, 150));
    	ahiriSwing_PnlConsoleAndStatus.setLayout(new BorderLayout());
    	ahiriSwing_PnlConsoleAndStatus.add(ahiriSwing_PnlConsole, BorderLayout.CENTER);
    	ahiriSwing_PnlConsoleAndStatus.add(ahiriSwing_PnlStatus, BorderLayout.PAGE_END);
    	
    	//Configure Map Controls Elements
    	txtName = new JTextField(txtSeed.getText(), 10);
		txtName.addFocusListener(this);
		btnSaveActions = new JButton("Save Actions");
		btnSaveActions.addActionListener(this);
		btnLoadActions = new JButton("Load Actions");
		btnLoadActions.addActionListener(this);
		btnSaveImages = new JButton("Save Image Dumps");
		btnSaveImages.addActionListener(this);
		btnSaveMap = new JButton("Save Map Files");
		btnSaveMap.addActionListener(this);
		btnShowDump = new JButton("Show Map View");
		btnShowDump.addActionListener(this);
		btnShowTopo = new JButton("Show Topo View");
		btnShowTopo.addActionListener(this);
		btnShowCave = new JButton("Show Cave View");
		btnShowCave.addActionListener(this);
		btnShowHeightMap = new JButton("Show Height View");
		btnShowHeightMap.addActionListener(this);
		
    	//Configure Map Controls Panel Container
		ahiriSwing_PnlMapControls = new JPanel();
		ahiriSwing_PnlMapControls.add(txtName);
		ahiriSwing_PnlMapControls.add(btnSaveActions);
		ahiriSwing_PnlMapControls.add(btnLoadActions);
		ahiriSwing_PnlMapControls.add(btnSaveImages);
		ahiriSwing_PnlMapControls.add(btnSaveMap);
		ahiriSwing_PnlMapControls.add(btnShowDump);
		ahiriSwing_PnlMapControls.add(btnShowTopo);
		ahiriSwing_PnlMapControls.add(btnShowCave);
		ahiriSwing_PnlMapControls.add(btnShowHeightMap);
		ahiriSwing_PnlMapControls.setPreferredSize(new Dimension(500, 65));
		
    	//Configure Map and Controls Panel Container
    	ahiriSwing_PnlMapAndControls = new JPanel();
    	ahiriSwing_PnlMapAndControls.setLayout(new BorderLayout());
    	ahiriSwing_PnlMapAndControls.add(pnlMap, BorderLayout.CENTER);
    	ahiriSwing_PnlMapAndControls.add(ahiriSwing_PnlMapControls, BorderLayout.PAGE_END);
    	
    	//Configure Map and Status Panel Container
    	ahiriSwing_PnlMapAndStatus = new JPanel();
    	ahiriSwing_PnlMapAndStatus.setLayout(new BorderLayout());
    	ahiriSwing_PnlMapAndStatus.add(ahiriSwing_PnlMapAndControls, BorderLayout.CENTER);
    	ahiriSwing_PnlMapAndStatus.add(ahiriSwing_PnlConsoleAndStatus, BorderLayout.PAGE_END);
    	
        //Configure Tabs
    	//HeightMap Tab
    	ahiriSwing_PnlHeightMapControls = new JPanel();
    	ahiriSwing_PnlHeightMapControls.setLayout(new BorderLayout());
    	ahiriSwing_PnlHeightMapControls.add(ahiriSwing_PnlHeightMapOptions, BorderLayout.CENTER);
    	ahiriSwing_PnlHeightMapControls.add(ahiriSwing_PnlHeightMapButtons, BorderLayout.PAGE_END);
    	//Erosion Tab
    	ahiriSwing_PnlErosionControls = new JPanel();
    	ahiriSwing_PnlErosionControls.setLayout(new BorderLayout());
    	ahiriSwing_PnlErosionControls.add(ahiriSwing_PnlErosionOptions, BorderLayout.CENTER);
    	ahiriSwing_PnlErosionControls.add(ahiriSwing_PnlErosionButtons, BorderLayout.PAGE_END);
    	//Dirt Tab
    	ahiriSwing_PnlDirtControls = new JPanel();
    	ahiriSwing_PnlDirtControls.setLayout(new BorderLayout());
    	ahiriSwing_PnlDirtControls.add(ahiriSwing_PnlDirtOptions, BorderLayout.CENTER);
    	ahiriSwing_PnlDirtControls.add(ahiriSwing_PnlDirtButtons, BorderLayout.PAGE_END);
    	//Biome Tab
    	ahiriSwing_PnlBiomeControls = new JPanel();
    	ahiriSwing_PnlBiomeControls.setLayout(new BorderLayout());
    	ahiriSwing_PnlBiomeControls.add(ahiriSwing_PnlBiomeOptions, BorderLayout.CENTER);
    	ahiriSwing_PnlBiomeControls.add(ahiriSwing_PnlBiomeButtons, BorderLayout.PAGE_END);
    	//Ore Tab
    	ahiriSwing_PnlOreControls = new JPanel();
    	ahiriSwing_PnlOreControls.setLayout(new BorderLayout());
    	ahiriSwing_PnlOreControls.add(ahiriSwing_PnlOreOptions, BorderLayout.CENTER);
    	ahiriSwing_PnlOreControls.add(ahiriSwing_PnlOreButtons, BorderLayout.PAGE_END);
        
        //Creates Icons for the tab panel
        ImageIcon ahiriSwing_IconHeightMap = createImageIcon("/icon-heightmap.png","Heightmap");
        ImageIcon ahiriSwing_IconErosion = createImageIcon("/icon-erosion.png","Erosion");
        ImageIcon ahiriSwing_IconDirt = createImageIcon("/icon-dirt.png","Dirt");
        ImageIcon ahiriSwing_IconBiome = createImageIcon("/icon-biome.png","Biome");
        ImageIcon ahiriSwing_IconOre = createImageIcon("/icon-ore.png","Ore");
        
        //Configure tab panel
        ahiriSwing_TabControls = new JTabbedPane();
        ahiriSwing_TabControls.setPreferredSize(new Dimension(300, 600));
        
        //Adds tabs to tab panel
        ahiriSwing_TabControls.addTab("Heightmap", ahiriSwing_IconHeightMap, ahiriSwing_PnlHeightMapControls, "Heightmap Controls");
        ahiriSwing_TabControls.addTab("Erosion", ahiriSwing_IconErosion, ahiriSwing_PnlErosionControls, "Erode Controls");
        ahiriSwing_TabControls.addTab("Dirt", ahiriSwing_IconDirt, ahiriSwing_PnlDirtControls, "Dirt Controls");
        ahiriSwing_TabControls.addTab("Biome", ahiriSwing_IconBiome, ahiriSwing_PnlBiomeControls, "Biome Controls");
        ahiriSwing_TabControls.addTab("Ore", ahiriSwing_IconOre, ahiriSwing_PnlOreControls, "Ore Controls");
        
        //Adds the top level panels
        this.add(ahiriSwing_PnlMapAndStatus, BorderLayout.CENTER);
        this.add(ahiriSwing_TabControls, BorderLayout.LINE_END);
        this.setVisible(true);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			return null;
		}
	}
	
	public WurmAPI getAPI() {
		if (apiClosed)
			api = null;
		
		if (api == null)
			try {
				api = WurmAPI.create("./maps/" + txtName.getText() + "/", (int) (Math.log(heightMap.getMapSize()) / Math.log(2)));
				apiClosed = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return api;
	}
	
	public void newHeightMap(long seed, int mapSize, double resolution, int iterations, int minEdge, double borderWeight, int maxHeight, boolean moreLand) {
		heightMap = new HeightMap(seed, mapSize, resolution, iterations, minEdge, borderWeight, maxHeight, moreLand);
		heightMap.generateHeights();
		
		updateMapView(false, 0);
	}
	
	public void newHeightMapFromFile(String newImageFilename, int maxHeight) {
		int index = newImageFilename.lastIndexOf(File.separator);
		String fileName = newImageFilename.substring(index + 1);
		if (fileName.indexOf(".") > 0)
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		txtName.setText(fileName);
		BufferedImage newImage = null;
		try{
			newImage = ImageIO.read(new File(newImageFilename));
		}catch(IOException e){
		}
		heightMap = new HeightMap(newImage, maxHeight);
		
		updateMapView(false, 0);
	}
	
	public void updateMapView(boolean apiView, int viewType) {
		if (!apiView) {
			Graphics g = pnlMap.getMapImage().getGraphics();
			
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					g.setColor(new Color((float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j), (float) heightMap.getHeight(i, j)));
					g.fillRect(i, j, 1, 1);
				}
			}
		} else {
			updateAPIMap();
			
			if (viewType == 1)
				pnlMap.setMapImage(getAPI().getMapData().createTopographicDump(true, (short) 250));
			else if (viewType == 2)
				pnlMap.setMapImage(getAPI().getMapData().createCaveDump(true));
			else
				pnlMap.setMapImage(getAPI().getMapData().createMapDump());
		}
		
		pnlMap.updateScale();
		pnlMap.checkBounds();
		pnlMap.repaint();
	}
	
	private void updateAPIMap() {
		MapData map = getAPI().getMapData();
		Random treeRand = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < heightMap.getMapSize(); i++) {
			for (int j = 0; j < heightMap.getMapSize(); j++) {
				map.setSurfaceHeight(i, j, tileMap.getSurfaceHeight(i, j));
				map.setRockHeight(i, j, tileMap.getRockHeight(i, j));
				
				if (tileMap.hasOres())
					map.setCaveTile(i, j, tileMap.getOreType(i, j), tileMap.getOreCount(i, j));
				
				if (tileMap.getType(i, j).isTree())
					map.setTree(i, j, tileMap.getType(i, j).getTreeType((byte) 0), 
							FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
				else if (tileMap.getType(i, j).isBush())
					map.setBush(i, j, tileMap.getType(i, j).getBushType((byte) 0), 
							FoliageAge.values()[treeRand.nextInt(FoliageAge.values().length)], GrowthTreeStage.MEDIUM);
				else 
					map.setSurfaceTile(i, j, tileMap.getType(i, j));
			}
		}
	}
	
	private void parseAction(String action) {
		String[] parts = action.split(":");
		if (parts.length < 2)
			return;
		
		String[] options = parts[1].split(",");		
		switch (parts[0]) {
			case "HEIGHTMAP":
				if (options.length < 8) {
					JOptionPane.showMessageDialog(this, "Not enough options for HEIGHTMAP", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					txtSeed.setText(options[0]);
					cmbMapSize.setSelectedIndex(Integer.parseInt(options[1]));
					txtRes.setText(options[2]);
					txtIterations.setText(options[3]);
					txtMinEdge.setText(options[4]);
					txtBorderWeight.setText(options[5]);
					txtMaxHeight.setText(options[6]);
					chkLand.setSelected(Boolean.parseBoolean(options[7]));
					
					btnGenHeightMap.doClick();
				} catch (Exception nfe) {
					JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "ERODE":
				if (options.length < 3) {
					JOptionPane.showMessageDialog(this, "Not enough options for ERODE", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtErodeIterations.setText(options[0]);
				txtErodeMinSlope.setText(options[1]);
				txtErodeMaxSediment.setText(options[2]);
					
				btnErodeHeightMap.doClick();
				break;
			case "DROPDIRT":
				if (options.length < 6) {
					JOptionPane.showMessageDialog(this, "Not enough options for DROPDIRT", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtBiomeSeed.setText(options[0]);
				txtWaterHeight.setText(options[1]);
				txtDirtAmnt.setText(options[2]);
				txtDirtSlope.setText(options[3]);
				txtDirtDiagSlope.setText(options[4]);
				txtMaxDirtHeight.setText(options[5]);
					
				btnDropDirt.doClick();
				break;
			case "UNDOBIOME":
				btnUndoBiome.doClick();
				break;
			case "RESETBIOMES":
				btnResetBiomes.doClick();
				break;
			case "GENORES":
				if (options.length < 12) {
					JOptionPane.showMessageDialog(this, "Not enough options for GENORES", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtRock.setText(options[0]);
				txtIron.setText(options[1]);
				txtGold.setText(options[2]);
				txtSilver.setText(options[3]);
				txtZinc.setText(options[4]);
				txtCopper.setText(options[5]);
				txtLead.setText(options[6]);
				txtTin.setText(options[7]);
				txtAddy.setText(options[8]);
				txtGlimmer.setText(options[9]);
				txtMarble.setText(options[10]);
				txtSlate.setText(options[11]);
					
				btnGenOres.doClick();
				break;
			default:
				if(parts[0].startsWith("SEEDBIOME")){
                                    if (options.length < 10) {
                                            JOptionPane.showMessageDialog(this, "Not enough options for SEEDBIOME", "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
                                            return;
                                    }

                                    try {
                                            cmbBiomeType.setSelectedIndex(Integer.parseInt(options[0]));
                                            txtBiomeSeedCount.setText(options[1]);
                                            txtBiomeSize.setText(options[2]);
                                            txtBiomeMaxSlope.setText(options[3]);
                                            txtBiomeRateN.setText(options[4]);
                                            txtBiomeRateS.setText(options[5]);
                                            txtBiomeRateE.setText(options[6]);
                                            txtBiomeRateW.setText(options[7]);
                                            txtBiomeMinHeight.setText(options[8]);
                                            txtBiomeMaxHeight.setText(options[9]);

                                            btnSeedBiome.doClick();
                                    } catch (Exception nfe) {
                                            JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Loading Actions", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                        break;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == btnLoadHeightMap) {
			try {
				api = null;
				genHistory = new ArrayList<String>();
				
				pnlMap.setMapSize((int) cmbMapSize.getSelectedItem());
				
				File heightmapFile = null;
				
				JFileChooser fc = new JFileChooser();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "PNG Heightmap Files", "png");
				fc.setFileFilter(filter);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setCurrentDirectory(new File("./maps/"));
				
				int returnVal = fc.showDialog(this, "Load Heightmap");

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            heightmapFile = fc.getSelectedFile();
		        }
				heightMapFilePath = heightmapFile.getAbsolutePath();
				newHeightMapFromFile(heightMapFilePath, Integer.parseInt(txtMaxHeight.getText()));
				
				/*genHistory.add("HEIGHTMAP:" + txtSeed.getText() + "," + cmbMapSize.getSelectedIndex() + "," + txtRes.getText() + "," +
						txtIterations.getText() + "," + txtMinEdge.getText() + "," + txtBorderWeight.getText() + "," +
						txtMaxHeight.getText() + "," + chkLand.isSelected());*/
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
		if (e.getSource() == btnReloadHeightMap) {
			if (heightMapFilePath == null) {
				JOptionPane.showMessageDialog(this, "Please load a map first!", "Error Reloading Heightmap", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				api = null;
				genHistory = new ArrayList<String>();
				
				pnlMap.setMapSize((int) cmbMapSize.getSelectedItem());
				
				newHeightMapFromFile(heightMapFilePath, Integer.parseInt(txtMaxHeight.getText()));
				
				/*genHistory.add("HEIGHTMAP:" + txtSeed.getText() + "," + cmbMapSize.getSelectedIndex() + "," + txtRes.getText() + "," +
						txtIterations.getText() + "," + txtMinEdge.getText() + "," + txtBorderWeight.getText() + "," +
						txtMaxHeight.getText() + "," + chkLand.isSelected());*/
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnGenHeightMap) {
			try {
				api = null;
				genHistory = new ArrayList<String>();
				
				pnlMap.setMapSize((int) cmbMapSize.getSelectedItem());
				
				newHeightMap(txtSeed.getText().hashCode(), (int) cmbMapSize.getSelectedItem(), 
						Double.parseDouble(txtRes.getText()), Integer.parseInt(txtIterations.getText()), 
						Integer.parseInt(txtMinEdge.getText()), Double.parseDouble(txtBorderWeight.getText()), 
						Integer.parseInt(txtMaxHeight.getText()), chkLand.isSelected());
				
				genHistory.add("HEIGHTMAP:" + txtSeed.getText() + "," + cmbMapSize.getSelectedIndex() + "," + txtRes.getText() + "," +
						txtIterations.getText() + "," + txtMinEdge.getText() + "," + txtBorderWeight.getText() + "," +
						txtMaxHeight.getText() + "," + chkLand.isSelected());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating HeightMap", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnErodeHeightMap) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				heightMap.erode(Integer.parseInt(txtErodeIterations.getText()), Integer.parseInt(txtErodeMinSlope.getText()), 
						Integer.parseInt(txtErodeMaxSediment.getText()));
				
				updateMapView(false, 0);
				
				genHistory.add("ERODE:" + txtErodeIterations.getText() + "," + txtErodeMinSlope.getText() + "," + txtErodeMaxSediment.getText());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Eroding HeightMap", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnDropWater) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this,"Please drop dirt at least one time first!",  "Error Reloading Water", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				tileMap.setWaterHeight(Integer.parseInt(txtWaterHeight.getText()));
				tileMap.reloadTileTypes();
				updateMapView(true, 0);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnDropDirt) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				tileMap = new TileMap(heightMap);
				tileMap.setBiomeSeed(txtBiomeSeed.getText().hashCode());
				tileMap.setWaterHeight(Integer.parseInt(txtWaterHeight.getText()));
				tileMap.dropDirt(Integer.parseInt(txtDirtAmnt.getText()), Integer.parseInt(txtDirtSlope.getText()), 
						Integer.parseInt(txtDirtDiagSlope.getText()), Integer.parseInt(txtMaxDirtHeight.getText()));
				
				updateMapView(true, 0);
				
				genHistory.add("DROPDIRT:" + txtBiomeSeed.getText() + "," + txtWaterHeight.getText() + "," + txtDirtAmnt.getText() + "," +
						txtDirtSlope.getText() + "," + txtDirtDiagSlope.getText() + "," + txtMaxDirtHeight.getText());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnSeedBiome) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Adding Biome", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				double[] rates = new double[4];
				
				rates[0] = Integer.parseInt(txtBiomeRateN.getText()) / 100.0;
				rates[1] = Integer.parseInt(txtBiomeRateS.getText()) / 100.0; 
				rates[2] = Integer.parseInt(txtBiomeRateE.getText()) / 100.0; 
				rates[3] = Integer.parseInt(txtBiomeRateW.getText()) / 100.0; 
				
				tileMap.plantBiome(Integer.parseInt(txtBiomeSeedCount.getText()), Integer.parseInt(txtBiomeSize.getText()), 
						rates, Integer.parseInt(txtBiomeMaxSlope.getText()), Integer.parseInt(txtBiomeMinHeight.getText()),
						Integer.parseInt(txtBiomeMaxHeight.getText()), (Tile) cmbBiomeType.getSelectedItem());
				
				updateMapView(true, 0);
				
				genHistory.add("SEEDBIOME("+cmbBiomeType.getSelectedItem()+"):" + cmbBiomeType.getSelectedIndex() + "," + txtBiomeSeedCount.getText() + "," + txtBiomeSize.getText() + "," +
						txtBiomeMaxSlope.getText() + "," + txtBiomeRateN.getText() + "," + txtBiomeRateS.getText() + "," +
						txtBiomeRateE.getText() + "," + txtBiomeRateW.getText() + "," + txtBiomeMinHeight.getText() + "," +
						txtBiomeMaxHeight.getText());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Dropping Dirt", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnUndoBiome) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			tileMap.undoLastBiome();
			
			updateMapView(true, 0);
			
			genHistory.add("UNDOBIOME:null");
		}
		
		if (e.getSource() == btnResetBiomes) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			for (int i = 0; i < heightMap.getMapSize(); i++) {
				for (int j = 0; j < heightMap.getMapSize(); j++) {
					tileMap.addDirt(i, j, 0);
				}
			}
			
			updateMapView(true, 0);
			
			genHistory.add("RESETBIOMES:null");
		}
		
		if (e.getSource() == btnGenOres) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Resetting Biomes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				double[] rates = { Double.parseDouble(txtRock.getText()), Double.parseDouble(txtIron.getText()), Double.parseDouble(txtGold.getText()),
						Double.parseDouble(txtSilver.getText()), Double.parseDouble(txtZinc.getText()), Double.parseDouble(txtCopper.getText()),
						Double.parseDouble(txtLead.getText()), Double.parseDouble(txtTin.getText()), Double.parseDouble(txtAddy.getText()),
						Double.parseDouble(txtGlimmer.getText()), Double.parseDouble(txtMarble.getText()), Double.parseDouble(txtSlate.getText())					
				};
				
				tileMap.generateOres(rates);
				
				updateAPIMap();
				
				updateMapView(true, 2);
				
				genHistory.add("GENORES:" + txtRock.getText() + "," + txtIron.getText() + "," + txtGold.getText() + "," +
						txtSilver.getText() + "," + txtZinc.getText() + "," + txtCopper.getText() + "," +
						txtLead.getText() + "," + txtTin.getText() + "," + txtAddy.getText() + "," +
						txtGlimmer.getText() + "," + txtMarble.getText() + "," + txtSlate.getText());
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(this, "Error parsing number " + nfe.getMessage().toLowerCase(), "Error Generating Ores", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource() == btnShowDump) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, 0);
		}
		
		if (e.getSource() == btnShowTopo) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, 1);
		}
		
		if (e.getSource() == btnShowCave) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!tileMap.hasOres()) {
				JOptionPane.showMessageDialog(this, "No Cave Map - Generate Ores first", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(true, 2);
		}
		
		if (e.getSource() == btnShowHeightMap) {
			if (heightMap == null) {
				JOptionPane.showMessageDialog(this, "HeightMap does not exist", "Error Showing Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateMapView(false, 0);
		}
		
		if (e.getSource() == btnSaveImages) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Images", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateAPIMap();
			
			MapData map = getAPI().getMapData();
			try {
				ImageIO.write(map.createMapDump(), "png", new File("./maps/" + txtName.getText() + "/map.png"));
				ImageIO.write(map.createTopographicDump(true, (short) 250), "png", new File("./maps/" + txtName.getText() + "/topography.png"));
				ImageIO.write(map.createCaveDump(true), "png", new File("./maps/" + txtName.getText() + "/cave.png"));
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getSource() == btnSaveMap) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			updateAPIMap();
			
			getAPI().getMapData().saveChanges();
			getAPI().close();
			apiClosed = true;
		}
		
		if (e.getSource() == btnSaveActions) {
			if (tileMap == null) {
				JOptionPane.showMessageDialog(this, "TileMap does not exist - Add Dirt first", "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				File actionsFile = new File("./maps/" + txtName.getText() + "/map_actions.txt");
				actionsFile.createNewFile();
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(actionsFile));
				for (String s : genHistory)
					bw.write(s + "\r\n");
				
				bw.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
		}
		
		if (e.getSource() == btnLoadActions) {
			try {
				File actionsFile;
				
				JFileChooser fc = new JFileChooser();
				fc.addChoosableFileFilter(new TextFileView());
				fc.setAcceptAllFileFilterUsed(false);
				fc.setCurrentDirectory(new File("./maps/"));
				
				int returnVal = fc.showDialog(this, "Load Actions");

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            actionsFile = fc.getSelectedFile();
		            txtName.setText(actionsFile.getParentFile().getName());
		            
					BufferedReader br = new BufferedReader(new FileReader(actionsFile));
					String line;
					while ((line = br.readLine()) != null) {
						parseAction(line);
					}
					
					br.close();
		        }
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Unable to load actions file", "Error Loading Map", JOptionPane.ERROR_MESSAGE);
				logger.log(Level.WARNING, "Error loading actions file: " + ex.getMessage());
			}
		}
		
		if (e.getSource() == btnResetHeightSeed) {
			txtSeed.setText("" + System.currentTimeMillis());
		}
		
		if (e.getSource() == btnResetBiomeSeed) {
			txtBiomeSeed.setText("" + System.currentTimeMillis());
		}
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	public static void main(String[] args) {
		new WGenerator(Constants.WINDOW_TITLE, Constants.WINDOW_SIZE_WIDTH, Constants.WINDOW_SIZE_HEIGHT);
	}

	@Override
	public void focusGained(FocusEvent e) {
		// Do Nothing
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			if (e.getSource() == txtName) {
				if (!apiClosed)
					getAPI().close();
				
				apiClosed = true;
				updateAPIMap();
			} else {
				try {
					double[] rates = { Double.parseDouble(txtIron.getText()), Double.parseDouble(txtGold.getText()),
							Double.parseDouble(txtSilver.getText()), Double.parseDouble(txtZinc.getText()), Double.parseDouble(txtCopper.getText()),
							Double.parseDouble(txtLead.getText()), Double.parseDouble(txtTin.getText()), Double.parseDouble(txtAddy.getText()),
							Double.parseDouble(txtGlimmer.getText()), Double.parseDouble(txtMarble.getText()), Double.parseDouble(txtSlate.getText())					
					};
					
					double total = 0;
					for (int i = 0; i < rates.length; i++)
						total += rates[i];
					
					txtRock.setText("" + (100.0 - total));
				} catch (NumberFormatException nfe) {
				
				}
			}
		}
	}
	
	class TextFileView extends FileFilter {
		
		public boolean accept(File f) {
		    if (f.isDirectory()) {
		        return true;
		    }

		    String extension = getExtension(f);
		    if (extension != null)
		        if (extension.equals("txt"))
		                return true;

		    return false;
		}
		
		private String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }

		@Override
		public String getDescription() {
			return "Action Files (.txt)";
		}
	}
}

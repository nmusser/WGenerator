package net.buddat.wgenerator;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.buddat.wgenerator.util.SimplexNoise;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.lang.Math;

/**
 * @author Budda
 *
 * Generates a heightmap with bounds set in the constructor.
 * All height values for each point will be between 0.0 and 1.0
 */
public class HeightMap {
	
	private static final Logger logger = Logger.getLogger(HeightMap.class.getName());

	private double[][] heightArray;
	
	private long noiseSeed;
	
	private int mapSize;
	
	private double resolution;
	
	private int iterations;
	
	private boolean moreLand;
	
	private int minimumEdge;
	
	private int maxHeight;
	
	private int borderCutoff;
	
	private float borderNormalize;
	
	private double singleDirt;
	
	public HeightMap(long seed, int mapSize, double resolution, int iterations, int minimumEdge, double borderWeight, int maxHeight, boolean moreLand) {
		this.noiseSeed = seed;
		this.mapSize = mapSize;
		this.resolution = resolution;
		this.iterations = iterations;
		this.minimumEdge = minimumEdge;
		this.maxHeight = maxHeight;
		this.moreLand = moreLand;
		
		this.heightArray = new double[mapSize][mapSize];
		this.borderCutoff = (int) (mapSize / borderWeight);
		this.borderNormalize = (float) (1.0f / borderCutoff);
		
		this.singleDirt = 1.0 / maxHeight;
		
		logger.setLevel(Level.INFO);
	}
	
	public HeightMap(BufferedImage newImage, int maxHeight) {
		this.noiseSeed = 0;
		this.mapSize = newImage.getHeight();
		logger.log(Level.INFO, "mapSize="+this.mapSize);
		this.resolution = 0;
		this.iterations = 0;
		this.minimumEdge = 0;
		this.maxHeight = maxHeight;
		this.moreLand = true;
		this.borderCutoff = 0;
		this.borderNormalize = 0;
		this.singleDirt = 1.0 / maxHeight;
		
		this.heightArray = new double[newImage.getHeight()][newImage.getWidth()];
		Raster heightRaster = newImage.getRaster();
		logger.setLevel(Level.INFO);
		for (int x = 0; x < mapSize; x++){
			for (int y = 0; y < mapSize; y++){
				int[] heightPixelColor = new int[2];
				heightRaster.getPixel(x, y, heightPixelColor);
				//double height = (short) ((heightPixelColor[0] - Short.MAX_VALUE));
				//if(height!=0) height = 1d/height;
				//double height = heightPixelColor[0];
				//setHeight(x, y, height, false);
				double height = (double)(heightPixelColor[0])/65535d;
				this.heightArray[x][y] = height;
				if (height > 1) logger.log(Level.INFO, "This pixel is too damn big: " + heightPixelColor[0]);
				if (height < 0) logger.log(Level.INFO, "This pixel is too damn small:" + heightPixelColor[0]);
			}
		}
		normalizeHeights();
		logger.log(Level.INFO, "We're done generating this shit");
	}
	
	/**
	 *  Generates a full heightmap with the current instance's set values.
	 *  Clamps the heightmap heights for the last iteration only.
	 */
	public void generateHeights() {
		logger.log(Level.INFO, "HeightMap seed set to: " + noiseSeed);
		SimplexNoise.genGrad(noiseSeed);
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			logger.log(Level.FINE, "HeightMap Generation (" + mapSize + ") - Iteration(" + (i + 1) + "/" + iterations + ")");
			
			double iRes = resolution / Math.pow(2, i - 1);
			double str = Math.pow(2, i - 1) * 2.0;
			
			for (int x = 0; x < mapSize; x++)
				for (int y = 0; y < mapSize; y++)
					setHeight(x, y, getHeight(x, y) + SimplexNoise.noise(x / iRes, y / iRes) / str, (i == iterations - 1));
		}
		
		logger.log(Level.INFO, "HeightMap Generation (" + mapSize + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
		
		normalizeHeights();
	}
	
	public void normalizeHeights() {
		long startTime = System.currentTimeMillis();
		
		double maxHeight = 0.0f;
		for (int i = 0; i < mapSize; i++) {
			for (int j = 0; j < mapSize; j++) {
				if (getHeight(i, j) > maxHeight)
					maxHeight = getHeight(i, j);
			}
		}
		
		double normalize = 1.0f / maxHeight;
		for (int i = 0; i < mapSize; i++)
			for (int j = 0; j < mapSize; j++)
				setHeight(i, j, getHeight(i, j) * normalize, false);
		
		double normalizeLow = 1.0 / 0.5;
		double normalizeHigh = 2.0f / 0.5;
		for (int i = 0; i < mapSize; i++)
			for (int j = 0; j < mapSize; j++) {
				if (getHeight(i, j) < 0.5) {
					setHeight(i, j, (getHeight(i, j) * normalizeLow) / 3.0, false);
				} else {
					double newHeight = 1.0f + (getHeight(i, j) - 0.5) * normalizeHigh;
					setHeight(i, j, newHeight / 3.0, false);
				}
			}
		
		logger.log(Level.INFO, "HeightMap Normalization (" + mapSize + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}
	
	public void erode(int iterations, int minSlope, int sedimentMax) {
		long startTime = System.currentTimeMillis();
		
		for (int iter = 0; iter < iterations; iter++) {
			for (int i = 0; i < mapSize; i++) {
				for (int j = 0; j < mapSize; j++) {
					double neighbours[] = new double[4];
					double currentTile = heightArray[i][j];
					
					neighbours[0] = heightArray[clamp(i - 1, 0, mapSize - 1)][j];
					neighbours[1] = heightArray[i][clamp(j - 1, 0, mapSize - 1)];
					neighbours[2] = heightArray[clamp(i + 1, 0, mapSize - 1)][j];
					neighbours[3] = heightArray[i][clamp(j + 1, 0, mapSize - 1)];
					
					int lowest = 0;
					double maxDiff = 0.0;
					for (int k = 0; k < 3; k++) {
						double diff = currentTile - neighbours[k];
						if (diff > maxDiff) {
							maxDiff = diff;
							lowest = k;
						}
					}
					
					double sediment = 0.0;
					if (maxDiff > minSlope * singleDirt) {
						sediment = (sedimentMax * singleDirt) * maxDiff;
						currentTile -= sediment;
						neighbours[lowest] += sediment;
					}
					
					setHeight(i, j, currentTile, false);
					setHeight(clamp(i - 1, 0, mapSize - 1), j, neighbours[0], false);
					setHeight(i, clamp(j - 1, 0, mapSize - 1), neighbours[1], false);
					setHeight(clamp(i + 1, 0, mapSize - 1), j, neighbours[2], false);
					setHeight(i, clamp(j + 1, 0, mapSize - 1), neighbours[3], false);
				}
			}
		}
		
		logger.log(Level.INFO, "HeightMap Erosion (" + iterations + ") completed in " + (System.currentTimeMillis() - startTime) + "ms.");
	}
	
	public double[][] getHeightArray() {
		return heightArray;
	}
	
	public double getHeight(int x, int y) {
		return heightArray[x][y];
	}
	
	/**
	 * @param x Location x
	 * @param y Location y
	 * @param newHeight Height to set the location to
	 * @param clamp Whether to clamp the location's height depending on x/y and the border cutoff (Constants.BORDER_WEIGHT)
	 * @return The height that was set after constraints and clamping
	 */
	public double setHeight(int x, int y, double newHeight, boolean clamp) {
		if (newHeight < (moreLand ? -1d : 0))
			newHeight = (moreLand ? -1d : 0);
		if (newHeight > 1d)
			newHeight = 1d;
		
		heightArray[x][y] = newHeight;
		
		if (clamp) {
			if (moreLand)
				heightArray[x][y] = (heightArray[x][y] + 1) * 0.5d;
			
			if (x <= borderCutoff + minimumEdge || y <= borderCutoff + minimumEdge) {
				if (x < y)
					heightArray[x][y] *= Math.max(0, ((Math.min(x, mapSize - y) - minimumEdge)) * borderNormalize);
				else
					heightArray[x][y] *= Math.max(0, ((Math.min(y, mapSize - x) - minimumEdge)) * borderNormalize);
			} else if (mapSize - x <= borderCutoff + minimumEdge || mapSize - y <= borderCutoff + minimumEdge) {
				heightArray[x][y] *= Math.max(0, ((Math.min(mapSize - x, mapSize - y) - minimumEdge)) * borderNormalize);
			}
		}
		
		return heightArray[x][y];
	}
	
	public long getSeed() {
		return noiseSeed;
	}
	
	public void setSeed(long newSeed) {
		this.noiseSeed = newSeed;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public void setMaxHeight(int newMaxHeight) {
		this.maxHeight = newMaxHeight;
	}
	
	public int getMapSize() {
		return mapSize;
	}
	
	/**
	 * @param newSize Size to set the map to
	 * 
	 * We don't want to set the map size after construction.
	 * To generate a new map at a different size, create a new instance.
	 */
	protected void setMapSize(int newSize) {
		this.mapSize = newSize;
	}
	
	public double getResolution() {
		return resolution;
	}
	
	public void setResolution(double newResolution) {
		this.resolution = newResolution;
	}
	
	public int getIterations() {
		return iterations;
	}
	
	public void setIterations(int newIterations) {
		this.iterations = newIterations;
	}
	
	public double getSingleDirt() {
		return singleDirt;
	}
	
	public static int clamp(int val, int min, int max) {
	    return Math.max(min, Math.min(max, val));
	}
}

package sprites;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.*;

import org.apache.log4j.Logger;

import com.google.common.collect.*;

public class UIModel {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(UIModel.class);

  private BufferedImage sheet = null;
  private int w, h;
  private List<Sprite> sprites = Lists.newArrayList();
  private int numRows, numCols;

  public void loadSheet(BufferedImage sheet) {
    this.sheet = sheet;
    w = sheet.getWidth();
    h = sheet.getHeight();

    sprites = createSprites();
    logger.debug("Loaded " + sprites.size() + " sprites.");
  }

  public void setRowsCols(int rows, int cols) {
    numRows = rows;
    numCols = cols;
    sprites = parseSprites();
  }

  private List<Sprite> parseSprites() {
    List<Sprite> ret = Lists.newArrayList();

    int sw = w / numCols;
    int sh = h / numRows;

    int y = 0;
    for (int j = 0; j < numRows; j++) {
      List<BufferedImage> images = Lists.newArrayList();
      int x = 0;
      for (int i = 0; i < numCols; i++) {
        images.add(sheet.getSubimage(x, y, sw, sh));
        x += sw;
      }
      ret.add(new Sprite(PixelUtils.trim(images)));
      y += sh;
    }

    return ret;
  }

  public List<Sprite> getSprites() {
    return ImmutableList.copyOf(sprites);
  }

  private List<Sprite> createSprites() {
    List<Rectangle> bounds = analyzeBounds();
    int smallestHeight = Integer.MAX_VALUE;
    for (Rectangle r : bounds) {
      smallestHeight = Math.min(smallestHeight, r.height);
    }

    Map<IntKey, List<Rectangle>> yRows = Maps.newTreeMap();
    for (Rectangle r : bounds) {
      IntKey key = new IntKey(r.y, smallestHeight);
      List<Rectangle> list = yRows.get(key);
      if (list == null) {
        yRows.put(key, list = Lists.newArrayList());
      }
      list.add(r);
    }

    numRows = yRows.keySet().size();
    numCols = (int) Math.ceil(1.0 * bounds.size() / numRows);

    return parseSprites();
  }

  private List<Rectangle> analyzeBounds() {
    Set<Integer> seen = Sets.newHashSet();

    List<Rectangle> objects = Lists.newArrayList();

    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        int rgb = sheet.getRGB(i, j);
        if (PixelUtils.background.apply(rgb)) {
          continue;
        }
        if (!seen.contains(i + j * w)) {
          Rectangle r = PixelUtils.floodfill(sheet, i, j, seen);
          if (r.width < 5 || r.height < 5) {
            continue;
          }
          objects.add(r);
        }
      }
    }

    return objects;
  }

  public BufferedImage getSheet() {
    return sheet;
  }

  public int getNumCols() {
    return numCols;
  }

  public int getNumRows() {
    return numRows;
  }

  private static final class IntKey implements Comparable<IntKey> {

    private final int y;
    private final int threshold;

    public IntKey(int y, int threshold) {
      this.y = y;
      this.threshold = threshold;
    }

    @Override
    public int compareTo(IntKey o) {
      if (Math.abs(y - o.y) < threshold) {
        return 0;
      }
      return y - o.y;
    }

    @Override
    public String toString() {
      return y + "";
    }
  }

}

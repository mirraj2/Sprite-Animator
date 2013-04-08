package sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.google.common.collect.*;

public class UIModel {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(UIModel.class);

  private BufferedImage sheet = null;
  private int w, h;
  private List<Sprite> sprites = Lists.newArrayList();

  public void loadSheet(BufferedImage sheet) {
    this.sheet = sheet;
    w = sheet.getWidth();
    h = sheet.getHeight();

    sprites = createSprites();
  }

  public List<Sprite> getSprites() {
    return ImmutableList.copyOf(sprites);
  }

  private List<Sprite> createSprites() {
    List<Sprite> ret = Lists.newArrayList();

    List<Rectangle> bounds = analyzeBounds();

    Multimap<Integer, Rectangle> yRows = LinkedListMultimap.create();
    for (Rectangle r : bounds) {
      yRows.put(Math.round(r.y / 5) * 5, r);
    }

    for (Integer key : yRows.keySet()) {
      List<BufferedImage> images = Lists.newArrayList();
      for (Rectangle r : yRows.get(key)) {
        int offset = r.y - key;
        images.add(sheet.getSubimage(r.x, key, r.width, r.height + offset));
      }
      ret.add(new Sprite(images));
    }

    return ret;
  }

  private List<Rectangle> analyzeBounds() {
    Set<Integer> seen = Sets.newHashSet();

    List<Rectangle> objects = Lists.newArrayList();

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int rgb = sheet.getRGB(i, j);
        if (isBackground(rgb)) {
          continue;
        }
        if (!seen.contains(hash(i, j))) {
          objects.add(floodfill(i, j, seen));
        }
      }
    }

    return objects;
  }

  private boolean isBackground(int rgb) {
    int alpha = (rgb >> 24) & 0xff;
    return alpha == 0;
  }

  private Rectangle floodfill(int x, int y, Set<Integer> seen) {
    int minX = x, maxX = x, minY = y, maxY = y;
    Rectangle bounds = new Rectangle(0, 0, w, h);

    Queue<Point> q = Lists.newLinkedList();
    q.add(new Point(x, y));

    while (!q.isEmpty()) {
      Point p = q.poll();
      if (!bounds.contains(p) || isBackground(sheet.getRGB(p.x, p.y))) {
        continue;
      }
      if (!seen.add(hash(p.x, p.y))) {
        continue;
      }
      minX = Math.min(minX, p.x);
      maxX = Math.max(maxX, p.x);
      minY = Math.min(minY, p.y);
      maxY = Math.max(maxY, p.y);

      q.add(new Point(p.x + 1, p.y));
      q.add(new Point(p.x - 1, p.y));
      q.add(new Point(p.x, p.y + 1));
      q.add(new Point(p.x, p.y - 1));
    }

    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  private int hash(int x, int y) {
    return x + y * w;
  }

  public BufferedImage getSheet() {
    return sheet;
  }

}

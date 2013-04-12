package sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

public class Sprite extends JComponent implements Iterable<BufferedImage> {

  private final List<BufferedImage> images;

  public Sprite(Iterable<BufferedImage> images) {
    this.images = ImmutableList.copyOf(images);

    Dimension dim = new Dimension();
    for (BufferedImage bi : images) {
      dim.width = Math.max(dim.width, bi.getWidth());
      dim.height = Math.max(dim.height, bi.getHeight());
    }
    setPreferredSize(dim);
  }

  @Override
  protected void paintComponent(Graphics g) {
    int i = (int) ((System.currentTimeMillis() / 100) % images.size());
    g.drawImage(images.get(i), 0, 0, null);
  }

  @Override
  public Iterator<BufferedImage> iterator() {
    return images.iterator();
  }

  public BufferedImage pack() {
    int totalWidth = 0;
    int maxHeight = 0;
    for (BufferedImage bi : images) {
      totalWidth += bi.getWidth();
      maxHeight = Math.max(maxHeight, bi.getHeight());
    }
    BufferedImage ret = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
    int x = 0;
    Graphics2D g = ret.createGraphics();
    for (BufferedImage b : images) {
      g.drawImage(b, x, 0, null);
      x += b.getWidth();
    }
    g.dispose();
    return ret;
  }
}

package sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

public class Sprite extends JComponent {

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
}

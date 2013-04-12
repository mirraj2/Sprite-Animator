package sprites;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PixelUtils {

  public static List<BufferedImage> trim(Iterable<BufferedImage> images) {
    try {
      int[] gaps = maxGaps();

      for (BufferedImage bi : images) {
        int[] g = calculateGaps(bi);
        for (int i = 0; i < 4; i++) {
          gaps[i] = Math.min(gaps[i], g[i]);
        }
      }

      List<BufferedImage> ret = Lists.newArrayList();

      for (BufferedImage bi : images) {
        ret.add(bi.getSubimage(gaps[0], gaps[1], bi.getWidth() - gaps[0] - gaps[2], bi.getHeight()
            - gaps[1] - gaps[3]));
      }
      return ret;
    } catch (Exception e) {
      return ImmutableList.copyOf(images);
    }
  }

  private static int[] calculateGaps(BufferedImage bi) {
    int[] gaps = maxGaps();

    // left gap
    outer: for (int i = 0; i < bi.getWidth(); i++) {
      for (int j = 0; j < bi.getHeight(); j++) {
        if (!background.apply(bi.getRGB(i, j))) {
          gaps[0] = i;
          break outer;
        }
      }
    }

    // top gap
    outer: for (int j = 0; j < bi.getHeight(); j++) {
      for (int i = 0; i < bi.getWidth(); i++) {
        if (!background.apply(bi.getRGB(i, j))) {
          gaps[1] = j;
          break outer;
        }
      }
    }

    // right gap
    outer: for (int i = bi.getWidth() - 1; i >= 0; i--) {
      for (int j = 0; j < bi.getHeight(); j++) {
        if (!background.apply(bi.getRGB(i, j))) {
          gaps[2] = bi.getWidth() - 1 - i;
          break outer;
        }
      }
    }

    // bottom gap
    outer: for (int j = bi.getHeight() - 1; j >= 0; j--) {
      for (int i = 0; i < bi.getWidth(); i++) {
        if (!background.apply(bi.getRGB(i, j))) {
          gaps[3] = bi.getHeight() - 1 - j;
          break outer;
        }
      }
    }

    return gaps;
  }

  private static int[] maxGaps() {
    return new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
  }

  public static Rectangle floodfill(BufferedImage bi, int x, int y, Set<Integer> seen) {
    int minX = x, maxX = x, minY = y, maxY = y;
    Rectangle bounds = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());

    Queue<Point> q = Lists.newLinkedList();
    q.add(new Point(x, y));

    while (!q.isEmpty()) {
      Point p = q.poll();
      if (!bounds.contains(p) || background.apply(bi.getRGB(p.x, p.y))) {
        continue;
      }
      if (!seen.add(p.x + p.y * bi.getWidth())) {
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
      q.add(new Point(p.x + 1, p.y + 1));
      q.add(new Point(p.x + 1, p.y - 1));
      q.add(new Point(p.x - 1, p.y + 1));
      q.add(new Point(p.x - 1, p.y - 1));
    }

    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  public static final Predicate<Integer> background = new Predicate<Integer>() {
    @Override
    public boolean apply(Integer rgb) {
      int alpha = (rgb >> 24) & 0xff;
      return alpha == 0;
    }
  };

}

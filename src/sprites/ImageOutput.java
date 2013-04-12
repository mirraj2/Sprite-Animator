package sprites;

import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageOutput {

  public static void saveSprites(List<Sprite> sprites) {
    try {
      File folder = getDownloadsFolder();
      int c = 0;
      for (Sprite sprite : sprites) {
        File f = new File(folder, (c++) + ".png");
        ImageIO.write(sprite.pack(), "png", f);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static File getDownloadsFolder() {
    StringBuilder ret = new StringBuilder();
    ret.append(System.getProperty("user.home"));
    if (ret.charAt(ret.length() - 1) != File.separatorChar) {
      ret.append(File.separatorChar);
    }
    ret.append("Downloads");

    String path = ret.toString();

    File file = new File(path);
    if (!file.exists()) {
      if (!file.mkdir()) {
        return null;
      }
    }

    return file;
  }


}

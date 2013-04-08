package sprites;

import java.awt.Color;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.log4j.*;

import com.google.common.util.concurrent.Uninterruptibles;

public class SpriteAnimator extends JFrame {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(SpriteAnimator.class);

  private static final Executor executor = Executors.newSingleThreadExecutor();

  private UIModel model = new UIModel();
  private JPanel contentPanel;

  public SpriteAnimator() {
    super("Sprite Animator");
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    setContentPane(createContent());
    setSize(800, 600);
    setLocationRelativeTo(null);
    setVisible(true);

    executor.execute(new Runnable() {
      @Override
      public void run() {
        while (true) {
          contentPanel.repaint();
          Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MILLISECONDS);
        }
      }
    });
  }

  private void loadImage(URL url) throws Exception {
    contentPanel.removeAll();

    BufferedImage bi = ImageIO.read(url);
    model.loadSheet(bi);

    for (Sprite sprite : model.getSprites()) {
      add(sprite);
    }

    contentPanel.invalidate();
    contentPanel.validate();
    contentPanel.repaint();
  }

  private JComponent createContent() {
    contentPanel = new JPanel();
    contentPanel.setBackground(Color.black);

    contentPanel.setTransferHandler(new TransferHandler() {
      @Override
      public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (DataFlavor flavor : transferFlavors) {
          if (flavor.getRepresentationClass() == URL.class) {
            return true;
          }
        }
        return false;
      }

      @Override
      public boolean importData(JComponent comp, Transferable t) {
        for (DataFlavor flavor : t.getTransferDataFlavors()) {
          if (flavor.getRepresentationClass() == URL.class) {
            try {
              URL url = (URL) t.getTransferData(flavor);
              loadImage(url);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }
        return false;
      }
    });

    return contentPanel;
  }

  public static void main(String[] args) {
    BasicConfigurator.configure();

    new SpriteAnimator();
  }

}

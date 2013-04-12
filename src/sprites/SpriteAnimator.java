package sprites;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.*;

import com.google.common.util.concurrent.Uninterruptibles;

public class SpriteAnimator extends JFrame {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(SpriteAnimator.class);

  private static final Executor executor = Executors.newSingleThreadExecutor();

  private UIModel model = new UIModel();
  private JPanel contentPanel;

  private final JTextField rowsField = new JTextField(4);
  private final JTextField colsField = new JTextField(4);

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

  private void loadImage(URL url) {
    try {
      BufferedImage bi = ImageIO.read(url);
      model.loadSheet(bi);

      rowsField.setText(model.getNumRows() + "");
      colsField.setText(model.getNumCols() + "");

      reload();

      logger.debug("Done.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void reload() {
    contentPanel.removeAll();
    for (Sprite sprite : model.getSprites()) {
      contentPanel.add(sprite);
    }

    contentPanel.invalidate();
    contentPanel.validate();
    contentPanel.revalidate();
    contentPanel.repaint();
  }

  private JComponent createContent() {
    JPanel container = new JPanel(new BorderLayout());

    contentPanel = new JPanel();
    contentPanel.setBackground(Color.black);

    contentPanel.setTransferHandler(new TransferHandler() {
      @Override
      public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (DataFlavor flavor : transferFlavors) {
          if (flavor.getRepresentationClass() == URL.class
              || flavor.getHumanPresentableName().equals("application/x-java-file-list")) {
            return true;
          }
        }
        return false;
      }

      @SuppressWarnings("deprecation")
      @Override
      public boolean importData(JComponent comp, Transferable t) {
        try {
          for (DataFlavor flavor : t.getTransferDataFlavors()) {
            if (flavor.getRepresentationClass() == URL.class) {
              URL url = (URL) t.getTransferData(flavor);
              loadImage(url);
              return true;
            } else if (flavor.getHumanPresentableName().equals("application/x-java-file-list")) {
              File file = (File) ((List) t.getTransferData(flavor)).get(0);
              loadImage(file.toURL());
              return true;
            }
          }
          return false;
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
      }
    });

    container.add(createControlsPanel(), BorderLayout.NORTH);
    container.add(contentPanel, BorderLayout.CENTER);

    return container;
  }

  private final Action saveAction = new AbstractAction("Save") {
    @Override
    public void actionPerformed(ActionEvent e) {
      ImageOutput.saveSprites(model.getSprites());
    }
  };

  private JComponent createControlsPanel() {
    JPanel ret = new JPanel(new MigLayout());

    ret.add(new JLabel("Cols:"), "");
    ret.add(colsField);
    ret.add(new JLabel("Rows:"), "");
    ret.add(rowsField);
    ret.add(new JButton(saveAction), "gapleft 20");

    ActionListener listener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        model.setRowsCols(Integer.parseInt(rowsField.getText()),
            Integer.parseInt(colsField.getText()));
        reload();
      }
    };

    rowsField.addActionListener(listener);
    colsField.addActionListener(listener);

    return ret;
  }

  public static void main(String[] args) {
    BasicConfigurator.configure();

    new SpriteAnimator();
  }

}

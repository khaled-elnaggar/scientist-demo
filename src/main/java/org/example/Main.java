package org.example;

import org.math.plot.Plot2DPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
  public static void main(String[] args) throws Exception {
    Demo demo = new Demo();
    demo.scientistExperiment();
//    extracted();
  }

  private static void extracted() {
    double[] x = IntStream.range(0, 30).mapToDouble(n -> n).toArray();
    double[] y = Arrays.stream(x).map(n -> Math.pow(n, 2)).toArray();

    Plot2DPanel plot = new Plot2DPanel();
    plot.addLinePlot("my plot", x, y);

    JFrame frame = new JFrame("a plot panel");
    frame.setSize(1800, 1000);
    frame.setContentPane(plot);
    frame.setVisible(true);

//    saveImage(plot, "hehe.jpeg");
  }


  private static void saveImage(Plot2DPanel panel, String name) {
    int w = panel.getWidth();
    int h = panel.getHeight();
    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = bi.createGraphics();
    panel.paint(g);
    g.dispose();
    try {
      ImageIO.write(bi, "jpeg", new File(name));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

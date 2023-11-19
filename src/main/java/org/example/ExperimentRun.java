package org.example;

import com.github.rawls238.scientist4j.metrics.DropwizardMetricsProvider;
import io.dropwizard.metrics5.ConsoleReporter;
import io.dropwizard.metrics5.MetricRegistry;
import org.math.plot.Plot2DPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.IntStream;


public class ExperimentRun {
  ProductRest productRest = new ProductRest();
  private MyExperiment<Product> experiment;
  private ConsoleReporter reporter;
  private final static Logger logger = Logger.getLogger(ExperimentRun.class.getName());
  private final static int IMAGE_WIDTH = 1800;
  private final static int IMAGE_HEIGHT = 1000;

  public ExperimentRun() {
    experiment = new MyExperiment<>("product", new DropwizardMetricsProvider());
    reporter = ConsoleReporter.forRegistry((MetricRegistry) experiment.getMetricsProvider().getRegistry())
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
  }

  public void scientistExperiment() throws Exception {
    runExperiment();
    plotMatchesAndMismatches();
    plotMismatches();
//    plotPerformance();
//    reporter.report();
  }
  private void plotMatchesAndMismatches() {
    TreeMap<Double, Double> mismatchPerSecond = experiment.getMismatchPerSecond();
    TreeMap<Double, Double> matchPerSecond = experiment.getMatchPerSecond();
    int minLength = Math.min(mismatchPerSecond.size(), matchPerSecond.size());
    double[] x = new double[minLength];
    double[] yMismatches = new double[minLength];
    double[] yMatches = new double[minLength];
    int next = 0;
    for (Map.Entry<Double, Double> point : mismatchPerSecond.entrySet()) {
      if(next >= minLength) break;
      x[next] = point.getKey();
      yMatches[next] = point.getValue();
      next++;
    }
    next = 0;
    for (Map.Entry<Double, Double> point : matchPerSecond.entrySet()) {
      yMismatches[next] = point.getValue();
      next++;
    }

    for (int i = x.length - 1; i >= 0; i--) {
      x[i] -= x[0];
    }
    Plot2DPanel plot = new Plot2DPanel();
    plot.addLinePlot("matches", x, yMatches);
    plot.addLinePlot("mismatches", x, yMismatches);
    plot.setAxisLabels("# Second", "Count");

    JFrame frame = new JFrame("Accuracy plot");
    frame.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
    frame.setContentPane(plot);
    frame.setVisible(true);
  }
  private void plotMismatches() {
    TreeMap<Double, Double> mismatchPerSecond = experiment.getMismatchPerSecond();
    double[] x = new double[mismatchPerSecond.size()];
    double[] y = new double[mismatchPerSecond.size()];
    int next = 0;
    for (Map.Entry<Double, Double> point : mismatchPerSecond.entrySet()) {
      x[next] = point.getKey();
      y[next] = point.getValue();
      next++;
    }
    for (int i = x.length - 1; i >= 0; i--) {
      x[i] -= x[0];
    }
    Plot2DPanel plot = new Plot2DPanel();
    plot.addLinePlot("mismatches", x, y);
    plot.setAxisLabels("# Second", "Mismatches");

    JFrame frame = new JFrame("Mismatch plot");
    frame.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
    frame.setContentPane(plot);
    frame.setVisible(true);

//    saveImage(plot, "mismatches.jpeg");
  }

  private void plotPerformance() {
    List<Double> candidatePerformance = experiment.getCandidatePerformance();
    List<Double> controlPerformance = experiment.getControlPerformance();

    double[] x = IntStream.range(1, candidatePerformance.size()).mapToDouble(n -> n).toArray();
    double[] yCandidatePerformance = candidatePerformance.stream().mapToDouble(n -> n).toArray();
    double[] yControlPerformance = controlPerformance.stream().mapToDouble(n -> n).toArray();

    Plot2DPanel plot = new Plot2DPanel();
    plot.addLinePlot("Candidate Performance", x, yCandidatePerformance);
    plot.addLinePlot("Control Performance", x, yControlPerformance);
    plot.setAxisLabels("# Run", "Runtime");

    // put the PlotPanel in a JFrame, as a JPanel
    JFrame frame = new JFrame("Mismatch plot");
    frame.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
    frame.setContentPane(plot);
    frame.setVisible(true);
//    saveImage(plot, "performance.jpeg");
  }

  private void runExperiment() throws Exception {
    for (int i = 0; i < 1000; i++) {
      int randomNumber = (int) (Math.random() * 5 + 1);
      Callable<Product> oldCodePath = () -> productRest.getProductsV1(randomNumber);
      Callable<Product> newCodePath = () -> productRest.getProductsV2(randomNumber);
      experiment.run(oldCodePath, newCodePath);
    }
  }
}

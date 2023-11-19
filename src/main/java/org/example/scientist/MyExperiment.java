package org.example.scientist;

import com.github.rawls238.scientist4j.Experiment;
import com.github.rawls238.scientist4j.Observation;
import com.github.rawls238.scientist4j.Result;
import com.github.rawls238.scientist4j.metrics.MetricsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MyExperiment<T> extends Experiment<T> {
  public MyExperiment(String name, MetricsProvider metricsProvider) {
    super(name, metricsProvider);
  }

  private final TreeMap<Double, Double> mismatchPerSecond = new TreeMap<>();
  private final TreeMap<Double, Double> matchPerSecond = new TreeMap<>();

  private final List<Double> candidatePerformance = new ArrayList<>();
  private final List<Double> controlPerformance = new ArrayList<>();

  public TreeMap<Double, Double> getMismatchPerSecond() {
    return this.mismatchPerSecond;
  }

  public TreeMap<Double, Double> getMatchPerSecond() {
    return matchPerSecond;
  }

  public List<Double> getCandidatePerformance() {
    return candidatePerformance;
  }

  public List<Double> getControlPerformance() {
    return controlPerformance;
  }

  @Override
  protected void publish(Result<T> r) {
    Double now = (System.currentTimeMillis() / 1000) * 1.0;

    if (r.getMatch().get()) {
      matchPerSecond.put(now, matchPerSecond.getOrDefault(now, 0D) + 1);
    } else {
      mismatchPerSecond.put(now, mismatchPerSecond.getOrDefault(now, 0D) + 1);
    }

    Observation<T> candidateObservation = r.getCandidate().get();
    Observation<T> controlObservation = r.getControl();

    candidatePerformance.add(candidateObservation.getDuration() / 1000 * 1.0);
    controlPerformance.add(controlObservation.getDuration() / 1000 * 1.0);
  }

}

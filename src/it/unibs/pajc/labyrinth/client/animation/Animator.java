package it.unibs.pajc.labyrinth.client.animation;

import javax.swing.Timer;

public class Animator {
  private static final int TIMER_CYCLE_DURATION = 16; // 60 FPS (~16 ms per frame)

  private Animatable target;
  private long duration;
  private EasingFunction easingFunction;
  private Timer timer;
  private long startTime;
  private int[] startValues;
  private int[] endValues;
  private int[] currentValues;
  private Runnable onAnimationEnd;

  public Animator(
      Animatable target, long duration, EasingFunction easingFunction, Runnable onAnimationEnd) {
    this.target = target;
    this.duration = duration;
    this.easingFunction = easingFunction;
    timer = new Timer(TIMER_CYCLE_DURATION, e -> animate());
    this.onAnimationEnd = onAnimationEnd;
  }

  /** Initializes animation with multiple parameters. */
  public Animator initializeAnimation(int[] startValues, int[] endValues) {
    if (startValues.length != endValues.length) {
      throw new IllegalArgumentException("Start and end arrays must have the same length.");
    }
    this.startValues = startValues;
    this.endValues = endValues;
    this.currentValues = new int[startValues.length];

    return this;
  }

  public Animator setDuration(long duration) {
    this.duration = duration;
    return this;
  }

  /** Starts the animation. */
  public void start() {
    if (startValues == null || endValues == null) {
      throw new IllegalStateException("Animation values must be initialized before starting.");
    }
    startTime = System.currentTimeMillis();
    timer.start();
  }

  /** Handles animation steps. */
  private void animate() {
    long elapsedTime = System.currentTimeMillis() - startTime;
    float progress = Math.min(1.0f, (float) elapsedTime / duration);
    float easedProgress = easingFunction.ease(progress);

    for (int i = 0; i < startValues.length; i++) {
      currentValues[i] = (int) (startValues[i] + easedProgress * (endValues[i] - startValues[i]));
    }

    target.updateAnimation(currentValues);

    if (progress >= 1.0f) {
      timer.stop();
      if (onAnimationEnd != null) {
        onAnimationEnd.run();
      }
    }
  }
}

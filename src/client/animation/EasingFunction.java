package client.animation;

// To add new easing functions, see https://easings.net/it and copy the Math function (converted to Java)

public enum EasingFunction {
  LINEAR {
    @Override
    public float ease(float x) {
      return x;
    }
  },
  EASE_OUT_ELASTIC {
    @Override
    public float ease(float x) {
      final double c4 = (2 * Math.PI) / 3;

      if (x == 0)
        return 0;
      if (x == 1)
        return 1;

      return (float) (Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);
    }
  },
  EASE_OUT_BOUNCE {
    @Override
    public float ease(float x) {
      double d1 = 2.75;
      double n1 = 7.5625;
      double result;

      if (x < 1 / d1) {
        result = n1 * x * x;
      } else if (x < 2 / d1) {
        x -= 1.5 / d1;
        result = n1 * x * x + 0.75;
      } else if (x < 2.5 / d1) {
        x -= 2.25 / d1;
        result = n1 * x * x + 0.9375;
      } else {
        x -= 2.625 / d1;
        result = n1 * x * x + 0.984375;
      }

      return (float) result;
    }
  };

  public abstract float ease(float x);
}

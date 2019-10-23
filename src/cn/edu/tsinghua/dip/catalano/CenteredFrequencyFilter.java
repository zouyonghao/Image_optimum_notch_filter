package cn.edu.tsinghua.dip.catalano;

import Catalano.Core.IntRange;
import Catalano.Imaging.Filters.FourierTransform;
import Catalano.Math.ComplexNumber;

public class CenteredFrequencyFilter {
    private IntRange freq = new IntRange(0, 1024);

    private int centerX;
    private int centerY;

    /**
     * Initializes a new instance of the FrequencyFilter class.
     * @param centerX x of center point
     * @param centerY y of center point
     */
    public CenteredFrequencyFilter(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    /**
     * Initializes a new instance of the FrequencyFilter class.
     * @param min Minimum value for to keep.
     * @param max Maximum value for to keep.
     * @param centerX x of center point
     * @param centerY y of center point
     */
    public CenteredFrequencyFilter(int min, int max, int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.freq = new IntRange(min, max);
    }

    public void ApplyInPlace(FourierTransform fourierTransform) {
        if (!fourierTransform.isFourierTransformed()) {
            try {
                throw new Exception("the image should be fourier transformed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int width = fourierTransform.getWidth();
        int height = fourierTransform.getHeight();

        int min = freq.getMin();
        int max = freq.getMax();

        ComplexNumber[][] c = fourierTransform.getData();

        for (int i = 0; i < height; i++) {
            int y = i - centerY;

            for (int j = 0; j < width; j++) {
                int x = j - centerX;
                int d = (int) Math.sqrt(x * x + y * y);

                // filter values outside the range
                if ((d > max) || (d < min)) {
                    c[i][j].real = 0;
                    c[i][j].imaginary = 0;
                }
                // else {
                //     if (i == 229 && j == 152) {
                //         System.out.println(c[i][j]);
                //     }
                //     // System.out.printf("Get points! x = %d, y = %d\n", i, j);
                // }
            }
        }

    }
}

package a7;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageAdjusterViewWidget extends JPanel implements ChangeListener {
	private PictureView picture_view;
	private Picture p;
	private Picture newPic;
	private Pixel[][] copyArray;
	private JSlider blur_slider;
	private JSlider saturation_slider;
	private JSlider brightness_slider;
	private JPanel main_panel;
	
	public ImageAdjusterViewWidget(Picture picture) {
		p = picture;
		copyArray = new Pixel[p.getWidth()][p.getHeight()];
		copyArray = copyPictureArray(p);

		setLayout(new BorderLayout());

		picture_view = new PictureView(picture.createObservable());
		add(picture_view, BorderLayout.CENTER);

		// Panels
		main_panel = new JPanel();
		main_panel.setLayout(new GridLayout(3, 1));
		add(main_panel, BorderLayout.SOUTH);

		JPanel blur_panel = new JPanel();
		JPanel saturation_panel = new JPanel();
		JPanel brightness_panel = new JPanel();
		
		main_panel.add(blur_panel);
		main_panel.add(saturation_panel);
		main_panel.add(brightness_panel);

		// Sliders
		blur_slider = new JSlider(0, 5, 0);
		blur_slider.setPaintTicks(true);
		blur_slider.setSnapToTicks(true);
		blur_slider.setPaintLabels(true);
		blur_slider.setMajorTickSpacing(1);

		saturation_slider = new JSlider(-100, 100, 0);
		saturation_slider.setPaintTicks(true);
		saturation_slider.setSnapToTicks(true);
		saturation_slider.setPaintLabels(true);
		saturation_slider.setMajorTickSpacing(25);

		brightness_slider = new JSlider(-100, 100, 0);
		brightness_slider.setPaintTicks(true);
		brightness_slider.setSnapToTicks(true);
		brightness_slider.setPaintLabels(true);
		brightness_slider.setMajorTickSpacing(25);

		// Labels
		JLabel blurLabel = new JLabel("Blur: ");
		JLabel saturationLabel = new JLabel("Saturation: ");
		JLabel brightnessLabel = new JLabel("Brightness: ");

		blur_panel.add(blurLabel, BorderLayout.WEST);
		blur_panel.add(blur_slider, BorderLayout.CENTER);

		saturation_panel.add(saturationLabel, BorderLayout.WEST);
		saturation_panel.add(saturation_slider, BorderLayout.CENTER);

		brightness_panel.add(brightnessLabel, BorderLayout.WEST);
		brightness_panel.add(brightness_slider, BorderLayout.CENTER);

		blur_slider.addChangeListener(this);
		saturation_slider.addChangeListener(this);
		brightness_slider.addChangeListener(this);

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (!((JSlider) e.getSource()).getValueIsAdjusting()) {
			blurMethod();
			saturationMethod();
			brightnessMethod();
		}

		// Setting copyArray to original picture array
		copyArray = copyPictureArray(p);
	}
	
	//blurMethod() creates a new picture with the new blur value
	private void blurMethod() {
		int blur = blur_slider.getValue();
		ArrayList<Double> redList = new ArrayList<Double>();
		ArrayList<Double> greenList = new ArrayList<Double>();
		ArrayList<Double> blueList = new ArrayList<Double>();
		Pixel[][] newPixels = new Pixel[copyArray.length][copyArray[0].length];

		for (int i = 0; i < copyArray.length; i++) {
			for (int j = 0; j < copyArray[0].length; j++) {

				for (int l = (i - blur); l <= (i + blur); l++) {
					for (int k = (j - blur); k <= (j + blur); k++) {

						try {
							if (copyArray[l][k] != null) {
								redList.add(copyArray[l][k].getRed());
								greenList.add(copyArray[l][k].getGreen());
								blueList.add(copyArray[l][k].getBlue());
							}
						} catch (ArrayIndexOutOfBoundsException e) {

						}
					}
				}
				// Calculating the average of each of the components of the lists
				double average_red = average(redList);
				double average_green = average(greenList);
				double average_blue = average(blueList);

				// Clearing lists
				redList.clear();
				greenList.clear();
				blueList.clear();

				newPixels[i][j] = new ColorPixel(average_red, average_green, average_blue);

			}
		}

		copyArray = newPixels.clone();
		newPic = new MutablePixelArrayPicture(copyArray, p.getCaption());
		picture_view.setPicture(newPic.createObservable());

	}
	
	//saturationMethod() creates a new picture with the new saturation value
	private void saturationMethod() {
		double pixel_brightness;
		double red_c = -1;
		double blue_c = -1;
		double green_c = -1;
		Pixel[][] newPixelArray = new Pixel[p.getWidth()][p.getHeight()];
		double saturation = saturation_slider.getValue();

		if (saturation <= 0) {

			for (int i = 0; i < copyArray.length; i++) {
				for (int j = 0; j < copyArray[0].length; j++) {

					pixel_brightness = copyArray[i][j].getIntensity(); // Getting pixel brightness

					// Calculating each of the components of the pixel. Saturation from -100 to 0
					// new = old * (1.0 + (f / 100.0) ) - (b * f / 100.0)
					red_c = copyArray[i][j].getRed() * (1.0 + (saturation / 100.0))
							- (pixel_brightness * saturation / 100.0);
					blue_c = copyArray[i][j].getBlue() * (1.0 + (saturation / 100.0))
							- (pixel_brightness * saturation / 100.0);
					green_c = copyArray[i][j].getGreen() * (1.0 + (saturation / 100.0))
							- (pixel_brightness * saturation / 100.0);

					newPixelArray[i][j] = new ColorPixel(red_c, green_c, blue_c);
				}
			}
		} else {

			for (int i = 0; i < copyArray.length; i++) {
				for (int j = 0; j < copyArray[0].length; j++) {

					// Finding largest component of the pixel (lc)
					double lc = -1;
					lc = (copyArray[i][j].getRed() > copyArray[i][j].getBlue()) ? copyArray[i][j].getRed()
							: copyArray[i][j].getBlue();
					lc = (lc > copyArray[i][j].getGreen()) ? lc : copyArray[i][j].getGreen();

					// Calculating each of the components of a pixel. Saturation from 0 to 100
					// new = old * ((lc + ((1.0 - lc) * (f / 100.0))) / lc)
					if ((lc - 0.0) >= 0.01) {
						red_c = copyArray[i][j].getRed() * ((lc + ((1.0 - lc) * (saturation / 100.0))) / lc);
						blue_c = copyArray[i][j].getBlue() * ((lc + ((1.0 - lc) * (saturation / 100.0))) / lc);
						green_c = copyArray[i][j].getGreen() * ((lc + ((1.0 - lc) * (saturation / 100.0))) / lc);
					} else {
						red_c = copyArray[i][j].getRed();
						blue_c = copyArray[i][j].getBlue();
						green_c = copyArray[i][j].getGreen();
					}

					newPixelArray[i][j] = new ColorPixel(red_c, green_c, blue_c);
				}
			}

		}

		copyArray = newPixelArray.clone();
		newPic = new MutablePixelArrayPicture(copyArray, p.getCaption());
		picture_view.setPicture(newPic.createObservable());
	}

	// brightnessMethod() creates a new picture with the new brightness value
	private void brightnessMethod() {
		double brightness = brightness_slider.getValue();

		for (int i = 0; i < copyArray.length; i++) {
			for (int j = 0; j < copyArray[0].length; j++) {

				if (brightness > 0) {
					copyArray[i][j] = copyArray[i][j].lighten(brightness / 100.0);
				}
				if (brightness < 0) {
					copyArray[i][j] = copyArray[i][j].darken(Math.abs(brightness) / 100.0);
				}
			}
		}
		newPic = new MutablePixelArrayPicture(copyArray, p.getCaption());
		picture_view.setPicture(newPic.createObservable());

	}

	// copyPictureArray(Picture p) returns a copy of the original picture array
	// passed as a parameter
	private Pixel[][] copyPictureArray(Picture p) {
		Pixel[][] newPixelArray = new Pixel[p.getWidth()][p.getHeight()];
		for (int i = 0; i < p.getWidth(); i++) {
			for (int j = 0; j < p.getHeight(); j++) {
				newPixelArray[i][j] = p.getPixel(i, j);
			}
		}

		return newPixelArray;
	}

	// average(ArrayList<Double> a) returns the average of the ArrayList passed as a
	// parameter
	private double average(ArrayList<Double> a) {
		double sum = 0;
		double average = 0;
		for (int i = 0; i < a.size(); i++) {
			sum = sum + a.get(i);
		}
		average = sum / a.size();
		return average;
	}
}

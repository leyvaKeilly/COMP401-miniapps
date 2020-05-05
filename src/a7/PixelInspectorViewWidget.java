package a7;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelInspectorViewWidget extends JPanel implements MouseListener {
	private PictureView picture_view;
	private JLabel xLabel;
	private JLabel yLabel;
	private JLabel redLabel;
	private JLabel blueLabel;
	private JLabel greenLabel;
	private JLabel brightnessLabel;
	private JPanel pixel_info_panel;

	public PixelInspectorViewWidget(Picture picture) {
		setLayout(new BorderLayout());

		picture_view = new PictureView(picture.createObservable());
		picture_view.addMouseListener(this);
		add(picture_view, BorderLayout.CENTER);

		// Pixel_info panel
		pixel_info_panel = new JPanel();
		pixel_info_panel.setLayout(new GridLayout(6, 1));

		// Labels
		xLabel = new JLabel("X: ");
		pixel_info_panel.add(xLabel);

		yLabel = new JLabel("Y: ");
		pixel_info_panel.add(yLabel);

		redLabel = new JLabel("Red: ");
		pixel_info_panel.add(redLabel);

		greenLabel = new JLabel("Green: ");
		pixel_info_panel.add(greenLabel);

		blueLabel = new JLabel("Blue: ");
		pixel_info_panel.add(blueLabel);

		brightnessLabel = new JLabel("Brightness: ");
		pixel_info_panel.add(brightnessLabel);

		add(pixel_info_panel, BorderLayout.WEST);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("You clicked on the frame at: " + e.getX() + "," + e.getY());
	}

	// displays the (x,y) coordinates; red, blue, green components; and brightness
	// of the pixel pressed
	@Override
	public void mousePressed(MouseEvent e) {
		DecimalFormat df = new DecimalFormat("0.00"); // Formating output to two decimal places
		xLabel.setText("X: " + e.getX());
		yLabel.setText("Y: " + e.getY());
		redLabel.setText(
				String.format("Red: %s", df.format(picture_view.getPicture().getPixel(e.getX(), e.getY()).getRed())));
		greenLabel.setText(String.format("Green: %s",
				df.format(picture_view.getPicture().getPixel(e.getX(), e.getY()).getGreen())));
		blueLabel.setText(
				String.format("Blue: %s", df.format(picture_view.getPicture().getPixel(e.getX(), e.getY()).getBlue())));
		brightnessLabel.setText(String.format("Brightness: %s",
				df.format(picture_view.getPicture().getPixel(e.getX(), e.getY()).getIntensity())));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}

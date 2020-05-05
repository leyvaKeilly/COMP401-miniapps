package a7;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FramePuzzleViewWidget extends JPanel implements MouseListener, KeyListener {
	static final int PUZZLE_DIMS = 7;
	private Picture p;
	private Picture whitePic;
	private SubPicture[][] subPics;
	private PictureView[][] picViewArray;
	private int xBlankIndex = (PUZZLE_DIMS - 1);
	private int yBlankIndex = (PUZZLE_DIMS - 1);

	public FramePuzzleViewWidget(Picture picture) {
		p = picture;
		subPics = new SubPicture[PUZZLE_DIMS][PUZZLE_DIMS];
		picViewArray = new PictureView[PUZZLE_DIMS][PUZZLE_DIMS];
		subPics = extractingSubPics(p);

		setLayout(new GridLayout(PUZZLE_DIMS, PUZZLE_DIMS));

		// adding PictureViews to the layout
		for (int j = 0; j < PUZZLE_DIMS; j++) {
			for (int i = 0; i < PUZZLE_DIMS; i++) {
				picViewArray[i][j] = new PictureView(subPics[i][j].createObservable());
				picViewArray[i][j].addMouseListener(this);
				add(picViewArray[i][j]);
			}
		}

		// Creating whiteColor picture in bottom right corner of puzzle
		whitePic = new ImmutablePixelArrayPicture(whitePixelArray(), p.getCaption());
		picViewArray[PUZZLE_DIMS - 1][PUZZLE_DIMS - 1].setPicture(whitePic.createObservable());

		this.setFocusable(true);
		this.grabFocus();
		this.addKeyListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

		int x_pic_index = -1;
		int y_pic_index = -1;
		int coordinateInLine = -1;

		// Getting position of picture pressed
		for (int i = 0; i < picViewArray.length; i++) {
			for (int j = 0; j < picViewArray[0].length; j++) {
				if (e.getSource() == picViewArray[i][j]) {
					x_pic_index = i;
					y_pic_index = j;
				}
			}
		}

		if ((x_pic_index == xBlankIndex) || (y_pic_index == yBlankIndex)) {

			if (x_pic_index == xBlankIndex) {
				coordinateInLine = x_pic_index;

				if (y_pic_index < yBlankIndex) {
					for (int j = yBlankIndex; j > y_pic_index; j--) {
						picViewArray[coordinateInLine][j]
								.setPicture(picViewArray[coordinateInLine][j - 1].getPicture());
					}
				} else {
					for (int j = yBlankIndex; j < y_pic_index; j++) {
						picViewArray[coordinateInLine][j]
								.setPicture(picViewArray[coordinateInLine][j + 1].getPicture());
					}
				}
			} else if (y_pic_index == yBlankIndex) {
				coordinateInLine = y_pic_index;

				if (x_pic_index < xBlankIndex) {
					for (int i = xBlankIndex; i > x_pic_index; i--) {
						picViewArray[i][coordinateInLine]
								.setPicture(picViewArray[i - 1][coordinateInLine].getPicture());
					}
				} else {
					for (int i = xBlankIndex; i < x_pic_index; i++) {
						picViewArray[i][coordinateInLine]
								.setPicture(picViewArray[i + 1][coordinateInLine].getPicture());
					}
				}
			}

			xBlankIndex = x_pic_index;
			yBlankIndex = y_pic_index;
			picViewArray[xBlankIndex][yBlankIndex].setPicture(whitePic.createObservable());
		}

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

	@Override
	public void keyPressed(KeyEvent e) {
		// Note: Press Tab to grab focus
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			if ((xBlankIndex + 1) < PUZZLE_DIMS) {
				picViewArray[xBlankIndex][yBlankIndex]
						.setPicture(picViewArray[xBlankIndex + 1][yBlankIndex].getPicture());
				xBlankIndex++;
				picViewArray[xBlankIndex][yBlankIndex].setPicture(whitePic.createObservable());
			}
			break;

		case KeyEvent.VK_LEFT:
			if ((xBlankIndex - 1) >= 0) {
				picViewArray[xBlankIndex][yBlankIndex]
						.setPicture(picViewArray[xBlankIndex - 1][yBlankIndex].getPicture());
				xBlankIndex--;
				picViewArray[xBlankIndex][yBlankIndex].setPicture(whitePic.createObservable());
			}
			break;

		case KeyEvent.VK_DOWN:
			if ((yBlankIndex + 1) < PUZZLE_DIMS) {
				picViewArray[xBlankIndex][yBlankIndex]
						.setPicture(picViewArray[xBlankIndex][yBlankIndex + 1].getPicture());
				yBlankIndex++;
				picViewArray[xBlankIndex][yBlankIndex].setPicture(whitePic.createObservable());
			}
			break;

		case KeyEvent.VK_UP:
			if ((yBlankIndex - 1) >= 0) {
				picViewArray[xBlankIndex][yBlankIndex]
						.setPicture(picViewArray[xBlankIndex][yBlankIndex - 1].getPicture());
				yBlankIndex--;
				picViewArray[xBlankIndex][yBlankIndex].setPicture(whitePic.createObservable());
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	// extractingSubPics(Picture p) returns subPics from original picture to fill a
	// puzzle of defined dimensions
	private SubPicture[][] extractingSubPics(Picture p) {
		SubPicture[][] pArray = new SubPicture[PUZZLE_DIMS][PUZZLE_DIMS];
		int setWidth = (int) (p.getWidth() / PUZZLE_DIMS);
		int setHeight = (int) (p.getHeight() / PUZZLE_DIMS);
		int xRemainder = ((p.getWidth()) % PUZZLE_DIMS);
		int yRemainder = ((p.getHeight()) % PUZZLE_DIMS);
		int xoffset = 0;
		int yoffset = 0;

		for (int j = 0; j < PUZZLE_DIMS; j++) {
			int height = setHeight;
			if (yRemainder > 0) {
				height++;
				yRemainder--;
			}
			for (int i = 0; i < PUZZLE_DIMS; i++) {
				int width = setWidth;
				if (xRemainder > 0) {
					width++;
					xRemainder--;
				}
				pArray[i][j] = new SubPictureImpl(p, xoffset, yoffset, width, height);
				xoffset = xoffset + width;
			}
			yoffset = yoffset + height;
			xoffset = 0;
		}
		return pArray;
	}

	// whitePixelArray() returns a white pixel array
	private Pixel[][] whitePixelArray() {
		Pixel whiteColor = new ColorPixel(1, 1, 1);
		Pixel[][] blank_pixel = new Pixel[subPics[PUZZLE_DIMS - 1][PUZZLE_DIMS - 1]
				.getWidth()][subPics[PUZZLE_DIMS - 1][PUZZLE_DIMS - 1].getHeight()];
		for (int i = 0; i < blank_pixel.length; i++) {
			for (int j = 0; j < blank_pixel[0].length; j++) {
				blank_pixel[i][j] = whiteColor;
			}
		}

		return blank_pixel;
	}

}

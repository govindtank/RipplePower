package org.ripple.power.ui.graphics.chart;

import java.awt.Frame;
import java.util.ArrayList;

public class LineChartCanvas extends ChartBaseCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<LineChartCanvas> mJoinLines = new ArrayList<LineChartCanvas>(100);

	private ArrayList<ChartValueSerie> mSeries = new ArrayList<ChartValueSerie>();
	private int mXnum = 0;
	private int mLabelMaxNum = 10;

	private Paint mPnt = new Paint();

	public LineChartCanvas(Frame frame) {
		super(frame);
	}

	public LineChartCanvas(int w, int h) {
		super(w, h);
	}

	public void onDraw(Canvas cnv) {

		if ((mBmp == null) || (bRedraw)) {

			getViewSizes();
			getXYminmax();
			if (p_yscale_auto) {
				calcYgridRange();
			}
			calcXYcoefs();
			if (mBmp == null) {
				mBmp = Bitmap.createBitmap(p_width, p_height);
			}
			if (mCnv == null) {
				mCnv = new Canvas(mBmp);
			}
			if (p_grid_vis) {
				drawGrid();
			}
			if (p_xtext_vis) {
				drawXlabel();
			}
			if (p_ytext_vis) {
				drawYlabel();
			}
			if (p_border_vis) {
				drawBorder();
			}
			if (p_axis_vis) {
				drawAxis();
			}
			drawData();
			if (mJoinLines.size() > 0) {
				for (LineChartCanvas c : mJoinLines) {
					if (c != null) {
						c.drawLine(mCnv);
					}
				}
			}
			bRedraw = false;
		}

		cnv.drawBitmap(mBmp, 0, 0);
	}

	public void clearSeries() {
		while (mSeries.size() > 0) {
			mSeries.remove(0);
		}
		bRedraw = true;

	}

	public void addSerie(ChartValueSerie serie) {
		mSeries.add(serie);
		bRedraw = true;

	}

	public ArrayList<ChartValueSerie> getSeries() {
		return mSeries;
	}

	public void setLineVis(int index, boolean show) {
		mSeries.get(index).setVisible(show);
		bRedraw = true;

	}

	public void setLineStyle(int index, int color, float size) {
		mSeries.get(index).setStyle(color, size);
		bRedraw = true;

	}

	public void setLineStyle(int index, int color, float size, boolean usedip) {
		mSeries.get(index).setStyle(color, size, usedip);
		bRedraw = true;

	}

	public void setLabelMaxNum(int maxnum) {
		if (maxnum <= 0) {
			return;
		}
		mLabelMaxNum = maxnum;
		bRedraw = true;

	}

	public void joinLine(LineChartCanvas line) {
		mJoinLines.add(line);
	}

	public void joinRemove(LineChartCanvas line) {
		mJoinLines.remove(line);
	}

	protected void getXYminmax() {
		ChartValueSerie serie;
		for (int ii = 0; ii < mSeries.size(); ii++) {
			serie = mSeries.get(ii);
			if (ii == 0) {
				mXnum = serie.getSize();
				mYmin = serie.mYmin;
				mYmax = serie.mYmax;
			} else {
				if (serie.getSize() > mXnum)
					mXnum = serie.getSize();
				if (serie.mYmin < mYmin)
					mYmin = serie.mYmin;
				if (serie.mYmax > mYmax)
					mYmax = serie.mYmax;
			}
		}
	}

	private void drawLine(Canvas c) {
		getViewSizes();
		getXYminmax();
		if (p_yscale_auto) {
			calcYgridRange();
		}
		calcXYcoefs();
		float pY;
		boolean pValid;
		for (ChartValueSerie serie : mSeries) {
			if (serie.isVisible()) {
				mPnt.reset();
				mPnt.setStyle(Style.STROKE);
				mPnt.setColor(serie.mColor);
				if (serie.mUseDip) {
					mPnt.setStrokeWidth(dipToPixel(serie.mWidth));
				} else {
					mPnt.setStrokeWidth(serie.mWidth);
				}
				mPnt.setAntiAlias(true);
				pValid = false;
				mPath.reset();
				for (int ii = 0; ii < serie.mPointList.size(); ii++) {
					pY = serie.mPointList.get(ii).y;
					if (Float.isNaN(pY)) {
						pValid = false;
					} else if (!pValid) {
						mPath.moveTo(sX + bX + ii * aX, eY - (pY - bY) * aY);
						pValid = true;
					} else {
						mPath.lineTo(sX + bX + ii * aX, eY - (pY - bY) * aY);
					}
				}
				c.drawPath(mPath, mPnt);
			}
		}
	}

	protected void drawData() {
		float pY;
		boolean pValid;
		for (ChartValueSerie serie : mSeries) {
			if (serie.isVisible()) {
				mPnt.reset();
				mPnt.setStyle(Style.STROKE);
				mPnt.setColor(serie.mColor);
				if (serie.mUseDip) {
					mPnt.setStrokeWidth(dipToPixel(serie.mWidth));
				} else
					mPnt.setStrokeWidth(serie.mWidth);
				mPnt.setAntiAlias(true);
				pValid = false;
				mPath.reset();
				for (int ii = 0; ii < serie.mPointList.size(); ii++) {
					pY = serie.mPointList.get(ii).y;
					if (Float.isNaN(pY)) {
						pValid = false;
					} else if (!pValid) {
						mPath.moveTo(sX + bX + ii * aX, eY - (pY - bY) * aY);
						pValid = true;
					} else {
						mPath.lineTo(sX + bX + ii * aX, eY - (pY - bY) * aY);
					}
				}
				mCnv.drawPath(mPath, mPnt);
			}
		}
	}

	protected void calcXYcoefs() {
		aX = (float) dX / mXnum;
		bX = (float) aX / 2;
		aY = (float) dY / Math.abs(mYmaxGrid - mYminGrid);
		bY = (float) mYminGrid;
	}

	protected void drawXlabel() {
		mPntText.setTextAlign(Align.CENTER);
		mPath.reset();
		ChartValueSerie mLabel = mSeries.get(0);
		String label;
		int numlab = mLabel.getSize();
		int numdiv = 1 + (numlab - 1) / mLabelMaxNum;
		if (p_xtext_bottom) {
			for (int ii = 0; ii < mLabel.getSize(); ii++) {
				mPath.moveTo(sX + bX + ii * aX, eY - 3);
				mPath.lineTo(sX + bX + ii * aX, eY + 3);
				label = mLabel.mPointList.get(ii).t;
				if ((label != null) && (ii < numlab) && ((ii % numdiv) == 0))
					mCnv.drawText(label, sX + bX + ii * aX, eY + p_text_size + 2, mPntText);
			}
		} else {
			for (int ii = 0; ii < mLabel.getSize(); ii++) {
				mPath.moveTo(sX + bX + ii * aX, sY - 3);
				mPath.lineTo(sX + bX + ii * aX, sY + 3);
				label = mLabel.mPointList.get(ii).t;
				if ((label != null) && (ii < numlab) && ((ii % numdiv) == 0))
					mCnv.drawText(label, sX + bX + ii * aX, sY - p_text_size + 3, mPntText);
			}
		}
		mCnv.drawPath(mPath, mPntAxis);
	}

}

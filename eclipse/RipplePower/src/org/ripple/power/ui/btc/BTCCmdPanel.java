package org.ripple.power.ui.btc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.OtherData;
import org.ripple.power.txns.Updateable;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.utils.GraphicsUtils;

public class BTCCmdPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon blocks = new ImageIcon(new LImage("icons/web.png").scaledInstance(48, 48).getBufferedImage());

	private ImageIcon wallet = new ImageIcon(new LImage("icons/wallet.png").scaledInstance(48, 48).getBufferedImage());

	private ImageIcon brain = new ImageIcon(new LImage("icons/safe.png").scaledInstance(48, 48).getBufferedImage());

	private ImageIcon exchange = new ImageIcon(new LImage("icons/post.png").scaledInstance(48, 48).getBufferedImage());

	private BTCPricePanel price;
	private LineChartCanvas btcChartCanvas;
	private ChartValueSerie btcChart = new ChartValueSerie(LColor.red, 1);
	private int frameWidth = 1;
	private int frameHeight = 450;
	private boolean isRunning;

	public void stop() {
		isRunning = false;
		if (price != null) {
			price.stop();
		}
	}

	public void start() {
		isRunning = true;
		if (price != null) {
			price.start();
		}
	}

	public BTCCmdPanel() {
		super(null);
		frameWidth = LSystem.applicationMain.getWidth();
		frameHeight = LSystem.applicationMain.getHeight() - 200;

		setPreferredSize(new Dimension(frameWidth, frameHeight));
		setSize(new Dimension(frameWidth, frameHeight));
		setBackground(UIConfig.dialogbackground);

		price = new BTCPricePanel();
		add(price);

		RPCButton downloadBlockButton = new RPCButton(blocks);
		downloadBlockButton.setText("Download Blocks");
		downloadBlockButton.setFont(GraphicsUtils.getFont(LangConfig.getFontName(), 1, 20));
		downloadBlockButton.setBounds(30, price.getHeight() + 50, 320, 100);
		downloadBlockButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BTCLoader.start(new String[] {});
				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {

						DownloadBlocksDialog.showDialog("Download Blocks", LSystem.applicationMain);
					}
				});

			}
		});
		add(downloadBlockButton);

		RPCButton walletButton = new RPCButton(wallet);
		walletButton.setText("Bitcoin Wallet");
		walletButton.setFont(GraphicsUtils.getFont(LangConfig.getFontName(), 1, 20));
		walletButton.setBounds(downloadBlockButton.getX() + downloadBlockButton.getWidth() + 20, price.getHeight() + 50,
				320, 100);
		walletButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				BTCLoader.start(new String[] {});
				LSystem.invokeLater(new Runnable() {

					@Override
					public void run() {

						BitcoinWalletDialog.showDialog("Bitcoin Wallet", LSystem.applicationMain);
					}
				});

			}
		});
		add(walletButton);

		// Brain wallet hacker
		/**
		 * BTC transactions for monitoring, found the wallet in line
		 * dictionaries brain immediately send to your address......
		 */
		RPCButton brainButton = new RPCButton(brain);
		brainButton.setText("Brain wallet hacker");
		brainButton.setFont(GraphicsUtils.getFont(LangConfig.getFontName(), 1, 20));
		brainButton.setBounds(walletButton.getX() + walletButton.getWidth() + 20, price.getHeight() + 50, 320, 100);
		add(brainButton);

		// Exchange BTC to XRP
		RPCButton toXrpButton = new RPCButton(exchange);
		toXrpButton.setText("Exchange BTC to XRP");
		toXrpButton.setFont(GraphicsUtils.getFont(LangConfig.getFontName(), 1, 20));
		toXrpButton.setBounds(brainButton.getX() + brainButton.getWidth() + 20, price.getHeight() + 50, 320, 100);
		add(toXrpButton);

		final int width = frameWidth - price.getWidth() - 70;

		if (!isRunning) {
			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {

					btcChartCanvas = addChart(btcChartCanvas, width, 410, btcChart);
					btcChartCanvas.setLocation(frameWidth - width - 70, 15);
					add(btcChartCanvas);
					for (;;) {
						if (BTCLoader.shutdown) {
							try {
								addData(btcChart, 1, "bitcoin");
								addChart(btcChartCanvas, width, 410, btcChart);
							} catch (Exception ex) {
							}
							LSystem.sleep(LSystem.SECOND);
						} else {
							LSystem.sleep(LSystem.SECOND * 10);
						}
					}
				}
			};
			LSystem.postThread(update);
			isRunning = true;
		}

	}

	private LineChartCanvas addChart(LineChartCanvas canvas, int w, int h, ChartValueSerie my) {
		if (canvas == null) {
			canvas = new LineChartCanvas(w, h);
			canvas.setTextVis(false, false, true, true);
			canvas.setAxisVis(false);
			canvas.setBackground(UIConfig.background);
			canvas.addSerie(my);
		} else {
			final LineChartCanvas tmp = canvas;
			LSystem.invokeLater(new Runnable() {

				@Override
				public void run() {
					BTCCmdPanel.this.repaint();
					tmp.validate();
					tmp.repaint();
				}
			});

		}
		return canvas;
	}

	private void addData(ChartValueSerie chart, int day, String cur) throws Exception {
		ArrayMap arrays = OtherData.getCapitalization(day, cur);
		if (arrays != null && arrays.size() > 0) {
			chart.clearPointList();
			for (int i = 0; i < arrays.size(); i++) {
				if (i < arrays.size()) {
					String key = (String) arrays.getKey(i);
					chart.addPoint(new ChartValue(key, Float.parseFloat((String) arrays.getValue(key))));
				}
			}
		}
	}

}

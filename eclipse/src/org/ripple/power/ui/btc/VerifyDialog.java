package org.ripple.power.ui.btc;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.ripple.power.txns.btc.ECKey;
import org.ripple.power.txns.btc.SignatureException;
import org.ripple.power.ui.errors.ErrorLog;
import org.ripple.power.ui.view.ButtonPane;

public class VerifyDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JTextField addressField;

	private final JTextArea messageField;

	private final JScrollPane scrollPane;

	private final JTextField signatureField;

	public VerifyDialog(JFrame parent) {
		super(parent, "Verify Message", Dialog.ModalityType.DOCUMENT_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addressField = new JTextField("", 34);
		JPanel addressPane = new JPanel();
		addressPane.add(new JLabel("Address  ", JLabel.RIGHT));
		addressPane.add(addressField);
		messageField = new JTextArea(6, 70);
		messageField.setLineWrap(true);
		messageField.setWrapStyleWord(true);
		messageField.setFont(addressField.getFont());
		scrollPane = new JScrollPane(messageField);
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel("Message  ", JLabel.RIGHT));
		messagePane.add(scrollPane);
		signatureField = new JTextField("", 70);
		JPanel signaturePane = new JPanel();
		signaturePane.add(new JLabel("Signature  ", JLabel.RIGHT));
		signaturePane.add(signatureField);
		JPanel buttonPane = new ButtonPane(this, 10, new String[] { "Verify",
				"verify" }, new String[] { "Done", "done" });
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(true);
		contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		contentPane.add(addressPane);
		contentPane.add(Box.createVerticalStrut(15));
		contentPane.add(messagePane);
		contentPane.add(Box.createVerticalStrut(15));
		contentPane.add(signaturePane);
		contentPane.add(Box.createVerticalStrut(15));
		contentPane.add(buttonPane);
		setContentPane(contentPane);
	}

	public static void showDialog(JFrame parent) {
		try {
			JDialog dialog = new VerifyDialog(parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			ErrorLog.logException("Exception while displaying dialog", exc);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			String action = ae.getActionCommand();
			switch (action) {
			case "verify":
				String message = messageField.getText();
				String address = addressField.getText();
				String signature = signatureField.getText();
				if (address.length() == 0) {
					JOptionPane.showMessageDialog(this,
							"You must enter the signing address", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else if (message.length() == 0) {
					JOptionPane.showMessageDialog(this,
							"You must enter the message text to verify",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else if (signature.length() == 0) {
					JOptionPane.showMessageDialog(this,
							"You must enter the message signature", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (ECKey.verifyMessage(address, message, signature)) {
						JOptionPane.showMessageDialog(this,
								"The signature is valid", "Valid Signature",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this,
								"The signature is not valid",
								"Invalid Signature", JOptionPane.ERROR_MESSAGE);
					}
				}
				break;
			case "done":
				setVisible(false);
				dispose();
				break;
			}
		} catch (SignatureException exc) {
			JOptionPane.showMessageDialog(this, "The signature is not valid",
					"Invalid Signature", JOptionPane.ERROR_MESSAGE);
		} catch (Exception exc) {
			ErrorLog.logException("Exception while processing action event",
					exc);
		}
	}
}

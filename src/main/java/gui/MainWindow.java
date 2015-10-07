/*
 * EML to PDF Converter
 * Copyright (C) 2015 Nick Russler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import cli.Main;

import com.google.common.base.Strings;
import com.google.common.io.Files;

/**
 * Main Window GUI Class.
 *
 * @author Nick Russler
 */
public class MainWindow {

	private JFrame frmEmlToPdf;
	private JTextField textField;
	private JLabel lblConvertingEmail;
	private JProgressBar progressBar;
	private JButton btnStartConversion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// ignore error
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmEmlToPdf.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEmlToPdf = new JFrame();
		frmEmlToPdf.setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/icons/logo_emlconverter-482_16x16.png")));
		frmEmlToPdf.setResizable(false);
		frmEmlToPdf.setTitle("EML to PDF/HTML Converter");
		frmEmlToPdf.getContentPane().setBackground(Color.WHITE);
		frmEmlToPdf.setBackground(Color.WHITE);
		frmEmlToPdf.setBounds(100, 100, 700, 703);
		frmEmlToPdf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEmlToPdf.getContentPane().setLayout(null);
		frmEmlToPdf.setLocationRelativeTo(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 507, 210);
		frmEmlToPdf.getContentPane().add(scrollPane);
		scrollPane.setBorder(new TitledBorder(null, "EML Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane.setBackground(Color.WHITE);

		final JList<String> list = new JList<String>();
		scrollPane.setViewportView(list);
		final DefaultListModel<String> listModel = new DefaultListModel<String>();
		list.setModel(listModel);
		list.setBackground(new Color(255, 255, 255));
		list.setBorder(null);
		listModel.addListDataListener(new ListDataListener() {
			public void contentsChanged() {
				lblConvertingEmail.setText("Email 0 of " + listModel.getSize());
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				contentsChanged();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				contentsChanged();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				contentsChanged();
			}
		});

		final JFileChooser dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		final JButton btnAddFolder = new JButton("Add Folder");
		btnAddFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dirChooser.showOpenDialog(frmEmlToPdf) == JFileChooser.APPROVE_OPTION) {
					btnAddFolder.setEnabled(false);
					frmEmlToPdf.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							for (File f : Files.fileTreeTraverser().preOrderTraversal(dirChooser.getSelectedFile())) {
								if (f.getName().endsWith(".eml")) {
									listModel.addElement(f.getAbsolutePath());
								}
							}

							btnAddFolder.setEnabled(true);
							frmEmlToPdf.setCursor(Cursor.getDefaultCursor());
						}
					});
				}
			}
		});
		btnAddFolder.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/folder_add.png")));
		btnAddFolder.setBounds(527, 72, 140, 34);
		frmEmlToPdf.getContentPane().add(btnAddFolder);

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);

		JButton btnAddFile = new JButton("Add File(s)");
		btnAddFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/email_add.png")));
		btnAddFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(frmEmlToPdf) == JFileChooser.APPROVE_OPTION) {
					for (File f : fileChooser.getSelectedFiles()) {
						listModel.addElement(f.getAbsolutePath());
					}
				}
			}
		});
		btnAddFile.setBounds(527, 36, 140, 34);
		frmEmlToPdf.getContentPane().add(btnAddFile);

		JButton btnClearList = new JButton("Clear List");
		btnClearList.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/cross.png")));
		btnClearList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.clear();
			}
		});
		btnClearList.setBounds(527, 154, 140, 34);
		frmEmlToPdf.getContentPane().add(btnClearList);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 232, 664, 100);
		frmEmlToPdf.getContentPane().add(panel);
		panel.setLayout(null);

		final JCheckBox chckbxAddEmailHeaders = new JCheckBox("Add Email headers to the PDF/HTML document");
		chckbxAddEmailHeaders.setSelected(true);
		chckbxAddEmailHeaders.setBackground(Color.WHITE);
		chckbxAddEmailHeaders.setBounds(10, 17, 270, 23);
		panel.add(chckbxAddEmailHeaders);

		final JRadioButton rdbtnOutputPDF = new JRadioButton("Output PDF");
		rdbtnOutputPDF.setSelected(true);
		rdbtnOutputPDF.setBackground(Color.WHITE);
		rdbtnOutputPDF.setBounds(280, 17, 100, 23);
		panel.add(rdbtnOutputPDF);
		final JRadioButton rdbtnOutputHTML = new JRadioButton("Output HTML");
		rdbtnOutputHTML.setBackground(Color.WHITE);
		rdbtnOutputHTML.setBounds(380, 17, 100, 23);
		panel.add(rdbtnOutputHTML);
		{ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnOutputPDF);
		bg.add(rdbtnOutputHTML);}

		final JRadioButton rdbtnAutomaticProxySelection = new JRadioButton("Automatic Proxy Selection");
		rdbtnAutomaticProxySelection.setSelected(true);
		rdbtnAutomaticProxySelection.setEnabled(false);
		rdbtnAutomaticProxySelection.setBackground(Color.WHITE);
		rdbtnAutomaticProxySelection.setBounds(98, 43, 180, 23);
		panel.add(rdbtnAutomaticProxySelection);

		textField = new JTextField();
		textField.setEnabled(false);
		textField.setBounds(352, 44, 290, 20);
		panel.add(textField);
		textField.setColumns(10);

		final JRadioButton rdbtnManual = new JRadioButton("Manual");
		rdbtnManual.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				textField.setEnabled(rdbtnManual.isSelected());
			}
		});
		rdbtnManual.setEnabled(false);
		rdbtnManual.setBackground(Color.WHITE);
		rdbtnManual.setBounds(280, 43, 66, 23);
		panel.add(rdbtnManual);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnManual);
		bg.add(rdbtnAutomaticProxySelection);

		final JCheckBox chckbxUseProxy = new JCheckBox("Use Proxy");
		chckbxUseProxy.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				rdbtnAutomaticProxySelection.setEnabled(chckbxUseProxy.isSelected());
				rdbtnManual.setEnabled(chckbxUseProxy.isSelected());

				if (!chckbxUseProxy.isSelected()) {
					textField.setEnabled(false);
				}
			}
		});
		chckbxUseProxy.setBackground(Color.WHITE);
		chckbxUseProxy.setBounds(10, 43, 86, 23);
		panel.add(chckbxUseProxy);

		final JCheckBox chckbxExtractAttachments = new JCheckBox("Extract Attachments");
		chckbxExtractAttachments.setBackground(Color.WHITE);
		chckbxExtractAttachments.setBounds(10, 69, 180, 23);
		panel.add(chckbxExtractAttachments);

		JPanel panelProgress = new JPanel();
		panelProgress.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelProgress.setBackground(Color.WHITE);
		panelProgress.setBounds(10, 343, 664, 127);
		frmEmlToPdf.getContentPane().add(panelProgress);
		panelProgress.setLayout(null);

		btnStartConversion = new JButton("Start Conversion");
		btnStartConversion.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/arrow_refresh.png")));
		btnStartConversion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStartConversion.setEnabled(false);
				frmEmlToPdf.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				String proxyTmp = null;

				if (chckbxUseProxy.isSelected()) {
					if (rdbtnAutomaticProxySelection.isSelected()) {
						proxyTmp = "auto";
					} else {
						proxyTmp = textField.getText();
					}
				}

				final String proxy = proxyTmp;
				new Thread(new Runnable() {
					@Override
					public void run() {
						startConversion(Collections.list(listModel.elements()), rdbtnOutputHTML.isSelected(), chckbxAddEmailHeaders.isSelected(), proxy, chckbxExtractAttachments.isSelected());
					}
				}).start();
			}
		});
		btnStartConversion.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnStartConversion.setBounds(117, 75, 425, 41);
		panelProgress.add(btnStartConversion);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 32, 644, 32);
		panelProgress.add(progressBar);

		lblConvertingEmail = new JLabel("Email 0 of 0");
		lblConvertingEmail.setBounds(12, 8, 400, 14);
		panelProgress.add(lblConvertingEmail);

		JButton btnRemoveSelectedFrom = new JButton("Remove selected");
		btnRemoveSelectedFrom.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/email_delete.png")));
		btnRemoveSelectedFrom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (String s : list.getSelectedValuesList()) {
					listModel.removeElement(s);
				}
			}
		});
		btnRemoveSelectedFrom.setBounds(527, 117, 140, 34);
		frmEmlToPdf.getContentPane().add(btnRemoveSelectedFrom);

		JScrollPane scrollPaneLog = new JScrollPane();
		scrollPaneLog.setBounds(10, 481, 664, 183);
		scrollPaneLog.setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPaneLog.setBackground(Color.WHITE);
		frmEmlToPdf.getContentPane().add(scrollPaneLog);

		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		textArea.setForeground(Color.GREEN);
		textArea.setBackground(Color.BLACK);
		textArea.setEditable(false);
		scrollPaneLog.setViewportView(textArea);

		MessageConsole mc = new MessageConsole(textArea);
		try {
			mc.redirectOut(Color.GREEN, null);
			mc.redirectErr(Color.RED, null);
		} catch (UnsupportedEncodingException e1) {
		}

		mc.setContainer(scrollPaneLog);
		mc.setMessageLines(1000);
	}

	/**
	 * Start converting the eml files.
	 *
	 * @param enumeration
	 */
	private void startConversion(List<String> l, boolean outputHTML, boolean showHeaders, String proxy, boolean extractAttachments) {
		try {
			ArrayList<String> argsOptions = new ArrayList<String>();

			if (outputHTML) {
				argsOptions.add("--html");
			}

			if (!showHeaders) {
				argsOptions.add("--hide-headers");
			}

			if (!Strings.isNullOrEmpty(proxy)) {
				argsOptions.add("--proxy");
				argsOptions.add(proxy);
			}

			if (extractAttachments) {
				argsOptions.add("--extract-attachments");
			}

			int listSize = l.size();
			for (int i = 0; i < listSize; i++) {
				String[] args = new String[argsOptions.size() + 1];
				argsOptions.toArray(args);
				args[args.length - 1] = l.get(i);

				try {
					Main.main(args);
				} catch (IOException e1) {
					// ignore this error
				}

				final String text = "Email " + (i + 1) + " of " + listSize;
				final int percent = (int) Math.ceil(((i + 1d) * 100d) / listSize);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						lblConvertingEmail.setText(text);
						progressBar.setValue(percent);
					}
				});
			}
		} finally {
			btnStartConversion.setEnabled(true);
			frmEmlToPdf.setCursor(Cursor.getDefaultCursor());
		}
	}
}
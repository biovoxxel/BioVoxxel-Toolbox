package de.biovoxxel.toolbox;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ImageProcessor;

@Plugin(type = Command.class, menuPath = "Plugins>BioVoxxel>Pseudo Flat-Field Correction")
public class PseudoFlatFieldCorrection extends JFrame implements Command {

	private ImagePlus currentImagePlus = null;
	
	private JPanel contentPane;
	private JSpinner spinnerRadius;
	private JCheckBox chckbxHideBackground;
	private JCheckBox chckbxPreview;
	private double blurringRadius = 50;
	private boolean hideBackground = true;
	private boolean processStack = false;
	private boolean preview = false;
	private JButton btnOk;
	private JButton btnCancel;
	private JCheckBox chckbxStack;

	
	@Override
	public void run() {
		
		currentImagePlus = WindowManager.getCurrentImage();
		
		PseudoFlatFieldCorrection pffc = new PseudoFlatFieldCorrection();
		pffc.setVisible(true);
	}
	
	
	/**
	 * Create the frame.
	 */
	protected PseudoFlatFieldCorrection() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 236, 187);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 84, 0, 0};
		gbl_contentPane.rowHeights = new int[]{42, 42, 42, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblSpinner = new JLabel("Blurring radius");
		GridBagConstraints gbc_lblSpinner = new GridBagConstraints();
		gbc_lblSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpinner.gridx = 1;
		gbc_lblSpinner.gridy = 0;
		contentPane.add(lblSpinner, gbc_lblSpinner);
		
		spinnerRadius = new JSpinner();
		spinnerRadius.setModel(new SpinnerNumberModel(new Double(50), new Double(1), null, new Double(1)));
		spinnerRadius.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			}
		});
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 2;
		gbc_spinner.gridy = 0;
		contentPane.add(spinnerRadius, gbc_spinner);
		
		chckbxHideBackground = new JCheckBox("hide background");
		chckbxHideBackground.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dialogChanged();
			}
		});
		GridBagConstraints gbc_chckbxHideBackground = new GridBagConstraints();
		gbc_chckbxHideBackground.fill = GridBagConstraints.BOTH;
		gbc_chckbxHideBackground.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxHideBackground.gridx = 1;
		gbc_chckbxHideBackground.gridy = 1;
		contentPane.add(chckbxHideBackground, gbc_chckbxHideBackground);
		
		chckbxPreview = new JCheckBox("Preview");
		chckbxPreview.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dialogChanged();
			}
		});
		
		chckbxStack = new JCheckBox("process stack");
		GridBagConstraints gbc_chckbxStack = new GridBagConstraints();
		gbc_chckbxStack.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxStack.gridx = 2;
		gbc_chckbxStack.gridy = 1;
		contentPane.add(chckbxStack, gbc_chckbxStack);
		GridBagConstraints gbc_chckbxPreview = new GridBagConstraints();
		gbc_chckbxPreview.fill = GridBagConstraints.BOTH;
		gbc_chckbxPreview.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxPreview.gridx = 1;
		gbc_chckbxPreview.gridy = 2;
		contentPane.add(chckbxPreview, gbc_chckbxPreview);
		
		btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processImage();
			}

		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 1;
		gbc_btnOk.gridy = 3;
		contentPane.add(btnOk, gbc_btnOk);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogChanged();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 3;
		contentPane.add(btnCancel, gbc_btnCancel);
		
	}
	
	
	private void dialogChanged() {
		blurringRadius = (double) spinnerRadius.getValue();
		hideBackground = chckbxHideBackground.isSelected();
		preview = chckbxPreview.isSelected();
		processStack = chckbxStack.isSelected();
		
		
	}
	
	
	private void processImageProcessor(ImageProcessor ip) {
		ImageProcessor duplicateProcessor = ip.duplicate();
		
		if (duplicateProcessor.getBitDepth() == 24) {
			
			getBrightnessChannel();
			
			
		} else {
			
		}
	}
	
	
	private void processImage() {
		
		if (rootPaneCheckingEnabled) {
			
		}
		
	}
	
	
	private void processStack() {
		
		
	}
	
}

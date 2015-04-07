package com.albertoborsetta.formscanner.gui.view;

import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;

import com.albertoborsetta.formscanner.api.FormArea;
import com.albertoborsetta.formscanner.api.FormQuestion;
import com.albertoborsetta.formscanner.api.FormPoint;
import com.albertoborsetta.formscanner.api.commons.Constants.FieldType;
import com.albertoborsetta.formscanner.commons.CharSequenceGenerator;
import com.albertoborsetta.formscanner.commons.FormScannerConstants;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Action;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.FieldsTableColumn;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Frame;
import com.albertoborsetta.formscanner.commons.resources.FormScannerResources;
import com.albertoborsetta.formscanner.commons.resources.FormScannerResourcesKeys;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslation;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslationKeys;
import com.albertoborsetta.formscanner.gui.builder.ButtonBuilder;
import com.albertoborsetta.formscanner.gui.builder.CheckBoxBuilder;
import com.albertoborsetta.formscanner.gui.builder.ComboBoxBuilder;
import com.albertoborsetta.formscanner.gui.builder.LabelBuilder;
import com.albertoborsetta.formscanner.gui.builder.PanelBuilder;
import com.albertoborsetta.formscanner.gui.builder.ScrollPaneBuilder;
import com.albertoborsetta.formscanner.gui.builder.SpinnerBuilder;
import com.albertoborsetta.formscanner.gui.builder.TabbedPaneBuilder;
import com.albertoborsetta.formscanner.gui.builder.TextFieldBuilder;
import com.albertoborsetta.formscanner.gui.controller.ManageTemplateController;
import com.albertoborsetta.formscanner.gui.model.FormScannerModel;

public class ManageTemplateFrame extends InternalFrame implements TabbedView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private JScrollPane positionsTableScrollPane;
	private JScrollPane fieldsTableScrollPane;

	private JButton addFieldButton;
	private JButton removeFieldButton;
	private JButton saveTemplateButton;
	private JButton cancelTemplateButton;

	private JSpinner colsNumber;
	private JSpinner rowsNumber;
	private JComboBox<InternalFieldType> typeComboBox;
	private JCheckBox isMultiple;
	private JCheckBox rejectMultiple;
	private JButton okPropertiesButton;
	private JButton cancelPropertiesButton;
	private JButton okTypesButton;
	private JButton cancelTypesButton;

	private JTable positionsTable;
	private JTable fieldsTable;
	private JButton okPositionButton;
	private JButton cancelPositionButton;

	private ManageTemplateController manageTemplateController;
	private int previousRowsCount;
	private InternalFieldType[] types;
	private JPanel fieldsTypeButtonPanel;
	private JTextField questionLabel;
	private String fieldsType;

	private class InternalFieldType {

		private FieldType type;

		protected InternalFieldType(FieldType type) {
			this.type = type;
		}

		protected FieldType getType() {
			return type;
		}

		public String toString() {
			return FormScannerTranslation.getTranslationFor(type.getValue());
		}
	}

	protected class FieldsTableModel extends DefaultTableModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public FieldsTableModel() {
			super();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return (columnIndex == 2) ? Boolean.class : super
					.getColumnClass(columnIndex);
		}
	}

	public class PositionsTableCellRenderer extends JTextArea implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String type;

		public PositionsTableCellRenderer(String type) {
			setLineWrap(true);
			setWrapStyleWord(true);
			setOpaque(true);
			this.type = type;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			boolean header = false;
			switch (type) {
			case FormScannerConstants.QUESTIONS_BY_ROWS:
			case FormScannerConstants.QUESTIONS_BY_COLS:
				header = ((row == 0) || (column == 0));
				break;
			case FormScannerConstants.RESPONSES_BY_GRID:
			case FormScannerConstants.BARCODE:
				header = ((column % 2) == 0);
				break;
			}

			if (header) {
				setBackground(new java.awt.Color(238, 238, 238));
			} else {
				if (isSelected && !header) {
					setForeground(table.getSelectionForeground());
					setBackground(table.getSelectionBackground());
				} else {
					setForeground(table.getForeground());
					setBackground(table.getBackground());
				}
			}
			setFont(table.getFont());
			if (hasFocus && !header) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				if (table.isCellEditable(row, column)) {
					setForeground(UIManager
							.getColor("Table.focusCellForeground"));
					setBackground(UIManager
							.getColor("Table.focusCellBackground"));
				}
			} else {
				setBorder(new EmptyBorder(1, 2, 1, 2));
			}
			if (value != null) {
				setText(value.toString());
			} else {
				setText("");
			}
			return this;
		}
	}
	
	public class FieldsTableCellRenderer extends JTextArea implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FieldsTableCellRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

				if (isSelected) {
					setForeground(table.getSelectionForeground());
					setBackground(table.getSelectionBackground());
				} else {
					setForeground(table.getForeground());
					setBackground(table.getBackground());
				}
			setFont(table.getFont());
			if (hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				if (table.isCellEditable(row, column)) {
					setForeground(UIManager
							.getColor("Table.focusCellForeground"));
					setBackground(UIManager
							.getColor("Table.focusCellBackground"));
				}
			} else {
				setBorder(new EmptyBorder(1, 2, 1, 2));
			}
			if (value != null) {
				setText(value.toString());
			} else {
				setText("");
			}
			return this;
		}
	}

	/**
	 * Create the frame.
	 */
	public ManageTemplateFrame(FormScannerModel model) {
		super(model);

		manageTemplateController = new ManageTemplateController(model);
		manageTemplateController.add(this);

		setName(Frame.MANAGE_TEMPLATE_FRAME.name());
		setTitle(FormScannerTranslation
				.getTranslationFor(FormScannerTranslationKeys.MANAGE_TEMPLATE_FRAME_TITLE));
		setBounds(model.getLastPosition(Frame.MANAGE_TEMPLATE_FRAME));
		setMinimumSize(new Dimension(300, 500));
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		setResizable(true);
		previousRowsCount = 1;

		JPanel fieldListPanel = getFieldListPanel();
		JPanel fieldTypePanel = getFieldTypePanel();
		JPanel fieldPositionPanel = getFieldPositionPanel();

		tabbedPane = new TabbedPaneBuilder(JTabbedPane.TOP, orientation)
				.addTab(FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.FIELD_LIST_TAB_NAME),
						fieldListPanel)
				.addTab(FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.FIELD_TYPE_TAB_NAME),
						fieldTypePanel)
				.addTab(FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.FIELD_PROPERTIES_TAB_NAME),
						null)
				.addTab(FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.FIELD_POSITION_TAB_NAME),
						fieldPositionPanel).build();

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);
		tabbedPane.setEnabledAt(3, false);
	}

	public void setupNextTab(String action) {
		int currTab = tabbedPane.getSelectedIndex();
		int nextTab = 0;
		Action act = Action.valueOf(action);
		switch (act) {
		case CONFIRM:
			nextTab = (currTab + 1) % tabbedPane.getTabCount();

			switch (nextTab) {
			case 0:
				for (int i = nextTab + 1; i < tabbedPane.getTabCount(); i++) {
					tabbedPane.setEnabledAt(i, false);
				}
				model.disposeRelatedFrame(this);

				switch (fieldsType) {
				case FormScannerConstants.QUESTIONS_BY_COLS:
				case FormScannerConstants.QUESTIONS_BY_ROWS:
				case FormScannerConstants.RESPONSES_BY_GRID:
				HashMap<String, FormQuestion> fields = createFields();
				model.updateTemplate(fields);
				break;
				case FormScannerConstants.BARCODE:
					HashMap<String, FormArea> area = createArea();
					model.updateTemplate(area);
					break;
				}
				setupFieldsTable();
				saveTemplateButton.setEnabled(true);
				resetSelectedValues();
				break;
			case 2:
				JPanel fieldPropertiesPanel = getFieldPropertiesPanel();
				tabbedPane.setComponentAt(nextTab, fieldPropertiesPanel);
				break;
			case 3:
				resetPositionsTable();
				model.createTemplateImageFrame(fieldsType);
				break;
			default:
				break;
			}
			break;
		case CANCEL:
			nextTab = currTab - 1;
			if (currTab > 0) {
				tabbedPane.setEnabledAt(currTab, false);
				tabbedPane.setSelectedIndex(nextTab);
			}
			switch (nextTab) {
			case 0:
				resetFieldsType();
				break;
			case 1:
				resetSelectedValues();
				break;
			case 2:
				model.resetPoints();
				model.disposeRelatedFrame(this);
				break;
			default:
				dispose();
				break;
			}
			break;
		default:
			break;
		}

		tabbedPane.setEnabledAt(nextTab, true);
		tabbedPane.setSelectedIndex(nextTab);
	}

	private void resetFieldsType() {
		typeComboBox.setSelectedIndex(0);
	}

	private HashMap<String, FormQuestion> createFields() {
		HashMap<String, FormQuestion> fields = new HashMap<String, FormQuestion>();
		
		Integer rows = (Integer) rowsNumber.getValue();
		Integer cols = (Integer) colsNumber.getValue();
		
		switch (fieldsType) {
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
			for (int i = 1; i < rows + 1; i++) {
				String name = (String) positionsTable.getValueAt(i, 0);
				FormQuestion field = new FormQuestion(name);
				for (int j = 1; j < cols + 1; j++) {
					String value = (String) positionsTable.getValueAt(0, j);
					FormPoint p = getPointFromTable(i, j);
					field.setPoint(value, p);
					field.setType(getFieldType());
				}
				field.setMultiple(isMultiple.isSelected());
				field.setRejectMultiple(!isMultiple.isSelected()
						&& rejectMultiple.isSelected());
				fields.put(name, field);
			}
			break;
		case FormScannerConstants.RESPONSES_BY_GRID:
			String name = questionLabel.getText();
			FormQuestion field = new FormQuestion(name);
			for (int i = 0; i < rows; i++) {
				for (int j = 1; j < cols; j+=2) {
					String value = (String) positionsTable.getValueAt(i, j-1);
					FormPoint p = getPointFromTable(i, j);
					field.setPoint(value, p);
				}
			}
			field.setType(getFieldType());
			field.setMultiple(isMultiple.isSelected());
			field.setRejectMultiple(!isMultiple.isSelected()
					&& rejectMultiple.isSelected());
			fields.put(name, field);
			break;
		}
		return fields;
	}

	private FormPoint getPointFromTable(int i, int j) {
		return FormPoint.toPoint((String) positionsTable.getValueAt(i, j));
	}

	public int getRowsNumber() {
		switch (fieldsType) {
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
		case FormScannerConstants.RESPONSES_BY_GRID:
			return (Integer) rowsNumber.getValue();
		case FormScannerConstants.BARCODE:
			return 2;
		default:
			return 1;
		}
	}

	public int getValuesNumber() {
		switch (fieldsType) {
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
		case FormScannerConstants.RESPONSES_BY_GRID:
			return (Integer) colsNumber.getValue();
		case FormScannerConstants.BARCODE:
			return 2;
		default:
			return 1;
		}
	}

	public void setupPositionTable(List<FormPoint> points) {
		Integer rows = (rowsNumber != null) ? (Integer) rowsNumber.getValue() : 2;
		Integer cols = (colsNumber != null) ? (Integer) colsNumber.getValue() : 2;

		switch (fieldsType) {
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					int index = (cols * i) + j;
					FormPoint p = points.get(index);
					positionsTable.setValueAt(p.toString(), (i + 1), (j + 1));
				}
			}
			break;
		case FormScannerConstants.BARCODE:
			rows = 2;
			cols = 2;
		case FormScannerConstants.RESPONSES_BY_GRID:
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					int index = ((cols % 2) * i) + (j % 2);
					FormPoint p = points.get(index);
					positionsTable.setValueAt(p.toString(), i, ((2 * j) +1));
				}
			}
			break;
		}
	}

	private void removePositionsTable() {
		positionsTableScrollPane.remove(positionsTable);
	}

	private void resetPositionsTable() {
		removePositionsTable();

		switch (fieldsType) {
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
			positionsTable = createSimplePositionsTable(
					((Integer) rowsNumber.getValue()) + 1,
					((Integer) colsNumber.getValue()) + 1);
			break;
		case FormScannerConstants.RESPONSES_BY_GRID:
			positionsTable = createGridPositionsTable(
					((Integer) rowsNumber.getValue()),
					((Integer) colsNumber.getValue()) * 2);
			break;
		case FormScannerConstants.BARCODE:
			positionsTable = createBarcodePositionsTable(2, 4);
			break;
		}

		positionsTableScrollPane.setViewportView(positionsTable);
		positionsTable.setVisible(true);
	}

	private void setupFieldsTable() {
		HashMap<String, FormQuestion> fields = model.getTemplate().getFields();
		FieldsTableModel fieldsTableModel = (FieldsTableModel) fieldsTable
				.getModel();
		while (fieldsTable.getRowCount() > 0) {
			fieldsTableModel.removeRow(fieldsTable.getRowCount() - 1);
		}

		for (FormQuestion field : fields.values()) {
			fieldsTableModel.addRow(new Object[] {
					field.getName(),
					FormScannerTranslation.getTranslationFor(field.getType()
							.getValue()), field.isMultiple(),
					field.getPoints().size() });
		}

		// TODO: Add Areas.
	}

	private void resetSelectedValues() {
		if (!fieldsType.equals(FormScannerConstants.BARCODE)) {
			rowsNumber.setValue(0);
			colsNumber.setValue(0);
			isMultiple.setSelected(false);
			rejectMultiple.setSelected(false);
		}
		questionLabel.setText(StringUtils.EMPTY);
	}

	public FieldType getFieldType() {
		return (types[typeComboBox.getSelectedIndex()]).getType();
	}

	public void setAdvanceable() {
		int currTab = tabbedPane.getSelectedIndex();

		switch (currTab) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			okPropertiesButton.setEnabled(verifyPropertiesValues());
		case 3:
			okPositionButton.setEnabled(true);
			break;
		default:
			break;
		}
	}

	private boolean verifyPropertiesValues() {
		if (colsNumber != null && (Integer) colsNumber.getValue() < 0) {
			colsNumber.setValue(0);
		}
		if (rowsNumber != null && (Integer) rowsNumber.getValue() < 0) {
			rowsNumber.setValue(0);
		}
		return (((colsNumber != null && (Integer) colsNumber.getValue() > 0)
				&& (rowsNumber != null && (Integer) rowsNumber.getValue() > 0) && StringUtils
					.isNotBlank(questionLabel.getText())) || (colsNumber == null
				&& rowsNumber == null && StringUtils.isNotBlank(questionLabel
				.getText())));
	}

	private JTable createSimplePositionsTable(int rows, int cols) {
		TableColumnModel columnModel = new DefaultTableColumnModel();

		for (int i = 0; i < cols; i++) {
			TableColumn column = new TableColumn(i);
			column.setMinWidth(100);
			columnModel.addColumn(column);
		}

		JTable table = new JTable(new DefaultTableModel(rows, cols) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void addTableModelListener(
					TableModelListener tableModelListener) {
				super.addTableModelListener(manageTemplateController);
			}

			public boolean isCellEditable(int row, int col) {
				if (row == 0 && col == 0) {
					return false;
				}
				return ((row * col) == 0);
			}
		}, columnModel);

		table.setDefaultRenderer(Object.class, new PositionsTableCellRenderer(fieldsType));

		CharSequenceGenerator charSequence = new CharSequenceGenerator();

		for (int i = 1; i < cols; i++) {
			table.setValueAt(charSequence.next(), 0, i);
		}

		for (int i = 1; i < rows; i++) {
			table.setValueAt(
					(String) FormScannerTranslation
							.getTranslationFor(FormScannerTranslationKeys.QUESTION)
							+ " "
							+ StringUtils.leftPad("" + previousRowsCount++, 3,
									"0"), i, 0);
		}
		table.setComponentOrientation(orientation);
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		return table;
	}

	private JTable createGridPositionsTable(int rows, int cols) {
		TableColumnModel columnModel = new DefaultTableColumnModel();

		for (int i = 0; i < cols; i++) {
			TableColumn column = new TableColumn(i);
			column.setMinWidth(100);
			columnModel.addColumn(column);
		}

		JTable table = new JTable(new DefaultTableModel(rows, cols) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void addTableModelListener(
					TableModelListener tableModelListener) {
				super.addTableModelListener(manageTemplateController);
			}

			public boolean isCellEditable(int row, int col) {
				return (col % 2 == 0);
			}
		}, columnModel);

		table.setDefaultRenderer(Object.class, new PositionsTableCellRenderer(fieldsType));

		CharSequenceGenerator charSequence = new CharSequenceGenerator();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j += 2) {
				table.setValueAt(charSequence.next(), i, j);
			}
		}
		table.setComponentOrientation(orientation);
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		return table;
	}

	private JTable createBarcodePositionsTable(int rows, int cols) {
		TableColumnModel columnModel = new DefaultTableColumnModel();

		for (int i = 0; i < cols; i++) {
			TableColumn column = new TableColumn(i);
			column.setMinWidth(100);
			columnModel.addColumn(column);
		}

		JTable table = new JTable(new DefaultTableModel(rows, cols) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void addTableModelListener(
					TableModelListener tableModelListener) {
				super.addTableModelListener(manageTemplateController);
			}

			public boolean isCellEditable(int row, int col) {
				return (false);
			}
		}, columnModel);

		table.setDefaultRenderer(Object.class, new PositionsTableCellRenderer(fieldsType));

		table.setValueAt(FormScannerTranslation
				.getTranslationFor(FormScannerTranslationKeys.TOP_LEFT_CORNER),
				0, 0);
		table.setValueAt(
				FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.TOP_RIGHT_CORNER),
				0, 2);
		table.setValueAt(
				FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.BOTTOM_LEFT_CORNER),
				1, 0);
		table.setValueAt(
				FormScannerTranslation
						.getTranslationFor(FormScannerTranslationKeys.BOTTOM_RIGHT_CORNER),
				1, 2);

		table.setComponentOrientation(orientation);
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		return table;
	}

	private JTable createFieldsTable() {
		FieldsTableModel fieldsTableModel = new FieldsTableModel();
		JTable table = new JTable(fieldsTableModel);
		table.setRowSorter(new TableRowSorter<FieldsTableModel>(
				fieldsTableModel));

		for (FieldsTableColumn tableColumn : FieldsTableColumn.values()) {
			fieldsTableModel.addColumn(FormScannerTranslation
					.getTranslationFor(tableColumn.getValue()));
		}

		table.setDefaultRenderer(Object.class, new FieldsTableCellRenderer());

		table.setComponentOrientation(orientation);
		table.setRowSelectionAllowed(true);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				manageTemplateController);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return table;
	}

	private JPanel getFieldListPanel() {

		fieldsTable = createFieldsTable();
		fieldsTableScrollPane = new ScrollPaneBuilder(fieldsTable, orientation)
				.build();

		JPanel fieldListManageButtonPanel = getManageListButtonPanel();
		JPanel fieldListButtonPanel = getFieldListButtonPanel();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.addComponent(fieldsTableScrollPane, BorderLayout.CENTER)
				.addComponent(fieldListManageButtonPanel, BorderLayout.EAST)
				.addComponent(fieldListButtonPanel, BorderLayout.SOUTH).build();
	}

	private JPanel getFieldPropertiesPanel() {

		fieldsType = getFieldType().getName();

		JPanel propertiesButtonPanel = getPropertiesButtonPanel();

		PanelBuilder pPanelBuilder = new PanelBuilder(orientation).withLayout(
				new BorderLayout()).add(propertiesButtonPanel,
				BorderLayout.SOUTH);

		switch (fieldsType) {
		case FormScannerConstants.BARCODE:
			JPanel barcodePanel = getBarcodePanel();
			pPanelBuilder.add(barcodePanel, BorderLayout.NORTH);
			break;
		case FormScannerConstants.RESPONSES_BY_GRID:
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
			JPanel rowsColsPanel = getRowsColsPanel();
			pPanelBuilder.add(rowsColsPanel, BorderLayout.NORTH);
			break;
		}

		return pPanelBuilder.build();
	}

	private JPanel getBarcodePanel() {
		questionLabel = new TextFieldBuilder(10, orientation)
				.withActionListener(manageTemplateController).build();

		return new PanelBuilder(orientation).withLayout(new SpringLayout())
				.add(getLabel(FormScannerTranslationKeys.BARCODE_LABEL))
				.add(questionLabel).withGrid(1, 2).build();
	}

	protected JPanel getFieldPositionPanel() {

		JPanel positionButtonPanel = getPositionButtonPanel();

		positionsTable = new JTable();
		positionsTableScrollPane = new ScrollPaneBuilder(positionsTable,
				orientation).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.addComponent(positionButtonPanel, BorderLayout.SOUTH)
				.addComponent(positionsTableScrollPane, BorderLayout.CENTER)
				.build();
	}

	private JPanel getFieldTypePanel() {

		FieldType fields[] = FieldType.values();
		types = new InternalFieldType[fields.length];

		for (FieldType type : fields) {
			types[type.getIndex()] = new InternalFieldType(type);
		}

		typeComboBox = new ComboBoxBuilder<InternalFieldType>(
				FormScannerConstants.TYPE_COMBO_BOX, orientation)
				.withModel(new DefaultComboBoxModel<InternalFieldType>(types))
				.withActionListener(manageTemplateController).build();

		fieldsTypeButtonPanel = getFieldsTypeButtonPanel();

		JPanel typePanel = new PanelBuilder(orientation)
				.withLayout(new SpringLayout())
				.add(getLabel(FormScannerTranslationKeys.FIELD_PROPERTIES_TYPE_LABEL))
				.add(typeComboBox).withGrid(1, 2).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.addComponent(fieldsTypeButtonPanel, BorderLayout.SOUTH)
				.addComponent(typePanel, BorderLayout.NORTH).build();
	}

	private JPanel getRowsColsPanel() {

		boolean isGrid = fieldsType
				.equals(FormScannerConstants.RESPONSES_BY_GRID);

		questionLabel = new TextFieldBuilder(10, orientation)
				.withActionListener(manageTemplateController).build();

		isMultiple = new CheckBoxBuilder(FormScannerConstants.IS_MULTIPLE,
				orientation)
				.withActionCommand(FormScannerConstants.IS_MULTIPLE)
				.withActionListener(manageTemplateController).setChecked(false)
				.build();

		rejectMultiple = new CheckBoxBuilder(
				FormScannerConstants.REJECT_IF_NOT_MULTIPLE, orientation)
				.setChecked(false).setEnabled(!isMultiple.isSelected()).build();

		rowsNumber = new SpinnerBuilder(FormScannerConstants.NUMBER_ROWS,
				orientation).withActionListener(manageTemplateController)
				.withFocusListener(manageTemplateController).build();

		colsNumber = new SpinnerBuilder(FormScannerConstants.NUMBER_COLS,
				orientation).withActionListener(manageTemplateController)
				.withFocusListener(manageTemplateController).build();

		PanelBuilder propertiesPanelBuilder = new PanelBuilder(orientation)
				.withLayout(new SpringLayout());
		switch (fieldsType) {
		case FormScannerConstants.RESPONSES_BY_GRID:
			propertiesPanelBuilder.add(getLabel(isGrid ? FormScannerTranslationKeys.SINGLE_QUESTION_LABEL
					: FormScannerTranslationKeys.SET_OF_QUESTIONS_LABEL))
					.add(questionLabel).withGrid(5, 2);
			break;
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
					propertiesPanelBuilder.withGrid(4, 2);
			break;
		}
		
		return propertiesPanelBuilder.add(getLabel(FormScannerTranslationKeys.FIELD_PROPERTIES_IS_MULTIPLE))
				.add(isMultiple)
				.add(getLabel(FormScannerTranslationKeys.FIELD_PROPERTIES_REJECT_MULTIPLE))
				.add(rejectMultiple)
				.add(getLabel(isGrid ? FormScannerTranslationKeys.FIELD_PROPERTIES_N_ROWS_LABEL
						: FormScannerTranslationKeys.FIELD_PROPERTIES_N_QUESTIONS_LABEL))
				.add(rowsNumber)
				.add(getLabel(isGrid ? FormScannerTranslationKeys.FIELD_PROPERTIES_N_COLS_LABEL
						: FormScannerTranslationKeys.FIELD_PROPERTIES_N_VALUES_LABEL))
				.add(colsNumber).build();
	}

	private JLabel getLabel(String value) {
		return new LabelBuilder(
				FormScannerTranslation.getTranslationFor(value), orientation)
				.withBorder(BorderFactory.createEmptyBorder()).build();
	}

	private JPanel getFieldsTypeButtonPanel() {

		okTypesButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CONFIRM)
				.withActionListener(manageTemplateController).setEnabled(true)
				.build();

		cancelTypesButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CANCEL)
				.withActionListener(manageTemplateController).build();

		JPanel innerPanel = new PanelBuilder(orientation)
				.withLayout(new SpringLayout()).add(okTypesButton)
				.add(cancelTypesButton).withGrid(1, 2).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.add(innerPanel, BorderLayout.EAST).build();
	}

	private JPanel getPropertiesButtonPanel() {

		okPropertiesButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CONFIRM)
				.withActionListener(manageTemplateController)
				.setEnabled(fieldsType.equals(FormScannerConstants.BARCODE))
				.build();

		cancelPropertiesButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CANCEL)
				.withActionListener(manageTemplateController).build();

		JPanel innerPanel = new PanelBuilder(orientation)
				.withLayout(new SpringLayout()).add(okPropertiesButton)
				.add(cancelPropertiesButton).withGrid(1, 2).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.add(innerPanel, BorderLayout.EAST).build();
	}

	private JPanel getFieldListButtonPanel() {

		saveTemplateButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.SAVE_TEMPLATE_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.SAVE_TEMPLATE_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.SAVE_TEMPLATE)
				.withActionListener(manageTemplateController).setEnabled(false)
				.build();

		cancelTemplateButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CANCEL)
				.withActionListener(manageTemplateController).build();

		JPanel innerPanel = new PanelBuilder(orientation)
				.withLayout(new SpringLayout()).add(saveTemplateButton)
				.add(cancelTemplateButton).withGrid(1, 2).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.add(innerPanel, BorderLayout.EAST).build();
	}

	private JPanel getManageListButtonPanel() {

		addFieldButton = new ButtonBuilder(orientation)
				.withIcon(
						FormScannerResources
								.getIconFor(FormScannerResourcesKeys.ADD_FIELD_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.ADD_FIELD_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.ADD_FIELD)
				.withActionListener(manageTemplateController).build();

		removeFieldButton = new ButtonBuilder(orientation)
				.withIcon(
						FormScannerResources
								.getIconFor(FormScannerResourcesKeys.REMOVE_FIELD_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.REMOVE_FIELD_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.REMOVE_FIELD)
				.withActionListener(manageTemplateController).setEnabled(false)
				.build();

		return new PanelBuilder(orientation).withLayout(new SpringLayout())
				.add(addFieldButton).add(removeFieldButton).withGrid(2, 1)
				.build();
	}

	private JPanel getPositionButtonPanel() {

		okPositionButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CONFIRM)
				.withActionListener(manageTemplateController).setEnabled(false)
				.build();

		cancelPositionButton = new ButtonBuilder(orientation)
				.withText(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON))
				.withToolTip(
						FormScannerTranslation
								.getTranslationFor(FormScannerTranslationKeys.CANCEL_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CANCEL)
				.withActionListener(manageTemplateController).build();

		JPanel innerPanel = new PanelBuilder(orientation)
				.withLayout(new SpringLayout()).add(okPositionButton)
				.add(cancelPositionButton).withGrid(1, 2).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.add(innerPanel, BorderLayout.EAST).build();
	}

	public String getSelectedItem() {

		String fieldName = (String) fieldsTable.getValueAt(
				fieldsTable.getSelectedRow(), 0);
		return fieldName;
	}

	public void enableRemoveFields() {
		removeFieldButton.setEnabled(true);
	}

	public void removeSelectedField() {
		FieldsTableModel fieldsTableModel = (FieldsTableModel) fieldsTable
				.getModel();

		fieldsTableModel.removeRow(fieldsTable.getSelectedRow());
	}

	public boolean getIsMultiple() {
		return isMultiple.isSelected();
	}

	public void enableRejectMultiple(boolean enable) {
		rejectMultiple.setEnabled(enable);
	}
}

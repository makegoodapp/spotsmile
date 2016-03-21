package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single column for ColumnChart. One column can be divided into multiple sub-columns(ColumnValues) especially for
 * stacked ColumnChart.
 * <p/>
 * Note: you can set X value for columns or sub-columns, columns are by default indexed from 0 to numOfColumns-1 and
 * column index is used as column X value, so first column has X value 0, second clumn has X value 1 etc.
 * If you want to display AxisValue for given column you should initialize AxisValue with X value of that column.
 */
public class Column {
	private boolean hasLabels = false;
	private boolean hasLabelsOnlyForSelected = false;
	private ColumnChartValueFormatter formatter = new SimpleColumnChartValueFormatter();
	// TODO: consider Collections.emptyList()
	private List<SubColumnValue> values = new ArrayList<SubColumnValue>();

	public Column() {

	}

	public Column(List<SubColumnValue> values) {
		setValues(values);
	}

	public Column(Column column) {
		this.hasLabels = column.hasLabels;
		this.hasLabelsOnlyForSelected = column.hasLabelsOnlyForSelected;
		this.formatter = column.formatter;

		for (SubColumnValue columnValue : column.values) {
			this.values.add(new SubColumnValue(columnValue));
		}
	}

	public void update(float scale) {
		for (SubColumnValue value : values) {
			value.update(scale);
		}

	}

	public void finish() {
		for (SubColumnValue value : values) {
			value.finish();
		}
	}

	public List<SubColumnValue> getValues() {
		return values;
	}

	public Column setValues(List<SubColumnValue> values) {
		if (null == values) {
			this.values = new ArrayList<SubColumnValue>();
		} else {
			this.values = values;
		}
		return this;
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public Column setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
		if (hasLabels) {
			this.hasLabelsOnlyForSelected = false;
		}
		return this;
	}

	/**
	 * @see #setHasLabelsOnlyForSelected(boolean)
	 */
	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

	/**
	 * Set true if you want to show value labels only for selected value, works best when chart has
	 * isValueSelectionEnabled set to true {@link Chart#setValueSelectionEnabled(boolean)}.
	 */
	public Column setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public ColumnChartValueFormatter getFormatter() {
		return formatter;
	}

	public Column setFormatter(ColumnChartValueFormatter formatter) {
		if (null != formatter) {
			this.formatter = formatter;
		}
		return this;
	}
}

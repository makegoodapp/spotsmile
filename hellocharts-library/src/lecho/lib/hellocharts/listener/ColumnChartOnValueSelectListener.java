package lecho.lib.hellocharts.listener;


import lecho.lib.hellocharts.model.SubColumnValue;

public interface ColumnChartOnValueSelectListener extends OnValueDeselectListener {

	public void onValueSelected(int columnIndex, int subcolumnIndex, SubColumnValue value);

}

package lecho.lib.hellocharts.listener;


import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubColumnValue;

public interface ComboLineColumnChartOnValueSelectListener extends OnValueDeselectListener {

	public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubColumnValue value);

	public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value);

}

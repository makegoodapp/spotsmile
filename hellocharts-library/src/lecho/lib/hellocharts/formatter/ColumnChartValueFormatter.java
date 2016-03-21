package lecho.lib.hellocharts.formatter;

import lecho.lib.hellocharts.model.SubColumnValue;

public interface ColumnChartValueFormatter {

	public int formatChartValue(char[] formattedValue, SubColumnValue value);

}

package xyz.chranness;

import java.util.ArrayList;

public class MyCsv extends ArrayList<MyCsvRow> {

	public void join(MyCsv joinCsv) {
		for(MyCsvRow r : joinCsv) {
			this.add(r);
		}
	}
	
	public String toCsvString() {
		
		StringBuilder sb = new StringBuilder();
		int i = -1;
		for (MyCsvRow row : this) {
			i++;
			if (i != 0) {
				sb.append(",");
			}

			sb.append(row.toCsvString());
		}
		return sb.toString();
	}

}

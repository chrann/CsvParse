package xyz.chranness;

import java.util.ArrayList;

public class MyCsv extends ArrayList<MyCsvRow> {

	public void join(MyCsv joinCsv) {
		for(MyCsvRow r : joinCsv) {
			this.add(r);
		}
	}
}

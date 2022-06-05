package xyz.chranness;

import java.util.ArrayList;

public class MyCsvRow extends ArrayList<Object> {

	public String toCsvString() {
		StringBuilder sb = new StringBuilder();
		int i = -1;
		for (Object o : this) {
			i++;
			if (i != 0) {
				sb.append(",");
			}

			if (o instanceof Boolean) {
				Boolean b = (Boolean) o;
				sb.append(b);
			}
			if (o instanceof Long) {
				Long l = (Long) o;
				sb.append(l);
			}
			if (o instanceof Double) {
				Double d = (Double) o;
				sb.append(d);
			}
			if (o instanceof String) {
				String s = (String) o;
				sb.append("\"").append(s).append("\"");
			}
		}
		return sb.toString();
	}
}

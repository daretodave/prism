package com.prism.cache.impl;

import java.io.InputStream;
import java.util.ArrayList;

import com.prism.Context;
import com.prism.cache.Loader;
import com.prism.cache.StreamUtils;
import com.prism.model.Frame;
import com.prism.model.Sequence;

public class AnimLoader implements Loader<Sequence> {

	@Override
	public Sequence load(InputStream input, String title) throws Exception {
		String file = StreamUtils.sap(input, "#", "//");
		String[] frames = file.split("]");
		ArrayList<Frame> sequence = new ArrayList<Frame>();
		for(String frame : frames) {
			frame = frame.replace("[", "").replace("]", "").trim();
			Context values = new Context();
			String[] raw = frame.split(";");
			for(String fragment : raw) {
				String[] kv = fragment.split("~");
				String key   = kv[0].trim().toLowerCase();
				String value = kv[1].trim().replace("(", "").replace(")", "");
				String[] division = value.split(",");
				Object recived = null;
				switch(key) {
				case "arotation":
				case "ascalex":
				case "ascaley":
				case "brotation":
				case "bscalex":
				case "bscaley":
					Float[] resolved = new Float[3];
					if(division.length > 1) {
						resolved[0] = Float.parseFloat(division[0]);
						resolved[1] = Float.parseFloat(division[1]);
						resolved[2] = Float.parseFloat(division[2]);
					} else {
						resolved[0] = .5F;
						resolved[1] = .5F;
						resolved[2] = Float.parseFloat(value);
					}
					recived = resolved;
					break;
				case "atranslation":
				case "btranslation":
					Float[] translation = new Float[2];
					translation[0] = Float.parseFloat(division[0]);
					translation[1] = Float.parseFloat(division[1]);
					recived = translation;
					break;
				case "lapse":
				case "bopacity":
				case "aopacity":
					recived = new Float(value);
					break;
				default:
					throw new RuntimeException("Unexpexted Key: " + key);
				}
				values.set(key, recived);
			}
			if(!values.exist("lapse")) {
				throw new RuntimeException("Frame does not have a time lapse. use key: 'lapse'");
			}
			Frame resolved = new Frame(
					values.$("arotation", Float[].class, null),
					values.$("ascalex", Float[].class, null),
					values.$("ascaley", Float[].class, null),
					values.$("atranslation", Float[].class, null),
					values.$("aopacity", Float.class, null),
					values.$("brotation", Float[].class, null),
					values.$("bscalex", Float[].class, null),
					values.$("bscaley", Float[].class, null),
					values.$("btranslation", Float[].class, null),
					values.$("bopacity", Float.class, null),
					values.$("lapse", Float.class, null)
			);
			sequence.add(resolved);
		}
		return new Sequence(sequence.toArray(new Frame[0]));
	}

}

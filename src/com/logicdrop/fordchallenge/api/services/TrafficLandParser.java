package com.logicdrop.fordchallenge.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class TrafficLandParser {

	private static final String ns = null;
	private static HashMap<String, String> webIdMap = new HashMap<String, String>();
	
	
	@SuppressWarnings("rawtypes")
	public List parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List entries = new ArrayList();
		
		parser.require(XmlPullParser.START_TAG, ns, "cameras");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("camera")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}
	
	public static class Entry {
		public final String webid;
		public final String name;
		public final String orientation;
		public final String[] location;
		public final String fullimage;
		
		private Entry(String webid, String name, String orientation, String[] location, String fullimage) {
			this.webid = webid;
			this.name = name;
			this.orientation = orientation;
			this.location = location;
			this.fullimage = fullimage;
		}
	}
	
	private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "camera");
		String webid = null;
		String name = null;
		String orientation = null;
		String[] location = null;
		String fullimage = null;
		webid = parser.getAttributeValue(null, "webid");
		name = parser.getAttributeValue(null, "name");
		orientation = parser.getAttributeValue(null, "orientation");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String tag = parser.getName();
			if (tag.equals("location")) {
				location = readLocation(parser);
			} else if (tag.equals("fullimage")) {
				fullimage = readHalfImage(parser);
			} else {
				skip(parser);
			}
		}
		webIdMap.put(webid, name + "," + orientation + "," + fullimage);
		return new Entry(webid, name, orientation, location, fullimage);
	}
	
	private String[] readLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
		String[] location = new String[3];
		parser.require(XmlPullParser.START_TAG, ns, "location");
		String tag = parser.getName();
		if (tag.equals("location")) {
			location[0] = parser.getAttributeValue(null, "latitude");
			location[1] = parser.getAttributeValue(null, "longitude");
			location[2] = parser.getAttributeValue(null, "zipCode");
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, ns, "location");
		return location;
		
	}
	
	private String readHalfImage(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "fullimage");
		String fullimage = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "fullimage");
		return fullimage;
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
	
	public static HashMap<String, String> getWebIdMap() {
		return webIdMap;
	}
}

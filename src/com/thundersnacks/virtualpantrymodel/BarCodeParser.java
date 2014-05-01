package com.thundersnacks.virtualpantrymodel;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class BarCodeParser {
	// We don't use namespaces
	private static final String ns = null;

	public Table parse(String in) throws XmlPullParserException, IOException {

		int startIndex = in.indexOf("<table");
		int endIndex = in.lastIndexOf("</table>");
		// Include </table>
		if(startIndex != -1)
		{
			String in2 = in.substring(startIndex, endIndex+8);
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(new StringReader (in2));
			parser.nextTag();
			return readFeed(parser);
		}
		else return null;
	}

	private Table readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		Table t = readTable(parser);
		if(t.UPC_A != null || t.UPC_A != "")
		{
			return t;
		}
		else return null;
	}

	@SuppressWarnings("unused")
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

	public static class Table {
		public final String UPC_A;
		public final String EAN_UCC_13;
		public final String description;
		public final String size;
		public final FoodItemUnit foodItemUnit;

		private Table(String UPC_A, String EAN_UCC_13, String description, String size, FoodItemUnit foodItemUnit) {
			this.UPC_A = UPC_A;
			this.EAN_UCC_13 = EAN_UCC_13;
			this.description = description;
			this.size = size;
			this.foodItemUnit = foodItemUnit;
		}
	}

	// Parses the contents of an entry. If it encounters a textData, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the tag.
	private Table readTable(XmlPullParser parser) throws XmlPullParserException, IOException {
		String UPC_A = null;
		String EAN_UCC_13 = null;
		String description = null;
		String size = null;
		FoodItemUnit foodItemUnit = null;
		String textData = "";
		String link = null;
		String section = null;
		parser.require(XmlPullParser.START_TAG, ns, "table");
		while(parser.next() != XmlPullParser.END_TAG && !textData.equals("Issuing Country") ){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			parser.require(XmlPullParser.START_TAG, ns, "tr");
			while ( parser.next() != XmlPullParser.END_TAG && !textData.equals("Issuing Country") ){
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}

				while ( (parser.getEventType() != XmlPullParser.END_TAG) && !textData.equals("Issuing Country") ) {
					if (parser.getEventType() != XmlPullParser.START_TAG) {
						continue;
					}
					String name = parser.getName();
					if (name.equals("td")) {
						textData = readText(parser);

					} else if (name.equals("img")) {
						link = readLink(parser);

					}
				}

				if("UPC-A".equals(section == null ? "" : section) && link != null)
					UPC_A = link;
				else if("EAN/UCC-13".equals(section == null ? "" : section) && link != null)
					EAN_UCC_13 = link;
				else if("Description".equals(section == null ? "" : section) && (textData != "" && textData != null))
				{
					description = textData;
					section = null;
				}
				else if("Size/Weight".equals(section == null ? "" : section) && (textData != "" && textData != null))
				{
					int numberRange = 0;
					int numberPosition = textData.indexOf(' ');
					if(numberPosition == -1)
						numberPosition = textData.length();

					size = textData.substring(0, numberPosition);
					boolean match = false, m = false;
					for(numberRange = 0; numberRange < numberPosition;numberRange++)
					{
						//match = size.substring(numberRange,numberRange+1).matches("\\d..*");
						match = size.substring(numberRange,numberRange+1).matches("[0-9\\.].*");
						if(!match) // A non numeric is found, end loop
							break;
					}
					// Cut off numeric value
					size = size.substring(0, numberRange);
					textData = textData.substring(numberRange, textData.length());
					foodItemUnit = parseFoodItemUnit(textData);
					section = null;
				}
				else
					section = textData.equals("") ? section : textData;

				// Reset
				link = null;
			}	
		}
		return new Table(UPC_A, EAN_UCC_13, description, size, foodItemUnit);
	}

	private FoodItemUnit parseFoodItemUnit(String textData){

		FoodItemUnit unit = FoodItemUnit.UNITLESS;
		//String 

		if(textData.contains("oz") || textData.contains("OZ") || textData.contains("Oz"))
			unit = FoodItemUnit.BAGS;
		else if(textData.contains("SHEETS") || textData.contains("Sheets") || textData.contains("sheets"))
			unit = FoodItemUnit.UNITLESS;
		else if(textData.contains("L") || textData.contains("l"))
			unit = FoodItemUnit.BOTTLES;

		return unit;

	}

	// Processes link tags in the feed.
	private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
		String link = "";
		parser.require(XmlPullParser.START_TAG, ns, "img");
		//get img link alt 
		link = parser.getAttributeValue(null, "alt");  

		while( (parser.getEventType() != XmlPullParser.END_TAG) )
		{
			parser.next();
		}
		parser.next();
		return link;
	}

	// For the tags data, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

}
package com.example.MA02_20170953;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class BoxXmlParser {
    public enum TagType {NONE, NAME, LAT, LNG, LOCATION, WEEKDAYOPEN, WEEKDAYCLOSE, SATOPEN, SATCLOSE, HOLIOPEN, HOLICLOSE, FREEUSETIME};

    public BoxXmlParser() {

    }

    final static String TAG_ITEM = "item";
    final static String TAG_NAME = "fcltyNm";
    final static String TAG_LOCATION = "rdnmadr";
    final static String TAG_LAT = "latitude";
    final static String TAG_LNG = "longitude";
    final static String TAG_WEEKDAY_OPEN = "weekdayOperOpenHhmm";
    final static String TAG_WEEKDAY_CLOSE = "weekdayOperColseHhmm";
    final static String TAG_SAT_OPEN = "satOperOperOpenHhmm";
    final static String TAG_SAT_CLOSE = "satOperCloseHhmm";
    final static String TAG_HOLI_OPEN = "holidayOperOpenHhmm";
    final static String TAG_HOLI_CLOSE = "holidayCloseOpenHhmm";
    final static String TAG_FREE_USE_TIME = "freeUseTime";

    public ArrayList<BoxDto> parse(String xml){
        ArrayList<BoxDto> resultList = new ArrayList<BoxDto>();
        BoxDto dto = null;

        TagType tagType = TagType.NONE;

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equals(TAG_ITEM)){
                            dto = new BoxDto();
                        }else if(parser.getName().equals(TAG_NAME)){
                            tagType = TagType.NAME;
                        }else if(parser.getName().equals(TAG_LOCATION)){
                            tagType = TagType.LOCATION;
                        }else if(parser.getName().equals(TAG_LAT)){
                            tagType = TagType.LAT;
                        }else if(parser.getName().equals(TAG_LNG)){
                            tagType = TagType.LNG;
                        }else if(parser.getName().equals(TAG_WEEKDAY_OPEN)){
                            tagType = TagType.WEEKDAYOPEN;
                        }else if(parser.getName().equals(TAG_WEEKDAY_CLOSE)){
                            tagType = TagType.WEEKDAYCLOSE;
                        }else if(parser.getName().equals(TAG_SAT_OPEN)){
                            tagType = TagType.SATOPEN;
                        }else if(parser.getName().equals(TAG_SAT_CLOSE)){
                            tagType = TagType.SATCLOSE;
                        }else if(parser.getName().equals(TAG_HOLI_OPEN)){
                            tagType = TagType.HOLIOPEN;
                        }else if(parser.getName().equals(TAG_HOLI_CLOSE)){
                            tagType = TagType.HOLICLOSE;
                        }else if(parser.getName().equals(TAG_FREE_USE_TIME)){
                            tagType = TagType.FREEUSETIME;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(TAG_ITEM)){
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType){
                            case NAME:
                                dto.setFcltyNm(parser.getText());
                                break;
                            case LOCATION:
                                dto.setLocation(parser.getText());
                                break;
                            case LAT:
                                dto.setLatitude(Double.parseDouble(parser.getText()));
                                break;
                            case LNG:
                                dto.setLongitude(Double.parseDouble(parser.getText()));
                                break;
                            case WEEKDAYOPEN:
                                dto.setWeekdayOperOpenHhmm(parser.getText());
                                break;
                            case WEEKDAYCLOSE:
                                dto.setWeekdayOperColseHhmm(parser.getText());
                                break;
                            case SATOPEN:
                                dto.setSatOperOperOpenHhmm(parser.getText());
                                break;
                            case SATCLOSE:
                                dto.setSatOperCloseHhmm(parser.getText());
                                break;
                            case HOLIOPEN:
                                dto.setHolidayOperOpenHhmm(parser.getText());
                                break;
                            case HOLICLOSE:
                                dto.setHolidayCloseOpenHhmm(parser.getText());
                                break;
                            case FREEUSETIME:
                                dto.setFreeUseTime(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return resultList;
    }
}

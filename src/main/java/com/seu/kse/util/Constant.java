package com.seu.kse.util;

public class Constant {
    public static String URL = "120.78.165.80";
    public static final String CURRENT_USER = "LOGIN_USER";
    public static String sender = "877717444@qq.com";
    public static String emailhost = "smtp.qq.com";
    public static String paperinfoURL = "http://"+URL+"8090/AIOnlineRecommedation/paperinfo?id=";
    public static String ES_INDEX = "reasearch_kg";
    public static String ES_TYPE = "paper";
    public static String[] ES_SEARCH_FIELDS = {"title","paper_abstract"};


    public static String ES_FIELD_ABSTRACT = "paper_abstract";
    public static String ES_FIELD_TITLE = "title";
    public static String ES_FIELD_AUTHOR = "author";
    public static String ES_FIELD_ID = "id";
    public static String ES_FIELD_KEYWORDS = "keywords";
    public static String ES_FIELD_TIME = "time";
    public static String ES_FIELD_URL = "url";
    public static String ES_FIELD_TYPE = "type";
    public static String ES_FIELD_PUBLISHER = "publisher";
    public static String[] ES_FIELDS ={ES_FIELD_ABSTRACT,ES_FIELD_TITLE,ES_FIELD_AUTHOR,ES_FIELD_ID,
            ES_FIELD_KEYWORDS,ES_FIELD_TIME,ES_FIELD_URL,ES_FIELD_TYPE,ES_FIELD_PUBLISHER};
}

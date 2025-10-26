package config;

import io.restassured.RestAssured;

public class URLBase {
    public static void setUp(){
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
    }
}
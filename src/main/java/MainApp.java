import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainApp implements Runnable {

    private Scanner scanner;

    private void startApp() {
        scanner = new Scanner(System.in);
        System.out.println("\nWybierz, po czym chcesz znaleźć miejsce, dla którego wyświetlisz pogodę: \n0 - Zakończ działanie \n1 - Nazwa Miasta - pogoda aktualna \n2 - Kod pocztowy - pogoda aktualna \n3 - Nazwa miasta - pogoda na 5 dni \n4 - Kod pocztowy - pogoda na 5 dni");
        Integer name = scanner.nextInt();
        chooseTypeSearching(name);
    }

    private void chooseTypeSearching(Integer typeNumber) {
        switch (typeNumber) {
            case 0:
                break;
            case 1:
                parseJson(connectByCityName(getCityName()));
                startApp();
                break;
            case 2:
                parseJson(connectByZipCode(getZipCode()));
                startApp();
                break;
            case 3:
                parseJson5DayWeather(connectByWeatherFor5Days(getCityName()));
                startApp();
                break;
            case 4:
                parseJson5DayWeather(connectByWeatherFor5DaysByZipCode(getZipCode()));
                startApp();
                break;
        }
    }


    public String getCityName() {
        System.out.println("Podaj nazwę miasta: ");
        String cityName = scanner.next();
        return cityName;
    }

    public String connectByCityName(String cityName) {
        String json = null;
        try {
            json = new HttpService().connect(Config.APP_URL + "?q=" + cityName + "&appid=" + Config.APP_ID);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getZipCode() {
        System.out.println("Podaj kod pocztowy miasta: ");
        String zipCode = scanner.next();
        return zipCode;
    }

    public String connectByZipCode(String zipCode) {
        String json = null;
        try {
            json = new HttpService().connect(Config.APP_URL + "?zip=" + zipCode + ",pl" + "&appid=" + Config.APP_ID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    public String connectByWeatherFor5Days(String cityName) {
        String json = null;
        try {
            json = new HttpService().connect(Config.APP_URL_5DAY + "?q=" + cityName + "&appid=" + Config.APP_ID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String connectByWeatherFor5DaysByZipCode(String zipCode) {
        String json = null;
        try {
            json = new HttpService().connect(Config.APP_URL_5DAY + "?zip=" + zipCode + ",pl" + "&appid=" + Config.APP_ID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    private void parseJson(String json) {
        double temp;
        int humidity;
        int pressure;
        int clouds;

        JSONObject rootObject = new JSONObject(json);
        if (rootObject.getInt("cod") == 200) {
            JSONObject mainObject = rootObject.getJSONObject("main");
            DecimalFormat df = new DecimalFormat("#.##");
            temp = mainObject.getDouble("temp");
            temp = temp - 273;

            humidity = mainObject.getInt("humidity");
            pressure = mainObject.getInt("pressure");
            JSONObject cloudsObject = rootObject.getJSONObject("clouds");
            clouds = cloudsObject.getInt("all");

            System.out.println("Temperatura: " + df.format(temp) + " \u00b0C");
            System.out.println("Wilgotność: " + humidity + " %");
            System.out.println("Zachmurzenie: " + clouds + "%");
            System.out.println("Ciśnienie: " + pressure + " hPa");

        } else {
            System.out.println("Error");
        }
    }

    private void parseJson5DayWeather(String json) {
        double temp;
        int wind;
        int pressure;
        int clouds;
        String dt_txt;

        JSONObject rootObject = new JSONObject(json);
        JSONArray weather5DayList = rootObject.getJSONArray("list");

        if (rootObject.getInt("cod") == 200) {
            JSONArray jsonArrayMain = rootObject.getJSONArray("list");
            for (int i = 0; i < jsonArrayMain.length(); i++) {
                JSONObject dayObject = weather5DayList.getJSONObject(i);
                JSONObject mainObject = dayObject.getJSONObject("main");
                DecimalFormat df = new DecimalFormat("#.##");
                temp = mainObject.getDouble("temp");
                temp = temp - 273;

                JSONObject windObject = dayObject.getJSONObject("wind");
                wind = windObject.getInt("speed");
                pressure = mainObject.getInt("pressure");
                JSONObject cloudsObject = dayObject.getJSONObject("clouds");
                clouds = cloudsObject.getInt("all");
                dt_txt = dayObject.getString("dt_txt");

                System.out.println("\nData i godzina: " + dt_txt);
                System.out.print("Temperatura: " + df.format(temp) + " \u00b0C");
                System.out.print("  Wiatr: " + wind + " m/s");
                System.out.print("  Zachmurzenie: " + clouds + "%");
                System.out.print("  Ciśnienie: " + pressure + " hPa");

            }
        } else {
            System.out.println("Error");
        }
    }

    @Override
    public void run() {
        startApp();
    }
}

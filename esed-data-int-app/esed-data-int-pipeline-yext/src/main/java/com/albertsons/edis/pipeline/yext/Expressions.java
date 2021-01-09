package com.albertsons.edis.pipeline.yext;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.JsonPath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Expressions {
    private static ImmutableMap<String, Class<? extends Function>> functions;
    private static Gson gson = new Gson();

    static {
        Map<String, Class<? extends Function>> map = new HashMap<>();
        Class<?>[] classes = Expressions.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (!Modifier.isAbstract(c.getModifiers()) && Function.class.isAssignableFrom(c)) {
                String name = c.getSimpleName();
                name = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(name);
                map.put(name, (Class<? extends Function>) c);
            }
        }
        functions = ImmutableMap.copyOf(map);
    }

    private Expressions() {
    }

    public static JsonElement evaluate(JsonElement input, String expression) {
        JsonElement value = input;
        String[] exps = expression.split("\\)\\.");

        for (int i = 0; i < exps.length; i++) {
            exps[i] = exps[i].trim();
            if (!exps[i].endsWith(")")) {
                exps[i] = exps[i] + ")";
            }
            value = create(exps[i].trim()).execute(value);
        }

        return value;
    }

    static Function create(String expression) {
        Function function = null;
        if (expression.contains("(") && expression.endsWith(")")) {
            int index = expression.indexOf("(");
            String name = expression.substring(0, index);
            String parameters = expression.substring(index + 1, expression.length() - 1);
            String[] args = new String[0];
            if (parameters.trim().length() > 0) {
                args = parameters.split(",");
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].trim();
                    if (args[i].startsWith("'") && args[i].endsWith("'")) {
                        args[i] = args[i].substring(1, args[i].length() - 1);
                    }
                }
            }

            Class<?> clazz = functions.get(name);
            try {
                function = (Function) clazz.getConstructors()[0].newInstance(new Object[]{args});
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return function;
    }

    static abstract class Function {
        protected String[] args;

        public Function(String[] args) {
            this.args = args;
        }

        public abstract JsonElement execute(JsonElement value);
    }

    static class Truncat extends Function {

        public Truncat(String[] args) {
            super(args);
        }

        @Override
        public JsonElement execute(JsonElement value) {
            String string = value.getAsString();
            if (args.length == 2) {
                int start = Integer.parseInt(args[0]);
                int end = Integer.parseInt(args[1]);
                if (string.length() < end) {

                } else {
                    string = string.substring(start, end);
                }
            }

            return new JsonPrimitive(string);
        }
    }

    static class ShiftRight extends Function {

        public ShiftRight(String[] args) {
            super(args);
        }

        @Override
        public JsonElement execute(JsonElement value) {
            String st = value.getAsString();
            while (st.contains(args[0])) {
                st = st.substring(0, st.length() - 1);
            }
            return new JsonPrimitive(st);
        }
    }

    static class FillLeft extends Function {

        public FillLeft(String[] args) {
            super(args);
        }

        @Override
        public JsonElement execute(JsonElement value) {
            String st = value.getAsString();
            int len = Integer.parseInt(args[0]);
            String token = args[1];
            while (st.length() < len) {
                st = token + st;
            }

            return new JsonPrimitive(st);
        }
    }

    static class FromJson extends Function {

        public FromJson(String[] args) {
            super(args);
        }

        @Override
        public JsonElement execute(JsonElement value) {
            if (value != null) {
                return gson.toJsonTree(value);
            }
            return new JsonPrimitive("");
        }
    }

    static class Jsonpath extends Function {

        public Jsonpath(String[] args) {
            super(args);
        }

        @Override
        public JsonElement execute(JsonElement value) {
            try {
                String exp = args[0];
                String result = JsonPath.parse(new Gson().toJson(value))
                        .read(exp);
                return new JsonPrimitive(result);

            } catch (Exception e) {
                return null;
            }
        }
    }

    public static void main(String[] args) {

        String exp = "$.customFields.16236";
        String json = "{\"id\":\"1058\",\"uid\":\"rodyq3\",\"timestamp\":1574698488212,\"timezone\":\"America/New_York\",\"accountId\":\"1402918\",\"locationName\":\"ACME Markets\",\"address\":\"125 Franklin Turnpike\",\"city\":\"Mahwah\",\"state\":\"NJ\",\"zip\":\"07430\",\"countryCode\":\"US\",\"language\":\"en\",\"phone\":\"2015299775\",\"isPhoneTracked\":false,\"categoryIds\":[\"1276932\",\"1143\"],\"googleAttributes\":[{\"id\":\"has_salad_bar\",\"customValues\":[\"true\"]},{\"id\":\"sells_organic_products\",\"customValues\":[\"true\"]},{\"id\":\"sells_food_bulk\",\"customValues\":[\"false\"]},{\"id\":\"sells_goods_bulk\",\"customValues\":[\"false\"]},{\"id\":\"sells_food_prepared\",\"customValues\":[\"true\"]},{\"id\":\"has_gift_wrapping\",\"customValues\":[\"false\"]},{\"id\":\"has_restroom_public\",\"customValues\":[\"true\"]},{\"id\":\"has_help_desk\",\"customValues\":[\"true\"]}],\"featuredMessage\":\"Ads, justforU Digital Coupons & Earn Gas Rewards.\",\"featuredMessageUrl\":\"[[WEBSITE URL]]\",\"websiteUrl\":\"[[Pages URL]]\",\"displayWebsiteUrl\":\"[[Pages URL]]\",\"description\":\"Visit your neighborhood [[NAME]] located at [[ADDRESS]], [[CITY]], [[STATE]], for a convenient and friendly grocery experience! From our bakery and deli, to fresh produce and helpful pharmacy staff, we’ve got you covered! Our bakery features customizable cakes, cupcakes and more while the deli offers a variety of party trays, made to order. At the butcher block you’ll find an assortment of meat and seafood, even offering sushi in select locations, while the produce department is full of fresh fruits and veggies galore! Shop the floral department for exclusive debi lily design™ products and services and stop by the pharmacy for specialty care, including immunizations, prescription refills and so much more!\\n \\n[[NAME]] is dedicated to being your one-stop-shop and provides an in-store bank, Coin Star system, and Western Union in select locations! Further enhance your shopping experience by grabbing a hot cup of coffee at your in-store Starbucks or Seattle’s Best/Buck’s County Drip Coffee and enjoy renting a movie from Redbox or DVD xpress. Check out our Weekly Ad for store savings, earn Gas Rewards with purchases and download our [[NAME]] app for just for U® personalized offers. For more information, stop by or call [[PHONE]]. Our service will make [[NAME]] your favorite local supermarket!\",\"logo\":{\"url\":\"http://a.mktgcdn.com/p/eLIw18Xjsl9s9ldLL9x6tZo84MnwWePXGnELYozJf4o/346x346.png\",\"alternateText\":\"Acme Logo\",\"width\":346,\"height\":346,\"derivatives\":[{\"url\":\"http://a.mktgcdn.com/p/eLIw18Xjsl9s9ldLL9x6tZo84MnwWePXGnELYozJf4o/150x150.png\",\"width\":150,\"height\":150},{\"url\":\"http://a.mktgcdn.com/p/eLIw18Xjsl9s9ldLL9x6tZo84MnwWePXGnELYozJf4o/93x93.png\",\"width\":93,\"height\":93}]},\"hours\":\"1:6:00:22:00,2:6:00:0:00,3:6:00:0:00,4:6:00:0:00,5:6:00:0:00,6:6:00:0:00,7:6:00:23:00\",\"holidayHours\":[{\"date\":\"2019-11-27\",\"hours\":\"6:00:0:00\",\"isRegularHours\":false},{\"date\":\"2019-11-28\",\"hours\":\"6:00:17:00\",\"isRegularHours\":false},{\"date\":\"2019-11-29\",\"hours\":\"6:00:0:00\",\"isRegularHours\":false}],\"twitterHandle\":\"AcmeMarkets\",\"facebookPageUrl\":\"https://www.facebook.com/ACMEMarketsMahwahNJ/\",\"facebookCoverPhoto\":{\"url\":\"http://a.mktgcdn.com/p/Y39YB3vcBrlnWjkxrPGa9l7GQPlRKBngaGhGDTnk3Lc/720x513.jpg\",\"width\":720,\"height\":513,\"sourceUrl\":\"https://scontent.xx.fbcdn.net/v/t31.0-8/s720x720/20229801_10155238435693145_1772996870620220907_o.jpg?oh=8964854376611fab7a162b103566e9ef&oe=5A30E990\"},\"facebookProfilePicture\":{\"url\":\"http://a.mktgcdn.com/p/JnB0FJu1rDiUqzRmt6LC9Qfff7CViah0G67tyRTg1Ks/200x200.jpg\",\"width\":200,\"height\":200,\"sourceUrl\":\"https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/19989231_10155226082563145_5128333117981189589_n.jpg?_nc_cat=0&oh=62e2f12f84cefa42b1cc12e3fdf02be7&oe=5C10D65A\"},\"googleCoverPhoto\":{\"url\":\"http://a.mktgcdn.com/p/1GKHxChnC02D8OukiE4tkLT_FZYzjSDA-2Fwxf5KLNo/480x270.jpg\",\"width\":480,\"height\":270},\"googleProfilePhoto\":{\"url\":\"http://a.mktgcdn.com/p/-HXWpSbluOclMbBgg3ebzq_ohTMZxDQ25OE2tk4ir0c/1382x1382.png\",\"width\":1382,\"height\":1382,\"derivatives\":[{\"url\":\"http://a.mktgcdn.com/p/-HXWpSbluOclMbBgg3ebzq_ohTMZxDQ25OE2tk4ir0c/500x500.png\",\"width\":500,\"height\":500}],\"sourceUrl\":\"http://a.mktgcdn.com/p/-HXWpSbluOclMbBgg3ebzq_ohTMZxDQ25OE2tk4ir0c/1382x1382.png\"},\"paymentOptions\":[\"AMERICANEXPRESS\",\"ANDROIDPAY\",\"APPLEPAY\",\"CASH\",\"CHECK\",\"DINERSCLUB\",\"DISCOVER\",\"MASTERCARD\",\"SAMSUNGPAY\",\"TRAVELERSCHECK\",\"VISA\"],\"yextDisplayLat\":41.0990633,\"yextDisplayLng\":-74.1446481,\"yextRoutableLat\":41.09961600897585,\"yextRoutableLng\":-74.14696417283267,\"yextWalkableLat\":41.09934617469736,\"yextWalkableLng\":-74.14511344869419,\"services\":[\"Boars Head\",\"Coinstar\",\"debi lilly design™ Destination\",\"DVDXpress\",\"Fed Ex Drop Off and Pick Up Location\",\"Gift Card Mall\",\"Rush Delivery\",\"Rush Pick Up\",\"Salad Bar\",\"Starbucks\",\"Sushi\",\"Western Union\"],\"folderId\":\"121945\",\"customFields\":{\"13548\":\"77\",\"13549\":\"Acme\",\"13550\":\"34\",\"13622\":\"5631\",\"13637\":\"5655\",\"13638\":\"true\",\"13761\":\"Grocery\",\"13866\":\"1058\",\"16168\":{\"url\":\"http://a.mktgcdn.com/p/eEyncur8EDvbaPWs-xw7Do6fLBpvGRBlfjf_021mSbs/432x250.png\",\"alternateText\":\"Acme Markets store front picture at 125 Franklin Turnpike in Mahwah, NJ\",\"width\":432,\"height\":250,\"derivatives\":[{\"url\":\"http://a.mktgcdn.com/p/eEyncur8EDvbaPWs-xw7Do6fLBpvGRBlfjf_021mSbs/196x113.png\",\"width\":196,\"height\":113}]},\"16169\":\"Mahwah\",\"16170\":\"[[STORE ID]]-P\",\"16172\":\"http://www.acmemarkets.com/pharmacy/\",\"16174\":\"Find Great Deals at Your Local [[NAME]]\",\"16175\":\"https://www.acmemarkets.com/set-store.html?storeId=1058&target=weeklyad\",\"16176\":\"Visit your neighborhood [[NAME]] located at [[ADDRESS]], [[CITY]], [[STATE]], for a convenient and friendly grocery experience! From our bakery and deli, to fresh produce and helpful pharmacy staff, we’ve got you covered! Our bakery features customizable cakes, cupcakes and more while the deli offers a variety of party trays, made to order. At the butcher block you’ll find an assortment of meat and seafood, even offering sushi in select locations, while the produce department is full of fresh fruits and veggies galore! Shop the floral department for exclusive debi lily design™ products and services and stop by the pharmacy for specialty care, including immunizations, prescription refills and so much more!\\n \\n[[NAME]] is dedicated to being your one-stop-shop and provides an in-store bank, Coin Star system, and Western Union in select locations! Further enhance your shopping experience by grabbing a hot cup of coffee at your in-store Starbucks or Seattle’s Best/Buck’s County Drip Coffee and enjoy renting a movie from Redbox or DVD xpress. Check out our Weekly Ad for store savings, earn Gas Rewards with purchases and download our [[NAME]] app for just for U® personalized offers. For more information, stop by or call [[PHONE]]. Our service will make [[NAME]] your favorite local supermarket!\",\"16178\":[{\"url\":\"http://a.mktgcdn.com/p/cRvrlfEj8qSrjVoJtYh-lVfZETGAmL6PJu2qM0JgO1M/25x25.png\",\"description\":\"Fresh Produce\",\"width\":25,\"height\":25,\"sourceUrl\":\"http://a.mktgcdn.com/p/cRvrlfEj8qSrjVoJtYh-lVfZETGAmL6PJu2qM0JgO1M/25x25.png\"},{\"url\":\"http://a.mktgcdn.com/p/mKpzCqATfl3tg5YzNkOkcvXUog6qzRlQyGzF3npIXq0/26x26.png\",\"description\":\"Fresh Meat\",\"width\":26,\"height\":26,\"sourceUrl\":\"http://a.mktgcdn.com/p/mKpzCqATfl3tg5YzNkOkcvXUog6qzRlQyGzF3npIXq0/26x26.png\"},{\"url\":\"http://a.mktgcdn.com/p/TYHG0wfAAXagQY5wYBzd0lJoh4Ua0vD7DT6px9TkE2g/27x27.png\",\"description\":\"Fresh Seafood\",\"width\":27,\"height\":27,\"sourceUrl\":\"http://a.mktgcdn.com/p/TYHG0wfAAXagQY5wYBzd0lJoh4Ua0vD7DT6px9TkE2g/27x27.png\"},{\"url\":\"http://a.mktgcdn.com/p/Hy6oc2apBjwKDNx4ZNZfbkTIxEImLBhUT07GTWiGax0/28x28.png\",\"description\":\"Bakery\",\"width\":28,\"height\":28,\"sourceUrl\":\"http://a.mktgcdn.com/p/Hy6oc2apBjwKDNx4ZNZfbkTIxEImLBhUT07GTWiGax0/28x28.png\"},{\"url\":\"http://a.mktgcdn.com/p/MalTTrhR9N8SaGFQT8I7wZcF4wXyuKpxTtPQG2peC2U/29x29.png\",\"description\":\"Service Deli\",\"width\":29,\"height\":29,\"sourceUrl\":\"http://a.mktgcdn.com/p/MalTTrhR9N8SaGFQT8I7wZcF4wXyuKpxTtPQG2peC2U/29x29.png\"}],\"16236\":\"https://local.acmemarkets.com/nj/mahwah/125-franklin-turnpike.html\",\"16399\":[\"China Union\",\"JCB\",\"EBT\",\"WIC\",\"eWIC\",\"FSA Flex Spending Cards\",\"Gift Cards\"],\"17089\":\"false\",\"17928\":\"true\",\"17932\":\"true\",\"17934\":\"http://www1.acmemarkets.com/ShopStores/RewardPoints.page?fuelid=341&cmpid=strlocat_fuel341_acme\",\"17935\":\"false\",\"17944\":[\"7163\",\"7305\",\"7307\",\"7308\",\"7309\",\"7618\",\"7619\"],\"18651\":\"128158\",\"18652\":\"2015-10-18\",\"18780\":\"Acme\",\"18784\":\"341\",\"18786\":\"5-341\",\"23521\":\"true\",\"23762\":\"true\",\"23763\":\"false\",\"23899\":\"10751\",\"24424\":\"true\",\"24425\":\"http://www1.acmemarkets.com/ShopStores/grocery-rewards.page?reward_id=5-341&cmpid=strlocat_groceryrewards_5-341_acme\",\"24491\":\"15333\",\"24985\":\"false\",\"24986\":\"false\",\"24987\":\"true\",\"24988\":\"true\",\"24989\":\"true\",\"24990\":\"true\",\"24991\":\"false\",\"24992\":\"false\",\"24993\":\"false\",\"24994\":\"false\",\"24995\":\"false\",\"24996\":\"true\",\"25008\":\"false\",\"25009\":\"false\",\"25010\":\"false\",\"25011\":\"false\",\"25012\":\"true\",\"25013\":\"false\",\"25014\":\"false\",\"25015\":\"false\",\"25016\":\"false\",\"25017\":\"false\",\"25018\":\"false\",\"25019\":\"false\",\"25020\":\"false\",\"25021\":\"true\",\"25022\":\"false\",\"25023\":\"false\",\"25024\":\"false\",\"25025\":\"false\",\"25026\":\"false\",\"25027\":\"false\",\"25028\":\"false\",\"25029\":\"false\",\"25030\":\"false\",\"25031\":\"false\",\"25032\":\"false\",\"25033\":\"false\",\"25034\":\"false\",\"25035\":\"false\",\"25036\":\"false\",\"25037\":\"false\",\"25038\":\"false\",\"25039\":\"false\",\"25040\":\"false\",\"25041\":\"false\",\"25042\":\"false\",\"25043\":\"false\",\"25044\":\"false\",\"25045\":\"false\",\"25046\":\"false\",\"25047\":\"false\",\"25056\":\"false\",\"25158\":\"false\",\"25159\":\"false\",\"25960\":\"false\",\"27135\":\"false\",\"27136\":\"false\",\"27137\":\"false\",\"27138\":\"false\",\"27139\":\"false\",\"27141\":\"false\",\"27142\":\"false\",\"27182\":\"false\",\"27183\":\"false\",\"27184\":\"false\",\"27255\":\"false\",\"27256\":\"false\",\"27257\":\"false\",\"28160\":\"false\",\"28927\":\"Acme Market\",\"30438\":\"false\",\"30996\":\"true\",\"31022\":\"$1.00\",\"31023\":\"$1.00\",\"31063\":\"false\",\"31064\":\"false\",\"32153\":\"false\",\"32154\":\"false\",\"32155\":\"false\",\"32156\":\"false\",\"32157\":\"false\",\"32158\":\"false\",\"32184\":\"false\",\"32185\":\"false\",\"32941\":\"false\",\"32942\":\"false\",\"35970\":\"false\",\"35971\":\"false\",\"35972\":\"false\",\"35973\":\"false\",\"35974\":\"false\",\"35975\":\"false\",\"35976\":\"false\",\"69748\":\"false\",\"69753\":\"false\",\"69761\":\"false\",\"69767\":\"false\",\"69772\":\"false\",\"69778\":\"false\",\"94887\":\"false\",\"95097\":\"false\",\"95227\":\"false\",\"95965\":\"acmemarkets\",\"98320\":\"false\",\"98321\":\"false\",\"98378\":\"false\",\"98407\":\"true\",\"98439\":\"5/12/17\",\"98440\":\"54571\",\"100082\":\"false\",\"100830\":\"true\",\"101203\":\"false\",\"101345\":\"false\",\"101346\":\"false\",\"101524\":\"false\",\"101525\":\"false\",\"101526\":\"false\",\"101527\":\"false\",\"101528\":\"false\",\"101529\":\"false\",\"101530\":\"false\",\"101595\":\"false\",\"101596\":\"false\",\"101597\":\"false\",\"101598\":\"false\",\"101599\":\"false\",\"101600\":\"false\",\"101601\":\"false\",\"101602\":\"false\",\"101603\":\"false\",\"101604\":\"false\",\"101605\":\"false\",\"101606\":\"false\",\"101607\":\"false\",\"101608\":\"false\",\"101609\":\"false\",\"101610\":\"false\",\"101759\":\"false\",\"103871\":\"false\",\"104700\":\"5044796\",\"105228\":[\"4048861\"],\"105229\":\"328\",\"105230\":\"4048864\",\"105568\":\"false\",\"105569\":\"false\",\"165336\":\"true\",\"166130\":\"false\",\"167485\":\"false\",\"167715\":\"false\",\"167813\":\"false\",\"167814\":\"false\",\"167815\":\"false\",\"167816\":\"false\",\"167817\":\"false\",\"167818\":\"false\",\"167819\":\"false\",\"167820\":\"false\",\"167821\":\"false\",\"167822\":\"false\",\"167823\":\"false\",\"167824\":\"false\",\"168540\":\"false\",\"169460\":\"false\",\"169597\":\"false\",\"169814\":\"false\",\"169815\":\"false\",\"169816\":\"false\",\"169817\":\"false\",\"169818\":\"false\",\"169819\":\"false\",\"169820\":\"false\",\"169821\":\"false\",\"169822\":\"false\",\"169823\":\"false\",\"169824\":\"false\",\"169825\":\"false\",\"169906\":\"false\",\"179637\":\"false\",\"179638\":\"false\",\"179639\":\"false\",\"179640\":\"false\",\"179641\":\"false\",\"179642\":\"false\",\"179643\":\"false\",\"179644\":\"false\",\"179645\":\"false\",\"179646\":\"false\",\"179647\":\"false\",\"179648\":\"false\",\"179649\":\"false\",\"179650\":\"false\",\"179651\":\"false\",\"179652\":\"false\",\"179653\":\"false\",\"179654\":\"false\",\"179655\":\"false\",\"179656\":\"false\",\"179657\":\"false\",\"179658\":\"false\",\"179659\":\"false\",\"179660\":\"false\",\"179661\":\"false\",\"179662\":\"false\",\"179663\":\"false\",\"179664\":\"false\",\"179665\":\"false\",\"179666\":\"false\",\"179667\":\"false\",\"179668\":\"false\",\"179669\":\"false\",\"179670\":\"false\",\"179779\":\"false\",\"179780\":\"false\",\"179781\":\"false\",\"181443\":\"false\",\"181489\":\"false\",\"181490\":\"false\",\"181491\":\"false\",\"181492\":\"false\"},\"instagramHandle\":\"acmemarkets\",\"labelIds\":[\"80500\"],\"menusLabel\":\"\",\"menuIds\":[],\"bioListsLabel\":\"\",\"bioListIds\":[],\"productListsLabel\":\"\",\"productListIds\":[\"1184796\",\"1091451\",\"586494\",\"1218109\",\"854949\",\"1176738\"],\"eventListsLabel\":\"\",\"eventListIds\":[\"Events - 1058\"],\"locationType\":\"LOCATION\",\"uberLink\":\"http://a.gotoloc.com/uber/E3QiTaOGWQ\",\"uberLinkRaw\":\"https://m.uber.com/ul/?client_id=KXQcwoj2Zb8ymDzKgVgbIaDE5iAE_TAj&action=setPickup&pickup=my_location&dropoff%5Bnickname%5D=ACME%20Markets&dropoff%5Blatitude%5D=41.09961600897585&dropoff%5Blongitude%5D=-74.14696417283267&dropoff%5Bformatted_address%5D=125%20Franklin%20Turnpike%2C%2CMahwah%2CNJ\",\"intelligentSearchTrackingEnabled\":true,\"intelligentSearchTrackingFrequency\":\"WEEKLY\",\"locationKeywords\":[\"NAME\",\"PRIMARY_CATEGORY\"],\"queryTemplates\":[\"KEYWORD\",\"KEYWORD_ZIP\",\"KEYWORD_CITY\",\"KEYWORD_NEAR_ME\"],\"alternateNames\":[\"Acme Grocery\",\"Acme Market\",\"Acme Store\"],\"alternateWebsites\":[\"http://www.acmemarkets.com\"],\"competitors\":[{\"name\":\"Giant\",\"website\":\"https://giantfoodstores.com\"},{\"name\":\"Stop & Shop\",\"website\":\"https://stopandshop.com\"},{\"name\":\"ShopRite\",\"website\":\"http://www.shoprite.com\"},{\"name\":\"Walmart\",\"website\":\"https://www.walmart.com\"}],\"trackingSites\":[\"GOOGLE_DESKTOP\",\"GOOGLE_MOBILE\",\"BING_DESKTOP\",\"YAHOO_DESKTOP\"],\"isoRegionCode\":\"NJ\",\"schemaTypes\":[\"GroceryStore\"],\"googleAccountId\":\"106236667470735976785\"}";

        System.out.println(new Jsonpath(new String[]{exp}).execute(JsonParser.parseString(json)));
    }

}

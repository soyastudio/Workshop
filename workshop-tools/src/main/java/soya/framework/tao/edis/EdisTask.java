package soya.framework.tao.edis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EdisTask {

    protected static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static class Mapping {
        protected String rule;
        protected String source;

    }

    static class Unknown {

    }

}

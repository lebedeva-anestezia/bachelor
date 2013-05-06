package ru.ifmo.mailru.google.pr;

/**
 * @author Anastasia Lebedeva
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * <b>PageRankService provides simple API to Google PageRank Technology</b>
 * <br>
 * PageRankService queries google toolbar webservice and returns a
 * google page rank retrieved from one of the available datacenters.
 * Each new connection created by PageRankService will be made to a ip on the list
 * this was we will not get google security ringing their bells... ;-) ...as fast...
 */
public class PageRankService {

    static private int dataCenterIdx = 0;

    /**
     * List of available google datacenter IPs and addresses
     */
    static final public String [] GOOGLE_PR_DATACENTER_IPS = new String[]{
//                "www.google.com",
//                "64.233.161.101",
//                "64.233.177.17",
//                "64.233.183.91",
//                "64.233.185.19",
//                "64.233.189.44",
//                "66.102.1.103",
//                "66.102.9.115",
//                "66.249.81.101",
//                "66.249.89.83",
//                "66.249.91.99",
//                "66.249.93.190",
//                "72.14.203.107",
//                "72.14.205.113",
//                "72.14.255.107",
            "toolbarqueries.google.com",
    };

    /**
     * Must receive a domain in form of: "http://www.domain.com"
     * @param domain - (String)
     * @return PR rating (int) or -1 if unavailable or internal error happened.
     */

    public int getPR(String domain) throws IOException {

        int result = -1;
        JenkinsHash jHash = new JenkinsHash();

        String googlePrResult = "";

        long hash = jHash.hash(("info:" + domain).getBytes());

        String url = "http://"+GOOGLE_PR_DATACENTER_IPS[dataCenterIdx]+"/tbr?client=navclient-auto&hl=en&"+
                "ch=6"+hash+"&ie=UTF-8&oe=UTF-8&features=Rank&q=info:" + domain;


        URLConnection con = new URL(url).openConnection();
        InputStream is = con.getInputStream();
        byte [] buff = new byte[1024];
        int read = is.read(buff);
        while (read > 0) {
            googlePrResult = new String(buff, 0, read);
            read = is.read(buff);
        }
        try {
            googlePrResult = googlePrResult.split(":")[2].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
        result = new Long(googlePrResult).intValue();


        dataCenterIdx++;
        if (dataCenterIdx == GOOGLE_PR_DATACENTER_IPS.length) {
            dataCenterIdx = 0;
        }

        return result;
    }
}


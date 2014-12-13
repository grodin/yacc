/*
 * Copyright 2014 Omricat Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
   For step-by-step instructions on connecting your Android application to
   this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree
   /master/HelloWorld
*/

package com.omricat.yacc.backend.servlets;

import com.omricat.yacc.backend.util.HttpUtils;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticTestServlet extends HttpServlet {

    final static String RESPONSE = "{\"timestamp\":\"1415210401\"," +
            "\"currencies\":[{\"value\":\"1\",\"code\":\"USD\"}," +
            "{\"value\":\"3.6732\",\"code\":\"AED\"},{\"value\":\"57.34\"," +
            "\"code\":\"AFN\"},{\"value\":\"111.42\",\"code\":\"ALB\"}," +
            "{\"value\":\"413.51\",\"code\":\"AMD\"},{\"value\":\"1.7725\"," +
            "\"code\":\"ANG\"},{\"value\":\"99.7335\",\"code\":\"AOA\"}," +
            "{\"value\":\"8.5096\",\"code\":\"ARS\"},{\"value\":\"1.1653\"," +
            "\"code\":\"AUD\"},{\"value\":\"1.79\",\"code\":\"AWG\"}," +
            "{\"value\":\"0.7844\",\"code\":\"AZN\"},{\"value\":\"1.5689\"," +
            "\"code\":\"BAM\"},{\"value\":\"2.00\",\"code\":\"BBD\"}," +
            "{\"value\":\"77.305\",\"code\":\"BDT\"},{\"value\":\"1.5722\"," +
            "\"code\":\"BGN\"},{\"value\":\"0.3771\",\"code\":\"BHD\"}," +
            "{\"value\":\"1555.00\",\"code\":\"BIF\"},{\"value\":\"1.00\"," +
            "\"code\":\"BMD\"},{\"value\":\"1.2955\",\"code\":\"BND\"}," +
            "{\"value\":\"6.91\",\"code\":\"BOB\"},{\"value\":\"2.5141\"," +
            "\"code\":\"BRL\"},{\"value\":\"1.00\",\"code\":\"BSD\"}," +
            "{\"value\":\"61.44\",\"code\":\"BTN\"},{\"value\":\"9.2166\"," +
            "\"code\":\"BWP\"},{\"value\":\"10750.00\",\"code\":\"BYR\"}," +
            "{\"value\":\"1.995\",\"code\":\"BZD\"},{\"value\":\"1.1385\"," +
            "\"code\":\"CAD\"},{\"value\":\"920.00\",\"code\":\"CDF\"}," +
            "{\"value\":\"0.9636\",\"code\":\"CHF\"},{\"value\":\"0.0242\"," +
            "\"code\":\"CLF\"},{\"value\":\"590.00\",\"code\":\"CLP\"}," +
            "{\"value\":\"6.114\",\"code\":\"CNY\"},{\"value\":\"2082.00\"," +
            "\"code\":\"COP\"},{\"value\":\"540.80\",\"code\":\"CRC\"}," +
            "{\"value\":\"1.00\",\"code\":\"CUP\"},{\"value\":\"87.13\"," +
            "\"code\":\"CVE\"},{\"value\":\"22.245\",\"code\":\"CZK\"}," +
            "{\"value\":\"182.00\",\"code\":\"DJF\"},{\"value\":\"5.9561\"," +
            "\"code\":\"DKK\"},{\"value\":\"44.15\",\"code\":\"DOP\"}," +
            "{\"value\":\"84.095\",\"code\":\"DZD\"},{\"value\":\"7.1445\"," +
            "\"code\":\"EGP\"},{\"value\":\"20.061\",\"code\":\"ETB\"}," +
            "{\"value\":\"0.8005\",\"code\":\"EUR\"},{\"value\":\"1.9099\"," +
            "\"code\":\"FJD\"},{\"value\":\"0.6286\",\"code\":\"FKP\"}," +
            "{\"value\":\"0.6257\",\"code\":\"GBP\"},{\"value\":\"1.751\"," +
            "\"code\":\"GEL\"},{\"value\":\"3.2225\",\"code\":\"GHS\"}," +
            "{\"value\":\"0.6287\",\"code\":\"GIP\"},{\"value\":\"43.10\"," +
            "\"code\":\"GMD\"},{\"value\":\"7135.00\",\"code\":\"GNF\"}," +
            "{\"value\":\"7.6095\",\"code\":\"GTQ\"},{\"value\":\"208.45\"," +
            "\"code\":\"GYD\"},{\"value\":\"7.7523\",\"code\":\"HKD\"}," +
            "{\"value\":\"21.275\",\"code\":\"HNL\"},{\"value\":\"6.1327\"," +
            "\"code\":\"HRK\"},{\"value\":\"46.50\",\"code\":\"HTG\"}," +
            "{\"value\":\"248.45\",\"code\":\"HUF\"},{\"value\":\"12190.00\"," +
            "\"code\":\"IDR\"},{\"value\":\"0.6307\",\"code\":\"IEP\"}," +
            "{\"value\":\"3.8055\",\"code\":\"ILS\"},{\"value\":\"61.425\"," +
            "\"code\":\"INR\"},{\"value\":\"1161.00\",\"code\":\"IQD\"}," +
            "{\"value\":\"26738.00\",\"code\":\"IRR\"},{\"value\":\"122.92\"," +
            "\"code\":\"ISK\"},{\"value\":\"112.45\",\"code\":\"JMD\"}," +
            "{\"value\":\"0.706\",\"code\":\"JOD\"},{\"value\":\"114.635\"," +
            "\"code\":\"JPY\"},{\"value\":\"89.80\",\"code\":\"KES\"}," +
            "{\"value\":\"57.5995\",\"code\":\"KGS\"},{\"value\":\"4069.00\"," +
            "\"code\":\"KHR\"},{\"value\":\"394.275\",\"code\":\"KMF\"}," +
            "{\"value\":\"900.00\",\"code\":\"KPW\"},{\"value\":\"1090.50\"," +
            "\"code\":\"KRW\"},{\"value\":\"0.291\",\"code\":\"KWD\"}," +
            "{\"value\":\"180.915\",\"code\":\"KZT\"},{\"value\":\"8057.50\"," +
            "\"code\":\"LAK\"},{\"value\":\"1513.50\",\"code\":\"LBP\"}," +
            "{\"value\":\"130.875\",\"code\":\"LKR\"},{\"value\":\"84.50\"," +
            "\"code\":\"LRD\"},{\"value\":\"11.16\",\"code\":\"LSL\"}," +
            "{\"value\":\"2.764\",\"code\":\"LTL\"},{\"value\":\"0.5093\"," +
            "\"code\":\"LVL\"},{\"value\":\"1.207\",\"code\":\"LYD\"}," +
            "{\"value\":\"8.8499\",\"code\":\"MAD\"},{\"value\":\"14.535\"," +
            "\"code\":\"MDL\"},{\"value\":\"2705.00\",\"code\":\"MGA\"}," +
            "{\"value\":\"49.38\",\"code\":\"MKD\"},{\"value\":\"1015.00\"," +
            "\"code\":\"MMK\"},{\"value\":\"1864.50\",\"code\":\"MNT\"}," +
            "{\"value\":\"7.985\",\"code\":\"MOP\"},{\"value\":\"290.50\"," +
            "\"code\":\"MRO\"},{\"value\":\"31.85\",\"code\":\"MUR\"}," +
            "{\"value\":\"15.40\",\"code\":\"MVR\"},{\"value\":\"477.30\"," +
            "\"code\":\"MWK\"},{\"value\":\"13.5969\",\"code\":\"MXN\"}," +
            "{\"value\":\"3.3455\",\"code\":\"MYR\"},{\"value\":\"31.25\"," +
            "\"code\":\"MZN\"},{\"value\":\"11.1465\",\"code\":\"NAD\"}," +
            "{\"value\":\"167.45\",\"code\":\"NGN\"},{\"value\":\"26.455\"," +
            "\"code\":\"NIO\"},{\"value\":\"6.8283\",\"code\":\"NOK\"}," +
            "{\"value\":\"99.31\",\"code\":\"NPR\"},{\"value\":\"1.2956\"," +
            "\"code\":\"NZD\"},{\"value\":\"0.3849\",\"code\":\"OMR\"}," +
            "{\"value\":\"1.00\",\"code\":\"PAB\"},{\"value\":\"2.9315\"," +
            "\"code\":\"PEN\"},{\"value\":\"2.5261\",\"code\":\"PGK\"}," +
            "{\"value\":\"45.095\",\"code\":\"PHP\"},{\"value\":\"102.55\"," +
            "\"code\":\"PKR\"},{\"value\":\"3.3851\",\"code\":\"PLN\"}," +
            "{\"value\":\"4626.8999\",\"code\":\"PYG\"}," +
            "{\"value\":\"3.6418\",\"code\":\"QAR\"},{\"value\":\"3.5385\"," +
            "\"code\":\"RON\"},{\"value\":\"95.5105\",\"code\":\"RSD\"}," +
            "{\"value\":\"44.919\",\"code\":\"RUB\"},{\"value\":\"688.00\"," +
            "\"code\":\"RWF\"},{\"value\":\"3.7517\",\"code\":\"SAR\"}," +
            "{\"value\":\"7.4461\",\"code\":\"SBD\"},{\"value\":\"14.105\"," +
            "\"code\":\"SCR\"},{\"value\":\"5.6925\",\"code\":\"SDG\"}," +
            "{\"value\":\"7.3679\",\"code\":\"SEK\"},{\"value\":\"1.2935\"," +
            "\"code\":\"SGD\"},{\"value\":\"0.6287\",\"code\":\"SHP\"}," +
            "{\"value\":\"4365.00\",\"code\":\"SLL\"},{\"value\":\"783.50\"," +
            "\"code\":\"SOS\"},{\"value\":\"3.275\",\"code\":\"SRD\"}," +
            "{\"value\":\"19615.00\",\"code\":\"STD\"},{\"value\":\"8.747\"," +
            "\"code\":\"SVC\"},{\"value\":\"166.75\",\"code\":\"SYP\"}," +
            "{\"value\":\"11.166\",\"code\":\"SZL\"},{\"value\":\"32.845\"," +
            "\"code\":\"THB\"},{\"value\":\"5.0518\",\"code\":\"TJS\"}," +
            "{\"value\":\"2.85\",\"code\":\"TMT\"},{\"value\":\"1.8275\"," +
            "\"code\":\"TND\"},{\"value\":\"1.9889\",\"code\":\"TOP\"}," +
            "{\"value\":\"2.2443\",\"code\":\"TRY\"},{\"value\":\"6.3445\"," +
            "\"code\":\"TTD\"},{\"value\":\"30.64\",\"code\":\"TWD\"}," +
            "{\"value\":\"1705.50\",\"code\":\"TZS\"},{\"value\":\"13.50\"," +
            "\"code\":\"UAH\"},{\"value\":\"2690.00\",\"code\":\"UGX\"}," +
            "{\"value\":\"24.42\",\"code\":\"UYU\"},{\"value\":\"2388.25\"," +
            "\"code\":\"UZS\"},{\"value\":\"6.2877\",\"code\":\"VEF\"}," +
            "{\"value\":\"21233.00\",\"code\":\"VND\"},{\"value\":\"98.10\"," +
            "\"code\":\"VUV\"},{\"value\":\"2.4335\",\"code\":\"WST\"}," +
            "{\"value\":\"525.2698\",\"code\":\"XAF\"},{\"value\":\"2.70\"," +
            "\"code\":\"XCD\"},{\"value\":\"0.6797\",\"code\":\"XDR\"}," +
            "{\"value\":\"525.65\",\"code\":\"XOF\"},{\"value\":\"95.40\"," +
            "\"code\":\"XPF\"},{\"value\":\"214.80\",\"code\":\"YER\"}," +
            "{\"value\":\"11.1481\",\"code\":\"ZAR\"},{\"value\":\"5189.50\"," +
            "\"code\":\"ZMK\"},{\"value\":\"322.355\",\"code\":\"ZWL\"}]}" +
            "";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpUtils.setJsonUTF8ContentType(resp);
        resp.getWriter().print(RESPONSE);
    }

}

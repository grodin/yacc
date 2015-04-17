/*
 * Copyright 2015 Omricat Software
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

package com.omricat.yacc.data;

import com.google.common.collect.ImmutableSet;
import com.omricat.yacc.data.model.Currency;

import java.util.Collection;

public class TestCurrencies {
    public final static Currency CNY = new Currency("6.1993", "CNY",
            "Chinese Yuan");
    public final static Currency LVL = new Currency("0.6594", "LVL",
            "Latvian Lats");
    public final static Currency SDG = new Currency("5.6925", "SDG",
            "Sudanese Pound");
    public final static Currency GBP = new Currency("0.6781", "GBP",
            "British Pound Sterling");
    public final static Currency TWD = new Currency("31.486", "TWD",
            "New Taiwan Dollar");
    public final static Currency MUR = new Currency("36.55", "MUR",
            "Mauritian Rupee");
    public final static Currency INR = new Currency("62.4949", "INR",
            "Indian Rupee");
    public final static Currency EUR = new Currency("0.9388", "EUR", "Euro");
    public final static Currency CLF = new Currency("0.0246", "CLF",
            "Chilean Unit of Account (UF)");
    public final static Currency TMT = new Currency("3.5", "TMT",
            "Turkmenistani Manat");
    public final static Currency ZAR = new Currency("12.3007", "ZAR",
            "South African Rand");
    public final static Currency AUD = new Currency("1.3076", "AUD",
            "Australian Dollar");
    public final static Currency CZK = new Currency("25.747", "CZK",
            "Czech Republic Koruna");
    public final static Currency XCD = new Currency("2.7", "XCD",
            "East Caribbean Dollar");
    public final static Currency RUB = new Currency("60.0495", "RUB",
            "Russian Ruble");
    public final static Currency KZT = new Currency("185.805", "KZT",
            "Kazakhstani Tenge");
    public final static Currency BYR = new Currency("14850", "BYR",
            "Belarusian Ruble");
    public final static Currency CDF = new Currency("920", "CDF",
            "Congolese Franc");
    public final static Currency IEP = new Currency("0.7389", "IEP", "");
    public final static Currency XDR = new Currency("0.7274", "XDR",
            "Special Drawing Rights");
    public final static Currency LBP = new Currency("1503.5", "LBP",
            "Lebanese Pound");
    public final static Currency HUF = new Currency("284.89", "HUF",
            "Hungarian Forint");
    public final static Currency CAD = new Currency("1.2714", "CAD",
            "Canadian Dollar");
    public final static Currency AZN = new Currency("1.0486", "AZN",
            "Azerbaijani Manat");
    public final static Currency MXN = new Currency("15.2846", "MXN",
            "Mexican Peso");
    public final static Currency BTN = new Currency("62.5375", "BTN",
            "Bhutanese Ngultrum");
    public final static Currency VND = new Currency("21485", "VND",
            "Vietnamese Dong");
    public final static Currency AMD = new Currency("479.46", "AMD",
            "Armenian Dram");
    public final static Currency NOK = new Currency("8.0933", "NOK",
            "Norwegian Krone");
    public final static Currency HKD = new Currency("7.7596", "HKD",
            "Hong Kong Dollar");
    public final static Currency UZS = new Currency("2478.23", "UZS",
            "Uzbekistan Som");
    public final static Currency CHF = new Currency("0.9902", "CHF",
            "Swiss Franc");
    public final static Currency COP = new Currency("2630.23", "COP",
            "Colombian Peso");
    public final static Currency JMD = new Currency("115.08", "JMD",
            "Jamaican Dollar");
    public final static Currency USD = new Currency("1", "USD",
            "United States Dollar");
    public final static Currency MDL = new Currency("18.3", "MDL",
            "Moldovan Leu");
    public final static Currency JPY = new Currency("120.8", "JPY",
            "Japanese Yen");
    public final static Currency XOF = new Currency("615.4309", "XOF",
            "CFA Franc BCEAO");

    public final static Collection<Currency> currencies = ImmutableSet.of
            (AMD, AUD, AZN, BTN, BYR,
                    CAD, CDF, CHF, CLF, CNY,
                    COP, CZK, EUR, GBP, HKD,
                    HUF, IEP, INR, JMD, JPY,
                    KZT, LBP, LVL, MDL, MUR,
                    MXN, NOK, RUB, SDG, TMT,
                    TWD, USD, UZS, VND, XCD,
                    XDR, XOF, ZAR);

}

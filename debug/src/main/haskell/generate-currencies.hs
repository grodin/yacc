{-# LANGUAGE OverloadedStrings, GeneralizedNewtypeDeriving #-}
module Main where

import Control.Applicative ((<$>), (<*>))
import Control.Monad (mzero)
import Data.Aeson
import Data.Text (Text)
import qualified Data.Text as T
import Data.Text.Encoding (encodeUtf8)

main :: IO ()
main = interact $ (header ++) . (++ footer) . T.unpack
            . display . process . parse
            . T.pack

header :: String
header = "/*\n" ++
    "" ++
    " * Copyright 2015 Omricat Software\n" ++
    "" ++
    " *\n" ++
    "" ++
    " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" ++
    " * you may not use this file except in compliance with the License.\n" ++
    " * You may obtain a copy of the License at\n" ++
    " *\n" ++
    " *     http://www.apache.org/licenses/LICENSE-2.0\n" ++
    " *\n" ++
    " * Unless required by applicable law or agreed to in writing, software\n" ++
    " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" ++
    " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" ++
    " * See the License for the specific language governing permissions and\n" ++
    " * limitations under the License.\n" ++
    " */\n" ++
    "\n" ++
    "package com.omricat.yacc.data;\n" ++
    "\n" ++
    "import com.omricat.yacc.data.model.Currency;\n" ++
    "import com.google.common.collect.ImmutableSet;\n" ++
    "\n" ++
    "public class TestCurrencies {\n"

footer :: String
footer = "\n}\n"

parse :: Text -> CurrData
parse t = maybe empty id $ decodeStrict $ encodeUtf8 t

process :: CurrData -> CurrData
process cd = CD codes $ CL . filter go . unCL . cData $ cd
    where
        go = (`elem` codes) . curCode
        codes = cCodes cd

display :: CurrData -> Text
display (CD codes (CL cs)) = (`T.append` list) . T.intercalate "\n" $ map displayCurrency cs
    where
        list = T.replace listPin listNeedle listLine
        listNeedle = (T.intercalate "," . map cCode $ codes)
        listPin = "~~~"

listLine :: Text
listLine = "\n\n    public final static Collection<Currency> currencies = ImmutableSet.of(~~~);\n"


data CurrData = CD {cCodes :: [CurrCode], cData :: CurrList}

empty :: CurrData
empty = CD [] $ CL []

instance FromJSON CurrData where
    parseJSON (Object o) = CD <$>
                            o .: "currency-codes" <*>
                            o .: "currency-data"

newtype CurrCode = CCode {cCode :: Text} deriving (Show, FromJSON, Eq)

data Currency = Cur {curCode :: CurrCode, curValue :: Text, curName :: Text}
    deriving (Show)

instance FromJSON Currency where
    parseJSON (Object o) =  Cur <$>
                            o .: "code" <*>
                            o .: "value" <*>
                            o .: "name"
    parseJSON _          =  mzero

data CurrList = CL {unCL :: [Currency]} deriving (Show)

instance FromJSON CurrList where
    parseJSON (Object o) = CL <$> o .: "currencies"
    parseJSON _          = mzero

displayCurrency :: Currency -> Text
displayCurrency c = T.replace codePin code . T.replace valuePin value
                . T.replace namePin name $ line
    where
        code  = cCode . curCode $ c
        value = curValue c
        name  = curName c
        codePin = "###"
        valuePin = "@@@"
        namePin = "¬¬¬"

line :: Text
line = "    public final static Currency ### = new Currency(\"@@@\",\"###\",\"¬¬¬\");"

testData = encodeUtf8 . T.pack $ "{\"currencies\":[{\"code\":\"TMT\"," ++
            "\"value\":\"3.50\",\"name\":\"Turkmenistani Manat\"}," ++
            "{\"code\":\"KZT\",\"value\":\"184.285\",\"name\":\"Kazakhstani " ++
            "Tenge\"},{\"code\":\"BTN\",\"value\":\"61.615\"," ++
            "\"name\":\"Bhutanese Ngultrum\"},{\"code\":\"CHF\"," ++
            "\"value\":\"0.8615\",\"name\":\"Swiss Franc\"}," ++
            "{\"code\":\"MDL\",\"value\":\"17.36\"," ++
            "\"name\":\"Moldovan Leu\"},{\"code\":\"LVL\"," ++
            "\"value\":\"0.6064\",\"name\":\"Latvian Lats\"}," ++
            "{\"code\":\"CDF\",\"value\":\"927.00\",\"name\":\"Congolese " ++
            "Franc\"},{\"code\":\"XDR\",\"value\":\"0.7038\"," ++
            "\"name\":\"Special Drawing Rights\"},{\"code\":\"AUD\"," ++
            "\"value\":\"1.2156\",\"name\":\"Australian Dollar\"}," ++
            "{\"code\":\"SDG\",\"value\":\"5.6925\"," ++
            "\"name\":\"Sudanese Pound\"},{\"code\":\"RUB\"," ++
            "\"value\":\"65.8605\",\"name\":\"Russian Ruble\"}," ++
            "{\"code\":\"VND\",\"value\":\"21380.00\",\"name\":\"Vietnamese " ++
            "Dong\"},{\"code\":\"MUR\",\"value\":\"32.45\"," ++
            "\"name\":\"Mauritian Rupee\"},{\"code\":\"JMD\"," ++
            "\"value\":\"115.10\",\"name\":\"Jamaican Dollar\"}," ++
            "{\"code\":\"LBP\",\"value\":\"1510.00\"," ++
            "\"name\":\"Lebanese Pound\"},{\"code\":\"GBP\"," ++
            "\"value\":\"0.6613\",\"name\":\"British Pound Sterling\"}," ++
            "{\"code\":\"CAD\",\"value\":\"1.2069\"," ++
            "\"name\":\"Canadian Dollar\"},{\"code\":\"MXN\"," ++
            "\"value\":\"14.6014\",\"name\":\"Mexican Peso\"}," ++
            "{\"code\":\"HUF\",\"value\":\"271.585\"," ++
            "\"name\":\"Hungarian Forint\"},{\"code\":\"CNY\"," ++
            "\"value\":\"6.2101\",\"name\":\"Chinese Yuan\"}," ++
            "{\"code\":\"COP\",\"value\":\"2376.45\"," ++
            "\"name\":\"Colombian Peso\"},{\"code\":\"HKD\"," ++
            "\"value\":\"7.7529\",\"name\":\"Hong Kong Dollar\"}," ++
            "{\"code\":\"JPY\",\"value\":\"117.367\"," ++
            "\"name\":\"Japanese Yen\"},{\"code\":\"UZS\"," ++
            "\"value\":\"2430.1699\",\"name\":\"Uzbekistan Som\"}," ++
            "{\"code\":\"AZN\",\"value\":\"0.7831\",\"name\":\"Azerbaijani " ++
            "Manat\"},{\"code\":\"INR\",\"value\":\"61.58\"," ++
            "\"name\":\"Indian Rupee\"},{\"code\":\"CLF\"," ++
            "\"value\":\"0.0247\",\"name\":\"Chilean Unit of Account (UF)\"}," ++
            "{\"code\":\"EUR\",\"value\":\"0.8617\",\"name\":\"Euro\"}," ++
            "{\"code\":\"IEP\",\"value\":\"0.6795\",\"name\":\"\"}," ++
            "{\"code\":\"CZK\",\"value\":\"24.078\",\"name\":\"Czech Republic" ++
            " Koruna\"},{\"code\":\"AMD\",\"value\":\"477.91\"," ++
            "\"name\":\"Armenian Dram\"},{\"code\":\"XCD\"," ++
            "\"value\":\"2.70\",\"name\":\"East Caribbean Dollar\"}," ++
            "{\"code\":\"BYR\",\"value\":\"15090.00\",\"name\":\"Belarusian " ++
            "Ruble\"},{\"code\":\"XOF\",\"value\":\"565.9925\"," ++
            "\"name\":\"CFA Franc BCEAO\"},{\"code\":\"TWD\"," ++
            "\"value\":\"31.492\",\"name\":\"New Taiwan Dollar\"}," ++
            "{\"code\":\"USD\",\"value\":\"1.0\",\"name\":\"United States " ++
            "Dollar\"},{\"code\":\"ZAR\",\"value\":\"11.5518\"," ++
            "\"name\":\"South African Rand\"},{\"code\":\"NOK\"," ++
            "\"value\":\"7.6024\",\"name\":\"Norwegian Krone\"}]," ++
            "\"timestamp\":1421847301}"

tinyTest = encodeUtf8 . T.pack $ "{\"code\":\"TMT\",\"value\":\"3.50\",\"name\":\"Turkmenistani Manat\"}"
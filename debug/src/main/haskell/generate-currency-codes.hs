{-# LANGUAGE OverloadedStrings #-}
module Main where

import qualified Data.Text as T

main :: IO ()
main = interact $ (header ++) . (++ footer) . unlines . generateCodes . lines

header :: String
header = "/*\n" ++
    " * Copyright 2015 Omricat Software\n" ++
    " *\n" ++
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
    "\npackage com.omricat.yacc.data;\n" ++
    "\nimport com.omricat.yacc.data.model.CurrencyCode;\n" ++
    "\n/**\n" ++
    " * Class containing CurrencyCodes as constants, to be used as test data.\n" ++
    " */\n" ++
    "public class TestCurrencyCodes {\n"

footer :: String
footer = "}\n"

generateCodes :: [String] -> [String]
generateCodes =  map (T.unpack . go . T.pack)
    where
        go s = T.replace pin s line

pin :: T.Text
pin = "@@@"

line :: T.Text
line = "    public final static CurrencyCode @@@_CODE = new CurrencyCode(\"@@@\");"
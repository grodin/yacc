# Yacc Core Module

This gradle project contains all the "pure" Java 7 code for `yacc`.

Anything that can be written as pure Java code, without a dependency on
Android, goes in here, as long as it isn't pushing things to extremes. We
don't want every tiny scrap of pure Java in here, but things for which it
makes sense to break them out should be.

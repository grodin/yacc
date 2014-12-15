# Lighttpd Config

This directory contains a config file for [lighttpd][1] and a file called
`latest` which contains json corresponding to the format returned from the
backend.

When run with `lighttpd -D -f lighttpd.conf` this will start a server on the
local machine on **port 3000** to provide something to write against without
needing to connect to the real backend all the time.

[1]: http://www.lighttpd.net/

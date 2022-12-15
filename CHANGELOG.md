# Changelog

All notable changes to this project will be documented in this file.

## [2.0.4.1] - 2022-12-15

- Sources of `me.botsko:elixr` dependency included into repository. Since original maven repository was down.
- Make project buildable.
- Speedup mysql batch inserts.
- Remove warnings about SSL when connecting to mysql.
- Fix mysql connection leaks.
- Disable `RemoveAbandoned` mysql setting. It harms when database has lots of data and query
  lasts too long thus triggering unwanted connection closing.

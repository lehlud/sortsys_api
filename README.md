# pgql

API Interace for PostgresSQL RDBMS using an SQL-like query language usefule providing JSON-based APIs with finegrained authorization and powerful dynamic queries.

## Why?

For one specific API I wanted to provide dynamic query possibilities without sacrificing security while still being able to build those queries client side.

This is why I created this query language. Its purpose is mainly to be able to inject extra conditions into select and join queries without opening the door for sql injections.

## Postgres Mapping

### JSON

```sql
SELECT { ...json(x), "json": json(x) } FROM x
```
maps to
```sql
SELECT to_jsonb("x") || jsonb_build_object('json', to_jsonb("x"))
FROM "x" AS "x"
```

#### Hiding properties
```sql
SELECT { "a": 17, "b": 42 } WITHOUT ["a"]
```
maps to
```sql
SELECT jsonb_build_object('a', 17, 'b', 42) - 'a'
```

### Standard query

```sql
SELECT json(x) FROM x
```
maps to
```sql
SELECT to_jsonb("x") FROM "x" AS "x"
```

### Subqueries

```sql
SELECT * FROM (SELECT json(x) AS x FROM x)
```
maps to
```sql
SELECT * FROM (SELECT to_jsonb("x") AS "x" FROM "x" AS "x")
```

<br />
<br />

```sql
SELECT json(array(SELECT json(y) FROM y)) FROM x
```
maps to
```sql
SELECT to_jsonb(
  ARRAY(SELECT to_jsonb("y") FROM "y" AS "y")
) FROM "x" AS "x"
```

### Joins

```sql
SELECT json(y.*) FROM x JOIN y ON y.id = x.y
```
maps to
```sql
SELECT to_jsonb("y".*) FROM "x" AS "x"
LEFT JOIN "y" AS "y" ON "y"."id" = "x"."y"
```

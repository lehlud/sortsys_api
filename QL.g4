grammar QL;

prog: (stmts | query) EOF;

stmts: stmt | stmts stmt;
stmt: query ';';

query: select;

select: SELECT columns distinct? from? join* where? groupBy? having? limit? offset? orderBy?;

columns: column | columns ',' column;
column: expr (AS word)?;

distinct: DISTINCT ON exprs;
from: FROM table;
join: FULL? (LEFT | RIGHT)? (INNER | OUTER)? JOIN table ON expr;
where: WHERE expr;
groupBy: GROUP BY exprs;
having: HAVING expr;
limit: LIMIT expr;
offset: OFFSET expr;

orderBy: ORDER BY orders;
orders: order | orders ',' order;
order: expr (ASC | DESC);

table: (expr | '(' query ')') (AS word)?;

exprs: expr | exprs ',' expr;
expr: term;

term: term6;
term6: term5 | term6 (OR) term5;
term5: term4 | term5 (AND) term4;
term4: term3 | term4 ('==' | '!=' | '>' | '<' | '>=' | '<=') term3;
term3: term2 | term3 ('+' | '-') term2;
term2: term1 | term2 ('*' | '/' | '%') term1;
term1: term0 | term1 ('||') term0;
term0: call | literal | '(' expr ')' | op=(NOT | '+' | '-') term0;

literal: str | json | NUM | BOOL | NULL | identifier;
str: STR;

json: jsonObject | jsonArray;
jsonArray: '[' (exprs (',')?)? ']';
jsonObject: '{' (jsonProps (',')?)? '}' | jsonObject WITHOUT jsonArray;
jsonProps: jsonProp | jsonProps ',' jsonProp;
jsonProp: str ':' expr;

call: word '(' ((| exprs (',')?) | query) ')';

identifier: '*' | word ('.' ('*' | word))?;

word: keyword | IDENTIFIER;
keyword:
  AS
	| FROM
	| FULL
    | HAVING
	| INNER
	| JOIN
	| LEFT
	| LIMIT
	| OFFSET
	| OUTER
	| SELECT
	| UPDATE
	| WHERE
	| WITHOUT;

ALL: 'ALL' | 'all';
AND: 'AND' | 'and';
AS: 'AS' | 'as';
ASC: 'ASC' | 'asc';
BY: 'BY' | 'by';
DESC: 'DESC' | 'desc';
DISTINCT: 'DISTINCT' | 'distinct';
FROM: 'FROM' | 'from';
FULL: 'FULL' | 'full';
GROUP: 'GROUP' | 'group';
HAVING: 'HAVING' | 'having';
INNER: 'INNER' | 'inner';
IS: 'IS' | 'is';
JOIN: 'JOIN' | 'join';
LEFT: 'LEFT' | 'left';
LIMIT: 'LIMIT' | 'limit';
NOT: 'NOT' | 'not';
OFFSET: 'OFFSET' | 'offset';
ON: 'ON' | 'on';
OR: 'OR' | 'or';
ORDER: 'ORDER' | 'order';
OUTER: 'OUTER' | 'outer';
RIGHT: 'RIGHT' | 'right';
SELECT: 'SELECT' | 'select';
UPDATE: 'UPDATE' | 'update';
WHERE: 'WHERE' | 'where';
WITHOUT: 'WITHOUT' | 'without';

STR: '"' (~["] | '\\"')* '"';
NUM: ([0-9]* '.')? [0-9]+;
BOOL: 'TRUE' | 'FALSE' | 'true' | 'false';
NULL: 'null' | 'NULL';

IDENTIFIER: [a-zA-Z_] [0-9a-zA-Z_]*;

WHITESPACE: [\n\r\t ]+ -> skip;
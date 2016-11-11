| Number | Production Name | Production |
|--------|-----------------|------------|
|  1 | *statement* -> | **if** *expression* **then** *statement* |
|  2 | *factor* -> | **id** |
|  3 | *program* -> | **program** **id** **(** *identifier_list* **)** **;** *declarations* *subprogram_declarations* *compound_statement* **.** |
|  4 | *identifier_list* -> | **id** \| *identifier_list* **,** **id** |
|  5 | *declarations* -> | *declarations* **var** *identifier_list* **:** type **;** \| **ϵ** |
|  6 | *type* -> | *standard_type* \| **array** **[** **num** **\.\.** **num** **]** **of** *standard_type* |
|  7 | *standard_type* -> | **integer** \| **real** |
|  8 | *subprogram_declarations* -> | *subprogram_declarations* *subprogram_declaration* **;** \| **ϵ** |
|  9 | *subprogram_declaration* -> | *subprogram_head* *declarations* *compound_statement* |
| 10 | *subprogram_head* -> | **function** **id** *arguments* **:** *standard_type* **;** \| **procedure** **id** *arguments* **;** |
| 11 | *arguments* -> | **(** *parameter_list* **)** \| **ϵ** |
| 12 | *parameter_list* -> | *identifier_list* **:** *type* \| *parameter_list* **;** *identifier_list* **:** *type* |
| 13 | *compound_statement* -> | **begin** *optional_statements* **end** |
| 14 | *optional_statements* -> | *statement_list* \| **ϵ** |
| 15 | *statement_list* -> | *statement* \| *statement_list* **;** *statement* |
| 16 | *statement* -> | *variable* **assignop** *expression* \| *procedure_statement* \| *compound_statement* \| **if** *expression* **then** *statement* **else** *statement* \| **while** *expression* **do** *statement* |
| 17 | *variable* -> | **id** \| **id** **[** *expression* **]** |
| 18 | *procedure_statement* -> | **id** \| **id** **(** *expression_list* **)** |
| 19 | *expression_list* -> | *expression* \| *expression_list* **,** *expression* |
| 20 | *expression* -> | *simple_expression* \| *simple_expression* **relop** *simple_expression* |
| 21 | *simple_expression* -> | *term* \| *sign* *term* \| *simple_expression* **addop** *term* |
| 22 | *term* -> | *factor* \| *term* **mulop** *factor* |
| 23 | *factor* -> | **id** \| **id** **(** *expression_list* **)** \| **num** \| **(** *expression* **)** \| **not** *factor* |
| 24 | *sign* -> | **+** \| **-** |

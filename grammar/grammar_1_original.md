| Number | Production Name | Production |
|--------|-----------------|------------|
|  1 | *program* → | **program** **id** **(** *identifier_list* **)** **;** *declarations* *subprogram_declarations* *compound_statement* **.** |
|  2 | *identifier_list* → | **id** \| *identifier_list* **,** **id** |
|  3 | *declarations* → | *declarations* **var** *identifier_list* **:** type **;** \| **ϵ** |
|  4 | *type* → | *standard_type* \| **array** **[** **num** **\.\.** **num** **]** **of** *standard_type* |
|  5 | *standard_type* → | **integer** \| **real** |
|  6 | *subprogram_declarations* → | *subprogram_declarations* *subprogram_declaration* **;** \| **ϵ** |
|  7 | *subprogram_declaration* → | *subprogram_head* *declarations* *compound_statement* |
|  8 | *subprogram_head* → | **function** **id** *arguments* **:** *standard_type* **;** \| **procedure** **id** *arguments* **;** |
|  9 | *arguments* → | **(** *parameter_list* **)** \| **ϵ** |
| 10 | *parameter_list* → | *identifier_list* **:** *type* \| *parameter_list* **;** *identifier_list* **:** *type* |
| 11 | *compound_statement* → | **begin** *optional_statements* **end** |
| 12 | *optional_statements* → | *statement_list* \| **ϵ** |
| 13 | *statement_list* → | *statement* \| *statement_list* **;** *statement* |
| 14 | *statement* → | *variable* **assignop** *expression* \| *procedure_statement* \| *compound_statement* \| **if** *expression* **then** *statement* **else** *statement* \| **while** *expression* **do** *statement* |
| 15 | *variable* → | **id** \| **id** **[** *expression* **]** |
| 16 | *procedure_statement* → | **id** \| **id** **(** *expression_list* **)** |
| 17 | *expression_list* → | *expression* \| *expression_list* **,** *expression* |
| 18 | *expression* → | *simple_expression* \| *simple_expression* **relop** *simple_expression* |
| 19 | *simple_expression* → | *term* \| *sign* *term* \| *simple_expression* **addop** *term* |
| 20 | *term* → | *factor* \| *term* **mulop** *factor* |
| 21 | *factor* → | **id** \| **id** **(** *expression_list* **)** \| **num** \| **(** *expression* **)** \| **not** *factor* |
| 22 | *sign* → | **+** \| **-** |
